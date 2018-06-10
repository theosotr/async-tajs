package dk.brics.tajs.util;


import dk.brics.tajs.lattice.ObjectLabel;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dk.brics.tajs.util.Collections.newSet;


public class Chain<T> {

    private Set<T> elements;

    private Chain<T> next;

    private Chain(Set<T> elements,
                  Chain<T> next) {
        this.elements = elements;
        this.next = next;
    }

    public static <T> Chain<T> make() {
        return new Chain<>(newSet(), null);
    }

    public static <T> Chain<T> make(Set<T> elements,
                                    Chain<T> next) {
        return new Chain<>(elements, next);
    }

    public static <T> Chain<T> appendElement(T elem,
                                             Chain<T> next) {
        return new Chain<>(Collections.singleton(elem), next);
    }

    public static <T> Chain<T> appendElement(Set<T> elems,
                                             Chain<T> next) {
        return new Chain<>(elems, next);
    }

    private static <T> Chain<T> toChain(List<T> list) {
        Chain<T> chain = Chain.make(
                Collections.singleton(list.get(0)), null);
        for (int i = 1; i < list.size(); i++)
            chain.appendLast(Chain.make(
                    Collections.singleton(list.get(i)), null));
        return chain;
    }

    public static <T> Chain<T> toChain(Set<List<T>> setList) {
        Chain<T> chain = null;
        for (List<T> list : setList)
            chain = join(chain, toChain(list), true);
        return chain;
    }

    public Set<T> getAllElements() {
        if (this.next == null)
            return this.elements;
        Set<T> elements = newSet(this.elements);
        elements.addAll(this.next.getAllElements());
        return elements;
    }

    public void appendLast(Chain<T> elem) {
        if (elem == null)
            return;
        if (this.next == null)
            this.next = elem;
        else
            this.next.appendLast(elem);
    }

    public void appendLast(Set<T> elements) {
        if (this.elements.isEmpty())
            return;
        if (this.next == null)
            this.elements.addAll(elements);
        else
            this.next.appendLast(elements);
    }

    public void appendNextToLast(T elem) {
        if (this.next == null)
            this.elements.add(elem);
        else
            this.next.appendNextToLast(elem);
    }

    public static Chain<ObjectLabel> joinAlt(
            Chain<ObjectLabel> p1, Chain<ObjectLabel> p2,
            Chain<ObjectLabel> accum) {
        if (p1 == null) {
            if (p2 == null)
                return accum;
            else {
                if (accum.isEmpty())
                    return p2;
                else {
                    accum.appendLast(p2);
                    return accum;
                }
            }
        }
        if (p2 == null) {
            if (accum.isEmpty())
                return p1;
            else {
                accum.appendLast(p1);
                return accum;
            }
        }
        ObjectLabel l;
        Set<ObjectLabel> jointElements = newSet();
        jointElements.addAll(p1.elements
                .stream()
                .collect(Collectors.toSet()));
        jointElements.addAll(p2.elements
                .stream()
                .collect(Collectors.toSet()));
        Set<ObjectLabel> finalJointElements = jointElements;
        jointElements = jointElements
                .stream()
                .filter(x -> {
                    if (x.isSingleton()) {
                        ObjectLabel sum = x.makeSummary();
                        return !finalJointElements.contains(sum);
                    }
                    return true;
                })
                .collect(Collectors.toSet());
        if (accum.getTop() == null)
            accum.elements = jointElements;
        else if (!jointElements.isEmpty())
            accum.appendLast(Chain.make(jointElements, null));
        return joinAlt(p1.next, p2.next, accum);
    }

    public static <T> Chain<T> join(Chain<T> p1, Chain<T> p2,
                                    boolean removeDuplicates) {
        if (p1 == null)
            return p2;
        if (p2 == null)
            return p1;
        Chain<T> newChain = join(p1.next, p2.next, removeDuplicates);
        Set<T> jointCalls = newSet();
        // TODO revisit.
        if (newChain != null && removeDuplicates) {
            jointCalls.addAll(p1.elements
                    .stream()
                    .filter(x -> !newChain.has(x))
                    .collect(Collectors.toSet()));
            jointCalls.addAll(p2.elements
                    .stream()
                    .filter(x -> !newChain.has(x))
                    .collect(Collectors.toSet()));
            if (jointCalls.isEmpty())
                return newChain;
        } else {
            jointCalls.addAll(p1.elements);
            jointCalls.addAll(p2.elements);
        }
        return new Chain<>(jointCalls, newChain);
    }

    public boolean has(T elem) {
        if (this.next == null)
            return this.elements.contains(elem);
        boolean contains = this.elements.contains(elem);
        return contains || this.next.has(elem);
    }

    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    public Set<T> getTop() {
        if (this.isEmpty())
            return null;
        return this.elements;
    }

    public Set<T> getLast() {
        if (this.next == null)
            return this.getTop();
        else
            return this.next.getLast();
    }

    public Chain<T> getNode(Set<T> elements) {
        if (this.elements
                .stream()
                .anyMatch(elements::contains))
            return this.clone();
        if (this.next == null)
            return null;
        else
            return this.next.getNode(elements);
    }

    public void removeLast(T elem) {
        if (this.elements.contains(elem)) {
            if (this.next == null ||
                    !this.next.has(elem)) {
                this.elements.remove(elem);
                return;
            }
        }
        this.next.removeLast(elem);
    }

    public int occurrences(T elem) {
        int occ = 0;
        if (this.elements.contains(elem))
            occ = 1;
        if (this.next == null)
            return occ;
        else
            return occ + this.next.occurrences(elem);
    }

    public int size() {
        if (this.next == null)
            return 1;
        else
            return 1 + this.next.size();
    }

    public void replaceElement(T elem1, T elem2) {
        if (this.elements.contains(elem1)) {
            this.elements.remove(elem1);
            this.elements.add(elem2);
            return;
        }
        if (this.next != null)
            this.next.replaceElement(elem1, elem2);
    }

    public Chain<T> getSubChain(int start, int end) {
        if (start < 0 || end < 0)
            throw new AnalysisException("Indexes should not be negative");

        Chain<T> c = this;
        int count = 1;
        Chain<T> ret = null;
        while (c != null) {
            if (count >= start) {
                if (count > end)
                    break;
                if (ret == null)
                    ret = Chain.make(c.getTop(), null);
                else
                    ret.appendLast(Chain.make(c.getTop(), null));
            }
            count++;
            c = c.getNext();
        }
        return ret;
    }

    public Chain<T> clone() {
        return new Chain<>(
                newSet(this.elements),
                next != null ? next.clone() : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chain<?> chain = (Chain<?>) o;

        if (elements != null ? !elements.equals(chain.elements) : chain.elements != null) return false;
        return next != null ? next.equals(chain.next) : chain.next == null;
    }

    @Override
    public int hashCode() {
        int result = elements != null ? elements.hashCode() : 0;
        result = 31 * result + (next != null ? next.hashCode() : 0);
        return result;
    }

    public Chain<T> getNext() {
        return next;
    }

}
