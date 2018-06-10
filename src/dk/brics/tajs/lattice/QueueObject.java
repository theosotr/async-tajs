package dk.brics.tajs.lattice;

import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajs.util.Collections;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newSet;


/**
 * This class represents a queue object which describes the state of an
 * asynchronous operation.
 */
public final class QueueObject {

    /** The state of the asynchronous operation. */
    private QueueState state;

    /** Callbacks to be executed when the queue object is fulfilled. */
    private CallbackContainer resolvedCallbacks;

    /** Callbacks to be executed when the queue object is rejected. */
    private CallbackContainer rejectedCallbacks;

    /**
     * This value is used to call resolved or rejected callbacks with.
     *
     * This value is only used if the queue object is not settled with
     * user provided arguments.
     */
    private Value defaultValue;

    /**
     * Queue objects which should be resolved or rejected as the current
     * object.
     */
    private Set<ObjectLabel> dependentObjects;

    /**
     * Queue object on which the current object is dependent.
     *
     * If this is not null, the current object can be resolved or rejected only
     * when the `dependentOn` object is resolved or rejected.
     */
    private QueueDependency dependentOn;

    /** Kind of the current queue object. */
    private Kind kind;

    QueueObject(Set<ObjectLabel> dependentObjects,
                Kind kind) {
        this.state = QueueState.PENDING;
        this.resolvedCallbacks = CallbackContainer.make(kind);
        this.rejectedCallbacks = CallbackContainer.make(kind);
        this.dependentObjects = dependentObjects == null ?
                newSet() : dependentObjects;
        this.dependentOn = QueueDependency.make();
        this.defaultValue = null;
        this.kind = kind;
    }

    private QueueObject(QueueState state,
                        CallbackContainer resolvedCallbacks,
                        CallbackContainer rejectedCallbacks,
                        Value defaultValue,
                        Set<ObjectLabel> dependentObjects,
                        QueueDependency queueDependency,
                        Kind kind) {
        this.state = state;
        this.resolvedCallbacks = resolvedCallbacks;
        this.rejectedCallbacks = rejectedCallbacks;
        this.defaultValue = defaultValue;
        this.dependentOn = queueDependency == null ?
                QueueDependency.make() : queueDependency;
        this.dependentObjects = dependentObjects == null ?
                newSet() : dependentObjects;
        this.kind = kind;
    }

    /**
     * It creates the default QueueObject whose state is `PENDING`
     * with empty resolved and rejected callbacks.
     */
    public static QueueObject make() {
        return new QueueObject(null,
                               Kind.PROMISE);
    }

    public static Set<CallbackDescription> toScheduledCallbacks(
            ObjectLabel objectLabel,
            Set<QueueObject> queueObjects,
            Value settledBy) {
        Set<CallbackDescription> callbacks = newSet();
        for (QueueObject queueObject : queueObjects) {
            if (queueObject.getState() == QueueObject.QueueState.PENDING)
                continue;
            callbacks.addAll(
                    queueObject.getCallbacks()
                        .stream()
                        .map(x -> new CallbackDescription(
                             objectLabel,
                             queueObject.getState(),
                             x.getCallback(),
                             x.getQueueObject(),
                             settledBy
                        ))
                        .collect(Collectors.toSet()));
        }
        return callbacks;
    }

    /**
     * Adds a new callback to the container of resolved or rejected callbacks.
     *
     * If the provided args is null or empty, the `defaultValue` is used
     * to invoke callback.
     */
    private void addCallback(CallbackContainer callbacks,
                             CallbackExecutionInfo clbInfo,
                             List<Value> args) {
        if (args == null || args.size() == 0) {
            if (this.defaultValue == null) {
                // We register callback without any arguments specified.
                callbacks.addCallback(clbInfo);
                return;
            }
            clbInfo.addArg(this.defaultValue);
        } else {
            clbInfo.setArgs(args);
        }
        callbacks.addCallback(clbInfo);
    }

