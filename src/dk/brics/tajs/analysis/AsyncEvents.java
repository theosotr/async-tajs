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

package dk.brics.tajs.analysis;

import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.jsnodes.EventDispatcherNode;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.QueueObject;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.UnknownValueResolver;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.solver.CallbackGraph;
import dk.brics.tajs.util.AnalysisException;

import java.util.Set;


/**
 * Processing of asynchronous events (that do not involve HTML DOM).
 */
public class AsyncEvents {

    private static final String maySetKey = EventDispatcherNode.Type.ASYNC.name();

    private AsyncEvents() {}

    public static void listen(AbstractNode node, Value handler, Solver.SolverInterface c) {
        State state = c.getState();
        handler = UnknownValueResolver.getRealValue(handler, state);
        Set<ObjectLabel> objectLabels = Conversion.toObjectLabels(node, handler, c);
        c.getMonitoring().visitEventHandlerRegistration(node, c.getState().getContext(), Value.makeObject(objectLabels));
        state.getExtras().addToMaySet(maySetKey, objectLabels);
    }

    public static void pushLevels(State state, ObjectLabel objectLabel) {
        Set<QueueObject> queueObjects = state.getQueue()
                .get(objectLabel);
        if (queueObjects.size() != 1)
            throw new AnalysisException();
        QueueObject queueObject = queueObjects.iterator().next();
        queueObject.getResolvedCallbacks().pushLevel();
        queueObject.getRejectedCallbacks().pushLevel();
    }

    public static void emit(EventDispatcherNode n, Solver.SolverInterface c) {
        if (n.getType() != EventDispatcherNode.Type.ASYNC) {
            return;
        }

        State state = c.getState();
        Set<CallbackGraph.CallbackGraphNode> firstCallbacks = state
                .getNextCallbacksToRun();
        if (firstCallbacks == null || firstCallbacks.isEmpty())
            return;
        CallbackGraph callbackGraph = c.getAnalysisLatticeElement()
                .getCallbackGraph();
        for (CallbackGraph.CallbackGraphNode node : firstCallbacks) {
            CallbackCallInfo firstCallback = callbackGraph
                    .getCallbackInfo(node);
            State callState = state.clone();
            pushLevels(callState, InitialStateBuilder.SET_TIMEOUT_QUEUE_OBJ);
            pushLevels(callState, InitialStateBuilder.ASYNC_IO);
            c.withState(callState, () -> {
                    callState.setCallbackContext(node.getSecond());
                    callState.appendToQueueChain(firstCallback.getDependentQueueObjects(),
                                                 firstCallback.isImplicit());
                    FunctionCalls.callFunction(
                        new FunctionCalls.AsyncCall(
                            n,
                            firstCallback.getCallback(),
                            firstCallback.getArgs(),
                            firstCallback.getThisObjs(),
                            callState,
                            firstCallback.getQueueObjects(),
                            firstCallback.getDependentQueueObjects()),
                    c);
            });
        }
    }
}
