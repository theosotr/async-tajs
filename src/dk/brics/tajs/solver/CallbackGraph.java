package dk.brics.tajs.solver;

import dk.brics.tajs.analysis.AnalysisFunction;
import dk.brics.tajs.analysis.CallbackCallInfo;
import dk.brics.tajs.analysis.InitialStateBuilder;
import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.lattice.CallbackContext;
import dk.brics.tajs.lattice.HostObject;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajs.util.Chain;
import dk.brics.tajs.util.Pair;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newMap;
import static dk.brics.tajs.util.Collections.newSet;

/**
 * Graph which defines execution order of callbacks.
 *
 * It can be seen as a chain where the execution order of callbacks
 * of the same level is not known to the analysis.
 */
public class CallbackGraph {

    private Map<CallbackGraphNode, Set<CallbackGraphEdge>> callbackGraph;

    private Map<CallbackGraphNode, CallbackCallInfo> callInfo;

    private Map<CallbackGraphNode, State> entryStates;

    private Map<CallbackGraphNode, Boolean> visited;

    private Map<CallbackGraphNode, Boolean> analyzedFunctions;

    private List<SchedulePlan> schedulePlans;

    private Chain<CallbackCallInfo> currPromiseChain;

    private Chain<CallbackCallInfo> currTimeIOChain;

    private List<SchedulePlan> cacheSchedulePlans;

    public CallbackGraph() {
        this.callbackGraph = newMap();
        this.callInfo = newMap();
        this.entryStates = new HashMap<>();
        this.visited = newMap();
        this.analyzedFunctions = newMap();
        this.schedulePlans = newList();
        this.currPromiseChain = null;
        this.currTimeIOChain = null;
        this.cacheSchedulePlans = newList();
    }

    public void preBuild() {
        this.callbackGraph = newMap();
        for (SchedulePlan s : schedulePlans) {
            Chain<CallbackCallInfo> chain;
            if (s.isPromise)
                chain = currPromiseChain.getSubChain(s.startIndex, s.endIndex);
            else
                chain = currTimeIOChain.getSubChain(s.startIndex, s.endIndex);
            toCallbackGraph(chain);
        }
    }

    public boolean isAnalyzed(Function function, CallbackContext callbackContext) {
        Boolean isAnalyzed = this.isAnalyzed(
                new CallbackGraphNode(new AnalysisFunction(function), callbackContext));
        return isAnalyzed != null && isAnalyzed;
    }

    public boolean isAnalyzed(HostObject function, CallbackContext callbackContext) {
        Boolean isAnalyzed = this.isAnalyzed(
                new CallbackGraphNode(new AnalysisFunction(function), callbackContext));
        return isAnalyzed != null && isAnalyzed;
    }

    private Boolean isAnalyzed(CallbackGraphNode node) {
        return this.analyzedFunctions.get(node);
    }

    public boolean isVisited(CallbackGraphNode node) {
        return this.visited.get(node);
    }

    public void markAnalyzed(Function function, CallbackContext callbackContext) {
        this.markAnalyzed(new CallbackGraphNode(
                new AnalysisFunction(function), callbackContext));
    }

    public void markAnalyzed(HostObject function, CallbackContext callbackContext) {
        this.markAnalyzed(new CallbackGraphNode(
                new AnalysisFunction(function), callbackContext));
    }

    private void markAnalyzed(CallbackGraphNode node) {
        this.analyzedFunctions.put(node, true);
    }

    public void markVisited(CallbackGraphNode node) {
        this.visited.put(node, true);
    }

    public void resetStates() {
        for (CallbackGraphNode node : this.callbackGraph.keySet()) {
            this.entryStates.put(node, null);
            this.visited.put(node, false);
        }
    }

    public boolean propagateState(State state, CallbackGraphNode node) {
        State currentState = this.entryStates.get(node);
        if (currentState == null) {
            this.entryStates.put(node, state.clone());
            return false;
        }
        currentState = currentState.clone();
        boolean changed = currentState.propagate(state, false);
        this.entryStates.put(node, currentState);
        return changed;
    }

