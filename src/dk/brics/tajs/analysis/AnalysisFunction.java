package dk.brics.tajs.analysis;

import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.lattice.HostObject;

public class AnalysisFunction {

    private Function userFunction;

    private HostObject nativeFunction;

    public AnalysisFunction(Function userFunction) {
        this.userFunction = userFunction;
    }

    public AnalysisFunction(HostObject nativeFunction) {
        this.nativeFunction = nativeFunction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalysisFunction that = (AnalysisFunction) o;

        if (userFunction != null ? !userFunction.equals(that.userFunction) : that.userFunction != null) return false;
        return nativeFunction != null ? nativeFunction.equals(that.nativeFunction) : that.nativeFunction == null;
    }

    @Override
    public int hashCode() {
        int result = userFunction != null ? userFunction.hashCode() : 0;
        result = 31 * result + (nativeFunction != null ? nativeFunction.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (userFunction != null)
            return userFunction.toString();
        else
            return nativeFunction.toString();
    }

    public boolean isNative() {
        return nativeFunction != null;
    }

    public Function getUserFunction() {
        return userFunction;
    }

    public HostObject getNativeFunction() {
        return nativeFunction;
    }
}
