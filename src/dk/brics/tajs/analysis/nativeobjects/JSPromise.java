package dk.brics.tajs.analysis.nativeobjects;

import dk.brics.tajs.analysis.Exceptions;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.PropVarOperations;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.QueueObject;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.UnknownValueResolver;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajs.util.Collections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newSet;


/**
 * A class that is used to evaluate promise-related built-in functions.
 */
public class JSPromise {

    private JSPromise() { }

    /**
     * Evaluate promise-related functions.
     */
    public static Value evaluate(ECMAScriptObjects nativeobject,
                                 FunctionCalls.CallInfo call,
                                 Solver.SolverInterface c) {
        State state = c.getState();
        switch (nativeobject) {
            case PROMISE:
                return evaluatePromise(state, call, c);
            case PROMISE_RACE:
                return evaluateRace(state, call);
            case PROMISE_RESOLVE:
                return evaluateResolve(state, call, c);
            case PROMISE_REJECT:
                return evaluateReject(state, call);
            case PROMISE_THEN:
                return evaluateThen(state, call, c);
            case PROMISE_CATCH:
                return evaluateCatch(state, call, c);

        }
        return null;
    }

    private static ObjectLabel getInternalFunctionLabel(State state,
            FunctionCalls.CallInfo call) {
        Value funValue = call.getFunctionValue();
        funValue = UnknownValueResolver.getRealValue(funValue, state);
        ObjectLabel objectLabel = funValue.getObjectLabelUnique();

        if (objectLabel == null)
            throw new AnalysisException();
        return objectLabel;
    }

    private static void scheduleThenable(ObjectLabel objectLabel,
                                         Value thenCallables,
                                         State state,
                                         ObjectLabel promise) {
        /* We add a new object to the queue. This object is just
         the thenable object which is passed in the resolve function
         of the current promise.

         Then as 25.4.1.3.2 states we schedule the execution of the
         `then` function where we pass the internal resolve and
         reject arguments as arguments.

         Also, we make the current promise dependent on the thenable
         object.
         */
        state.newQueueObject(objectLabel);
        state.settleQueueObject(objectLabel, null, true, null, false);
        List<Value> args = makeExecutorArgs(promise, state, thenCallables,
                                            objectLabel);
        state.onResolve(objectLabel, thenCallables, objectLabel, promise,
                        args, false);
        QueueObject queueObject = QueueObject.make();
        queueObject.makeDependentOn(objectLabel);
        state.addNewQueueObjectTo(promise, queueObject);
    }

    public static void resolve(State state, ObjectLabel promise,
                               Value resolveValue,
                               PropVarOperations pVarOps,
                               Value resolvedFrom) {
        resolveValue = UnknownValueResolver.getRealValue(
                resolveValue, state);
        if (!resolveValue.isMaybeObject()
                && resolveValue.isMaybePrimitiveOrSymbol()) {
            // Case 1: The resolution value is not an object.
            state.settleQueueObject(promise, resolveValue, true,
                                    true, resolvedFrom, false);
            return;
        }
        Value propertyVal = pVarOps.readPropertyValue(
                resolveValue.getObjectLabels(), "then");
        propertyVal = UnknownValueResolver.getRealValue(propertyVal, state);
        boolean maybeFun = propertyVal.getObjectLabels().stream()
                .anyMatch((x) -> x.getKind() == ObjectLabel.Kind.FUNCTION);
        if (!maybeFun) {
            // Case 2: The resolution value may be an object
            // which does not have a callable 'then' property.
            state.settleQueueObject(promise, resolveValue, true,
                                    true, resolvedFrom, false);
            return;
        }
        Set<ObjectLabel> nonThenables = newSet();
        for (ObjectLabel objectLabel: resolveValue.getObjectLabels()) {
            // Case 3: Check for objects that are thenables.
            Value thenValue = pVarOps.readPropertyValue(
                    Collections.singleton(objectLabel), "then");
            thenValue = UnknownValueResolver.getRealValue(thenValue, state);
            Set<ObjectLabel> funObjLabels = thenValue.filterFunctionObjectLabels();
            Value thenCallbables = Value.makeObject(funObjLabels);
            scheduleThenable(objectLabel, thenCallbables, state, promise);
            if (funObjLabels.size() != thenValue.getObjectLabels().size())
                // This object may be a non-thenable object.
                nonThenables.add(objectLabel);
        }
        if (nonThenables.size() > 0) {
            // These objects labels contained in the resolved value
            // are not thenables.
            Value value = Value.makeObject(nonThenables);
            state.settleQueueObject(promise, value, true,
                                    true, resolvedFrom, false);
        }
    }