    public State getCallbackState(CallbackGraphNode node,
                                  State state) {
        State currentState = this.entryStates.get(node);
        if (currentState == null)
            return state;
        return currentState;
    }

    public Chain<CallbackCallInfo> getDiffPromises(
            Chain<CallbackCallInfo> callbacks) {
        if (currPromiseChain == null) {
            if (callbacks == null)
                return null;
            this.cacheSchedulePlans.add(
                    new SchedulePlan(1, callbacks.size(), true));
            return callbacks;
        }
        int currSize = currPromiseChain.size();
        int size = callbacks.size() - currSize;
        if (size == 0)
            return null;
        int count = 1;
        Chain<CallbackCallInfo> c = callbacks;
        while (c != null) {
            if (count == currSize) {
                if (c.getNext() == null)
                    return null;
                this.cacheSchedulePlans.add(
                        new SchedulePlan(count + 1, count + c.getNext().size(), true));
                return c.getNext();
            }
            count++;
            c = c.getNext();
        }
        throw new AnalysisException("Never reach that place");
    }

    public Chain<CallbackCallInfo> getDiffTimers(
            Chain<CallbackCallInfo> callbacks) {
        if (currTimeIOChain == null) {
            if (callbacks == null)
                return null;
            this.cacheSchedulePlans.add(
                    new SchedulePlan(1, callbacks.size(), false));
            return callbacks;
        }
        int currSize = currTimeIOChain.size();
        int size = callbacks.size() - currSize;
        if (size == 0)
            return null;
        int count = 1;
        Chain<CallbackCallInfo> c = callbacks;
        while (c != null) {
            if (count == currSize) {
                if (c.getNext() == null)
                    return null;
                this.cacheSchedulePlans.add(
                        new SchedulePlan(count + 1, count + c.getNext().size(), false));
                return c.getNext();
            }
            count++;
            c = c.getNext();
        }
        throw new AnalysisException("Never reach that place");
    }

    public void commitScheduledPlans() {
        this.schedulePlans.addAll(cacheSchedulePlans);
        cacheSchedulePlans = newList();
    }

    public Set<CallbackGraphNode> getFirstCalls() {
        Set<CallbackGraphNode> targets = this.callbackGraph
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .map(Edge::getTarget)
                .collect(Collectors.toSet());
        return this.callbackGraph.keySet()
                .stream()
                .filter(x -> !targets.contains(x))
                .collect(Collectors.toSet());
    }

    private void topologicalSortUtil(CallbackGraphNode node,
                                     Map<CallbackGraphNode, Boolean> visited,
                                     Stack<CallbackGraphNode> stack) {
        visited.put(node, true);
        Set<CallbackGraphEdge> edges = this.callbackGraph
                .get(node);
        for (CallbackGraphEdge edge : edges) {
            if (!visited.get(edge.getTarget()))
                topologicalSortUtil(
                        edge.getTarget(), visited, stack);
        }
        stack.push(node);
    }

    private List<CallbackGraphNode> inTopologicalOrder() {
        Stack<CallbackGraphNode> stack =  new Stack<>();

        Map<CallbackGraphNode, Boolean> visited = newMap();
        for (CallbackGraphNode node : this.callbackGraph.keySet())
            visited.put(node, false);
        for (CallbackGraphNode node : this.callbackGraph.keySet())
            if (!visited.get(node))
                topologicalSortUtil(node, visited, stack);

        List<CallbackGraphNode> nodes = newList();
        while (!stack.empty())
            nodes.add(stack.pop());
        return nodes;
    }


    private int countNumberOfConnectedPairs() {
        Map<CallbackGraphNode, Set<CallbackGraphNode>> reachableNodes =
                newMap();
        for (CallbackGraphNode node : this.callbackGraph.keySet())
            reachableNodes.put(node, newSet());
        List<CallbackGraphNode> topologicalSorted = inTopologicalOrder();
        for (CallbackGraphNode node : topologicalSorted) {
            Set<CallbackGraphEdge> edges = this.callbackGraph.get(node);
            for (CallbackGraphEdge edge : edges) {
                Set<CallbackGraphNode> reachSources =
                        reachableNodes.get(node);
                reachableNodes.get(edge.getTarget()).add(node);
                reachableNodes.get(edge.getTarget()).addAll(reachSources);
            }
        }
        return reachableNodes.values()
                .stream()
                .mapToInt(Set::size)
                .sum();
    }

