package dk.brics.tajs.analysis.nativeobjects;

import dk.brics.tajs.analysis.Exceptions;
import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;

import java.util.List;

import static dk.brics.tajs.util.Collections.newList;

public class AsyncIO {

    private AsyncIO() { }

    public static void addCallback(State state,
                                   FunctionCalls.CallInfo call,
                                   Solver.SolverInterface c) {
        Value callback = FunctionCalls.readParameter(call, state, 0);
        boolean maybeNonFun = callback.isMaybeNonFunction();
        if (maybeNonFun && !callback.isMaybeFunction()) {
            Exceptions.throwTypeError(c);
            return;
        }
        if (maybeNonFun)
            Exceptions.throwTypeError(c);
        callback = callback.restrictToFunctions();
        List<Value> args = newList();
        int nargs = call.getNumberOfArgs();

        // At this point, we read any additional parameters passed in
        // the callback.
        for (int i = 1; i < nargs; i++)
            args.add(FunctionCalls.readParameter(call, state, i));

        state.onResolve(InitialStateBuilder.ASYNC_IO,
                callback, InitialStateBuilder.GLOBAL,
                InitialStateBuilder.ASYNC_IO, args, false);
    }
}
