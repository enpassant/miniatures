package exception;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Right<L, R> implements Either<L, R> {
    private final R right;

    private Right(R r) {
        right = r;
    }

    public static <L, R> Right<L, R> of(R r) {
        return new Right<L, R>(r);
    }

    @Override
    public <B> Either<L, B> map(Function<R, B> f) {
        return new Right<L, B>(f.apply(right));
    }

    @Override
    public <B> Either<L, B> flatMap(Function<R, Either<L, B>> f) {
        return f.apply(right);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Either<L, B> flatten() {
        return (Either<L, B>) right;
    }

    @Override
    public Either<L, R> forEachLeft(Consumer<L> f) {
        return this;
    }

    @Override
    public Either<L, R> forEach(Consumer<R> f) {
        f.accept(right);
        return this;
    }

    @Override
    public Either<L, R> orElse(R value) {
        return this;
    }

    @Override
    public Optional<L> left() {
        return Optional.empty();
    }

    @Override
    public Optional<R> right() {
        return Optional.of(right);
    }

    @Override
    public R get() {
        return right;
    }

    @Override
    public String toString() {
        return "Right(" + right + ")";
    }

    @Override
    public boolean equals(Object value) {
        if (value == this) return true;
        if (value instanceof Right) {
            @SuppressWarnings("unchecked")
            Right<L, R> valueRight = (Right<L, R>) value;
            return right.equals(valueRight.right().get());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return right.hashCode();
    }
}