    private static BigInteger factorial(int n) {
        BigInteger factorial = BigInteger.valueOf(1);
        for (long factor = 2; factor <= n; factor++) {
            factorial = factorial.multiply(BigInteger.valueOf(factor));
        }
        return factorial;
    }

    private int getNumberOfPairs() {
        int n = this.callbackGraph.size();
        return factorial(n).divide(factorial(n - 2).multiply(
                BigInteger.valueOf(2))).intValue();
    }

    public Double precision() {
        if (this.callbackGraph.isEmpty())
            return null;
        double precision = (double) this.countNumberOfConnectedPairs() / getNumberOfPairs();
        if (precision > 1.0 || precision < 0) {
            throw new AnalysisException("Computed callback graph precision is wrong");
        }
        return precision;
    }

    public int countCallbacksExecuted() {
        List<AnalysisFunction> callbacks = newList();
        for (CallbackGraphNode node : this.callbackGraph.keySet()) {
            if (node.getFirst().isNative()) {
                callbacks.add(node.getFirst());
                continue;
            }
            if (!callbacks.contains(node.getFirst()))
                callbacks.add(node.getFirst());
        }
        return callbacks.size();
    }

    private static String toFunctionLabel(AnalysisFunction function) {
        if (function.isNative()) {
            return function.getNativeFunction().toString();
        }
        String functionName = function.getUserFunction()
                .getName();
        String functionLabel = functionName == null ?
                "anonymous" : functionName;
        return functionLabel + "-" +
                function.getUserFunction()
                        .getSourceLocation()
                        .getLineNumber();
    }

    private Map<String, List<String>> sortGraph() {
        Map<String, List<String>> sortedMap = new TreeMap<>();
        for (Map.Entry<CallbackGraphNode, Set<CallbackGraphEdge>> entry :
                this.callbackGraph.entrySet()) {
            Set<CallbackGraphEdge> edges = entry.getValue();
            List<String> keysList = newList();
            for (CallbackGraphEdge edge : edges) {
                CallbackGraphNode n = edge.getTarget();
                String label = "\"" + toFunctionLabel(n.getFirst())
                        + "[" + n.getSecond().toString() + "]\"";
                keysList.add(label);
            }
            Collections.sort(keysList);
            CallbackGraphNode n = entry.getKey();
            String label = "\"" + toFunctionLabel(n.getFirst())
                    + "[" + n.getSecond().toString() + "]\"";
            sortedMap.put(label, keysList);
        }
        return sortedMap;
    }

    public String toDot(PrintWriter out) {
        Map<String, List<String>> sortedMap = this.sortGraph();
        StringBuilder dot = new StringBuilder("digraph {\n");
        dot.append("node [shape=circle]\n");
        for (Map.Entry<String, List<String>> e : sortedMap.entrySet()) {
            if (e.getValue().isEmpty())
                dot.append(e.getKey()).append(";\n");
            else {
                for (String target : e.getValue())
                    dot.append(e.getKey()).append(" -> ")
                            .append(target).append(";\n");
            }
        }
        dot.append("}");
        out.println(dot);
        return dot.toString();
    }

    private Set<CallbackCallInfo> getNextCallbacks(
            Chain<CallbackCallInfo> next) {
        if (next == null)
            return newSet();
        Set<CallbackCallInfo> nextCallbacks = next.getTop();
        if (nextCallbacks == null)
            return newSet();
        return nextCallbacks;
    }

    private Set<CallbackGraphNode> toPair(CallbackCallInfo call) {
        Set<ObjectLabel> objectLabels = call.getCallback()
                .getObjectLabels();
        return objectLabels
                .stream()
                .map(x -> {
                    if (x.isHostObject())
                        return new CallbackGraphNode(
                                new AnalysisFunction(x.getHostObject()),
                                CallbackContext.makeCallbackContext(call));
                    else
                        return new CallbackGraphNode(
                                new AnalysisFunction(x.getFunction()),
                                CallbackContext.makeCallbackContext(call));
                })
                .collect(Collectors.toSet());
    }

