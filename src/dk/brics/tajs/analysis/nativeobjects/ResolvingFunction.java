package dk.brics.tajs.analysis.nativeobjects;

import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;

import java.util.Set;


public class ResolvingFunction extends InternalFunction {

    private ObjectLabel promise;

    private Kind kind;

    /**
     * This keeps the object which has the function in which
     * the resolving function is passed as argument.
     *
     * Typically, this is defined only to thenable objects.
     */
    private ObjectLabel resolvedFrom;

    /**
     * This set keeps the callee functions where we
     * pass the resolving functions as arguments.
     */
    private Set<Function> calleeFuns;

    ResolvingFunction(ObjectLabel promise, Kind kind,
                      ObjectLabel resolvedFrom,
                      Set<Function> calleeFuns) {
        this.promise = promise;
        this.kind = kind;
        this.resolvedFrom = resolvedFrom;
        this.calleeFuns = calleeFuns;
    }

    @Override
    /*
      This method evaluates the internal functions which are
      used to resolve and reject a promise.

      These functions are passed as arguments in the promise
      executor.
     */
    public Value evaluate(State state, FunctionCalls.CallInfo call,
                          Solver.SolverInterface c) {
        switch (this.kind) {
            case RESOLVE:
                return JSPromise.evaluateInternalResolve(state, call, c);
            case REJECT:
                return JSPromise.evaluateInternalReject(state, call);
        }
        return null;
    }

    public enum Kind {
        RESOLVE("resolve"),
        REJECT("reject");

        private String name;

        Kind(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    public ObjectLabel getPromise() {
        return this.promise;
    }

    public Kind getKind() {
        return this.kind;
    }

    public ObjectLabel getResolvedFrom() {
        return resolvedFrom;
    }

    public Set<Function> getCalleeFuns() {
        return calleeFuns;
    }

    public String toString() {
        return this.kind.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResolvingFunction that = (ResolvingFunction) o;

        if (promise != null ? !promise.equals(that.promise) : that.promise != null) return false;
        if (kind != that.kind) return false;
        if (resolvedFrom != null ? !resolvedFrom.equals(that.resolvedFrom) : that.resolvedFrom != null) return false;
        return calleeFuns != null ? calleeFuns.equals(that.calleeFuns) : that.calleeFuns == null;
    }

    @Override
    public int hashCode() {
        int result = promise != null ? promise.hashCode() : 0;
        result = 31 * result + (kind != null ? kind.hashCode() : 0);
        result = 31 * result + (resolvedFrom != null ? resolvedFrom.hashCode() : 0);
        result = 31 * result + (calleeFuns != null ? calleeFuns.hashCode() : 0);
        return result;
    }
}
