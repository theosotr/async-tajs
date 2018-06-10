package dk.brics.tajs.analysis.nativeobjects;

import dk.brics.tajs.analysis.Exceptions;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.CallbackDescription;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.QueueObject;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.Collections;

import java.util.Set;


/**
 * This class represents an internal function used to
 * perform promise race based on 25.6.4.3.1.
 */
public class RaceFunction extends InternalFunction {

    private void iterateArray(ObjectLabel array,
                              State state,
                              ObjectLabel promise,
                              Solver.SolverInterface c) {
        Double length = c.getAnalysis()
                .getPropVarOperations()
                .readPropertyValue(Collections.singleton(array), "length")
                .getNum();
        if (length == null) {
            state.settleQueueObject(promise, Exceptions.makeTypeError(c),
                                   false, true, null, false);
            return;
        }

        for (int i = 0; i < length.intValue(); i++) {
            Value value = c.getAnalysis()
                    .getPropVarOperations()
                    .readPropertyValue(Collections.singleton(array),
                                      String.valueOf(i));
            if (value.isNone()) {
                state.settleQueueObject(promise, Exceptions.makeTypeError(c),
                                       false, true, null, false);
                return;
            }
            boolean isPending = state.getQueue().get(promise)
                    .stream().anyMatch(x -> !x.isSettled());
            if (!isPending)
                // Promise has been resolved.
                break;
            if (value.isMaybeNonPromise()) {
                /* Case 1: The array element is not a promise
                   so we simply resolve the promise with this value. */
                boolean canBeResolved = state.getQueue()
                        .get(promise)
                        .stream()
                        .filter(x -> !x.isSettled())
                        .anyMatch(x -> x.canBeSettledBy(null, false));
                if (!canBeResolved) {
                    QueueObject queueObject = QueueObject.make();
                    Set<QueueObject> queueObjects = QueueObject.join(
                            state.getQueue().get(promise),
                            Collections.singleton(queueObject),
                            QueueObject.Join.DEFAULT, true,
                            QueueObject.Kind.PROMISE);
                    state.addQueueObjects(promise, queueObjects);
                }
                JSPromise.resolve(state, promise,
                                  value.restrictToNonPromises(),
                                  c.getAnalysis().getPropVarOperations(),
                                  null);
            }

            if (!value.isMaybePromise())
                continue;
            // Case 2: The array element is a promise.
            Value promiseValue = value.restrictToPromises();
            for (ObjectLabel l: promiseValue.getObjectLabels()) {
                Set<QueueObject> queueObjects = state.getQueue().get(l);
                for (QueueObject q: queueObjects) {
                    if (!q.isSettled())
                        continue;
                    QueueObject queueObject = QueueObject.make();
                    if (q.getState() == QueueObject.QueueState.FULFILLED)
                        queueObject.resolve(q.getDefaultValue(), false);
                    else
                        queueObject.reject(q.getDefaultValue(), false);
                    Set<QueueObject> qObjs = state.getQueue()
                            .get(promise);
                    Set<QueueObject> jointQueueObjs = QueueObject.join(
                            qObjs, Collections.singleton(queueObject),
                            QueueObject.Join.DEFAULT, true,
                            QueueObject.Kind.PROMISE);
                    state.addQueueObjects(promise, jointQueueObjs);
                    isPending = jointQueueObjs
                            .stream().anyMatch(x -> !x.isSettled() && !x.isDependent());
                    if (isPending)
                        continue;
                    Set<CallbackDescription> callbacks = QueueObject
                            .toScheduledCallbacks(promise, jointQueueObjs, null);
                    state.appendToScheduledCallbacks(callbacks, false);
                }
            }
        }
    }

    @Override
    public Value evaluate(State state, FunctionCalls.CallInfo call,
                          Solver.SolverInterface c) {
        Value iterable = FunctionCalls.readParameter(call, state, 0);
        ObjectLabel promise = call.getThis().restrictToPromises()
                .getObjectLabelUnique();
        if (!iterable.isMaybeArray() && iterable.isMaybeNonArray()) {
            /* We do not have an array; therefore, we reject the promise
               with a type error. */
            state.settleQueueObject(promise, Exceptions.makeTypeError(c),
                                    false, true, null, false);
            return Value.makeUndef();
        }
        if (iterable.isMaybeNonArray()) {
            state.settleQueueObject(promise, Exceptions.makeTypeError(c),
                                    false, true, null, false);
            /*
              We add one more queue object because it seems that this
              might be resolved or rejected with multiple ways.
             */
            state.addNewQueueObjectTo(promise, QueueObject.make());
        }

        /* Let's iterate all elements of every array represented
           by the value of iterable based on 25.6.4.3.1. */
        iterable = iterable.restrictToArrays();
        for (ObjectLabel l: iterable.getObjectLabels())
            iterateArray(l, state, promise, c);
        return Value.makeUndef();
    }

    @Override
    public String toString() {
        return "race";
    }
}