    public void addResolvedCallback(CallbackExecutionInfo callback,
                                    List<Value> args) {
        this.addCallback(this.resolvedCallbacks, callback, args);
    }

    public void addRejectedCallback(CallbackExecutionInfo callback,
                                    List<Value> args) {
        this.addCallback(this.rejectedCallbacks, callback, args);
    }

    private boolean canBeSettledByValue(Value value) {
        if (value == null)
            return !this.isDependent();
        Set<ObjectLabel> objectLabels = value.getObjectLabels();
        if (objectLabels.size() != 1)
            return false;
        return !this.isDependent() || this.dependentOn.isDependent(
                objectLabels.iterator().next());
    }

    public boolean canBeSettledBy(Value value, boolean forceJoin) {
        if (this.isSettled())
            return forceJoin && canBeSettledByValue(value);
        return canBeSettledByValue(value);
    }

    public boolean resolve(Value value, boolean forceJoin) {
        if (this.state == QueueState.REJECTED)
            return false;
        QueueState prevState = this.state;
        this.state = QueueState.FULFILLED;
        if (forceJoin) {
            value = this.defaultValue != null ?
                    this.defaultValue.join(value) : value;
        }
        this.defaultValue = value;
        QueueObject.addValueToCallbacks(this.resolvedCallbacks, value,
                                        forceJoin);
        return prevState == QueueState.PENDING;
    }

    public boolean reject(Value value, boolean forceJoin) {
        if (this.state == QueueState.FULFILLED)
            return false;
        QueueState prevState = this.state;
        this.state = QueueState.REJECTED;
        if (forceJoin) {
            value = this.defaultValue != null ?
                    this.defaultValue.join(value) : value;
        }
        this.defaultValue = value;
        QueueObject.addValueToCallbacks(this.rejectedCallbacks, value,
                                        forceJoin);
        return prevState == QueueState.PENDING;
    }

    /**
     * Updates the arguments of already registered callbacks only
     * if they have been registered with empty arguments.
     */
    private static void addValueToCallbacks(
            CallbackContainer callbacks, Value value,
            boolean forceUpdate) {
        if (callbacks != null)
            callbacks.propagateValue(value, forceUpdate);
    }

