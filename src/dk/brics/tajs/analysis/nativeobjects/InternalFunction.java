package dk.brics.tajs.analysis.nativeobjects;

import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.HostAPIs;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.HostAPI;
import dk.brics.tajs.lattice.HostObject;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;


/**
 * This class represents internal functions as specified in
 * ECMAScript Specification.
 *
 * These functions are not directly transparent to the programmer.
 */
public abstract class InternalFunction implements HostObject {

    @Override
    public HostAPI getAPI() {
        return HostAPIs.ECMASCRIPT_INTERNAL;
    }

    public abstract Value evaluate(State state, FunctionCalls.CallInfo call,
                          Solver.SolverInterface c);
}
