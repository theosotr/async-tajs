package dk.brics.tajs.lattice;

public class NativeCallbackContext {

    private int pos;

    boolean isPromise;

    public NativeCallbackContext(int pos, boolean isPromise) {
        this.pos = pos;
        this.isPromise = isPromise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NativeCallbackContext that = (NativeCallbackContext) o;

        if (pos != that.pos) return false;
        return isPromise == that.isPromise;
    }

    @Override
    public int hashCode() {
        int result = pos;
        result = 31 * result + (isPromise ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        String prefix = this.isPromise ? "@" : "#";
        return prefix + Integer.toString(this.pos);
    }

    public int getPos() {
        return pos;
    }

    public boolean isPromise() {
        return isPromise;
    }
}
