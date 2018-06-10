package dk.brics.tajs.analysis;

import dk.brics.tajs.lattice.NativeCallbackContext;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.util.AnalysisException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newSet;


public class CallbackCallInfo {

    private Set<ObjectLabel> thisObjs;

    private Set<ObjectLabel> queueObjects;

    private Value callback;

    private List<Value> args;

    private Set<ObjectLabel> dependentQueueObjects;

    private boolean implicit;

    private NativeCallbackContext nativeContext;

    public CallbackCallInfo(Value callback, List<Value> args,
                            Set<ObjectLabel> thisObjs,
                            Set<ObjectLabel> queueObjects,
                            Set<ObjectLabel> dependentQueueObjects,
                            boolean implicit,
                            NativeCallbackContext nativeContext) {
        this.callback = callback;
        this.args = args;
        this.thisObjs = thisObjs;
        this.queueObjects = queueObjects;
        this.dependentQueueObjects = dependentQueueObjects;
        this.implicit = implicit;
        this.nativeContext = nativeContext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallbackCallInfo that = (CallbackCallInfo) o;

        if (implicit != that.implicit) return false;
        if (thisObjs != null ? !thisObjs.equals(that.thisObjs) : that.thisObjs != null) return false;
        if (queueObjects != null ? !queueObjects.equals(that.queueObjects) : that.queueObjects != null) return false;
        if (callback != null ? !callback.equals(that.callback) : that.callback != null) return false;
        if (args != null ? !args.equals(that.args) : that.args != null) return false;
        if (dependentQueueObjects != null ? !dependentQueueObjects.equals(that.dependentQueueObjects) : that.dependentQueueObjects != null)
            return false;
        return nativeContext != null ? nativeContext.equals(that.nativeContext) : that.nativeContext == null;
    }

    @Override
    public int hashCode() {
        int result = thisObjs != null ? thisObjs.hashCode() : 0;
        result = 31 * result + (queueObjects != null ? queueObjects.hashCode() : 0);
        result = 31 * result + (callback != null ? callback.hashCode() : 0);
        result = 31 * result + (args != null ? args.hashCode() : 0);
        result = 31 * result + (dependentQueueObjects != null ? dependentQueueObjects.hashCode() : 0);
        result = 31 * result + (implicit ? 1 : 0);
        result = 31 * result + (nativeContext != null ? nativeContext.hashCode() : 0);
        return result;
    }

    public static CallbackCallInfo join(
            CallbackCallInfo call1,
            CallbackCallInfo call2) {
        //if (!call1.callback.equals(call2.callback))
        //    throw new AnalysisException(
        //            "Trying to merge the arguments of different callbacks");
        List<Value> jointArgs = newList();
        int argSize1 = call1.args.size();
        int argSize2 = call2.args.size();
        int size = argSize1 > argSize2 ?
                argSize1 : argSize2;
        for (int i = 0; i < size; i++) {
            if (i >= argSize1)
                jointArgs.add(call2.args.get(i));
            else if (i >= argSize2)
                jointArgs.add(call1.args.get(i));
            else
                jointArgs.add(Value.join(call1.args.get(i), call2.args.get(i)));
        }
        Set<ObjectLabel> queueObjects = newSet();
        queueObjects.addAll(call1.queueObjects);
        queueObjects.addAll(call2.queueObjects);
        Set<ObjectLabel> dependentQueueObjects = newSet();
        dependentQueueObjects.addAll(call1.dependentQueueObjects);
        dependentQueueObjects.addAll(call2.dependentQueueObjects);
        Set<ObjectLabel> thisObjs = newSet();
        thisObjs.addAll(call1.thisObjs);
        thisObjs.addAll(call2.thisObjs);
        return new CallbackCallInfo(
                call1.callback,
                jointArgs,
                thisObjs,
                queueObjects,
                dependentQueueObjects,
                call1.implicit,
                null); // We only merge non-native functions.
    }

    public void replaceObjectLabel(ObjectLabel singleton,
                                   ObjectLabel summary) {
        Set<ObjectLabel> newQueueObjects = new HashSet<>(
                this.queueObjects);
        Set<ObjectLabel> newDQueueObjects = new HashSet<>(
                this.dependentQueueObjects);
        if (newQueueObjects.remove(singleton))
            newQueueObjects.add(summary);
        if (newDQueueObjects.remove(singleton))
            newDQueueObjects.add(summary);
        this.queueObjects = newQueueObjects;
        this.dependentQueueObjects = newDQueueObjects;
    }

    public Set<ObjectLabel> getThisObjs() {
        return thisObjs;
    }

    public Set<ObjectLabel> getQueueObjects() {
        return queueObjects;
    }

    public Value getCallback() {
        return callback;
    }

    public List<Value> getArgs() {
        return args;
    }

    public Set<ObjectLabel> getDependentQueueObjects() {
        return dependentQueueObjects;
    }

    public boolean isImplicit() {
        return implicit;
    }

    public NativeCallbackContext getNativeContext() {
        return nativeContext;
    }
}
