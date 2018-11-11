package exception;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Left<L, R> implements Either<L, R> {
    private final L left;

    private Left(L l) {
        left = l;
    }

    public static <L, R> Left<L, R> of(L l) {
        return new Left<L, R>(l);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Either<L, B> map(Function<R, B> f) {
        return (Either<L, B>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Either<L, B> flatMap(Function<R, Either<L, B>> f) {
        return (Either<L, B>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Either<L, B> flatten() {
        return (Either<L, B>) this;
    }

    @Override
    public Either<L, R> forEach(Consumer<R> f) {
        return this;
    }

    @Override
    public Either<L, R> forEachLeft(Consumer<L> f) {
        f.accept(left);
        return this;
    }

    @Override
    public Either<L, R> orElse(R value) {
        return Right.of(value);
    }

    @Override
    public Optional<L> left() {
        return Optional.of(left);
    }

    @Override
    public Optional<R> right() {
        return Optional.empty();
    }

    @Override
    public R get() {
        throw new NoSuchElementException();
    }

    @Override
    public String toString() {
        return "Left(" + left + ")";
    }

    @Override
    public boolean equals(Object value) {
        if (value == this) return true;
        if (value instanceof Left) {
            @SuppressWarnings("unchecked")
            Left<L, R> valueLeft = (Left<L, R>) value;
            return left.equals(valueLeft.left().get());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return left.hashCode();
    }
}
