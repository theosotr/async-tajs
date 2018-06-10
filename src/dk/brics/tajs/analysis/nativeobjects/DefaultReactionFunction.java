package dk.brics.tajs.analysis.nativeobjects;

import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;


public class DefaultReactionFunction extends InternalFunction {

    private Kind kind;

    public DefaultReactionFunction(Kind kind) {
        this.kind = kind;
    }

    public enum Kind {
        ON_FULFILL("on_fulfill"),

        ON_REJECT("on_reject");

        private String name;

        Kind(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    public Value evaluate(State state, FunctionCalls.CallInfo call,
                          Solver.SolverInterface c) {
        return FunctionCalls.readParameter(call, state, 0);
    }

    public String toString() {
        return this.kind.toString();
    }

    public Kind getKind() {
        return kind;
    }
}
