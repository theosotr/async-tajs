package dk.brics.tajs.lattice;

import java.util.List;
import java.util.Map;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newMap;

public class Callback {
    private Map<ObjectLabel, List<Value>> callContext;

    public Callback(ObjectLabel callback) {
        this.callContext = newMap();
        List<Value> args = newList();
        this.callContext.put(callback, args);
    }

    public void addArg(ObjectLabel callback, Value value) {
        List<Value> args = this.callContext.get(callback);
        // TODO sanitize
        args.add(value);
    }

    public void updateArg(ObjectLabel callback, Value value) {
        this.addArg(callback, value);
    }
}
