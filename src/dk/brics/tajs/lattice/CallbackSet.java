package dk.brics.tajs.lattice;

import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajs.util.Collections;
import dk.brics.tajs.util.Collectors;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newSet;


/**
 * This class stores callbacks in a simple set.
 *
 * Therefore, no any execution order is enforced by this class.
 */
public class CallbackSet implements CallbackContainer {

    private Set<CallbackExecutionInfo> callbacks;

    private CallbackSet() {
        this.callbacks = newSet();
    }

    private CallbackSet(Set<CallbackExecutionInfo> callbacks) {
        this.callbacks = callbacks;
    }

    public static CallbackSet make() {
        return new CallbackSet();
    }

    @Override
    public void addCallback(CallbackExecutionInfo callback) {
        this.callbacks.add(callback);
    }

    @Override
    public CallbackContainer join(CallbackContainer callbackContainer) {
        if (callbackContainer == null)
            return this;
        if (!(callbackContainer instanceof CallbackSet))
            throw new AnalysisException(
                    "Cannot join callback containers of different type");
        Set<CallbackExecutionInfo> newCallbacks = Stream
                .concat(this.callbacks.stream(),
                        ((CallbackSet) callbackContainer).callbacks.stream())
                .collect(Collectors.toSet());
        return new CallbackSet(newCallbacks);

    }

    @Override
    public void propagateValue(Value value, boolean forceUpdate) {
        if (this.callbacks.size() == 0 || value == null)
            return;
        Set<CallbackExecutionInfo> newCallbacks = newSet();
        for (CallbackExecutionInfo callback : this.callbacks) {
            CallbackExecutionInfo c = callback.clone();
            if (forceUpdate) {
                List<Value> args = newList();
                args.add(value.restrictToNonPromises());
                c.setArgs(args);
            } else if (c.getArgs().size() == 0) {
                List<Value> cArgs = newList(c.getArgs());
                cArgs.add(value);
                c.setArgs(cArgs);
            }
            newCallbacks.add(c);
        }
        this.callbacks = newCallbacks;
    }

    @Override
    public Set<CallbackExecutionInfo> getAllCallbacks() {
        return this.callbacks;
    }

    @Override
    public boolean isEmpty() {
        return this.callbacks.isEmpty();
    }

    @Override
    public void replaceObjectLabel(ObjectLabel from, ObjectLabel to) {
        this.callbacks = this.callbacks
                .stream()
                .map(x -> {
                    if (x.getQueueObject().equals(from)) {
                        CallbackExecutionInfo y = new CallbackExecutionInfo(
                                        x.getThisObj(),
                                        to,
                                        x.getCallback(),
                                        x.isImplicit());
                        y.setArgs(newList(x.getArgs()));
                        return y;
                    } else
                        return x;
                })
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public void pushLevel() { }

    @Override
    public Set<List<CallbackExecutionInfo>> getCallbacksInOrder() {
        return this.callbacks
                .stream()
                .map(Collections::singletonList)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public CallbackContainer clone() {
        return new CallbackSet(newSet(this.callbacks));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallbackSet that = (CallbackSet) o;

        return callbacks != null ? callbacks.equals(that.callbacks) : that.callbacks == null;
    }

    @Override
    public int hashCode() {
        return callbacks != null ? callbacks.hashCode() : 0;
    }

    public Set<CallbackExecutionInfo> getCallbacks() {
        return callbacks;
    }
}
