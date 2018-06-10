package dk.brics.tajs.lattice;

import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajs.util.Chain;
import dk.brics.tajs.util.Collections;
import dk.brics.tajs.util.Collectors;

import java.util.List;
import java.util.Set;

import static dk.brics.tajs.util.Collections.joinSetLists;
import static dk.brics.tajs.util.Collections.newSet;


/**
 * This class stores the callbacks as a chain.
 *
 * The execution order of callbacks is defined by the level
 * on which they are registered. This means that callbacks
 * of the first level precede the callbacks registered on
 * subsequent levels.
 */
public class CallbackChain implements CallbackContainer {

    private Chain<CallbackExecutionInfo> callbacks;

    private CallbackChain() {
        this.callbacks = null;
    }

    private CallbackChain(Chain<CallbackExecutionInfo> callbacks) {
        this.callbacks = callbacks;
    }

    public static CallbackChain make() {
        return new CallbackChain();
    }

    @Override
    public void addCallback(CallbackExecutionInfo callback) {
        if (this.callbacks == null)
            this.callbacks = Chain
                    .make(Collections.singleton(callback), null);
        else
            this.callbacks.appendNextToLast(callback);
    }

    @Override
    public CallbackContainer join(CallbackContainer callbackContainer) {
        if (callbackContainer == null)
            return this;

        if (!(callbackContainer instanceof CallbackChain))
            throw new AnalysisException(
                    "Cannot join callback containers of different type");

        return new CallbackChain(Chain.join(
                this.callbacks,
                ((CallbackChain) callbackContainer).getCallbacks(), true));
    }

    @Override
    public void propagateValue(Value value, boolean forceUpdate) {
        if (value != null)
            throw new AnalysisException(
                    "Value propagation on callback chain is not supported");
    }

    @Override
    public Set<CallbackExecutionInfo> getAllCallbacks() {
        Set<CallbackExecutionInfo> callbackSet = newSet();
        Chain<CallbackExecutionInfo> callbackChain = this.callbacks;
        while (callbackChain != null) {
            Set<CallbackExecutionInfo> top = callbackChain
                    .getTop();
            if (top != null)
                callbackSet.addAll(top);
            callbackChain = callbackChain.getNext();
        }
        return callbackSet;
    }

    @Override
    public boolean isEmpty() {
        return this.callbacks == null || this.callbacks.isEmpty();
    }

    @Override
    public void replaceObjectLabel(ObjectLabel from, ObjectLabel to) {

    }

    @Override
    public void pushLevel() {
        if (this.callbacks == null)
            return;
        Set<CallbackExecutionInfo> last = this.callbacks.getLast();
        if (last != null && !last.isEmpty())
            this.callbacks.appendLast(Chain.make());
    }

    @Override
    public Set<List<CallbackExecutionInfo>> getCallbacksInOrder() {
        if (this.callbacks == null)
            return newSet();
        Chain<CallbackExecutionInfo> callbackChain = this.callbacks;
        Set<List<CallbackExecutionInfo>> callbackOrder = newSet();
        while (callbackChain != null) {
            Set<CallbackExecutionInfo> top = callbackChain
                    .getTop();
            if (top != null) {
                Set<List<CallbackExecutionInfo>> currLevel = top
                        .stream()
                        .map(Collections::singletonList)
                        .collect(Collectors.toSet());
                callbackOrder = joinSetLists(callbackOrder, currLevel);
            }
            callbackChain = callbackChain.getNext();
        }
        return callbackOrder;
    }

    @Override
    public CallbackContainer clone() {
        if (this.callbacks == null)
            return this;
        return new CallbackChain(this.callbacks.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallbackChain that = (CallbackChain) o;

        return callbacks != null ? callbacks.equals(that.callbacks) : that.callbacks == null;
    }

    @Override
    public int hashCode() {
        return callbacks != null ? callbacks.hashCode() : 0;
    }

    public Chain<CallbackExecutionInfo> getCallbacks() {
        return callbacks;
    }
}
