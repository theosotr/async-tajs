/*
 * Copyright 2009-2018 Aarhus University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.brics.tajs.lattice;

import dk.brics.tajs.analysis.CallbackCallInfo;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.nativeobjects.ECMAScriptObjects;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.lattice.ObjectLabel.Kind;
import dk.brics.tajs.lattice.PKey.StringPKey;
import dk.brics.tajs.lattice.PKey.SymbolPKey;
import dk.brics.tajs.options.OptionValues;
import dk.brics.tajs.options.Options;
import dk.brics.tajs.solver.BlockAndContext;
import dk.brics.tajs.solver.CallbackGraph;
import dk.brics.tajs.solver.GenericSolver;
import dk.brics.tajs.solver.IState;
import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajs.util.Chain;
import dk.brics.tajs.util.Collectors;
import dk.brics.tajs.util.Strings;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static dk.brics.tajs.util.Collections.addToMapSet;
import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newMap;
import static dk.brics.tajs.util.Collections.newSet;
import static dk.brics.tajs.util.Collections.singleton;
import static dk.brics.tajs.util.Collections.sortedEntries;

/**
 * Abstract state for block entries.
 * Mutable.
 */
public class State implements IState<State, Context, CallEdge> {

    private static Logger log = Logger.getLogger(State.class);

    private GenericSolver<State, Context, CallEdge, ? extends ILatticeMonitoring, ?>.SolverInterface c;

    /**
     * The basic block owning this state.
     */
    private BasicBlock block;

    /**
     * The context for this state.
     */
    private Context context; // may be shared by other State objects

    private CallbackContext callbackContext;

    /**
     * Map from ObjectLabel to Object.
     */
    private Map<ObjectLabel, Obj> store;

    private Map<ObjectLabel, Set<QueueObject>> queue;

    private Chain<QueueContext> queueChain;

    private Chain<CallbackDescription> scheduledCallbacks;

    private Obj store_default; // either the none obj (for program entry) or the unknown obj (all other locations)

    private boolean writable_store; // for copy-on-write

    /**
     * Reusable immutable part of the store.
     * Entries may be overridden by 'store'.
     * Not used if lazy propagation is enabled.
     */
    private Map<ObjectLabel, Obj> basis_store;

    /**
     * Current execution context.
     */
    private ExecutionContext execution_context;

    private boolean writable_execution_context; // for copy-on-write

    /**
     * Maybe/definitely summarized objects since function entry. (Contains the singleton object labels.)
     */
    private Summarized summarized;

    /**
     * Register values.
     */
    private List<Value> registers; // register values never have attributes or modified flag

    private boolean writable_registers; // for copy-on-write

    /**
     * Object labels that appear on the stack.
     */
    private Set<ObjectLabel> stacked_objlabels; // not used if lazy propagation is enabled

    private boolean writable_stacked_objlabels; // for copy-on-write

    private StateExtras extras;

    private static int number_of_states_created;

    private static int number_of_makewritable_store;

    public static Map<ObjectLabel, QueueObject.Kind> QUEUE_OBJECT_KINDS = newMap();


    /**
     * Constructs a new none-state (representing the empty set of concrete states).
     */
    public State(GenericSolver<State, Context, CallEdge, ? extends ILatticeMonitoring, ?>.SolverInterface c, BasicBlock block) {
        this.c = c;
        this.block = block;
        summarized = new Summarized();
        extras = new StateExtras();
        setToBottom();
        number_of_states_created++;
    }

    /**
     * Constructs a new state as a copy of the given state.
     */
    private State(State x) {
        c = x.c;
        block = x.block;
        context = x.context;
        callbackContext = x.callbackContext != null ? x.callbackContext.clone() : null;
        setToState(x);
        number_of_states_created++;
    }

    /**
     * Constructs a new state as a copy of this state.
     */
    @Override
    public State clone() {
        return new State(this);
    }

    /**
     * Sets this state to the same as the given one.
     */
    private void setToState(State x) {
        summarized = new Summarized(x.summarized);
        store_default = x.store_default.freeze();
        extras = new StateExtras(x.extras);
        store = newMap();
        queue = new LinkedHashMap<>();
        for (Map.Entry<ObjectLabel, Obj> xs : x.store.entrySet())
            writeToStore(xs.getKey(), xs.getValue().freeze());
        for (Map.Entry<ObjectLabel, Set<QueueObject>> ps: x.queue.entrySet())
            this.queue.put(
                    ps.getKey(),
                    ps.getValue()
                            .stream()
                            .map(QueueObject::clone)
                            .collect(Collectors.toSet()));
        this.queueChain = x.queueChain != null ?
                x.queueChain.clone() : null;
        this.scheduledCallbacks = x.scheduledCallbacks != null ?
                x.scheduledCallbacks.clone() : null;
        basis_store = x.basis_store;
        writable_store = true;
        execution_context = x.execution_context.clone();
        registers = newList(x.registers);
        writable_registers = true;
        stacked_objlabels = newSet(x.stacked_objlabels);
        writable_stacked_objlabels = true;
    }

    /**
     * Returns the solver interface.
     */
    public GenericSolver<State, Context, CallEdge, ? extends ILatticeMonitoring, ?>.SolverInterface getSolverInterface() {
        return c;
    }

    /**
     * Returns the extra stuff.
     */
    public StateExtras getExtras() {
        return extras;
    }