    /**
     * Checks if the edge we want to connect is maybe spurious,
     * that is the dependent queue object of the source node is the
     * same with the target.
     */
    private static boolean isSpuriousEdge(CallbackCallInfo sourceCall,
                                          CallbackCallInfo targetCall) {
        Set<ObjectLabel> nativeTimer = Collections.singleton(
                InitialStateBuilder.SET_TIMEOUT_QUEUE_OBJ);
        Set<ObjectLabel> nativeAsyncIO = Collections.singleton(
                InitialStateBuilder.ASYNC_IO);

        Set<ObjectLabel> queueObjects = targetCall.getQueueObjects();
        Set<ObjectLabel> dependentQueueObjects = targetCall
                .getDependentQueueObjects();
        boolean isNative = dependentQueueObjects.equals(nativeTimer) ||
                dependentQueueObjects.equals(nativeAsyncIO);
        return !isNative && queueObjects.equals(
                sourceCall.getQueueObjects())
                && dependentQueueObjects.equals(sourceCall.getDependentQueueObjects());
    }

    public Set<CallbackGraphNode> getSources(
            CallbackGraphNode node) {
        return this.callbackGraph
                .values()
                .stream()
                .flatMap(Collection::stream)
                .filter(x -> x.getTarget().equals(node))
                .map(Edge::getSource)
                .collect(Collectors.toSet());
    }

    public Set<CallbackGraphNode> getTargets(
            CallbackGraphNode node) {
        return this.callbackGraph.get(node)
                .stream()
                .map(Edge::getTarget)
                .collect(Collectors.toSet());
    }

    private void addEdges(
            Set<CallbackCallInfo> sourceCalls,
            CallbackCallInfo sourceCall,
            Set<CallbackCallInfo> targetCalls,
            Set<CallbackGraphNode> ignoreNodes,
            Set<CallbackCallInfo> pendingNodes,
            Set<CallbackGraphNode> newNodes) {
        if (targetCalls == null || targetCalls.isEmpty())
            return;

        Set<CallbackGraphNode> sourceNodes = toPair(sourceCall);
        for (CallbackGraphNode sourceNode : sourceNodes) {
            if (ignoreNodes.contains(sourceNode))
                return;
            Set<CallbackGraphEdge> newEdges = newSet();
            for (CallbackCallInfo targetCall : targetCalls) {
                Set<CallbackGraphNode> targetNodes = toPair(targetCall);
                for (CallbackGraphNode targetNode : targetNodes) {
                    if (targetNode.equals(sourceNode))
                        continue;
                    boolean contains = this.callbackGraph.containsKey(targetNode);
                    if (contains && !newNodes.contains(targetNode)) {
                        Set<CallbackGraphEdge> edges = this.callbackGraph.get(targetNode);
                        CallbackCallInfo currentCallInfo = this.callInfo
                                .get(targetNode);
                        CallbackCallInfo jointCall =
                                CallbackCallInfo.join(
                                        currentCallInfo, targetCall);
                        this.getSources(targetNode)
                                .stream()
                                .forEach(x -> this.getTargets(targetNode)
                                        .stream()
                                        .forEach(y -> this.callbackGraph.get(x)
                                                .add(new CallbackGraphEdge(x, y))));
                        this.callbackGraph.put(targetNode, newSet());
                        this.callInfo.put(targetNode, jointCall);
                        if (!edges.isEmpty())
                            pendingNodes.add(jointCall);
                    } else if (!isSpuriousEdge(sourceCall, targetCall)) {
                        if (!contains)
                            newNodes.add(targetNode);
                        this.callbackGraph.put(targetNode, newSet());
                        this.callInfo.put(targetNode, targetCall);
                        newEdges.add(new CallbackGraphEdge(
                                sourceNode, targetNode));
                        for (CallbackCallInfo pendingNode :
                                pendingNodes) {
                            Set<CallbackGraphNode> nodes = toPair(pendingNode);
                            for (CallbackGraphNode n : nodes) {
                                ignoreNodes.add(n);
                                if (!n.equals(targetNode) && !isSpuriousEdge(
                                        pendingNode, targetCall))
                                    this.callbackGraph.get(n).add(
                                            new CallbackGraphEdge(n, targetNode));
                                ignoreNodes.add(n);
                            }
                        }
                        pendingNodes.clear();
                    } else {
                        if (sourceCalls.size() == 1) {
                            // TODO Revisit
                            this.callbackGraph.values()
                                    .stream()
                                    .flatMap(Collection::stream)
                                    .filter(x -> x.getTarget().equals(sourceNode))
                                    .map(Edge::getSource)
                                    .forEach(x -> this.callbackGraph.get(x).add(
                                            new CallbackGraphEdge(x, targetNode)));
                        }
                    }
                }
            }

            if (!newEdges.isEmpty()) {
                pendingNodes.remove(sourceCall);
                this.callbackGraph.put(sourceNode, newEdges);
            } else
                pendingNodes.add(sourceCall);
        }
    }

