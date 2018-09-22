package total;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Pair<L, R> {
    public final L left;
    public final R right;

    private Pair(L l, R r) {
        left = l;
        right = r;
    }

    public static <L, R> Pair<L, R> of(L l, R r) {
        return new Pair<L, R>(l, r);
    }

    public <B> Pair<B, R> leftMap(Function<L, B> f) {
        return new Pair<B, R>(f.apply(left), right);
    }

    public <B> Pair<L, B> rightMap(Function<R, B> f) {
        return new Pair<L, B>(left, f.apply(right));
    }

    public <B> Optional<Pair<B, R>> leftOptMap(Function<L, Optional<B>> f) {
        return f.apply(left).map(l -> new Pair<B, R>(l, right));
    }

    public <B> Optional<Pair<L, B>> rightOptMap(Function<R, Optional<B>> f) {
        return f.apply(right).map(r -> new Pair<L, B>(left, r));
    }

    @Override
    public String toString() {
        return "Pair(" + left + ", " + right + ")";
    }

    @Override
    public boolean equals(Object value) {
        if (value == this) return true;
        if (value instanceof Pair) {
            @SuppressWarnings("unchecked")
            Pair<L, R> pair = (Pair<L, R>) value;
            return Objects.equals(left, pair.left)
                && Objects.equals(right, pair.right);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