    @Override
    public BasicBlock getBasicBlock() {
        return block;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public CallbackContext getCallbackContext() {
        return this.callbackContext;
    }

    /**
     * Checks whether the return register has a value.
     */
    public boolean hasReturnRegisterValue() {
        return AbstractNode.RETURN_REG < registers.size() && registers.get(AbstractNode.RETURN_REG) != null;
    }

    /**
     * Checks whether the exception register has a value.
     */
    public boolean hasExceptionRegisterValue() {
        return AbstractNode.EXCEPTION_REG < registers.size() && registers.get(AbstractNode.EXCEPTION_REG) != null;
    }

    /**
     * Sets the basic block owning this state.
     */
    public void setBasicBlock(BasicBlock block) {
        this.block = block;
    }

    /**
     * Sets the context.
     */
    public void setContext(Context context) {
        this.context = context;
    }

    public void setCallbackContext(CallbackContext context) {
        this.callbackContext = context;
    }

    /**
     * Returns the store (excluding the basis store).
     * Only for reading!
     */
    public Map<ObjectLabel, Obj> getStore() {
        return store;
    }

    public Map<ObjectLabel, Set<QueueObject>> getQueue() {
        return this.queue;
    }

    /**
     * Sets an object in the store.
     */
    public void putObject(ObjectLabel objlabel, Obj obj) {
        makeWritableStore();
        writeToStore(objlabel, obj);
    }

    private void writeToStore(ObjectLabel objlabel, Obj obj) {
        store.put(objlabel, obj);
    }

    /**
     * Removes an object in the store.
     */
    public void removeObject(ObjectLabel objlabel) {
        makeWritableStore();
        store.remove(objlabel);
    }

    /**
     * Looks up an object in the store.
     */
    public Obj getObject(ObjectLabel objlabel, boolean writable) {
        if (writable)
            makeWritableStore();
        Obj obj = store.get(objlabel);
        if (obj != null && writable && !obj.isWritable()) {
            // object exists but isn't yet writable, make it writable
            obj = new Obj(obj);
            writeToStore(objlabel, obj);
            if (log.isDebugEnabled())
                log.debug("making writable object from store: " + objlabel);
        }
        if (obj == null && basis_store != null) {
            // check the basis_store
            obj = basis_store.get(objlabel);
            if (obj != null && writable) {
                obj = new Obj(obj);
                writeToStore(objlabel, obj);
                if (log.isDebugEnabled())
                    log.debug("making writable object from basis store: " + objlabel);
            }
        }
        if (obj == null) {
            // take the default
            obj = store_default;
            if (writable) {
                obj = new Obj(obj);
                writeToStore(objlabel, obj);
                if (log.isDebugEnabled())
                    log.debug("making writable object from store default: " + objlabel + " at " + block.getSourceLocation());
            }
        }
        return obj;
    }

    /**
     * Returns the store default object.
     */
    public Obj getStoreDefault() {
        return store_default;
    }

    /**
     * Sets the store default object.
     */
    public void setStoreDefault(Obj obj) {
        store_default = obj;
    }

    public void appendToQueueChain(Set<ObjectLabel> objectLabels, boolean implicit) {
        Set<QueueContext> queueContext = objectLabels
                .stream()
                .map(x -> new QueueContext(x, implicit))
                .collect(Collectors.toSet());
        if (this.queueChain == null)
            this.queueChain = Chain.make(
                    queueContext,
                    null);
        else
            this.queueChain = Chain.appendElement(
                    queueContext, this.queueChain);
    }

    public void popQueueChain() {
        if (this.queueChain == null)
            return;
        this.queueChain = queueChain.getNext();
    }

    public boolean isQueueChainEmpty() {
        if (this.queueChain == null)
            return true;
        boolean isEmpty = this.queueChain.isEmpty();
        if (isEmpty && this.queueChain.getNext() != null)
            throw new AnalysisException(
                    "An empty queue chain with successors found");
        return isEmpty;
    }

    private void appendToScheduledCallbacks(ObjectLabel queueObject,
                                            QueueObject.QueueState state,
                                            Set<CallbackExecutionInfo> callbacks,
                                            Value settledBy,
                                            final boolean restrictSchedule) {
        if (callbacks == null || callbacks.isEmpty())
            return;
        Set<CallbackDescription> callbackDescs = callbacks
                .stream()
                .map(x -> new CallbackDescription(
                        queueObject,
                        state,
                        x.getCallback(),
                        x.getQueueObject(),
                        settledBy
                ))
                .collect(Collectors.toSet());
        this.appendToScheduledCallbacks(callbackDescs, restrictSchedule);

    }

    public void appendToScheduledCallbacks(Set<CallbackDescription> callbackDescs,
                                           final boolean restrictSchedule) {
        if (this.scheduledCallbacks != null) {
            callbackDescs = callbackDescs
                    .stream()
                    .filter(x -> restrictSchedule ? this.isNewFlow(x)
                            : !this.scheduledCallbacks.has(x))
                    .collect(Collectors.toSet());
            if (this.scheduledCallbacks.getLast().equals(callbackDescs))
                callbackDescs.clear();
        }
        if (callbackDescs.isEmpty())
            return;
        Chain<CallbackDescription> newScheduledCallback = Chain.make(
                callbackDescs, null);
        if (this.scheduledCallbacks == null)
            this.scheduledCallbacks = newScheduledCallback;
        else {
            if (this.scheduledCallbacks.size() >= Options.get().getBoundedSize()) {
                this.scheduledCallbacks.appendLast(callbackDescs);
            } else {
                this.scheduledCallbacks.appendLast(newScheduledCallback);
            }
        }
    }

    private void addToQueue(ObjectLabel objectLabel,
                            QueueObject.Kind kind) {
        Obj obj = store.get(objectLabel);
        if (obj == null)
            throw new AnalysisException(
                "You are going to put an object label in the queue "
                    + "that was not found in the store: "
                    + objectLabel.toString());
        Set<QueueObject> queueObjects = this.queue.get(objectLabel);
        QueueObject queueObject = new QueueObject(null, kind);
        if (queueObjects != null) {
            queueObjects.add(queueObject);
            return;
        }
        queueObjects = newSet();
        queueObjects.add(queueObject);
        this.queue.put(objectLabel, queueObjects);
    }

    public void newQueueObject(ObjectLabel objectLabel) {
        addToQueue(objectLabel, QueueObject.Kind.PROMISE);
    }

    public void newQueueObject(ObjectLabel objectLabel,
                               QueueObject.Kind kind) {
        addToQueue(objectLabel, kind);
    }

    public void addNewQueueObjectTo(ObjectLabel objectLabel,
                                    QueueObject queueObject) {
        Set<QueueObject> queueObjects = this.queue.get(objectLabel);
        if (queueObjects == null)
            throw new AnalysisException(
                 "We cannot find queue object " + objectLabel.toString()
                  + " in the queue");
        Set<QueueObject> pendingObjs = queueObjects
                .stream()
                .filter(x -> !x.isSettled() && !x.isDependent())
                .collect(java.util.stream.Collectors.toSet());
        if (pendingObjs.size() == 0) {
            queueObjects.add(queueObject);
            return;
        }
        queueObjects.removeAll(pendingObjs);
        for (QueueObject qObj: pendingObjs) {
            QueueObject q = queueObject.clone();
            q.setResolvedCallbacks(q.getResolvedCallbacks()
                    .join(qObj.getResolvedCallbacks()));
            q.setRejectedCallbacks(q.getRejectedCallbacks()
                    .join(qObj.getRejectedCallbacks()));
            if (q.getState() == QueueObject.QueueState.FULFILLED)
                q.resolve(q.getDefaultValue(), false);
            else if (q.getState() == QueueObject.QueueState.REJECTED)
                q.reject(q.getDefaultValue(), false);
            queueObjects.add(q);
        }
    }

    public void addQueueObjects(ObjectLabel objectLabel,
                                Set<QueueObject> queueObjects) {
        this.queue.put(objectLabel, queueObjects);
    }

    public boolean isNewFlow(CallbackDescription desc) {
        if (this.scheduledCallbacks != null) {
            Set<CallbackDescription> callbackDescriptions =
                    this.scheduledCallbacks.getAllElements();
            return callbackDescriptions
                    .stream().noneMatch(x -> x.getState()
                            .equals(desc.getState()) && x.getCallback().equals(desc.getCallback()));
        }
        return true;
    }

    public void settleQueueObject(ObjectLabel objectLabel, Value value,
                                  final boolean shouldResolve,
                                  final boolean forceJoin,
                                  Value settledBy,
                                  final boolean restrictSchedule) {
        Set<QueueObject> queueObjects = this.queue.get(objectLabel);
        if (queueObjects == null)
            throw new AnalysisException(
                "We cannot resolve object " + objectLabel.toString()
                + ": It was not found in the queue.");
        QueueObject.QueueState nextState = shouldResolve ?
                QueueObject.QueueState.FULFILLED : QueueObject.QueueState.REJECTED;
        boolean hasState = queueObjects.
                stream().anyMatch(x -> x.getState().equals(nextState));
        for (QueueObject queueObject: queueObjects) {
            if (queueObject.canBeSettledBy(settledBy, false)) {
                QueueObject.QueueState prevState = queueObject.getState();
                if (shouldResolve)
                    queueObject.resolve(value, forceJoin);
                else
                    queueObject.reject(value, forceJoin);
                HostObject hostObject = objectLabel.getHostObject();
                boolean isNative = hostObject == ECMAScriptObjects.ASYNC_IO ||
                        hostObject == ECMAScriptObjects.SET_TIMEOUT;
                if (prevState == QueueObject.QueueState.PENDING && !isNative)
                    this.appendToScheduledCallbacks(
                            objectLabel,
                            queueObject.getState(),
                            queueObject.getCallbacks(),
                            settledBy,
                            restrictSchedule);
                Set<ObjectLabel> dependentObjects = queueObject
                        .getDependentObjects();
                for (ObjectLabel dependentObj : dependentObjects) {
                    this.settleQueueObject(dependentObj, value, shouldResolve,
                                           forceJoin,
                                           Value.makeObject(objectLabel),
                                           restrictSchedule);
                }
            }
        }
        // It's a good idea to join queue objects after the settle.
        QueueObject.Kind queueObjKind = QUEUE_OBJECT_KINDS
                .getOrDefault(objectLabel, QueueObject.Kind.PROMISE);
        Set<QueueObject> empty = newSet();
        Set<QueueObject> jointObjects = QueueObject.join(
                queueObjects, empty,
                QueueObject.Join.DEFAULT, true,
                queueObjKind);
        this.addQueueObjects(objectLabel, jointObjects);
    }

    /**
     * Settles all the queue objects specified in the top of
     * the current queue chain based on the given value.
     */
    public void settleQueueObjects(
            Value value, boolean shouldResolve,
            Value settledBy,
            boolean implicitOnly,
            boolean restrictSchedule) {
        if (this.queueChain == null)
            return;
        Set<QueueContext> queueContexts = this.queueChain.getTop();
        if (queueContexts == null)
            return;
        for (QueueContext queueContext : queueContexts) {
            if (implicitOnly && !queueContext.isImplicit())
                continue;
            this.settleByContext(value, queueContext,
                    shouldResolve, settledBy, restrictSchedule);
        }

    }

    private void settleByContext(Value value,
                                 QueueContext queueContext,
                                 boolean shouldResolve,
                                 Value settledBy,
                                 boolean restrictSchedule) {
        if (value.isMaybePromise()) {
            // TODO revisit
            Set<QueueObject> queueObjects = this.queue.get(
                    queueContext.getQueueObject());
            Value val = value.restrictToPromises();
            boolean isPending = queueObjects
                    .stream()
                    .anyMatch(x -> !x.isSettled());
            for (ObjectLabel l : val.getObjectLabels()) {
                Set<QueueObject> qObjs = this.queue.get(l);
                queueObjects = QueueObject.join(
                        queueObjects,
                        qObjs,
                        QueueObject.Join.LEFT, true,
                        QUEUE_OBJECT_KINDS.getOrDefault(l, QueueObject.Kind.PROMISE));
                for (QueueObject q : qObjs)
                    if (!q.isSettled())
                        q.addDependentObject(queueContext.getQueueObject());
            }
            if (isPending && queueObjects.stream().allMatch(QueueObject::isSettled)) {
                Set<CallbackDescription> callbacks = QueueObject
                        .toScheduledCallbacks(queueContext.getQueueObject(),
                                              queueObjects, settledBy);
                if (!restrictSchedule)
                    this.appendToScheduledCallbacks(callbacks, restrictSchedule);
            }
            this.addQueueObjects(
                    queueContext.getQueueObject(), queueObjects);
        }
        if (value.isMaybeNonPromise()) {
            Value val = value.restrictToNonPromises();
            this.settleQueueObject(queueContext.getQueueObject(),
                    val, shouldResolve, true,
                    settledBy, restrictSchedule);
        }
    }

    public void settleQueueObject(ObjectLabel objectLabel, Value value,
                                  final boolean shouldResolve,
                                  Value settledBy,
                                  final boolean restrictSchedule) {
        this.settleQueueObject(objectLabel, value, shouldResolve, false,
                               settledBy, restrictSchedule);
    }

    public void registerCallback(ObjectLabel objectLabel,
                                 Value callback,
                                 ObjectLabel thisObj,
                                 ObjectLabel queueObj,
                                 List<Value> args,
                                 final boolean onFulfill,
                                 final boolean implicit) {
        Set<QueueObject> queueObjects = this.queue.get(objectLabel);
        if (queueObjects == null)
            throw new AnalysisException(
                    "We cannot add callback " + callback.toString()
                    + " to object " + objectLabel.toString()
                    + " because the latter was not found in the queue.");
        for (QueueObject queueObject: queueObjects) {
            CallbackExecutionInfo clbInfo = CallbackExecutionInfo
                    .make(callback, thisObj, queueObj, implicit);
            HostObject hostObject = objectLabel.getHostObject();
            boolean isNative = hostObject == ECMAScriptObjects.ASYNC_IO ||
                    hostObject == ECMAScriptObjects.SET_TIMEOUT;
            if (queueObject.isSettled() && !isNative) {
                if ((queueObject.getState() == QueueObject.QueueState.FULFILLED && onFulfill)
                    || queueObject.getState() == QueueObject.QueueState.REJECTED && !onFulfill)
                    this.appendToScheduledCallbacks(
                            objectLabel, queueObject.getState(),
                            Collections.singleton(clbInfo), null, false);
            }
            if (onFulfill)
                queueObject.addResolvedCallback(clbInfo, args);
            else
                queueObject.addRejectedCallback(clbInfo, args);
        }
    }

    public void onResolve(ObjectLabel queueObj, Value callback,
                          ObjectLabel thisObj, ObjectLabel dependentObj,
                          List<Value> args, boolean implicit) {
        this.registerCallback(queueObj, callback, thisObj, dependentObj, args,
                             true, implicit);
    }

    public void onReject(ObjectLabel queueObj, Value callback,
                         ObjectLabel thisObj, ObjectLabel dependentObj,
                         List<Value> args, boolean implicit) {
        this.registerCallback(queueObj, callback, thisObj, dependentObj, args,
                             false, implicit);
    }

    private Set<List<CallbackCallInfo>> getNextCallbacksToRun(
            Set<ObjectLabel> objectLabels) {
        Set<List<CallbackCallInfo>> scheduledCallbacks = newSet();
        for (ObjectLabel objectLabel : objectLabels) {
            Set<QueueObject> queueObjects = this.queue.get(objectLabel);
            if (queueObjects == null)
                throw new AnalysisException(
                        "Object Label " + objectLabel.toString()
                        + " is not in the queue");
            for (QueueObject queueObject : queueObjects) {

                if (queueObject.hasCallbacksToRun()) {
                    // FIXME
                    Set<List<CallbackCallInfo>> callbacks = queueObject
                            .getCallbacksOrder()
                            .stream()
                            .map(x -> IntStream.range(0, x.size())
                                    .mapToObj(i -> new CallbackCallInfo(
                                            x.get(i).getCallback(),
                                            x.get(i).getArgs(),
                                            Collections.singleton(x.get(i).getThisObj()),
                                            Collections.singleton(objectLabel),
                                            Collections.singleton(x.get(i).getQueueObject()),
                                            x.get(i).isImplicit(),
                                            new NativeCallbackContext(i, false)
                                    ))
                                    .collect(Collectors.toList()))
                            .collect(Collectors.toSet());
                    if (!callbacks.isEmpty())
                        scheduledCallbacks.addAll(callbacks);
                }
            }
        }
        return scheduledCallbacks;
    }

    private Chain<CallbackCallInfo> extractCallbackExecutionInfo() {
        Chain<CallbackCallInfo> callbacks = null;
        Chain<CallbackDescription> scheduledCallbacks = this.scheduledCallbacks;
        int i = 0;
        while (scheduledCallbacks != null) {
            Set<CallbackDescription> top = scheduledCallbacks.getTop();
            if (top == null)
                throw new AnalysisException("Top is empty");
            Set<CallbackCallInfo> callbackInfos = newSet();
            for (CallbackDescription x : top) {
                Set<QueueObject> queueObjects = this.queue.get(
                        x.getQueueObject());
                if (queueObjects == null)
                    throw new AnalysisException("It is not a queue object");
                boolean found = false;
                for (QueueObject queueObject : queueObjects) {
                    if (queueObject.getState() != x.getState())
                        continue;
                    int finalI = i;
                    Set<CallbackCallInfo> filteredCallbacks = queueObject
                            .getCallbacks()
                            .stream()
                            .filter(y -> y.getQueueObject().equals(x.getDependentQueueObject())
                                    && y.getCallback().equals(x.getCallback()))
                            .map(y -> new CallbackCallInfo(
                                    y.getCallback(),
                                    y.getArgs(),
                                    Collections.singleton(y.getThisObj()),
                                    Collections.singleton(x.getQueueObject()),
                                    Collections.singleton(y.getQueueObject()),
                                    y.isImplicit(),
                                    new NativeCallbackContext(finalI, true)
                            ))
                            .collect(java.util.stream.Collectors.toSet());
                    if (filteredCallbacks.size() != 1) {
                        throw new AnalysisException("Multiple callbacks found");
                    }
                    found = true;
                    callbackInfos.add(filteredCallbacks.iterator().next());
                    break;
                }
                if (!found)
                    throw new AnalysisException("Callback not found");
            }
            Chain<CallbackCallInfo> newChain = Chain.make(
                    callbackInfos, null);
            if (callbacks == null)
                callbacks = newChain;
            else
                callbacks.appendLast(newChain);
            i++;
            scheduledCallbacks = scheduledCallbacks.getNext();
        }
        return callbacks;
    }

    /**
     * Returns the next callbacks to be executed by the event loop.
     */
    public Set<CallbackGraph.CallbackGraphNode> getNextCallbacksToRun() {
        Chain<CallbackCallInfo> chain = extractCallbackExecutionInfo();
        Chain<CallbackCallInfo> nonExecuted = null;
        CallbackGraph callbackGraph = this.c.getAnalysisLatticeElement()
                .getCallbackGraph();
        if (chain != null)
            nonExecuted = callbackGraph.getDiffPromises(chain);
        callbackGraph.setCurrPromiseChain(chain);
        callbackGraph.preBuild();
        callbackGraph.commitScheduledPlans();
        Set<CallbackGraph.CallbackGraphNode> calls = null;
        if (chain != null)
            calls = callbackGraph.genCallbackGraphNodes(
                    chain.getAllElements());
        if (nonExecuted != null) {
            Set<CallbackGraph.CallbackGraphNode> nonExecutedCalls = callbackGraph
                    .genCallbackGraphNodes(nonExecuted.getAllElements());
            callbackGraph.toCallbackGraph(nonExecuted);
            callbackGraph.resetAnalyzed(nonExecutedCalls);
            calls.removeAll(nonExecutedCalls);
        }
        if (calls != null)
            callbackGraph.markAnalyzed(calls);
        if (nonExecuted == null) {
            Set<ObjectLabel> objectLabels = newSet();
            objectLabels.add(InitialStateBuilder.SET_TIMEOUT_QUEUE_OBJ);
            objectLabels.add(InitialStateBuilder.ASYNC_IO);
            Chain<CallbackCallInfo> c = Chain.
                    toChain(this.getNextCallbacksToRun(objectLabels));
            if (c != null) {
                Chain<CallbackCallInfo> n = callbackGraph
                        .getDiffTimers(c);
                if (n != null)
                    callbackGraph.toCallbackGraph(n);
            }
            callbackGraph.setCurrTimeIOChain(c);
            callbackGraph.commitScheduledPlans();
        }
        callbackGraph.resetStates();
        if (!Options.get().isCallbackSensitivityDisabled())
            return callbackGraph.getFirstCalls();
        return callbackGraph.getAllCallbacks();
    }

    /**
     * Removes objects that are equal to the default object.
     */
    public void removeObjectsEqualToDefault(boolean default_none_at_entry) {
        for (Iterator<Map.Entry<ObjectLabel, Obj>> it = store.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<ObjectLabel, Obj> me = it.next();
            if (me.getValue().equals(store_default)) {
                if (log.isDebugEnabled())
                    log.debug("removing object equal to the default: " + me.getKey());
                it.remove();
            } else if (default_none_at_entry && store_default.isUnknown() && me.getValue().isSomeNone()) {
                if (log.isDebugEnabled())
                    log.debug("removing none object: " + me.getKey());
                it.remove();
            }
        }
    }

    /**
     * Returns the summarized sets.
     */
    public Summarized getSummarized() {
        return summarized;
    }

    /**
     * Sets the current store contents as the basis store.
     * After this, objects in the basis store should never be summarized.
     * Ignored if lazy propagation is enabled.
     */
    public void freezeBasisStore() {
        if (Options.get().isLazyDisabled()) {
            basis_store = store;
            store = newMap();
            writable_store = true;
            log.debug("freezeBasisStore()");
        }
    }

    /**
     * Makes store writable (for copy-on-write).
     */
    public void makeWritableStore() {
        if (writable_store)
            return;
        store = newMap(store);
        writable_store = true;
        number_of_makewritable_store++;
    }

    /**
     * Makes execution_context writable (for copy-on-write).
     */
    private void makeWritableExecutionContext() {
        if (writable_execution_context)
            return;
        execution_context = execution_context.clone();
        writable_execution_context = true;
    }

    /**
     * Makes registers writable (for copy-on-write).
     */
    private void makeWritableRegisters() {
        if (writable_registers)
            return;
        registers = newList(registers);
        writable_registers = true;
    }

    /**
     * Returns the object labels that appear on the stack.
     * Not used if lazy propagation is enabled
     */
    public Set<ObjectLabel> getStackedObjects() {
        return stacked_objlabels;
    }

    /**
     * Sets the object labels that appear on the stack.
     */
    public void setStackedObjects(Set<ObjectLabel> so) {
        stacked_objlabels = so;
        writable_stacked_objlabels = true;
    }

    /**
     * Makes stacked object set writable (for copy-on-write).
     */
    private void makeWritableStackedObjects() {
        if (!Options.get().isLazyDisabled())
            return;
        if (writable_stacked_objlabels)
            return;
        stacked_objlabels = newSet(stacked_objlabels);
        writable_stacked_objlabels = true;
    }

    /**
     * Returns the total number of State objects created.
     */
    public static int getNumberOfStatesCreated() {
        return number_of_states_created;
    }

    /**
     * Resets the global counters.
     */
    public static void reset() {
        number_of_states_created = 0;
        number_of_makewritable_store = 0;
    }

    /**
     * Returns the total number of makeWritableStore operations.
     */
    public static int getNumberOfMakeWritableStoreCalls() {
        return number_of_makewritable_store;
    }

    /**
     * Clears modified flags for all values in the store.
     * Ignores the basis store.
     */
    private void clearModified() {
        Map<ObjectLabel, Obj> oldStore = store;
        store = newMap();
        for (Map.Entry<ObjectLabel, Obj> xs : oldStore.entrySet()) {
            Obj obj = xs.getValue();
            if (obj.isSomeModified()) {
                obj = new Obj(obj);
                obj.clearModified();
            }
            writeToStore(xs.getKey(), obj);
        }
        writable_store = true;
        number_of_makewritable_store++;
        log.debug("clearModified()");
    }

    /**
     * Sets this state to the bottom abstract state.
     * Used for representing 'no flow'.
     */
    public void setToBottom() {
        basis_store = null;
        summarized.clear();
        extras.setToBottom();
        store = newMap();
        queue = new LinkedHashMap<>();
        queueChain = null;
        scheduledCallbacks = null;
        writable_store = true;
        registers = new ArrayList<>();
        writable_registers = true;
        stacked_objlabels = newSet();
        writable_stacked_objlabels = true;
        execution_context = new ExecutionContext();
        writable_execution_context = true;
        store_default = Obj.makeNone();
    }

    @Override
    public boolean isBottom() {
        return execution_context.isEmpty();
    }

    /**
     * Propagates the given state into this state.
     * Replaces 'unknown' and polymorphic values when necessary.
     * Assumes that the states belong to the same block and context.
     *
     * @return true if an object changed (note there may be other changes due to recoveries)
     */
    @Override
    public boolean propagate(State s, boolean funentry) {
        if (Options.get().isDebugOrTestEnabled() && !store_default.isAllNone() && !s.store_default.isAllNone() && !store_default.equals(s.store_default))
            throw new AnalysisException("Expected store default objects to be equal");
        if (log.isDebugEnabled() && Options.get().isIntermediateStatesEnabled()) {
            log.debug("join this state: " + this);
            log.debug("join other state: " + s);
        }
        if (s.isBottom()) {
            if (log.isDebugEnabled())
                log.debug("propagate(...) - other is bottom");
            return false;
        }
        if (isBottom()) {
            setToState(s);
            if (log.isDebugEnabled())
                log.debug("propagate(...) - this is bottom, other is non-bottom");
            return true; // s is not none
        }
        makeWritableStore();
        makeWritableExecutionContext();
        makeWritableRegisters();
        makeWritableStackedObjects();
        boolean changed = execution_context.add(s.execution_context);
        Set<ObjectLabel> labs = newSet();
        labs.addAll(store.keySet());
        labs.addAll(s.store.keySet());
        for (ObjectLabel lab : labs)
            changed |= propagateObj(lab, s, lab, false);
        if (Options.get().isLazyDisabled())
            changed |= stacked_objlabels.addAll(s.stacked_objlabels);
        changed |= extras.propagate(s.extras);
        boolean queueChanged = propagateQueue(s.queue);
        if (!funentry)
            // TODO revisit
            changed |= queueChanged;
        Chain<QueueContext> newChain = Chain.join(
                this.queueChain, s.queueChain, true);
        changed |= newChain != null && !newChain.equals(this.queueChain);
        this.queueChain = newChain;

        Chain<CallbackDescription> newScheduledCallbacks = Chain.join(
                this.scheduledCallbacks, s.scheduledCallbacks, false);
        changed |= newScheduledCallbacks != null && !newScheduledCallbacks.equals(
                this.scheduledCallbacks);
        this.scheduledCallbacks = newScheduledCallbacks;
        if (!funentry) {
            for (int i = 0; i < registers.size() || i < s.registers.size(); i++) {
                Value v1 = i < registers.size() ? registers.get(i) : null;
                Value v2 = i < s.registers.size() ? s.registers.get(i) : null;
                Value v;
                if (v1 == null)
                    v = v2;
                else if (v2 == null)
                    v = v1;
                else
                    v = UnknownValueResolver.join(v1, this, v2, s);
                if (i < registers.size())
                    registers.set(i, v);
                else
                    registers.add(v);
                if (v != null && !v.equals(v1)) {
                    changed = true;
                }
            }
            changed |= summarized.join(s.summarized);
        }
        if (store_default.isAllNone() && !s.store_default.isAllNone()) {
            for (ObjectLabel lab : s.store.keySet()) { // materialize before changing default
                if (!store.containsKey(lab)) {
                    writeToStore(lab, store_default);
                }
            }
            store_default = s.store_default;
            store_default.freeze();
            changed = true;
        }
        if (log.isDebugEnabled()) {
            if (Options.get().isIntermediateStatesEnabled())
                log.debug("propagate result state: " + this);
            else
                log.debug("propagate(...)");
        }
        return changed;
    }

    private boolean propagateQueue(Map<ObjectLabel, Set<QueueObject>> queueFrom) {
        boolean changed = false;
        Map<ObjectLabel, Set<QueueObject>> newQueue = new LinkedHashMap<>();
        for (Map.Entry<ObjectLabel, Set<QueueObject>> qs: queueFrom.entrySet()) {
            Set<QueueObject> queueObjects = this.queue.get(qs.getKey());
            newQueue.put(qs.getKey(), newSet());
            if (queueObjects == null) {
                changed = true;
                newQueue.put(qs.getKey(), qs.getValue());
                continue;
            }
            newQueue.get(qs.getKey()).addAll(
                    QueueObject.join(queueObjects, qs.getValue(),
                                     QueueObject.Join.DEFAULT,
                                     false,
                                     QUEUE_OBJECT_KINDS.getOrDefault(
                                             qs.getKey(), QueueObject.Kind.PROMISE)));
            for (QueueObject qObj: qs.getValue()) {
                if (!qObj.isIncluded(queueObjects)) {
                    changed = true;
                }
            }
        }
        for (Map.Entry<ObjectLabel, Set<QueueObject>> qs: this.queue.entrySet()) {
            Set<QueueObject> queueObjects = queueFrom.get(qs.getKey());
            if (queueObjects == null) {
                changed = true;
                newQueue.put(qs.getKey(), qs.getValue());
            }

        }
        if (changed)
            this.queue = newQueue;
        return changed;
    }

    /**
     * Propagates objlabel2 from state2 into objlabel1 in this state.
     * Replaces 'unknown' and polymorphic values when necessary.
     * Assumes that the states belong to the same block and context.
     *
     * @param modified if true, set modified flag on written values
     * @return true if the object changed (note there may be other changes due to recoveries)
     */
    public boolean propagateObj(ObjectLabel objlabel_to, State state_from, ObjectLabel objlabel_from, boolean modified) {
        Obj obj_from = state_from.getObject(objlabel_from, false);
        Obj obj_to = getObject(objlabel_to, false);
        if (obj_from == obj_to && !modified) {
            // identical objects, so nothing to do
            return false;
        }
        if (obj_from.isAllNone()) { // may be a call edge or function entry state where not all properties have been propagated, so don't use isSomeNone here
            // obj_from object is none, so nothing to do
            return false;
        }
        // join all properties from obj_from into obj_to
        boolean changed = false;
        Value default_array_property_to = obj_to.getDefaultArrayProperty();
        Value default_array_property_from = obj_from.getDefaultArrayProperty();
        Value default_array_property_to_original = default_array_property_to;
        if (modified || !default_array_property_to.isUnknown() || !default_array_property_from.isUnknown()) {
            if (default_array_property_to.isUnknown())
                default_array_property_to = UnknownValueResolver.getDefaultArrayProperty(objlabel_to, this);
            if (default_array_property_from.isUnknown())
                default_array_property_from = UnknownValueResolver.getDefaultArrayProperty(objlabel_from, state_from);
            default_array_property_to = default_array_property_to.join(default_array_property_from);
            if (modified)
                default_array_property_to = default_array_property_to.joinModified();
            if (default_array_property_to != default_array_property_to_original) {
                if (!obj_to.isWritable())
                    obj_to = getObject(objlabel_to, true);
                obj_to.setDefaultArrayProperty(default_array_property_to);
                changed = true;
            }
        }
        Value default_nonarray_property_to = obj_to.getDefaultNonArrayProperty();
        Value default_nonarray_property_from = obj_from.getDefaultNonArrayProperty();
        Value default_nonarray_property_to_original = default_nonarray_property_to;
        if (modified || !default_nonarray_property_to.isUnknown() || !default_nonarray_property_from.isUnknown()) {
            if (default_nonarray_property_to.isUnknown())
                default_nonarray_property_to = UnknownValueResolver.getDefaultNonArrayProperty(objlabel_to, this);
            if (default_nonarray_property_from.isUnknown())
                default_nonarray_property_from = UnknownValueResolver.getDefaultNonArrayProperty(objlabel_from, state_from);
            default_nonarray_property_to = default_nonarray_property_to.join(default_nonarray_property_from);
            if (modified)
                default_nonarray_property_to = default_nonarray_property_to.joinModified();
            if (default_nonarray_property_to != default_nonarray_property_to_original) {
                if (!obj_to.isWritable())
                    obj_to = getObject(objlabel_to, true);
                obj_to.setDefaultNonArrayProperty(default_nonarray_property_to);
                changed = true;
            }
        }
        obj_from = state_from.getObject(objlabel_from, false); // propagating defaults may have materialized properties, so get the latest version
        for (PKey propertyname : obj_from.getProperties().keySet()) {
            if (!obj_to.getProperties().containsKey(propertyname)) {
                Value v = propertyname.isArrayIndex() ? default_array_property_to_original : default_nonarray_property_to_original;
                if (!obj_to.isWritable())
                    obj_to = getObject(objlabel_to, true);
                obj_to.setProperty(propertyname, v); // materializing from default doesn't affect 'changed'
//                if (log.isDebugEnabled())
//                  log.debug("Materialized " + objlabel_to + "." + propertyname + " = " + v);
            }
        }
        for (PKey propertyname : newList(obj_to.getPropertyNames())) { // TODO: need newList (to avoid ConcurrentModificationException)?
            Value v_to = obj_to.getProperty(propertyname);
            Value v_from = obj_from.getProperty(propertyname);
            if (modified || !v_to.isUnknown() || !v_from.isUnknown()) {
                Value v_to_original = v_to;
                if (v_to.isUnknown())
                    v_to = UnknownValueResolver.getProperty(objlabel_to, propertyname, this, v_from.isPolymorphic());
                if (v_from.isUnknown())
                    v_from = UnknownValueResolver.getProperty(objlabel_from, propertyname, state_from, v_to.isPolymorphic());
                v_to = UnknownValueResolver.join(v_to, this, v_from, state_from);
                if (modified)
                    v_to = v_to.joinModified();
                if (v_to != v_to_original) {
                    if (!obj_to.isWritable())
                        obj_to = getObject(objlabel_to, true);
                    obj_to.setProperty(propertyname, v_to);
                    changed = true;
                }
            }
        }
        Value internal_prototype_to = obj_to.getInternalPrototype();
        Value internal_prototype_from = obj_from.getInternalPrototype();
        if (modified || !internal_prototype_to.isUnknown() || !internal_prototype_from.isUnknown()) {
            Value internal_prototype_to_original = internal_prototype_to;
            if (internal_prototype_to.isUnknown())
                internal_prototype_to = UnknownValueResolver.getInternalPrototype(objlabel_to, this, internal_prototype_from.isPolymorphic());
            if (internal_prototype_from.isUnknown())
                internal_prototype_from = UnknownValueResolver.getInternalPrototype(objlabel_from, state_from, internal_prototype_to.isPolymorphic());
            internal_prototype_to = UnknownValueResolver.join(internal_prototype_to, this, internal_prototype_from, state_from);
            if (modified)
                internal_prototype_to = internal_prototype_to.joinModified();
            if (internal_prototype_to != internal_prototype_to_original) {
                if (!obj_to.isWritable())
                    obj_to = getObject(objlabel_to, true);
                obj_to.setInternalPrototype(internal_prototype_to);
                changed = true;
            }
        }
        Value internal_value_to = obj_to.getInternalValue();
        Value internal_value_from = obj_from.getInternalValue();
        if (modified || !internal_value_to.isUnknown() || !internal_value_from.isUnknown()) {
            Value internal_value_to_original = internal_value_to;
            if (internal_value_to.isUnknown())
                internal_value_to = UnknownValueResolver.getInternalValue(objlabel_to, this, internal_value_from.isPolymorphic());
            if (internal_value_from.isUnknown())
                internal_value_from = UnknownValueResolver.getInternalValue(objlabel_from, state_from, internal_value_to.isPolymorphic());
            internal_value_to = UnknownValueResolver.join(internal_value_to, this, internal_value_from, state_from);
            if (modified)
                internal_value_to = internal_value_to.joinModified();
            if (internal_value_to != internal_value_to_original) {
                if (!obj_to.isWritable())
                    obj_to = getObject(objlabel_to, true);
                obj_to.setInternalValue(internal_value_to);
                changed = true;
            }
        }
        if (modified || !obj_to.isScopeChainUnknown() || !obj_from.isScopeChainUnknown()) {
            boolean scopechain_to_unknown = obj_to.isScopeChainUnknown();
            ScopeChain scope_chain_to = obj_to.isScopeChainUnknown() ? UnknownValueResolver.getScopeChain(objlabel_to, this) : obj_to.getScopeChain();
            ScopeChain scope_chain_from = obj_from.isScopeChainUnknown() ? UnknownValueResolver.getScopeChain(objlabel_from, state_from) : obj_from.getScopeChain();
            ScopeChain new_scope_chain = ScopeChain.add(scope_chain_to, scope_chain_from);
            if ((new_scope_chain != null && !new_scope_chain.equals(scope_chain_to)) || scopechain_to_unknown) {
                if (!obj_to.isWritable())
                    obj_to = getObject(objlabel_to, true);
                obj_to.setScopeChain(new_scope_chain);
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Collection of property names.
     */
    public static class Properties {

        /**
         * Property names that are maybe (including definitely) included.
         */
        private Collection<PKey> maybe;

        /**
         * Property names that are definitely included.
         */
        private Collection<PKey> definitely;

        /**
         * If true, all array properties are maybe included.
         */
        private boolean array;

        /**
         * If true, all non-array properties are maybe included.
         */
        private boolean nonarray;

        public Properties() {
            maybe = newSet();
            definitely = newSet();
            array = false;
            nonarray = false;
        }

        /**
         * Returns the property names that are maybe (including definitely) included.
         */
        public Collection<PKey> getMaybe() {
            return maybe;
        }

        /**
         * Returns the property names that are definitely included.
         */
        public Collection<PKey> getDefinitely() {
            return definitely;
        }

        /**
         * Returns true if all array properties are maybe included.
         */
        public boolean isArray() {
            return array;
        }

        /**
         * Returns true if all non-array properties are maybe included.
         */
        public boolean isNonArray() {
            return nonarray;
        }

        @Override
        public int hashCode() {
            return maybe.hashCode() * 7 + definitely.hashCode() * 31 + (array ? 3 : 17) + (nonarray ? 5 : 113);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Properties other = (Properties) obj;
            if (array != other.array)
                return false;
            if (nonarray != other.nonarray)
                return false;
            return definitely.equals(other.definitely) && maybe.equals(other.maybe);
        }

        /**
         * Returns true if the exact set of property names is known and finite.
         */
        public boolean isDefinite() {
            return !array && !nonarray && maybe.equals(definitely);
        }

        /**
         * Returns a value that represents the least upper bound of the property names.
         */
        public Value toValue() {
            Collection<Value> vs = new ArrayList<>();
            if (array)
                vs.add(Value.makeAnyStrUInt());
            if (nonarray)
                vs.add(Value.makeAnyStrNotUInt());
            for (PKey k : maybe) // maybe includes definitely
                vs.add(k.toValue());
            return Value.join(vs);
        }

        /**
         * Returns a collection of values that represents the property names.
         */
        public Collection<Value> toValues() {
            Collection<Value> vs = maybe.stream().map(PKey::toValue).collect(Collectors.toList());
            if (array) {
                vs.add(Value.makeAnyStrUInt());
                vs.removeIf(prop -> prop.isMaybeSingleStr() && Strings.isArrayIndex(prop.getStr()));
            }
            if (nonarray) {
                vs.add(Value.makeAnyStrNotUInt());
                vs.removeIf(prop -> prop.isMaybeSingleStr() && !Strings.isArrayIndex(prop.getStr()));
            }
            return vs;
        }
    }

    /**
     * Returns a description of the names of the [enumerable] properties of the given objects [and their prototypes].
     */
    public Properties getProperties(Collection<ObjectLabel> objlabels, boolean onlyEnumerable, boolean onlySymbols, boolean includeSymbols, boolean usePrototypes) {
        Map<ObjectLabel, Set<ObjectLabel>> inverse_proto = newMap();
        Set<ObjectLabel> roots = newSet();
        if (usePrototypes) {
            // find relevant objects, prepare inverse_proto
            LinkedList<ObjectLabel> worklist = new LinkedList<>(objlabels);
            Set<ObjectLabel> visited = newSet(objlabels);
            while (!worklist.isEmpty()) {
                ObjectLabel ol = worklist.removeFirst();
                if (!inverse_proto.containsKey(ol))
                    inverse_proto.put(ol, dk.brics.tajs.util.Collections.newSet());
                Value proto = UnknownValueResolver.getInternalPrototype(ol, this, false);
                if (proto.isMaybeNull())
                    roots.add(ol);
                for (ObjectLabel p : proto.getObjectLabels()) {
                    addToMapSet(inverse_proto, p, ol);
                    if (!visited.contains(p)) {
                        worklist.add(p);
                        visited.add(p);
                    }
                }
            }
        } else {
            roots.addAll(objlabels);
        }
        // find properties info with fixpoint computation starting from the roots
        Map<ObjectLabel, Properties> props = newMap();
        Set<ObjectLabel> workset = newSet(roots);
        while (!workset.isEmpty()) {
            ObjectLabel ol = workset.iterator().next();
            workset.remove(ol);
            if (usePrototypes) {
                // inherit from prototypes
                Value proto = UnknownValueResolver.getInternalPrototype(ol, this, false);
                Properties p = mergeProperties(proto.getObjectLabels(), props);
                // overwrite with properties in the current object
                addOwnProperties(ol, onlyEnumerable, onlySymbols, includeSymbols, p);
                Properties oldp = props.get(ol);
                if (oldp == null || !oldp.equals(p)) {
                    props.put(ol, p);
                    workset.addAll(inverse_proto.get(ol));
                }
            } else {
                Properties p = new Properties();
                addOwnProperties(ol, onlyEnumerable, onlySymbols, includeSymbols, p);
                props.put(ol, p);
            }
        }
        return mergeProperties(objlabels, props);
    }

    /**
     * @see #getProperties(Collection, boolean, boolean, boolean, boolean)
     */
    private void addOwnProperties(ObjectLabel ol, boolean onlyEnumerable, boolean onlySymbols, boolean includeSymbols, Properties p) {
        Predicate<Value> isEligible = v -> v.isMaybePresent() && (!onlyEnumerable || v.isMaybeNotDontEnum());
        if (isEligible.test(UnknownValueResolver.getDefaultArrayProperty(ol, this))) {
            p.array = true;
        }
        if (isEligible.test(UnknownValueResolver.getDefaultNonArrayProperty(ol, this))) {
            p.nonarray = true;
        }
        for (Map.Entry<PKey, Value> me : UnknownValueResolver.getProperties(ol, this).entrySet()) {
            PKey propertyname = me.getKey();
            if (StringPKey.__PROTO__.equals(propertyname)) { // magic property that is invisible for reflection
                continue;
            }
            // Symbols are not enumerable, nor in the ownProperties
            if ((!includeSymbols && propertyname instanceof SymbolPKey) ||
                    (onlySymbols && !(propertyname instanceof SymbolPKey))) {
                continue;
            }
            Value v = UnknownValueResolver.getProperty(ol, propertyname, this, true);
            if (isEligible.test(v)) {
                p.maybe.add(propertyname);
                if ((!onlyEnumerable || !v.isMaybeDontEnum()) && !v.isMaybeAbsent())
                    p.definitely.add(propertyname);
            }
        }
        if (ol.getKind() == ObjectLabel.Kind.STRING) {
            // String objects have index-properties
            Value internalValue = UnknownValueResolver.getRealValue(readInternalValue(singleton(ol)), this);
            if (internalValue.isMaybeSingleStr()) {
                for (int i = 0; i < internalValue.getStr().length(); i++) {
                    p.maybe.add(StringPKey.make(Integer.toString(i)));
                    p.definitely.add(StringPKey.make(Integer.toString(i)));
                }
            }
            if (internalValue.isMaybeStrPrefix()) {
                for (int i = 0; i < internalValue.getPrefix().length(); i++) {
                    p.maybe.add(StringPKey.make(Integer.toString(i)));
                    p.definitely.add(StringPKey.make(Integer.toString(i)));
                }
            }
            if (internalValue.isMaybeFuzzyStr()) {
                p.array = true;
            }
        }
    }

    private static Properties mergeProperties(Collection<ObjectLabel> objlabels, Map<ObjectLabel, Properties> props) {
        Properties res = new Properties();
        boolean first = true;
        for (ObjectLabel objlabel : objlabels) {
            Properties p = props.get(objlabel);
            if (p != null) {
                if (first) {
                    res.maybe.addAll(p.maybe);
                    res.definitely.addAll(p.definitely);
                    res.array = p.array;
                    res.nonarray = p.nonarray;
                    first = false;
                } else {
                    res.maybe.addAll(p.maybe);
                    res.definitely.retainAll(p.definitely);
                    res.array |= p.array;
                    res.nonarray |= p.nonarray;
                }
            }
        }
        return res;
    }

    /**
     * Returns the set of objects in the prototype chain that contain the property.
     */
    public Set<ObjectLabel> getPrototypeWithProperty(ObjectLabel objlabel, PKeys propertyName) { // TODO: review
        if (Options.get().isDebugOrTestEnabled() && propertyName.isMaybeOtherThanStr()) {
            throw new AnalysisException("Uncoerced property name: " + propertyName);
        }
        Set<ObjectLabel> ol = Collections.singleton(objlabel);
        Set<ObjectLabel> visited = newSet();
        Set<ObjectLabel> res = newSet();
        while (!ol.isEmpty()) {
            Set<ObjectLabel> ol2 = newSet();
            for (ObjectLabel l : ol)
                if (!visited.contains(l)) {
                    visited.add(l);

                    Collection<Value> values = newList();
                    if (propertyName.isMaybeFuzzyStrOrSymbol()) {
                        if (propertyName.isMaybeStrSomeNonUInt()) {
                            values.add(UnknownValueResolver.getDefaultNonArrayProperty(l, this));
                        }
                        if (propertyName.isMaybeStrSomeUInt()) {
                            values.add(UnknownValueResolver.getDefaultNonArrayProperty(l, this));
                        }
                        // relevant properties have been materialized now
                        values.addAll(getObject(l, false).getProperties().keySet().stream()
                                .filter(k -> k instanceof StringPKey && propertyName.isMaybeStr(((StringPKey)k).getStr())) // FIXME: doesn't support Symbols?
                                .map(n -> UnknownValueResolver.getProperty(l, n, this, true))
                                .collect(Collectors.toList()));
                    } else { // FIXME: doesn't support Symbols?
                        values.add(UnknownValueResolver.getProperty(l, StringPKey.make(propertyName.getStr()), this, true));
                    }

                    boolean definitelyAbsent = values.stream().allMatch(Value::isNotPresent);
                    boolean maybeAbsent = values.stream().anyMatch(Value::isMaybeAbsent);

                    if (definitelyAbsent) {
                        Value proto = UnknownValueResolver.getInternalPrototype(l, this, false);
                        ol2.addAll(proto.getObjectLabels());
                    } else if (maybeAbsent) {
                        Value proto = UnknownValueResolver.getInternalPrototype(l, this, false);
                        ol2.addAll(proto.getObjectLabels());
                        res.add(l);
                    } else {
                        res.add(l);
                    }
                }
            ol = ol2;
        }
        return res;
    }

    // TODO: replace with getPrototypeWithProperty, but check messages!
    public Set<ObjectLabel> getPrototypesUsedForUnknown(ObjectLabel objlabel) { // TODO: review (used only in Monitoring)
        State state = c.getState();
        Set<ObjectLabel> ol = Collections.singleton(objlabel);
        Set<ObjectLabel> visited = newSet();
        Set<ObjectLabel> res = newSet();
        while (!ol.isEmpty()) {
            Set<ObjectLabel> ol2 = newSet();
            for (ObjectLabel l : ol)
                if (!visited.contains(l)) {
                    visited.add(l);
                    Value v = UnknownValueResolver.getDefaultArrayProperty(objlabel, state);
                    if (v.isMaybeAbsent()) {
                        Value proto = UnknownValueResolver.getInternalPrototype(l, state, false);
                        ol2.addAll(proto.getObjectLabels());
                        res.add(l);
                    }
                }
            ol = ol2;
        }
        return res;
    }

    /**
     * Materializes a singleton object from the given summary object.
     * @param definitely_only_one set to true if the object has been created only once since function entry
     * @return object label of the materialized singleton
     */
    public ObjectLabel materializeObj(ObjectLabel summary, boolean definitely_only_one) {
        if (basis_store != null && basis_store.containsKey(summary))
            throw new AnalysisException("Attempt to summarize object from basis store");
        if (summary.isSingleton())
            throw new AnalysisException("Expected summary object");
        if (Options.get().isRecencyDisabled())
            throw new AnalysisException("Can't materialize when recency is disabled");
        makeWritableStore();
        ObjectLabel singleton = summary.makeSingleton();
        Obj oldSummaryObj = getObject(summary, true);
        summarizeObj(singleton, summary, new Obj(oldSummaryObj));
        summarized.removeSummarized(singleton, definitely_only_one);
        if (log.isDebugEnabled())
            log.debug("materializeObj(" + summary + ")");
        return singleton;
    }

    public void replaceQueueObjectFromScheduledCallbacks(
            ObjectLabel singleton, ObjectLabel summary) {
        if (this.scheduledCallbacks == null)
            return;
        Chain<CallbackDescription> scheduledCallbacks = this
                .scheduledCallbacks;
        while (scheduledCallbacks != null) {
            Set<CallbackDescription> descs = scheduledCallbacks
                    .getTop();
            for (CallbackDescription callbackDesc : descs)
                callbackDesc.replaceObjectLabel(singleton, summary);
            scheduledCallbacks = scheduledCallbacks.getNext();
        }
    }

    private void replaceQueueChain(ObjectLabel singleton,
                                   ObjectLabel summary) {
        Chain<QueueContext> queueContexts = this.queueChain;
        while (queueContexts != null) {
            Set<QueueContext> top = queueContexts
                    .getTop();
            for (QueueContext queueContext : top)
                queueContext.replaceObjectLabel(singleton, summary);
            queueContexts = queueContexts.getNext();
        }
    }

    public void removeQueueObject(ObjectLabel singleton, ObjectLabel summary,
                                  boolean flag) {
        if (!this.queue.containsKey(singleton))
            return;
        this.queue.remove(singleton);
        for (Map.Entry<ObjectLabel, Set<QueueObject>> qs : this.queue.entrySet()) {
            for (QueueObject qObj : qs.getValue()) {
                qObj.replaceObjectLabel(singleton, summary);
            }
        }
        if (!flag)
            return;
        replaceQueueObjectFromScheduledCallbacks(singleton, summary);
        //CallbackGraph callbackGraph = this.c
        //        .getAnalysisLatticeElement().getCallbackGraph();
        //if (callbackGraph != null && !c.isScanning())
        //    callbackGraph.replaceCallbackGraph(singleton, summary);
        //if (this.callbackContext != null)
        //    callbackContext.replaceObjectLabels(singleton, summary);
        //if (queueChain != null)
        //    replaceQueueChain(singleton, summary);
    }

    public void summarizeQueue(ObjectLabel singleton, ObjectLabel summary) {
        Set<QueueObject> queueObjects = this.queue.get(singleton);
        if (queueObjects != null
                && this.queue.containsKey(singleton)) {
            Set<QueueObject> summaryQueueObjs = this.queue.get(summary);
            Set<QueueObject> joint = QueueObject.join(
                    queueObjects,
                    summaryQueueObjs == null ? newSet() : summaryQueueObjs,
                    QueueObject.Join.DEFAULT, false,
                    QueueObject.Kind.PROMISE);
            this.queue.put(summary, joint);
        }

    }

    /**
     * Adds an object label, representing a new empty object, to the store.
     * Takes recency abstraction into account.
     * Updates sets of summarized objects.
     */
    public void newObject(ObjectLabel objlabel) {
        if (basis_store != null && basis_store.containsKey(objlabel))
            throw new AnalysisException("Attempt to summarize object from basis store");
        makeWritableStore();
        c.getMonitoring().visitNewObject(c.getNode(), objlabel, this);
        if (!Options.get().isRecencyDisabled()) {
            if (!objlabel.isSingleton())
                throw new AnalysisException("Expected singleton object label");
            summarizeObj(objlabel, objlabel.makeSummary(), Obj.makeAbsentModified());
        } else {
            // join the empty object into oldobj (only relevant if recency abstraction is disabled)
            Obj obj = getObject(objlabel, true);
            Value old_array = UnknownValueResolver.getDefaultArrayProperty(objlabel, this);
            Value old_nonarray = UnknownValueResolver.getDefaultNonArrayProperty(objlabel, this);
            obj.setDefaultArrayProperty(old_array.joinAbsentModified());
            obj.setDefaultNonArrayProperty(old_nonarray.joinAbsentModified());
            for (Map.Entry<PKey, Value> me : newSet(UnknownValueResolver.getProperties(objlabel, this).entrySet())) {
                PKey propertyname = me.getKey();
                Value v = me.getValue();
                if (v.isUnknown())
                    v = UnknownValueResolver.getProperty(objlabel, propertyname, this, true);
                obj.setProperty(propertyname, v.joinAbsentModified());
            }
            obj.setInternalPrototype(UnknownValueResolver.getInternalPrototype(objlabel, this, true).joinAbsentModified());
            obj.setInternalValue(UnknownValueResolver.getInternalValue(objlabel, this, true).joinAbsentModified());
        }
        if (log.isDebugEnabled())
            log.debug("newObject(" + objlabel + ")");
    }

    private void summarizeObj(ObjectLabel singleton, ObjectLabel summary, Obj newObj) {
        Obj oldobj = getObject(singleton, false);
        if (!oldobj.isSomeNone()) {
            this.summarizeQueue(singleton, summary);
            this.removeQueueObject(singleton, summary, true);
            // join singleton object into its summary object
            // FIXME Support c.getMonitoring().visitRenameObject(c.getNode(), singleton, summary, this); (GitHub #413)
            propagateObj(summary, this, singleton, true);
            // update references
            Map<ScopeChain, ScopeChain> cache = new HashMap<>();
            for (ObjectLabel objlabel2 : newList(store.keySet())) {
                if (getObject(objlabel2, false).containsObjectLabel(singleton)) {
                    Obj obj = getObject(objlabel2, true);
                    obj.replaceObjectLabel(singleton, summary, cache);
                }
            }
            makeWritableExecutionContext();
            execution_context.replaceObjectLabel(singleton, summary, cache);
            makeWritableRegisters();
            for (int i = 0; i < registers.size(); i++) {
                Value v = registers.get(i);
                if (v != null) {
                    registers.set(i, v.replaceObjectLabel(singleton, summary));
                }
            }
            extras.replaceObjectLabel(singleton, summary);
            if (Options.get().isLazyDisabled())
                if (stacked_objlabels.contains(singleton)) {
                    makeWritableStackedObjects();
                    stacked_objlabels.remove(singleton);
                    stacked_objlabels.add(summary);
                }
            if (getObject(summary, false).isUnknown() && store_default.isUnknown())
                store.remove(summary);
        }
        // now the old object is gone
        summarized.addDefinitelySummarized(singleton);
        makeWritableStore();
        writeToStore(singleton, newObj);
    }

    /**
     * Summarizes the given objects.
     * Moves the given objects from singleton to summary, such that each represents
     * an unknown number of concrete objects.
     */
    public void summarize(Set<ObjectLabel> objs) {
        for (ObjectLabel objlabel : objs) {
            if (store.containsKey(objlabel)) {
                multiplyObject(objlabel);
            }
        }
    }

    /**
     * Moves the given object from singleton to summary, such that it represents
     * an unknown number of concrete objects.
     */
    public void multiplyObject(ObjectLabel objlabel) {
        if (!store.containsKey(objlabel))
            throw new AnalysisException("Object " + objlabel + " not found!?");
        makeWritableStore();
        if (objlabel.isSingleton()) { // TODO merge this implementation with the one in #newObject?
            // move the object
            ObjectLabel summarylabel = objlabel.makeSummary();
            c.getMonitoring().visitRenameObject(c.getNode(), objlabel, summarylabel, this);
            propagateObj(summarylabel, this, objlabel, true);
            store.remove(objlabel);
            // update references
            Map<ScopeChain, ScopeChain> cache = new HashMap<>();
            for (ObjectLabel objlabel2 : newList(store.keySet())) {
                if (getObject(objlabel2, false).containsObjectLabel(objlabel)) {
                    Obj obj = getObject(objlabel2, true);
                    obj.replaceObjectLabel(objlabel, summarylabel, cache);
                }
            }
            makeWritableExecutionContext();
            execution_context.replaceObjectLabel(objlabel, summarylabel, cache);
            makeWritableRegisters();
            for (int i = 0; i < registers.size(); i++) {
                Value v = registers.get(i);
                if (v != null) {
                    registers.set(i, v.replaceObjectLabel(objlabel, summarylabel));
                }
            }
            extras.replaceObjectLabel(objlabel, summarylabel);
            if (Options.get().isLazyDisabled())
                if (stacked_objlabels.contains(objlabel)) {
                    makeWritableStackedObjects();
                    stacked_objlabels.remove(objlabel);
                    stacked_objlabels.add(summarylabel);
                }
            if (log.isDebugEnabled())
                log.debug("multiplyObject(" + objlabel + ")");
        }
    }

    /**
     * Reads a variable directly from the current variable object, without considering the full scope chain.
     * (Only to be used for testing.)
     */
    public Value readVariableDirect(String var) {
        Collection<Value> values = newList();
        for (ObjectLabel objlabel : execution_context.getVariableObject()) {
            values.add(readProperty(ObjectProperty.makeOrdinary(objlabel, StringPKey.make(var)), false));
        }
        return UnknownValueResolver.join(values, this);
    }

    /**
     * Reads the designated property value.
     */
    public Value readProperty(ObjectProperty p, boolean partial) {
        ObjectLabel objlabel = p.getObjectLabel();
        switch (p.getKind()) {
            case ORDINARY:
                return UnknownValueResolver.getProperty(objlabel, p.getPropertyName(), this, partial);
            case DEFAULT_ARRAY:
                return UnknownValueResolver.getDefaultArrayProperty(objlabel, this);
            case DEFAULT_NONARRAY:
                return UnknownValueResolver.getDefaultNonArrayProperty(objlabel, this);
            case INTERNAL_PROTOTYPE:
                return UnknownValueResolver.getInternalPrototype(objlabel, this, partial);
            case INTERNAL_VALUE:
                return UnknownValueResolver.getInternalValue(objlabel, this, partial);
            default:
                throw new AnalysisException("Unexpected property reference");
        }
    }

    /**
     * Writes the designated property value.
     */
    public void writeProperty(ObjectProperty p, Value v) {
        Obj obj = getObject(p.getObjectLabel(), true);
        switch (p.getKind()) {
            case ORDINARY:
                obj.setProperty(p.getPropertyName(), v);
                break;
            case DEFAULT_ARRAY:
                obj.setDefaultArrayProperty(v);
                break;
            case DEFAULT_NONARRAY:
                obj.setDefaultNonArrayProperty(v);
                break;
            case INTERNAL_PROTOTYPE:
                obj.setInternalPrototype(v);
                break;
            case INTERNAL_VALUE:
                obj.setInternalValue(v);
                break;
            default:
                throw new AnalysisException("Unexpected property reference");
        }
    }

    /**
     * Assigns the given value to the internal prototype links of the given objects.
     * Modified is set on all values being written.
     */
    public void writeInternalPrototype(Collection<ObjectLabel> objlabels, Value value) {
        value.assertNonEmpty();
        for (ObjectLabel objlabel : objlabels) {
            Value newval;
            if (objlabels.size() == 1 && objlabel.isSingleton()) // strong update
                newval = value;
            else { // weak update
                Value oldval = UnknownValueResolver.getInternalPrototype(objlabel, this, true);
                newval = UnknownValueResolver.join(oldval, value, this);
            }
            newval = newval.joinModified();
            Obj obj = getObject(objlabel, true);
            // FIXME only null or object values are actually written! (see JSObject -> OBJECT_SETPROTOTYPEOF for example) (GitHub #356)
            // FIXME Property.__PROTO__ should be assigned `absent` when `newval.isMaybeNull` (GitHub #356)
            obj.setProperty(StringPKey.__PROTO__, newval.setAttributes(true, true, false));
            obj.setInternalPrototype(newval);
        }
        if (log.isDebugEnabled())
            log.debug("writeInternalPrototype(" + objlabels + "," + value + ")");
    }

    /**
     * Assigns the given value to the internal prototype link of the given object.
     * Modified is set on all values being written.
     */
    public void writeInternalPrototype(ObjectLabel objlabel, Value value) {
        writeInternalPrototype(Collections.singleton(objlabel), value);
    }

    /**
     * Assign the given value to the internal [[Value]] property of the given objects.
     * Modified is set on all values being written.
     */
    public void writeInternalValue(Collection<ObjectLabel> objlabels, Value value) {
        value.assertNonEmpty();
        for (ObjectLabel objlabel : objlabels)
            if (objlabels.size() == 1 && objlabel.isSingleton()) // strong update
                getObject(objlabel, true).setInternalValue(value.joinModified());
            else { // weak update
                Value oldval = UnknownValueResolver.getInternalValue(objlabel, this, true);
                Value newval = UnknownValueResolver.join(oldval, value, this);
                getObject(objlabel, true).setInternalValue(newval.joinModified());
            }
        if (log.isDebugEnabled())
            log.debug("writeInternalValue(" + objlabels + "," + value + ")");
    }

    /**
     * Assigns the given value to the internal [[Value]] property of the given object.
     * Modified is set on all values being written.
     */
    public void writeInternalValue(ObjectLabel objlabel, Value value) {
        writeInternalValue(Collections.singleton(objlabel), value);
    }

    /**
     * Returns the value of the internal value property of the given objects.
     */
    public Value readInternalValue(Collection<ObjectLabel> objlabels) {
        Collection<Value> values = newList();
        for (ObjectLabel obj : objlabels)
            values.add(UnknownValueResolver.getInternalValue(obj, this, true));
        Value v = UnknownValueResolver.join(values, this);
        if (log.isDebugEnabled())
            log.debug("readInternalValue(" + objlabels + ") = " + v);
        return v;
    }

    /**
     * Returns the value of the internal prototype of the given objects.
     */
    public Value readInternalPrototype(Collection<ObjectLabel> objlabels) {
        Collection<Value> values = newList();
        for (ObjectLabel obj : objlabels)
            values.add(UnknownValueResolver.getInternalPrototype(obj, this, true));
        Value v = UnknownValueResolver.join(values, this);
        if (log.isDebugEnabled())
            log.debug("readInternalPrototype(" + objlabels + ") = " + v);
        return v;
    }

    /**
     * Returns the value of the internal scope property of the given objects.
     *
     * @return unmodifiable set
     */
    public ScopeChain readObjectScope(ObjectLabel objlabel) {
        ScopeChain scope = UnknownValueResolver.getScopeChain(objlabel, this);
        if (log.isDebugEnabled())
            log.debug("readObjectScope(" + objlabel + ") = " + scope);
        return scope;
    }

    /**
     * Assigns a copy of the given scope chain to the internal scope property of the given object.
     */
    public void writeObjectScope(ObjectLabel objlabel, ScopeChain scope) {
        if (objlabel.getKind() == Kind.FUNCTION && !objlabel.isHostObject() && scope == null)
            throw new AnalysisException("Empty scope chain for function!?");
        getObject(objlabel, true).setScopeChain(scope);
        if (log.isDebugEnabled())
            log.debug("writeObjectScope(" + objlabel + "," + scope + ")");
    }

    /**
     * Returns the scope chain.
     *
     * @return new set
     */
    public ScopeChain getScopeChain() {
        ScopeChain scope = execution_context.getScopeChain();
        if (log.isDebugEnabled())
            log.debug("getScopeChain() = " + scope);
        return scope;
    }

    /**
     * Returns the execution context.
     */
    public ExecutionContext getExecutionContext() {
        return execution_context;
    }

    /**
     * Returns the queue chain.
     */
    public Chain<QueueContext> getQueueChain() {
        return this.queueChain;
    }

    public Chain<CallbackDescription> getScheduledCallbacks() {
        return this.scheduledCallbacks;
    }

    /**
     * Pushes a new item onto the scope chain.
     */
    public void pushScopeChain(Set<ObjectLabel> objlabels) {
        makeWritableExecutionContext();
        execution_context.pushScopeChain(objlabels);
    }

    /**
     * Pops the top item off the scope chain.
     */
    public void popScopeChain() {
        makeWritableExecutionContext();
        execution_context.popScopeChain();
    }

    /**
     * Clears the variable object pointer in the execution context.
     */
    public void clearVariableObject() {
        makeWritableExecutionContext();
        execution_context.setVariableObject(dk.brics.tajs.util.Collections.newSet());
    }

    /**
     * Sets the execution context.
     */
    public void setExecutionContext(ExecutionContext e) {
        execution_context = e;
        writable_execution_context = true;
    }

    public void setQueueChain(Chain<QueueContext> queueChain) {
        this.queueChain = queueChain;
    }

    public void setScheduledCallbacks(Chain<CallbackDescription> scheduledCallbacks) {
        this.scheduledCallbacks = scheduledCallbacks;
    }

    /**
     * Returns a string description of the differences between this state and the given one.
     */
    @Override
    public String diff(State old) {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<ObjectLabel, Obj> me : sortedEntries(store)) {
            Obj xo = old.getObject(me.getKey(), false);
            if (!me.getValue().equals(xo)) {
                b.append("\n      changed object ").append(me.getKey()).append(" at ").append(me.getKey().getSourceLocation()).append(": ");
                me.getValue().diff(xo, b);
            }
        }
        Set<ObjectLabel> temp = newSet(execution_context.getVariableObject());
        temp.removeAll(old.execution_context.getVariableObject());
        if (!temp.isEmpty())
            b.append("\n      new varobj: ").append(temp);
        if (!execution_context.getThis().equals(old.execution_context.getThis())) {
            b.append("\n      new this: ");
            execution_context.getThis().diff(old.execution_context.getThis(), b);
        }
        if (!ScopeChain.isEmpty(ScopeChain.remove(execution_context.getScopeChain(), old.execution_context.getScopeChain())))
            b.append("\n      new scope chain: ").append(ScopeChain.remove(execution_context.getScopeChain(), old.execution_context.getScopeChain()));
        temp = newSet(summarized.getMaybeSummarized());
        temp.removeAll(old.summarized.getMaybeSummarized());
        if (!temp.isEmpty())
            b.append("\n      new maybe-summarized: ").append(temp);
        temp = newSet(summarized.getDefinitelySummarized());
        temp.removeAll(old.summarized.getDefinitelySummarized());
        if (!temp.isEmpty())
            b.append("\n      new definitely-summarized: ").append(temp);
        temp = newSet(stacked_objlabels);
        temp.removeAll(old.stacked_objlabels);
        if (!temp.isEmpty())
            b.append("\n      new stacked object labels: ").append(temp);
        if (!registers.equals(old.registers))
            b.append("\n      registers changed");
        // TODO: implement diff for StateExtras?
        return b.toString();
    }

    /**
     * Returns a description of this abstract state.
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("Abstract state:");
        b.append("\n  Execution context: ").append(execution_context);
        b.append("\n  Summarized: ").append(summarized);
        b.append("\n  Store (excluding basis and default objects): ");
        for (Map.Entry<ObjectLabel, Obj> me : sortedEntries(store)) {
            b.append("\n    ").append(me.getKey()).append(" (").append(me.getKey().getSourceLocation()).append("): ").append(me.getValue()).append("");
        }
//        b.append("\n  Default object: ").append(store_default);
//        b.append("\n  Store default: ").append(store_default);
        b.append("\n  Registers: ");
        for (int i = 0; i < registers.size(); i++)
            if (registers.get(i) != null)
                b.append("\n    v").append(i).append("=").append(registers.get(i));
        b.append(extras);
        if (Options.get().isLazyDisabled())
            b.append("\n  Objects used by outer scopes: ").append(stacked_objlabels);
        return b.toString();
    }

    /**
     * Prints the objects of the given value.
     */
    public String printObject(Value v) {
        StringBuilder b = new StringBuilder();
        for (ObjectLabel obj : new TreeSet<>(v.getObjectLabels())) {
            if (b.length() > 0)
                b.append(", ");
            b.append(getObject(obj, false)); // TODO: .append(" at ").append(obj.getSourceLocation());
        }
        return b.toString();
    }

    /**
     * As {@link #toString()} but excludes registers and non-modified objects and properties.
     */
    @Override
    public String toStringBrief() {
        StringBuilder b = new StringBuilder("Abstract state:");
        b.append("\n  Execution context: ").append(execution_context);
        b.append("\n  Summarized: ").append(summarized);
        b.append("\n  Store (excluding non-modified): ");
        printModifiedStore(b);
        //b.append("\n  Default object: ").append(store_default);
        return b.toString();
    }

    /**
     * Prints the modified parts of the store.
     */
    public String toStringModified() {
        StringBuilder b = new StringBuilder();
        printModifiedStore(b);
        return b.toString();
    }

    /**
     * Prints the modified parts of the store.
     */
    private void printModifiedStore(StringBuilder b) {
        for (Map.Entry<ObjectLabel, Obj> me : sortedEntries(store))
            b.append("\n  ").append(me.getKey()).append(" (").append(me.getKey().getSourceLocation()).append("):").append(me.getValue().printModified());
    }

    @Override
    public String toDot() {
        StringBuilder ns = new StringBuilder("\n\t/* Nodes */\n");
        StringBuilder es = new StringBuilder("\n\t/* Edges */\n");
        // nodes
        TreeSet<ObjectLabel> objs = new TreeSet<>();
        for (Map.Entry<ObjectLabel, Obj> e : sortedEntries(store)) {
            ObjectLabel label = e.getKey();
            Obj obj = e.getValue();
            objs.add(label);
            objs.addAll(obj.getAllObjectLabels());
        }
        objs.addAll(execution_context.getObjectLabels());
        for (ObjectLabel label : objs) {
            StringBuilder s = new StringBuilder();
            String boxName = label.toString().replaceAll("<", "\\\\<").replaceAll(">", "\\\\>");
            s.append("\t").append(node(label)).append("[label=\"").append(boxName);
            int index = 0;
            Obj obj = store.get(label);
            if (obj != null) {
                for (Map.Entry<PKey, Value> ee : obj.getProperties().entrySet()) {
                    s.append("|").append("<f").append(index++).append("> ").append(ee.getKey()).append("=").append(esc(ee.getValue().restrictToNotObject().toString()));
                }
                if (!obj.getDefaultArrayProperty().isUnknown()) {
                    s.append("|").append("<f").append(index++).append("> [[DefaultArray]]=").append(esc(obj.getDefaultArrayProperty().restrictToNotObject().toString()));
                }
                if (!obj.getDefaultNonArrayProperty().isUnknown()) {
                    s.append("|").append("<f").append(index++).append("> [[DefaultNonArray]]=").append(esc(obj.getDefaultNonArrayProperty().restrictToNotObject().toString()));
                }
                if (!obj.getInternalPrototype().isUnknown()) {
                    s.append("|").append("<f").append(index++).append("> [[Prototype]]=").append(esc(obj.getInternalPrototype().restrictToNotObject().toString()));
                }
                if (!obj.getInternalValue().isUnknown()) {
                    s.append("|").append("<f").append(index++).append("> [[Value]]=").append(esc(obj.getInternalValue().restrictToNotObject().toString()));
                }
                if (!obj.isScopeChainUnknown()) {
                    s.append("|").append("<f").append(index).append("> [[Scope]]=");
                }
            }
            s.append("\"];\n");
            ns.append(s);
        }
        es.append("\tthis[label=this,shape=none];\n");
        es.append("\tvar[label=var,shape=none];\n");
        es.append("\tscope[label=scope,shape=none];\n");
        // edges
        for (Map.Entry<ObjectLabel, Obj> e : sortedEntries(store)) {
            ObjectLabel sourceLabel = e.getKey();
            Obj obj = e.getValue();
            int index = 0;
            for (Map.Entry<PKey, Value> ee : obj.getProperties().entrySet()) {
                Value value = ee.getValue();
                String source = node(sourceLabel) + ":f" + index;
                for (ObjectLabel targetLabel : value.getObjectLabels()) {
                    String target = node(targetLabel);
                    es.append("\t").append(source).append(" -> ").append(target).append(";\n");
                }
                index++;
            }
            if (!obj.getDefaultArrayProperty().isUnknown()) {
                String source = node(sourceLabel) + ":f" + index;
                for (ObjectLabel targetLabel : obj.getDefaultArrayProperty().getObjectLabels()) {
                    String target = node(targetLabel);
                    es.append("\t").append(source).append(" -> ").append(target).append(";\n");
                }
                index++;
            }
            if (!obj.getDefaultNonArrayProperty().isUnknown()) {
                String source = node(sourceLabel) + ":f" + index;
                for (ObjectLabel targetLabel : obj.getDefaultArrayProperty().getObjectLabels()) {
                    String target = node(targetLabel);
                    es.append("\t").append(source).append(" -> ").append(target).append(";\n");
                }
                index++;
            }
            if (!obj.getInternalPrototype().isUnknown()) {
                String source = node(sourceLabel) + ":f" + index;
                for (ObjectLabel targetLabel : obj.getInternalPrototype().getObjectLabels()) {
                    String target = node(targetLabel);
                    es.append("\t").append(source).append(" -> ").append(target).append(";\n");
                }
                index++;
            }
            if (!obj.getInternalValue().isUnknown()) {
                String source = node(sourceLabel) + ":f" + index;
                for (ObjectLabel targetLabel : obj.getInternalValue().getObjectLabels()) {
                    String target = node(targetLabel);
                    es.append("\t").append(source).append(" -> ").append(target).append(";\n");
                }
                index++;
            }
            if (!obj.isScopeChainUnknown() && obj.getScopeChain() != null) {
                String source = node(sourceLabel) + ":f" + index;
                for (ObjectLabel objlabel : obj.getScopeChain().getObject()) {
                    String target = node(objlabel);
                    es.append("\t").append(source).append(" -> ").append(target).append(";\n");
                }
            }
        }
        for (ObjectLabel objlabel : execution_context.getThis().getObjectLabels())
            es.append("\tthis -> ").append(node(objlabel)).append(";\n");
        for (ObjectLabel objlabel : execution_context.getVariableObject())
            es.append("\tvar -> ").append(node(objlabel)).append(";\n");
        for (Set<ObjectLabel> sc : ScopeChain.iterable(execution_context.getScopeChain()))
            for (ObjectLabel objlabel : sc)
                es.append("\tscope -> ").append(node(objlabel)).append(";\n");
        return "digraph {\n" +
                "\tnode [shape=record];\n" +
                "\trankdir=\"LR\"\n" +
                ns + es + "}";
    }

    private static String node(ObjectLabel objlabel) {
        int h = objlabel.hashCode();
        if (h > 0)
            return "node" + h;
        else if (h != Integer.MIN_VALUE) // :-)
            return "node_" + -h;
        else
            return "node_";
    }

    private static String esc(String s) {
        return Strings.escape(s).replace("|", " \\| ");
    }

    /**
     * Removes queue objects from the store.
     */
    public void removeQueueObjects() {
        for (ObjectLabel objectLabel : this.queue.keySet())
            this.removeObject(objectLabel);
    }

    /**
     * Reduces this state.
     *
     * @see #gc(Value)
     */
    public void reduce(Value extra) {
        gc(extra);
    }

    /**
     * Runs garbage collection on the contents of this state.
     * Ignored if {@link OptionValues#isGCDisabled()} or {@link OptionValues#isRecencyDisabled()} is set.
     */
    public void gc(Value extra) {
        if (Options.get().isGCDisabled() || Options.get().isRecencyDisabled())
            return;
        if (Options.get().isIntermediateStatesEnabled())
            if (log.isDebugEnabled())
                log.debug("gc(): Before: " + this);
        Set<ObjectLabel> dead = newSet(store.keySet());
        State entry_state = c.getAnalysisLatticeElement().getState(BlockAndContext.makeEntry(block, context));
        dead.removeAll(findLiveObjectLabels(extra, entry_state));
        if (log.isDebugEnabled()) {
            log.debug("gc(): Unreachable objects: " + dead);
        }
        makeWritableStore();
        for (ObjectLabel objlabel : dead) {
            if (noneAtEntry(objlabel, entry_state))
                store.remove(objlabel);
            else
                writeToStore(objlabel, Obj.makeNoneModified());
        }
        // don't remove from summarized (it may contain dead object labels)
        if (Options.get().isIntermediateStatesEnabled())
            if (log.isDebugEnabled())
                log.debug("gc(): After: " + this);
    }

    /**
     * Returns true if the given object label is definitely the none object at the given function entry state.
     */
    private static boolean noneAtEntry(ObjectLabel objlabel, State entry_state) {
        return entry_state.getObject(objlabel, false).getDefaultArrayProperty().isNone();
    }

    /**
     * Callbacks that are registered for execution (along with their arguments)
     * in the queue should be remain to the store to be executed later by
     * the event loop.
     *
     * Therefore, based on the current queue, we retrieve the object labels
     * of all registered callbacks both (resolved and rejected).
     */
    private Set<ObjectLabel> getLiveCallbacks() {
        Set<ObjectLabel> objectLabels = newSet();
        for (Set<QueueObject> queueObjects: this.queue.values()) {
            for (QueueObject qObj : queueObjects) {
                objectLabels.addAll(qObj.getCallbackObjectLabels());
                objectLabels.addAll(qObj.getArgumentObjectLabels());
            }
        }
        return objectLabels;
    }

    /**
     * Finds live object labels (i.e. those reachable from the execution context, registers, or stacked object labels).
     * Note that the summarized sets may contain dead object labels.
     *
     * @param extra       extra value that should be treated as root, ignored if null
     * @param entry_state at function entry
     */
    private Set<ObjectLabel> findLiveObjectLabels(Value extra, State entry_state) {
        Set<ObjectLabel> live = execution_context.getObjectLabels();
        if (extra != null)
            live.addAll(extra.getObjectLabels());
        for (Value v : registers)
            if (v != null)
                live.addAll(v.getObjectLabels());
        live.addAll(stacked_objlabels);
        extras.getAllObjectLabels(live);

        /* Queue objects and their registered callbacks should remain
           in the store. */
        //live.addAll(this.queue.keySet());
        live.addAll(this.getLiveCallbacks());

        if (!Options.get().isLazyDisabled())
            for (ObjectLabel objlabel : store.keySet()) {
                // some object represented by objlabel may originate from the caller (so it must be treated as live),
                // unless it is a singleton object marked as definitely summarized or it is 'none' at function entry
                if (!((objlabel.isSingleton() && summarized.isDefinitelySummarized(objlabel)) ||
                        noneAtEntry(objlabel, entry_state)))
                    live.add(objlabel);
            }
        LinkedHashSet<ObjectLabel> pending = new LinkedHashSet<>(live);
        while (!pending.isEmpty()) {
            Iterator<ObjectLabel> it = pending.iterator();
            ObjectLabel objlabel = it.next();
            it.remove();
            live.add(objlabel);
            for (ObjectLabel obj2 : getAllObjectLabels(objlabel))
                if (!live.contains(obj2))
                    pending.add(obj2);
        }
        return live;
    }

    /**
     * Returns the set of all object labels used in the given abstract object.
     * Does not resolve unknown values.
     */
    private Set<ObjectLabel> getAllObjectLabels(ObjectLabel objlabel) {
        Set<ObjectLabel> objlabels = newSet();
        Obj fo = getObject(objlabel, false);
        for (Value v : fo.getProperties().values())
            objlabels.addAll(v.getAllObjectLabels());
        objlabels.addAll(fo.getDefaultArrayProperty().getAllObjectLabels());
        objlabels.addAll(fo.getDefaultNonArrayProperty().getAllObjectLabels());
        objlabels.addAll(fo.getInternalPrototype().getAllObjectLabels());
        objlabels.addAll(fo.getInternalValue().getAllObjectLabels());
        if (!fo.isScopeChainUnknown())
            for (Set<ObjectLabel> ls : ScopeChain.iterable(fo.getScopeChain()))
                objlabels.addAll(ls);
        return objlabels;
    }

    /**
     * Models [[HasInstance]] (for instanceof).
     *
     * @param prototype external prototype of the second argument to instanceof
     * @param v         first argument to instanceof
     */
    public Value hasInstance(Collection<ObjectLabel> prototype, Value v) {
        boolean maybe_true = false;
        boolean maybe_false = false;
        if (v.isMaybePrimitiveOrSymbol())
            maybe_false = true;
        List<ObjectLabel> pending = newList(v.getObjectLabels());
        Set<ObjectLabel> visited = newSet(v.getObjectLabels());
        while (!pending.isEmpty()) {
            ObjectLabel obj = pending.remove(pending.size() - 1);
            Value proto = UnknownValueResolver.getInternalPrototype(obj, this, false);
            if (proto.isMaybeNull())
                maybe_false = true;
            for (ObjectLabel p : proto.getObjectLabels()) {
                if (prototype.contains(p))
                    maybe_true = true;
                else if (!visited.contains(p)) {
                    pending.add(p);
                    visited.add(p);
                }
            }
            if (maybe_true && maybe_false)
                return Value.makeAnyBool();
        }
        return maybe_true ? (maybe_false ? Value.makeAnyBool() : Value.makeBool(true))
                : (maybe_false ? Value.makeBool(false) : Value.makeNone());
    }

    /**
     * Assigns the given value to the given register (strong update).
     * All attribute information is cleared. 'unknown' values are not permitted.
     */
    public void writeRegister(int reg, Value value) {
        value.assertNonEmpty();
        value = value.setBottomPropertyData();
        if (value.isUnknown())
            throw new AnalysisException("Unexpected 'unknown'");
        makeWritableRegisters();
        while (reg >= registers.size())
            registers.add(null);
        registers.set(reg, value);
        if (log.isDebugEnabled())
            log.debug("writeRegister(v" + reg + "," + value + ")");
    }

    /**
     * Removes the given register (strong update).
     */
    public void removeRegister(int reg) {
        makeWritableRegisters();
        while (reg >= registers.size())
            registers.add(null);
        registers.set(reg, null);
        if (log.isDebugEnabled())
            log.debug("removeRegister(v" + reg + ")");
    }

    /**
     * Returns true if the given register is defined.
     */
    public boolean isRegisterDefined(int reg) {
        return reg >= 0 && reg < registers.size() && registers.get(reg) != null;
    }

    /**
     * Reads the value of the given register.
     */
    public Value readRegister(int reg) {
        Value res;
        if (reg >= registers.size())
            res = null;
        else
            res = registers.get(reg);
        if (res == null)
            throw new AnalysisException("Reading undefined register v" + reg);
        if (log.isDebugEnabled())
            log.debug("readRegister(v" + reg + ") = " + res);
        return res;
    }

    /**
     * Returns the list of registers.
     */
    public List<Value> getRegisters() {
        return registers;
    }

    /**
     * Sets the list of registers.
     */
    public void setRegisters(List<Value> registers) {
        this.registers = registers;
        writable_registers = true;
    }

    /**
     * Adds object labels used in current registers and execution
     * context to stacked object labels.
     */
    public void stackObjectLabels() {
        if (!Options.get().isLazyDisabled())
            return;
        makeWritableStackedObjects();
        for (Value v : registers)
            if (v != null)
                stacked_objlabels.addAll(v.getObjectLabels());
        stacked_objlabels.addAll(execution_context.getObjectLabels());
    }

    /**
     * Clears all registers.
     */
    public void clearRegisters() {
        if (writable_registers)
            registers.clear();
        else {
            registers = Collections.emptyList();
            writable_registers = false;
        }
    }

    /**
     * Clears the registers, starting from {@link AbstractNode#FIRST_ORDINARY_REG}, and excluding property list values.
     */
    public void clearOrdinaryRegisters() {
        List<Value> new_registers = newList();
        int reg = 0;
        for (Value v : registers) {
            if (reg++ >= AbstractNode.FIRST_ORDINARY_REG && v != null && !v.isExtendedScope())
                v = null;
            new_registers.add(v);
        }
        registers = new_registers;
        writable_registers = true;
    }

    /**
     * Returns the value of 'this'.
     */
    public Value readThis() {
        Value res = execution_context.getThis();
        if (log.isDebugEnabled())
            log.debug("readThis() = " + res);
        return res;
    }

    /**
     * Returns the value of 'this'.
     */
    public Set<ObjectLabel> readThisObjects() {
        return readThis().getObjectLabels(); // TODO: assert no primitive values (Github #479) ?
    }

    /**
     * Introduces 'unknown' values in this state according to the given function entry state.
     * Also clears modified flags and summarized sets.
     */
    @Override
    public void localize(State s) {
        if (!Options.get().isLazyDisabled()) {
            this.scheduledCallbacks = null;
            this.callbackContext = null;
            if (s == null) {
                // set everything to unknown
                store = newMap();
                writable_store = true;
                store_default = Obj.makeUnknown();
            } else {
                // localize each object
                makeWritableStore();
                for (ObjectLabel objlabel : s.store.keySet()) {
                    if (!store.containsKey(objlabel)) {
                        getObject(objlabel, true); // materialize default objects
                    }
                }
                for (ObjectLabel objlabel : newList(store.keySet())) {
                    Obj obj = getObject(objlabel, true);
                    Obj other = s.getObject(objlabel, false);
                    obj.localize(other, objlabel, this);
                }
                // remove all-unknown objects
                Map<ObjectLabel, Obj> oldStore = store;
                store = newMap();
                for (Map.Entry<ObjectLabel, Obj> xs : oldStore.entrySet())
                    if (!xs.getValue().isUnknown())
                        writeToStore(xs.getKey(), xs.getValue());
                store_default = Obj.makeUnknown();
            }
        } else {
            clearModified();
        }
        summarized.clear();
    }

    /**
     * Clears effects and summarized sets (for function entry).
     */
    public void clearEffects() {
        clearModified();
        summarized.clear();
    }

    @Override
    public Context transform(CallEdge edge, Context edge_context,
                             Map<Context, State> callee_entry_states, BasicBlock callee) {
        return edge_context;
    }

    @Override
    public boolean transformInverse(CallEdge edge, BasicBlock callee, Context callee_context) {
        return false;
    }
}
