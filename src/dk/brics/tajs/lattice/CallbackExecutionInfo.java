package dk.brics.tajs.lattice;

import dk.brics.tajs.analysis.InitialStateBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newList;

/**
 * This class describes the context on which a callback should be
 * executed.
 */
public class CallbackExecutionInfo {

    /** Object label of this object. */
    private ObjectLabel thisObj;

    /**
     * Object label of the queueObject which should be resolved or
     * rejected depending on the execution of this callback.
     */
    private ObjectLabel queueObject;

    /** Value of this callback */
    private Value callback;

    /** List of arguments to be called with */
    private List<Value> args;

    /**
     * True if the queue object (which is represented by `queueObject` field)
     * should be resolved as the return value of this callback or not.
     */
    private boolean implicit;

    CallbackExecutionInfo(ObjectLabel thisObj, ObjectLabel queueObject,
                          Value callback, boolean implicit) {
        this.thisObj = thisObj;
        this.queueObject = queueObject;
        this.callback = callback;
        this.args = newList();
        this.implicit = implicit;
    }

    public static CallbackExecutionInfo make(Value callback,
                                             ObjectLabel thisObj, ObjectLabel queueObj, boolean implicit) {
        if (thisObj == null)
            thisObj = InitialStateBuilder.GLOBAL;
        return new CallbackExecutionInfo(
                thisObj, queueObj, callback, implicit);
    }

    public void addArg(Value arg) {
        if (this.args == null) {
            this.args = newList();
            this.args.add(arg);
        } else
            this.args.add(arg);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallbackExecutionInfo that = (CallbackExecutionInfo) o;

        if (implicit != that.implicit) return false;
        if (thisObj != null ? !thisObj.equals(that.thisObj) : that.thisObj != null) return false;
        if (queueObject != null ? !queueObject.equals(that.queueObject) : that.queueObject != null) return false;
        if (callback != null ? !callback.equals(that.callback) : that.callback != null) return false;
        return args != null ? args.equals(that.args) : that.args == null;
    }

    @Override
    public int hashCode() {
        int result = thisObj != null ? thisObj.hashCode() : 0;
        result = 31 * result + (queueObject != null ? queueObject.hashCode() : 0);
        result = 31 * result + (callback != null ? callback.hashCode() : 0);
        result = 31 * result + (args != null ? args.hashCode() : 0);
        result = 31 * result + (implicit ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(
                "{this: " + this.thisObj.toString());
        string.append(", queueObj: ").append(this.queueObject.toString());
        string.append(", callback: ").append(this.callback.toString());
        if (args != null)
            string.append("(").append(args
                    .stream()
                    .filter(java.util.Objects::nonNull)
                    .map(Value::toString)
                    .collect(Collectors.joining(", "))).append(")");
        string.append(", implicit: ").append(this.implicit)
                .append("}");
        return string.toString();
    }

    @Override
    public CallbackExecutionInfo clone() {
        return new CallbackExecutionInfo(
                this.thisObj,
                this.queueObject,
                this.callback,
                this.implicit);
    }

    public void setArgs(List<Value> args) {
        this.args = args;
    }

    public ObjectLabel getThisObj() {
        return thisObj;
    }

    public ObjectLabel getQueueObject() {
        return queueObject;
    }

    public Value getCallback() {
        return callback;
    }

    public List<Value> getArgs() {
        return args;
    }

    public boolean isImplicit() {
        return implicit;
    }
}