    public static void reject(State state, ObjectLabel promise,
                              Value rejectedValue,
                              Value rejectedFrom) {
        state.settleQueueObject(
                promise, rejectedValue, false, true, rejectedFrom, false);
    }

    /**
     * This method implements the internal resolve function
     * based on 25.4.1.3.2.
     */
    public static Value evaluateInternalResolve(State state,
                                                 FunctionCalls.CallInfo call,
                                                 Solver.SolverInterface c) {
        ObjectLabel objectLabel = getInternalFunctionLabel(
                state, call);
        ResolvingFunction resolveFun = (ResolvingFunction)
                objectLabel.getHostObject();
        Value value = FunctionCalls.readParameter(call, state, 0);
        c.getMonitoring().visitPromiseResolve(
                call.getSourceNode(), resolveFun.getPromise(), value);

        boolean maybeSame = value.getObjectLabels().stream().anyMatch(
                l -> l.equals(resolveFun.getPromise()));
        if (maybeSame) {
            Exceptions.throwTypeError(c);
            state.settleQueueObject(resolveFun.getPromise(), value, false,
                                    true,null, false);
        } else {
            Value resolvedFrom = resolveFun.getResolvedFrom() == null ?
                    null : Value.makeObject(resolveFun.getResolvedFrom());
            resolve(state, resolveFun.getPromise(), value,
                    c.getAnalysis().getPropVarOperations(),
                    resolvedFrom);
        }
        return Value.makeUndef();
    }

    /**
     * This method implements the internal reject function
     * based on 25.4.1.7.
     */
    public static Value evaluateInternalReject(State state,
                                               FunctionCalls.CallInfo call) {
        ObjectLabel objectLabel = getInternalFunctionLabel(
                state, call);
        ResolvingFunction rejectFun = (ResolvingFunction)
                objectLabel.getHostObject();
        Value value = FunctionCalls.readParameter(call, state, 0);
        Value rejectedFrom = rejectFun.getResolvedFrom() == null ?
                null : Value.makeObject(rejectFun.getResolvedFrom());
        reject(state, rejectFun.getPromise(), value,
               rejectedFrom);
        return Value.makeUndef();
    }

    private static Value createInternalFunc(State state,
                                            ResolvingFunction internalFun) {
        ObjectLabel objlabel = ObjectLabel.make(
                internalFun, ObjectLabel.Kind.FUNCTION);
        state.newObject(objlabel);
        state.writeInternalPrototype(objlabel, Value.makeObject(
                InitialStateBuilder.FUNCTION_PROTOTYPE));
        return Value.makeObject(objlabel);
    }

    private static List<Value> makeExecutorArgs(ObjectLabel promise,
                                                State state,
                                                Value callee,
                                                ObjectLabel resolvedFrom) {
        /* We create resolving functions which are associated with
         the promise given as argument (based on 25.4.1.3). */
        callee = callee.restrictToFunctions();
        Set<Function> calleeFuns = callee.getObjectLabels()
                .stream()
                .map(ObjectLabel::getFunction)
                .collect(Collectors.toSet());
        ResolvingFunction res = new ResolvingFunction(
                promise, ResolvingFunction.Kind.RESOLVE,
                resolvedFrom, calleeFuns);
        ResolvingFunction rej = new ResolvingFunction(
                promise, ResolvingFunction.Kind.REJECT,
                resolvedFrom, calleeFuns);

        List<Value> args = newList();
        args.add(createInternalFunc(state, res));
        args.add(createInternalFunc(state, rej));
        return args;
    }

