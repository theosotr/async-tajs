package dk.brics.tajs.lattice;


public final class QueueContext {

    private ObjectLabel queueObject;

    private boolean implicit;

    QueueContext(ObjectLabel queueObject, boolean implicit) {
        this.queueObject = queueObject;
        this.implicit = implicit;
    }

    public void replaceObjectLabel(ObjectLabel singleton,
                                   ObjectLabel summary) {
        if (this.queueObject.equals(singleton))
            this.queueObject = summary;
    }

    public ObjectLabel getQueueObject() {
        return queueObject;
    }

    public boolean isImplicit() {
        return implicit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueueContext that = (QueueContext) o;

        if (implicit != that.implicit) return false;
        return queueObject != null ? queueObject.equals(that.queueObject)
                : that.queueObject == null;
    }

    @Override
    public int hashCode() {
        int result = queueObject != null ? queueObject.hashCode() : 0;
        result = 31 * result + (implicit ? 1 : 0);
        return result;
    }
}
