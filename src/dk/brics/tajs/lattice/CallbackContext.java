package dk.brics.tajs.lattice;

import dk.brics.tajs.analysis.CallbackCallInfo;
import dk.brics.tajs.options.Options;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Context specifically targeted for callbacks.
 */
public class CallbackContext {

    /** Queue object on which callback is registered. */
    private Value queueObject;

    /** Queue object that is fulfilled with the return value of callback. */
    private Value dependentQueueObject;

    /** Arguments of callback. */
    private List<Value> args;

    /** Context for distinguishing native functions. */
    private NativeCallbackContext nativeContext;

    public CallbackContext(Value queueObject, Value dependentQueueObject,
                           List<Value> args, NativeCallbackContext nativeContext) {
        this.queueObject = queueObject;
        this.dependentQueueObject = dependentQueueObject;
        this.args = args;
        this.nativeContext = nativeContext;
    }

    public CallbackContext clone() {
        return new CallbackContext(
                queueObject,
                dependentQueueObject,
                args,
                nativeContext);
    }

    public static CallbackContext makeCallbackContext(CallbackCallInfo callback) {
        Value callbackVal = callback.getCallback();
        ObjectLabel objectLabel = callbackVal.getObjectLabelUnique();
        List<Value> args = null;

        if (objectLabel.isHostObject())
            // If function is native, we use the native context for distinguishing
            // the different invocations of it.
            return new CallbackContext(Value.makeObject(callback.getQueueObjects()),
                                       Value.makeObject(callback.getDependentQueueObjects()),
                                       null,
                                       callback.getNativeContext());

        Value dQueueObj = null;
        Value queueObj = null;
        if (!Options.get().isQRSensitivityDisabled() || objectLabel.isHostObject()) {
            dQueueObj = Value.makeObject(callback.getDependentQueueObjects());
            queueObj = Value.makeObject(callback.getQueueObjects());
        }

        if (Options.get().isCallbackParameterSensitivityEnabled())
            args = callback.getArgs();

        return new CallbackContext(queueObj, dQueueObj, args, null);
    }

    public void replaceObjectLabels(ObjectLabel singleton,
                                    ObjectLabel summary) {

        if (queueObject != null) {
            Set<ObjectLabel> newObjectLabels = new HashSet<>(
                    queueObject.getObjectLabels());
            if (newObjectLabels.remove(singleton))
                newObjectLabels.add(summary);
            queueObject = Value.makeObject(newObjectLabels);
        }
        if (dependentQueueObject != null) {
            Set<ObjectLabel> newDObjectLabels = new HashSet<>(
                    dependentQueueObject.getObjectLabels());
            if (newDObjectLabels.remove(singleton))
                newDObjectLabels.add(summary);
            dependentQueueObject = Value.makeObject(newDObjectLabels);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallbackContext that = (CallbackContext) o;

        if (queueObject != null ? !queueObject.equals(that.queueObject) : that.queueObject != null) return false;
        if (dependentQueueObject != null ? !dependentQueueObject.equals(that.dependentQueueObject) : that.dependentQueueObject != null)
            return false;
        if (args != null ? !args.equals(that.args) : that.args != null) return false;
        return nativeContext != null ? nativeContext.equals(that.nativeContext) : that.nativeContext == null;
    }

    @Override
    public int hashCode() {
        int result = queueObject != null ? queueObject.hashCode() : 0;
        result = 31 * result + (dependentQueueObject != null ? dependentQueueObject.hashCode() : 0);
        result = 31 * result + (args != null ? args.hashCode() : 0);
        result = 31 * result + (nativeContext != null ? nativeContext.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (nativeContext != null)
            return nativeContext.toString();
        StringBuilder string = new StringBuilder();
        boolean any = false;
        if (queueObject != null) {
            string.append("Q=").append(queueObject);
            any = true;
        }
        if (dependentQueueObject != null) {
            if (any)
                string.append(", ");
            string.append("R=").append(dependentQueueObject);
        }
        if (args != null) {
            if (any)
                string.append(", ");
            string.append("A=").append(args.isEmpty() ?
                    "[]" : args.get(0));
        }
        return string.toString();
    }

    public Value getQueueObject() {
        return queueObject;
    }

    public Value getDependentQueueObject() {
        return dependentQueueObject;
    }

    public List<Value> getArgs() {
        return args;
    }
}
