package dk.brics.tajs.analysis.nativeobjects;

import dk.brics.tajs.analysis.Exceptions;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.solver.Message;

import java.util.List;

import static dk.brics.tajs.util.Collections.newList;

/**
 * Class for evaluating timers, e.g. `setTimeout()`.
 */
public class JSTimers {

    private JSTimers() { }

    public static Value evaluate(ECMAScriptObjects nativeobject,
                                 FunctionCalls.CallInfo call,
                                 Solver.SolverInterface c) {
        State state = c.getState();
        switch (nativeobject) {
            case SET_TIMEOUT:
                return evaluateSetTimeout(state, call, c);
        }
        return null;
    }

    private static Value evaluateSetTimeout(State state,
                                            FunctionCalls.CallInfo call,
                                            Solver.SolverInterface c) {
        Value callback = FunctionCalls.readParameter(call, state, 0);
        boolean maybeNonFun = callback.isMaybeNonFunction();
        if (maybeNonFun && !callback.isMaybeFunction()) {
            c.getMonitoring().addMessage(
                    call.getSourceNode(), Message.Status.CERTAIN,
                    Message.Severity.HIGH,
                    "Callback of setTimeout must be a function");
            Exceptions.throwTypeError(c);
            return Value.makeNone();
        }
        if (maybeNonFun) {
            c.getMonitoring().addMessage(
                    call.getSourceNode(), Message.Severity.HIGH,
                    "Callback of setTimeout must be a function");
            Exceptions.throwTypeError(c);
        }
        callback = callback.restrictToFunctions();
        List<Value> args = newList();
        int nargs = call.getNumberOfArgs();

        // At this point, we read any additional parameters passed in
        // the callback.
        for (int i = 2; i < nargs; i++)
            args.add(FunctionCalls.readParameter(call, state, i));

        state.onResolve(InitialStateBuilder.SET_TIMEOUT_QUEUE_OBJ,
                callback, InitialStateBuilder.GLOBAL,
                InitialStateBuilder.SET_TIMEOUT_QUEUE_OBJ, args, false);
        return Value.makeAnyNum();
    }
}
