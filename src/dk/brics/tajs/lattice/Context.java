/*
 * Copyright 2009-2018 Aarhus University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.brics.tajs.lattice;

import dk.brics.tajs.options.Options;
import dk.brics.tajs.solver.IContext;
import dk.brics.tajs.util.Canonicalizer;
import dk.brics.tajs.util.DeepImmutable;

import java.util.List;
import java.util.Map;

/**
 * Context for context sensitive analysis.
 * Immutable.
 */
public final class Context implements IContext<Context>, DeepImmutable {

    /**
     * Cached hashcode for immutable instance.
     */
    private final int hashcode;

    private final Value thisval;

    /**
     * Values of function arguments at function entry, or null if none.
     */
    private final ContextArguments funArgs;

    /**
     * Values of special registers, or null if none.
     */
    private final Map<Integer, Value> specialRegs;

    /**
     * Local context: e.g. number of times loops have been unrolled, or null if none.
     */
    private final LocalContext localContext;

    /**
     * Local context information at the function entry, or null if none.
     * (Unlike the other components of this bean, {@link #localContext} can change without interacting with the call graph. This field is used to restore the original context without querying the call graph.)
     */
    private final LocalContext localContextAtEntry;

    /**
     * Value of the queue object on which a callback is registered.
     */
    private final Value queueObject;

    /**
     * Object label of the queue object that is fulfilled
     * with the return value of the callback.
     */
    private final Value dependentQueueObject;

    private final List<Value> resolveValue;

    /**
     * Constructs a new context object.
     */
    private Context(Value thisval, ContextArguments funArgs, Map<Integer, Value> specialRegs,
                    LocalContext localContext, LocalContext localContextAtEntry,
                    Value queueObject, Value dependentQueueObject,
                    List<Value> resolveValue) {
        // ensure canonical representation of empty maps
        if (localContext != null && localContext.getQualifiers().isEmpty()) {
            localContext = null;
        }
        if (localContextAtEntry != null && localContextAtEntry.getQualifiers().isEmpty()) {
            localContextAtEntry = null;
        }
        if (specialRegs != null && specialRegs.isEmpty()) {
            specialRegs = null;
        }
        this.thisval = thisval;
        this.funArgs = funArgs;
        this.specialRegs = specialRegs;
        this.localContext = localContext;
        this.localContextAtEntry = localContextAtEntry;
        this.queueObject = queueObject;
        this.dependentQueueObject = dependentQueueObject;
        this.resolveValue = resolveValue;

        int hashcode = 1;
        hashcode = 31 * hashcode + (thisval != null ? thisval.hashCode() : 0);
        hashcode = 31 * hashcode + (funArgs != null ? funArgs.hashCode() : 0);
        hashcode = 31 * hashcode + (specialRegs != null ? specialRegs.hashCode() : 0);
        hashcode = 31 * hashcode + (localContext != null ? localContext.hashCode() : 0);
        hashcode = 31 * hashcode + (queueObject != null ? queueObject.hashCode() : 0);
        hashcode = 31 * hashcode + (dependentQueueObject != null ? dependentQueueObject.hashCode() : 0);
        hashcode = 31 * hashcode + (resolveValue != null ? resolveValue.hashCode() : 0);
        this.hashcode = hashcode;
    }

    public static Context make(Value thisval, ContextArguments funArgs, Map<Integer, Value> specialRegs,
                               LocalContext localContext, LocalContext localContextAtEntry,
                               Value queueObject, Value dependentQueueObject,
                               List<Value> resolveValue) {
        return Canonicalizer.get().canonicalize(new Context(
                thisval, funArgs, specialRegs, localContext, localContextAtEntry,
                queueObject, dependentQueueObject, resolveValue));
    }

    /**
     * Returns the this-value.
     */
    public Value getThisVal() {
        return thisval;
    }

    /**
     * Returns the function arguments.
     */
    public ContextArguments getFunArgs() {
        return funArgs;
    }

    /**
     * Returns the special registers map.
     */
    public Map<Integer, Value> getSpecialRegisters() {
        return specialRegs;
    }

    /**
     * Returns the local context information.
     */
    public LocalContext getLocalContext() {
        return localContext;
    }

    /**
     * Returns the local context at entry information.
     */
    public LocalContext getLocalContextAtEntry() {
        return localContextAtEntry;
    }

    public Value getQueueObject() {
        return queueObject;
    }

    public Value getDependentQueueObject() {
        return dependentQueueObject;
    }

    public List<Value> getResolveValue() {
        return resolveValue;
    }

    /**
     * Reconstructs the context at function or for-in entry.
     */
    @Override
    public Context makeEntryContext() {
        // reconstruct localContext from localContextAtEntry (all other properties are unchanged within a function or for-in body)
        if (localContextAtEntry != null && localContextAtEntry.equals(localContext)) {
            return this;
        }
        return make(thisval, funArgs, specialRegs, localContextAtEntry, localContextAtEntry,
                    queueObject, dependentQueueObject, resolveValue);
    }

    /** Creates a callback context from the given context. */
    public CallbackContext toCallbackContext() {
        Value qObj = null;
        Value dQObj = null;
        if (!Options.get().isQRSensitivityDisabled()) {
            qObj = this.queueObject;
            dQObj = this.dependentQueueObject;
        }
        List<Value> args = null;
        if (Options.get().isCallbackParameterSensitivityEnabled())
            args = resolveValue;
        // TODO Currently, we do not support parameter sensitivity of callbacks.
        return new CallbackContext(qObj, dQObj, args, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Context))
            return false;
        Context c = (Context) obj;
        if (this.hashcode != c.hashcode) {
            return false;
        }
        if ((thisval == null) != (c.thisval == null))
            return false;
        if (thisval != null && !thisval.equals(c.thisval)) // using collection equality
            return false;
        if ((funArgs == null) != (c.funArgs == null))
            return false;
        if (funArgs != null && !funArgs.equals(c.funArgs)) // using collection equality
            return false;
        if ((specialRegs == null) != (c.specialRegs == null))
            return false;
        if (specialRegs != null && !specialRegs.equals(c.specialRegs))
            return false;
        if ((localContext == null) != (c.localContext == null))
            return false;
        if ((queueObject == null) != (c.queueObject == null))
            return false;
        if (queueObject != null && !queueObject.equals(c.queueObject))
            return false;
        if ((dependentQueueObject == null) != (c.dependentQueueObject == null))
            return false;
        if (dependentQueueObject != null && !dependentQueueObject.equals(
                c.dependentQueueObject))
            return false;
        if (resolveValue != null && !resolveValue.equals(c.resolveValue))
            return false;
        return !(localContext != null && !localContext.equals(c.localContext));
    }

    @Override
    public int hashCode() {
        return this.hashcode;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("{");
        boolean any = false;
        if (thisval != null) {
            s.append("this=").append(thisval);
            any = true;
        }
        if (funArgs != null) {
            if (any)
                s.append(", ");
            s.append("funArgs=").append(funArgs);
            any = true;
        }
        if (specialRegs != null) {
            if (any)
                s.append(", ");
            s.append("specialRegs=").append(specialRegs);
            any = true;
        }
        if (localContext != null) {
            if (any)
                s.append(", ");
            s.append("localContext=").append(localContext);
            any = true;
        }
        if (queueObject != null) {
            if (any)
                s.append(", ");
            s.append("Q=").append(queueObject);
            any = true;
        }
        if (dependentQueueObject != null) {
            if (any)
                s.append(", ");
            s.append("R=").append(dependentQueueObject);
        }
        s.append("}");
        return s.toString();
    }
}