    private static void callPromiseExecutor(State state,
                                            FunctionCalls.CallInfo call,
                                            Value executor,
                                            ObjectLabel promise,
                                            Solver.SolverInterface c) {
        // We pass the internal resolve and reject functions to the executor.
        State callState = state.clone();
        List<Value> args = makeExecutorArgs(promise, state, executor, null);
        c.withState(callState, () -> {
            /* At this point, we invoke a promise executor,
             so if there is an error, we do not propagate the exception.
             Instead, we stop execution, we reject the promise, and
             we return from the ordinary exit of the executor. */
            callState.appendToQueueChain(Collections.singleton(promise), false);
            FunctionCalls.callFunction(
                new FunctionCalls.EventHandlerCall(
                    call.getSourceNode(),
                    executor,
                    args,
                    Collections.singleton(promise),
                    callState),
                c);
        });
    }

    private static ObjectLabel createNewPromise(State state,
                                                FunctionCalls.CallInfo call) {
        ObjectLabel promise = ObjectLabel.make(
                call.getSourceNode(), ObjectLabel.Kind.PROMISE);
        state.newObject(promise);
        state.writeInternalPrototype(promise, Value.makeObject(
                InitialStateBuilder.PROMISE_PROTOTYPE));
        state.newQueueObject(promise);
        return promise;
    }

    /**
     * This method evaluates the Promise constructor based on
     * 25.4.3.1.
     */
    private static Value evaluatePromise(State state,
                                         FunctionCalls.CallInfo call,
                                         Solver.SolverInterface c) {
        c.getMonitoring().visitPromiseCall(call.getSourceNode(), call);
        if (!call.isConstructorCall()) {
            // Use of Promise() without new keyword is forbidden.
            Exceptions.throwTypeError(c);
            return Value.makeNone();
        }
        Value executor = FunctionCalls.readParameter(call, state, 0);
        c.getMonitoring().visitPromiseExecutor(call.getSourceNode(), executor);
        if (!executor.isMaybeFunction()) {
            // Executor must be a callable.
            Exceptions.throwTypeError(c);
            return Value.makeNone();
        }

        if (executor.isMaybeNonFunction())
            Exceptions.throwTypeError(c);

        executor = executor.restrictToFunctions();
        ObjectLabel promise = createNewPromise(state, call);
        callPromiseExecutor(state, call, executor, promise, c);
        return Value.makeObject(promise);
    }

    /**
     * This method evaluates Promise.race function based on
     * 25.6.4.3.
     *
     * Note that this method always evaluates to a *pending*
     * promise which is resolved asynchronously via
     * internal `RaceFunction` which implements the semantics
     * of 25.6.4.3.1.
     *
     * For that reason, we create a new object which is used to
     * asynchronously execute the `RaceFunction`.
     */
    private static Value evaluateRace(State state,
                                      FunctionCalls.CallInfo call) {
        Value iterable = FunctionCalls.readParameter(call, state, 0);
        ObjectLabel promise = createNewPromise(state, call);

        /* We create a new fulfilled queue object to asynchronously
           perform promise race. */
        ObjectLabel obj = ObjectLabel.make(
                call.getSourceNode(), ObjectLabel.Kind.OBJECT);
        state.newObject(obj);
        state.newQueueObject(obj);
        state.settleQueueObject(obj, null, true, null, false);
        List<Value> args = newList();
        args.add(iterable);
        Value callback = Value.makeObject(
                InitialStateBuilder.PROMISE_RACE_FUN);
        state.onResolve(obj, callback, promise, promise, args, false);

        return Value.makeObject(promise);
    }