    /**
     * Get all object labels of the registered callbacks
     * (both resolved and rejected).
     */
    public Set<ObjectLabel> getCallbackObjectLabels() {
        return Stream
                .concat(
                        this.resolvedCallbacks.getAllCallbacks().stream(),
                        this.rejectedCallbacks.getAllCallbacks().stream()
                )
                .map(x -> x.getCallback().getObjectLabels())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Get all object labels of the arguments of the registered
     * callbacks.
     */
    public Set<ObjectLabel> getArgumentObjectLabels() {
        Set<ObjectLabel> objectLabels = newSet();
        for (CallbackExecutionInfo callbackInfo: this.resolvedCallbacks
                .getAllCallbacks())
            for (Value arg: callbackInfo.getArgs())
                objectLabels.addAll(arg.getObjectLabels());
        for (CallbackExecutionInfo callbackInfo: this.rejectedCallbacks
                .getAllCallbacks())
            for (Value arg: callbackInfo.getArgs())
                objectLabels.addAll(arg.getObjectLabels());
        if (this.defaultValue != null)
            objectLabels.addAll(this.defaultValue.getObjectLabels());
        return objectLabels;
    }

    public Set<CallbackExecutionInfo> getCallbacks() {
        switch (this.state) {
            case FULFILLED:
                return this.resolvedCallbacks.getAllCallbacks();
            case REJECTED:
                return this.rejectedCallbacks.getAllCallbacks();
        }
        return null;
    }

    public Set<List<CallbackExecutionInfo>> getCallbacksOrder() {
        Set<List<CallbackExecutionInfo>> callbacks = null;
        switch (this.state) {
            case FULFILLED:
                callbacks = this.resolvedCallbacks.getCallbacksInOrder();
                break;
            case REJECTED:
                callbacks = this.rejectedCallbacks.getCallbacksInOrder();
                break;
        }
        if (callbacks == null)
            return newSet();
        return callbacks;
    }

    public Set<CallbackExecutionInfo> getPromiseCallbacks() {
        if (this.kind != Kind.PROMISE)
            throw new AnalysisException("Queue object is not a promise");
        switch (this.state) {
            case FULFILLED:
                return this.resolvedCallbacks.getAllCallbacks();
            case REJECTED:
                return this.rejectedCallbacks.getAllCallbacks();
            default:
                return newSet();
        }
    }

    private static CallbackContainer[] joinCallbacks(
            Set<QueueObject> queueObjects,
            Kind kind) {
        CallbackContainer[] callbacks = new CallbackContainer[2];
        CallbackContainer resCallbacks = null;
        CallbackContainer rejCallbacks = null;
        for (QueueObject q: queueObjects) {
                resCallbacks = q.resolvedCallbacks.join(resCallbacks);
                rejCallbacks = q.rejectedCallbacks.join(rejCallbacks);
        }
        callbacks[0] = resCallbacks == null ?
                CallbackContainer.make(kind) : resCallbacks;
        callbacks[1] = rejCallbacks == null ?
                CallbackContainer.make(kind) : rejCallbacks;
        return callbacks;
    }

    private static CallbackContainer[] joinCallbacks(
            Join joinStrategy, Set<QueueObject> queue1,
            Set<QueueObject> jointQueue, Kind kind) {
        CallbackContainer[] callbacks = new CallbackContainer[2];
        switch (joinStrategy) {
            case WEAK:
                return callbacks;
            case LEFT:
                return joinCallbacks(queue1, kind);
            case DEFAULT:
                return joinCallbacks(jointQueue, kind);
        }
        return callbacks;
    }

    private static QueueObject joinByState(QueueState state,
                                           Set<QueueObject> queue1,
                                           Set<QueueObject> queue2,
                                           Join joinStrategy,
                                           Kind kind) {
        Set<QueueObject> filtQueueObjs1 = queue1
                .stream()
                .filter(x -> x.getState() == state)
                .collect(Collectors.toSet());
        Set<QueueObject> filtQueueObjs2 = queue2
                .stream()
                .filter(x -> x.getState() == state)
                .collect(Collectors.toSet());
        Set<QueueObject> joinedObjs = Stream
                .concat(filtQueueObjs1.stream(), filtQueueObjs2.stream())
                .collect(Collectors.toSet());
        if (joinedObjs.isEmpty())
            return null;

        // Merge default value coming from all queue objects.
        Value val = null;
        QueueDependency queueDependency = null;
        Set<ObjectLabel> dependentObjects = newSet();
        for (QueueObject q: joinedObjs) {
            Value defaultValue = q.getDefaultValue();
            if (defaultValue != null)
                val = val == null ? defaultValue : val.join(defaultValue);
            if (state == QueueState.PENDING) {
                queueDependency = QueueDependency.join(
                        queueDependency, q.dependentOn);
                if (joinStrategy == Join.DEFAULT)
                    dependentObjects.addAll(q.dependentObjects);
            }
        }
        CallbackContainer[] callbacks = joinCallbacks(
                joinStrategy, filtQueueObjs1, joinedObjs, kind);
        //if (state == QueueState.FULFILLED) {
            addValueToCallbacks(callbacks[0], val, true);
        //}
        //if (state == QueueState.REJECTED)
            addValueToCallbacks(callbacks[1], val, true);
        return new QueueObject(state, callbacks[0], callbacks[1],
                val, dependentObjects, queueDependency, kind);
    }

    private static void joinWithPending(QueueObject joinedObj,
                                        QueueObject pendingObj,
                                        boolean replacePending) {
        if (joinedObj.resolvedCallbacks == null) {
            joinedObj.resolvedCallbacks = CallbackSet.make();
        }
        if (joinedObj.rejectedCallbacks == null) {
            joinedObj.rejectedCallbacks = CallbackSet.make();
        }
        if (replacePending && pendingObj != null) {
            addValueToCallbacks(pendingObj.resolvedCallbacks,
                                joinedObj.defaultValue,
                                true);
            joinedObj.resolvedCallbacks = joinedObj.resolvedCallbacks
                    .join(pendingObj.resolvedCallbacks);
            addValueToCallbacks(pendingObj.rejectedCallbacks,
                    joinedObj.defaultValue,
                    true);
            joinedObj.rejectedCallbacks = joinedObj.rejectedCallbacks
                    .join(pendingObj.rejectedCallbacks);
        }
    }

    /**
     * Join the given sets of queue objects.
     *
     * This results to a new sets with up to three queue objects.
     * Queue objects are joined based on their state. In other words,
     * the resulting set contains a summary of fulfilled objects,
     * a summary of rejected objects and a summary of pending objects.
     *
     * If @replacePending is `True`, then the resulting set does not
     * contain a summary of pending objects. Instead in joins the
     * resolved and rejected callbacks of pending objects into
     * the rejected or resolved objects.
     *
     * If @joinStrategy is `DEFAULT`, this function also joins
     * the registered callbacks. If @joinStrategy is `LEFT`,
     * the functions keeps the summary of callbacks from @queue1.
     * Otherwise, if the @joinStrategy is `WEAK` it joins only
     * `the defaultValue`.
     */
    public static Set<QueueObject> join(Set<QueueObject> queue1,
                                        Set<QueueObject> queue2,
                                        Join joinStrategy,
                                        boolean replacePending,
                                        Kind kind) {
        if (queue1 == null || queue2 == null)
            throw new AnalysisException(
                    "Trying to join a null-value queue object");
        Set<QueueObject> joinedObjects = newSet();
        QueueObject pendObj = joinByState(
                QueueState.PENDING, queue1, queue2,
                joinStrategy, kind);
        QueueObject fulfilledObj;
        QueueObject rejectedObj;
        fulfilledObj = joinByState(QueueState.FULFILLED, queue1, queue2,
                                joinStrategy, kind);
        if (fulfilledObj != null) {
            joinedObjects.add(fulfilledObj);
            joinWithPending(fulfilledObj, pendObj, replacePending);
        }
        rejectedObj = joinByState(QueueState.REJECTED, queue1, queue2,
                                joinStrategy, kind);
        if (rejectedObj != null) {
            joinedObjects.add(rejectedObj);
            joinWithPending(rejectedObj, pendObj, replacePending);
        }
        if (pendObj == null && joinStrategy == Join.LEFT) {
            // Move callbacks from one object to another.
            if (fulfilledObj != null &&
                    !fulfilledObj.hasCallbacksToRun())
                joinWithPending(fulfilledObj, rejectedObj, true);
            else if (rejectedObj != null &&
                        !rejectedObj.hasCallbacksToRun())
                joinWithPending(rejectedObj, fulfilledObj, true);
        }
        if ((!replacePending && pendObj!= null) || joinedObjects.isEmpty()
                || (pendObj != null && pendObj.isDependent()))
            /* Add a pending object only if it is dependent to another
               or @replacePending is False. */
            joinedObjects.add(pendObj);
        return joinedObjects;
    }

    public boolean isIncluded(Set<QueueObject> queueObjects) {
        boolean isIncluded = false;
        for (QueueObject queueObject : queueObjects) {
            Set<QueueObject> jointObjects = QueueObject.join(
                    Collections.singleton(this),
                    Collections.singleton(queueObject),
                    Join.DEFAULT,
                    false,
                    this.kind);
            if (jointObjects.size() == 1) {
                QueueObject jointObj = jointObjects
                        .iterator().next();
                isIncluded |= jointObj.equals(queueObject);
            }
        }
        return isIncluded;
    }

    public void replaceObjectLabel(ObjectLabel from, ObjectLabel to) {
        Set<ObjectLabel> newDependents = newSet(this.dependentObjects);
        if (newDependents.remove(from))
            newDependents.add(to);
        this.dependentObjects = newDependents;
        if (this.dependentOn.isDependent(from)) {
            Set<ObjectLabel> newDependencies = newSet(
                    this.dependentOn.dependencies);
            newDependencies.remove(from);
            newDependencies.add(to);
            this.dependentOn = new QueueDependency(
                    this.dependentOn.maybeNonDependent, newDependencies);
        }
        this.resolvedCallbacks.replaceObjectLabel(from, to);
        this.rejectedCallbacks.replaceObjectLabel(from, to);
    }

    /**
     * Checks if the current queue object has callbacks ready to be executed.
     */
    public boolean hasCallbacksToRun() {
        return this.state != QueueState.PENDING
                && ((this.state == QueueState.REJECTED
                    && !this.rejectedCallbacks.isEmpty())
                || (this.state == QueueState.FULFILLED
                    && !this.resolvedCallbacks.isEmpty()));
    }

    public void addDependentObject(ObjectLabel objectLabel) {
        this.dependentObjects.add(objectLabel);
    }

    public void makeDependentOn(ObjectLabel objectLabel) {
        this.dependentOn.addDependency(objectLabel);
    }

    public boolean isFulfilled() {
        return this.state == QueueState.FULFILLED;
    }

    public boolean isRejected() {
        return this.state == QueueState.REJECTED;
    }

    @Override
    public QueueObject clone() {
        return new QueueObject(
                this.state,
                this.resolvedCallbacks.clone(),
                this.rejectedCallbacks.clone(),
                this.defaultValue,
                this.dependentObjects,
                this.dependentOn,
                this.kind
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueueObject that = (QueueObject) o;

        if (state != that.state) return false;
        if (resolvedCallbacks != null ? !resolvedCallbacks.equals(that.resolvedCallbacks) : that.resolvedCallbacks != null)
            return false;
        if (rejectedCallbacks != null ? !rejectedCallbacks.equals(that.rejectedCallbacks) : that.rejectedCallbacks != null)
            return false;
        if (defaultValue != null ? !defaultValue.equals(that.defaultValue) : that.defaultValue != null) return false;
        if (dependentObjects != null ? !dependentObjects.equals(that.dependentObjects) : that.dependentObjects != null)
            return false;
        if (dependentOn != null ? !dependentOn.equals(that.dependentOn) : that.dependentOn != null) return false;
        return kind == that.kind;
    }

    @Override
    public int hashCode() {
        int result = state != null ? state.hashCode() : 0;
        result = 31 * result + (resolvedCallbacks != null ? resolvedCallbacks.hashCode() : 0);
        result = 31 * result + (rejectedCallbacks != null ? rejectedCallbacks.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (dependentObjects != null ? dependentObjects.hashCode() : 0);
        result = 31 * result + (dependentOn != null ? dependentOn.hashCode() : 0);
        result = 31 * result + (kind != null ? kind.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String resVal = this.defaultValue == null ? "None" : this.defaultValue
                .toString();
        StringBuilder string = new StringBuilder(
                "State:\t[" + this.state.toString() + " - " + resVal + "]");
        string.append("\nonResolve:\t[").append(this.resolvedCallbacks
                .getAllCallbacks()
                .stream()
                .filter(java.util.Objects::nonNull)
                .map(CallbackExecutionInfo::toString)
                .collect(Collectors.joining(",\n\t\t\t")))
                .append("]");
        string.append("\nonReject:\t[").append(this.rejectedCallbacks
                .getAllCallbacks()
                .stream()
                .filter(java.util.Objects::nonNull)
                .map(CallbackExecutionInfo::toString)
                .collect(Collectors.joining(",\n\t\t\t")))
                .append("]");
        if (!this.dependentOn.maybeNonDependent())
            string.append("\nDependent on:\t")
                    .append(this.dependentOn.toString());
        if (this.dependentObjects != null && !this.dependentObjects.isEmpty())
            string.append("\n!").append(this.dependentObjects.toString());
        return string.toString();
    }

    public boolean isDependent() {
        return !this.dependentOn.maybeNonDependent();
    }

    public boolean isSettled() {
        return !(this.state == QueueState.PENDING);
    }

    public CallbackContainer getResolvedCallbacks() {
        return this.resolvedCallbacks;
    }

    public CallbackContainer getRejectedCallbacks() {
        return rejectedCallbacks;
    }

    public Set<ObjectLabel> getDependentObjects() {
        return this.dependentObjects;
    }

    public QueueState getState() {
        return state;
    }

    public Value getDefaultValue() {
        return defaultValue;
    }

    public void setResolvedCallbacks(CallbackContainer resolvedCallbacks) {
        this.resolvedCallbacks = resolvedCallbacks;
    }

    public void setRejectedCallbacks(CallbackContainer rejectedCallbacks) {
        this.rejectedCallbacks = rejectedCallbacks;
    }

    public enum QueueState {
        PENDING,

        FULFILLED,

        REJECTED
    }

    public static class QueueDependency {

        private boolean maybeNonDependent;

        private Set<ObjectLabel> dependencies;

        private QueueDependency() {
            this.maybeNonDependent = false;
            this.dependencies = newSet();
        }

        QueueDependency(boolean maybeNonDependent,
                               Set<ObjectLabel> dependencies) {
            this.maybeNonDependent = maybeNonDependent;
            this.dependencies = dependencies;
        }

        public static QueueDependency make() {
            return new QueueDependency();
        }

        public static QueueDependency join(QueueDependency dependency1,
                                           QueueDependency dependency2) {
            if (dependency1 == null)
                return dependency2;
            if (dependency2 == null)
                return dependency1;
            boolean mayNonDependent = dependency1.maybeNonDependent()
                    || dependency2.maybeNonDependent();
            Set<ObjectLabel> jointDependencies = newSet();
            jointDependencies.addAll(dependency1.dependencies);
            jointDependencies.addAll(dependency2.dependencies);
            return new QueueDependency(mayNonDependent, jointDependencies);
        }

        void addDependency(ObjectLabel objectLabel) {
            this.dependencies.add(objectLabel);
        }

        boolean maybeNonDependent() {
            return this.maybeNonDependent || this.dependencies.isEmpty();
        }

        boolean isDependent(ObjectLabel objectLabel) {
            return this.dependencies.contains(objectLabel);
        }

        boolean isIncluded(QueueDependency queueDependency) {
            boolean isIncluded = this.maybeNonDependent == queueDependency.maybeNonDependent;
            isIncluded |= this.dependencies
                    .stream()
                    .allMatch(x -> queueDependency.dependencies.contains(x));
            return isIncluded;
        }

        @Override
        public String toString() {
            return (this.maybeNonDependent ? "?" : "@")
                    + Value.makeObject(this.dependencies).toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            QueueDependency that = (QueueDependency) o;

            if (maybeNonDependent != that.maybeNonDependent) return false;
            return dependencies != null ? dependencies.equals(
                    that.dependencies) : that.dependencies == null;
        }

        @Override
        public int hashCode() {
            int result = (maybeNonDependent ? 1 : 0);
            result = 31 * result
                    + (dependencies != null ? dependencies.hashCode() : 0);
            return result;
        }

        public Set<ObjectLabel> getDependencies() {
            return dependencies;
        }
    }

    public enum Join {
        DEFAULT,
        LEFT,
        WEAK
    }

    public enum Kind {
        PROMISE,
        ASYNC_IO,
        TIMER
    }
}
