package dk.brics.tajs.lattice;

import java.util.List;
import java.util.Set;


/**
 * This interface defines the behaviour of containers
 * which hold information about the execution order of callbacks.
 */
public interface CallbackContainer {

    /** Adds a new callback to the container. */
    void addCallback(CallbackExecutionInfo callback);

    /**
     * It joins the current container with the given one.
     *
     * This should be pure and create a new container for
     * holding the joint information.
     */
    CallbackContainer join(CallbackContainer callbackContainer);

    /**
     * Propagates the given value to the arguments of
     * registered callbacks.
     */
    void propagateValue(Value value, boolean forceUpdate);

    /** Get all registered callbacks of the current container. */
    Set<CallbackExecutionInfo> getAllCallbacks();

    /**
     * Get the callbacks in their execution order.
     *
     * Every element of the returning set defines a possible execution
     * path.
     */
    Set<List<CallbackExecutionInfo>> getCallbacksInOrder();

    /** Checks if the container is empty or not. */
    boolean isEmpty();

    void replaceObjectLabel(ObjectLabel from, ObjectLabel to);

    void pushLevel();

    CallbackContainer clone();

    /**
     * Factory method for creating a new CallbackContainer for
     * Promises.
     */
    static CallbackContainer make() {
        return make(QueueObject.Kind.PROMISE);
    }

    /**
     * Factory method for creating a new CallbackContainer
     * based on the given kind.
     */
    static CallbackContainer make(QueueObject.Kind kind) {
        switch (kind) {
            case PROMISE:
                return CallbackSet.make();
            case TIMER:
            case ASYNC_IO:
                return CallbackChain.make();
            default:
                return CallbackSet.make();
        }
    }
}