    /**
     * This method evaluates Promise.resolve function based on
     * 25.4.4.5.
     */
    private static Value evaluateResolve(State state,
                                         FunctionCalls.CallInfo call,
                                         Solver.SolverInterface c) {
        Value resolveValue = FunctionCalls.readParameter(call, state, 0);
        Set<ObjectLabel> res = newSet();
        Set<ObjectLabel> promises = newSet();
        for (ObjectLabel objectLabel: resolveValue.getObjectLabels())
            if (objectLabel.getKind() == ObjectLabel.Kind.PROMISE)
                promises.add(objectLabel);
        res.addAll(promises);
        Value nonPromises = resolveValue.restrictToNonPromises();
        if (!nonPromises.isNone()) {
            /* if the given value is not a promise,
             we create a new promise, and we resolve it with the given
             value. */
            ObjectLabel promise = createNewPromise(state, call);
            resolve(state, promise, nonPromises,
                    c.getAnalysis().getPropVarOperations(), null);
            res.add(promise);
        }
        return Value.makeObject(res);
    }

    /**
     * This method evaluates Promise.reject function based on
     * 25.4.4.4.
     */
    private static Value evaluateReject(State state,
                                        FunctionCalls.CallInfo call) {
        Value rejectValue = FunctionCalls.readParameter(call, state, 0);
        ObjectLabel promise = createNewPromise(state, call);
        reject(state, promise, rejectValue, null);
        return Value.makeObject(promise);
    }

    /**
     * This method evaluates Promise.prototype.then function based on
     * 25.4.5.3.
     */
    private static Value evaluateThen(State state,
                                      FunctionCalls.CallInfo call,
                                      Solver.SolverInterface c) {
        Value onFulfill = FunctionCalls.readParameter(call, state, 0);
        Value onReject = FunctionCalls.readParameter(call, state, 1);
        /* If there is a case for the parameters of then() not to be functions,
         we have to use the default reactions. Therefore, we join the given
         values with the object labels of the corresponding default reactions.

         See 25.4.2.1 and 6.2.3.1.
         */
        if (onFulfill.isMaybeNonFunction())
            onFulfill = onFulfill.joinObject(
                    InitialStateBuilder.DEFAULT_ONFULFILL)
                    .restrictToFunctions();

        if (onReject.isMaybeNonFunction())
            onReject = onReject.joinObject(
                    InitialStateBuilder.DEFAULT_ONREJECT)
                    .restrictToFunctions();
        return then(state, call, c, onFulfill, onReject);
    }

    /**
     * This method evaluates Promise.prototype.catch function based on
     * 25.4.5.1.
     *
     * Note that `catch` is just a syntactic sugar of
     * `then(undefined, onReject)`.
     */
    private static Value evaluateCatch(State state,
                                       FunctionCalls.CallInfo call,
                                       Solver.SolverInterface c) {
        Value onReject = FunctionCalls.readParameter(call, state, 0);
        if (onReject.isMaybeNonFunction())
            onReject = onReject.joinObject(
                    InitialStateBuilder.DEFAULT_ONREJECT)
                    .restrictToFunctions();
        return then(state, call, c,
                    Value.makeObject(InitialStateBuilder.DEFAULT_ONFULFILL),
                    onReject);
    }

    /**
     * This method evaluates the abstract operation `PerformPromiseThen`
     * as specified in 25.4.5.3.1.
     */
    private static Value then(State state, FunctionCalls.CallInfo call,
                              Solver.SolverInterface c, Value onFulfill,
                              Value onReject) {
        Value thisVal = call.getThis();
        if (!thisVal.isMaybePromise()) {
            Exceptions.throwTypeError(c);
            return Value.makeNone();
        }
        if (thisVal.isMaybeNonPromise())
            Exceptions.throwTypeError(c);

        thisVal = thisVal.restrictToPromises();
        ObjectLabel promise = createNewPromise(state, call);
        for (ObjectLabel l: thisVal.getObjectLabels()) {
            state.onResolve(l, onFulfill, InitialStateBuilder.GLOBAL,
                    promise, null, true);
            state.onReject(l, onReject, InitialStateBuilder.GLOBAL,
                    promise, null, true);
        }
        return Value.makeObject(promise);
    }
}
