package dk.brics.tajs.lattice;

public class CallbackDescription {
    private ObjectLabel queueObject;

    private QueueObject.QueueState state;

    private Value callback;

    private ObjectLabel dependentQueueObject;

    private Value settledBy;

    public CallbackDescription(ObjectLabel queueObject, QueueObject.QueueState state,
                               Value callback, ObjectLabel dependentQueueObject,
                               Value settledBy) {
        this.queueObject = queueObject;
        this.state = state;
        this.callback = callback;
        this.dependentQueueObject = dependentQueueObject;
        this.settledBy = settledBy;
    }

    public CallbackDescription clone() {
        return new CallbackDescription(
                queueObject,
                state,
                callback,
                dependentQueueObject,
                settledBy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallbackDescription that = (CallbackDescription) o;

        if (queueObject != null ? !queueObject.equals(that.queueObject) : that.queueObject != null) return false;
        if (state != that.state) return false;
        if (callback != null ? !callback.equals(that.callback) : that.callback != null) return false;
        if (dependentQueueObject != null ? !dependentQueueObject.equals(that.dependentQueueObject) : that.dependentQueueObject != null)
            return false;
        return settledBy != null ? settledBy.equals(that.settledBy) : that.settledBy == null;
    }

    @Override
    public int hashCode() {
        int result = queueObject != null ? queueObject.hashCode() : 0;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (callback != null ? callback.hashCode() : 0);
        result = 31 * result + (dependentQueueObject != null ? dependentQueueObject.hashCode() : 0);
        result = 31 * result + (settledBy != null ? settledBy.hashCode() : 0);
        return result;
    }

    public void replaceObjectLabel(ObjectLabel singleton,
                                   ObjectLabel summary) {
        if (this.queueObject.equals(singleton))
            this.queueObject = summary;
        if (this.dependentQueueObject.equals(singleton))
            this.dependentQueueObject = summary;
    }

    public ObjectLabel getQueueObject() {
        return queueObject;
    }

    public QueueObject.QueueState getState() {
        return state;
    }

    public Value getCallback() {
        return callback;
    }

    public ObjectLabel getDependentQueueObject() {
        return dependentQueueObject;
    }

    public Value getSettledBy() {
        return settledBy;
    }

    public void setQueueObject(ObjectLabel queueObject) {
        this.queueObject = queueObject;
    }

    public void setDependentQueueObject(ObjectLabel dependentQueueObject) {
        this.dependentQueueObject = dependentQueueObject;
    }
}