    private void connectWithLastNodes(Set<CallbackCallInfo> targetCalls,
                                      Set<CallbackGraphNode> ignoreNodes,
                                      Set<CallbackCallInfo> pendingNodes,
                                      Set<CallbackGraphNode> newNodes) {
        Set<CallbackCallInfo> sourceCalls = this.callbackGraph
                .entrySet()
                .stream()
                .filter(x -> x.getValue().isEmpty())
                .map(x -> this.callInfo.get(x.getKey()))
                .collect(Collectors.toSet());
        for (CallbackCallInfo call : sourceCalls)
            addEdges(sourceCalls, call, targetCalls, ignoreNodes, pendingNodes,
                     newNodes);
    }

    public void toCallbackGraph(Chain<CallbackCallInfo> chain) {
        if (chain == null)
            return;
        Chain<CallbackCallInfo> c = chain;
        Set<CallbackCallInfo> pendingEdges = newSet();
        boolean flag = true;
        while (c != null) {
            Set<CallbackCallInfo> callbacks = c.getTop();
            if (callbacks == null)
                return;
            Chain<CallbackCallInfo> next = c.getNext();
            Set<CallbackCallInfo> targetNodes = getNextCallbacks(next);
            Set<CallbackGraphNode> ignoreNodes = newSet();
            Set<CallbackGraphNode> newNodes = newSet();
            if (!this.callbackGraph.isEmpty() && flag)
                connectWithLastNodes(callbacks, ignoreNodes,
                        pendingEdges, newNodes);
            flag = false;
            for (CallbackCallInfo call : callbacks) {
                Set<CallbackGraphNode> nodes = toPair(call);
                for (CallbackGraphNode node : nodes) {
                    if (!this.callbackGraph.containsKey(node)) {
                        this.callbackGraph.put(node, newSet());
                        this.callInfo.put(node, call);
                    }
                }
                addEdges(callbacks, call, targetNodes, ignoreNodes,
                        pendingEdges, newNodes);
            }
            c = c.getNext();
        }
    }

    private Set<CallbackGraphNode> getKeys(
            HostObject hostObject, CallbackContext context) {
        return this.callbackGraph
                .keySet()
                .stream()
                .filter(x -> x.getFirst().isNative() && x.getFirst().getNativeFunction().equals(hostObject)
                        && x.getSecond().equals(context))
                .collect(Collectors.toSet());
    }

    private Set<CallbackGraphNode> getKeys(
            Function function, CallbackContext context) {
        return this.callbackGraph
                .keySet()
                .stream()
                .filter(x -> !x.getFirst().isNative() && x.getFirst().getUserFunction().equals(function)
                        && x.getSecond().equals(context))
                .collect(Collectors.toSet());
    }

    public Set<CallbackGraphNode> getNextCalls(
            Function function, CallbackContext context) {
        Set<CallbackGraphNode> keys = getKeys(function, context);
        if (keys.size() != 1) {
            throw new AnalysisException("Next calls not found");
        }
        Set<CallbackGraphEdge> nextCalls = this.callbackGraph
                .get(keys.iterator().next());
        return nextCalls
                .stream()
                .map(Edge::getTarget)
                .collect(Collectors.toSet());
    }

    public CallbackCallInfo getCallbackInfo(CallbackGraphNode node) {
        CallbackCallInfo callInfo = this.callInfo.get(node);
        if (callInfo == null)
            throw new AnalysisException("Callback node not found");
        return callInfo;
    }

    public Set<CallbackGraphNode> getNextCalls(
            HostObject hostObject, CallbackContext context) {
        Set<CallbackGraphNode> keys = getKeys(hostObject, context);
        if (keys.size() != 1)
            throw new AnalysisException("Next calls not found");
        Set<CallbackGraphEdge> nextCalls = this.callbackGraph
                .get(keys.iterator().next());
        return nextCalls
                .stream()
                .map(Edge::getTarget)
                .collect(Collectors.toSet());
    }

    public Set<CallbackGraphNode> getAllCallbacks() {
        return this.callbackGraph.keySet();
    }

    private static CallbackGraphNode replaceNode(
            CallbackGraphNode node, ObjectLabel singleton,
            ObjectLabel summary) {
        CallbackContext context = node.getSecond().clone();
        context.replaceObjectLabels(singleton, summary);
        return new CallbackGraphNode(node.getFirst(), context);
    }

    public void replaceAnalyzedFunctions(ObjectLabel singleton,
                                         ObjectLabel summary) {
        Map<CallbackGraphNode, Boolean> newAnalyzed = newMap();
        for (Map.Entry<CallbackGraphNode, Boolean> e : this.analyzedFunctions
                .entrySet()) {
            CallbackGraphNode key = e.getKey();
            if (key.getSecond() != null)
                key = replaceNode(key, singleton, summary);
            newAnalyzed.put(key, e.getValue());
        }
        this.analyzedFunctions = newAnalyzed;
    }

    public void replaceCallbackGraph(ObjectLabel singleton,
                                     ObjectLabel summary) {
        replaceAnalyzedFunctions(singleton, summary);
    }

    public static class Edge<T> {
        private T source;

        private T target;

        Edge(T source, T target) {
            this.source = source;
            this.target = target;
        }

        public T getSource() {
            return source;
        }

        public T getTarget() {
            return target;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Edge<?> edge = (Edge<?>) o;

            if (source != null ? !source.equals(edge.source) : edge.source != null) return false;
            return target != null ? target.equals(edge.target) : edge.target == null;
        }

        @Override
        public int hashCode() {
            int result = source != null ? source.hashCode() : 0;
            result = 31 * result + (target != null ? target.hashCode() : 0);
            return result;
        }
    }

    public void setCurrPromiseChain(Chain<CallbackCallInfo> currPromiseChain) {
        this.currPromiseChain = currPromiseChain;
    }

    public void markAnalyzed(Set<CallbackGraphNode> nodes) {
        for (CallbackGraphNode node : nodes)
            this.analyzedFunctions.put(node, true);
    }

    public void resetAnalyzed(Set<CallbackGraphNode> nodes) {
        for (CallbackGraphNode node : nodes)
            this.analyzedFunctions.put(node, false);
    }

    public Set<CallbackGraphNode> genCallbackGraphNodes(Set<CallbackCallInfo> calls) {
        return calls
                .stream()
                .map(this::toPair)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public void setCurrTimeIOChain(Chain<CallbackCallInfo> currTimeIOChain) {
        this.currTimeIOChain = currTimeIOChain;
    }

    public static class CallbackGraphEdge extends Edge<CallbackGraphNode> {

        CallbackGraphEdge(CallbackGraphNode source,
                          CallbackGraphNode target) {
            super(source, target);
        }
    }

    public static class CallbackGraphNode extends Pair<AnalysisFunction, CallbackContext> {

        private CallbackGraphNode(AnalysisFunction fst, CallbackContext snd) {
            super(fst, snd);
        }
    }

    private static class SchedulePlan {
        int startIndex;

        int endIndex;

        boolean isPromise;

        public SchedulePlan(int startIndex, int endIndex, boolean isPromise) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.isPromise = isPromise;
        }
    }
}
