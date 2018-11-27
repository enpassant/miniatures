package exception;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Left<L, R> implements Either<L, R> {
    private final L value;

    private Left(L l) {
        value = l;
    }

    public static <L, R> Left<L, R> of(L l) {
        return new Left<>(l);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Either<L, B> map(Function<R, B> f) {
        return (Either<L, B>) this;
    }

    @Override
    public <B> Either<B, R> mapLeft(Function<L, B> f) {
        return new Left<>(f.apply(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Either<L, B> flatMap(Function<R, Either<L, B>> f) {
        return (Either<L, B>) this;
    }

    @Override
    public <B> Either<B, R> flatMapLeft(Function<L, Either<B, R>> f) {
        return f.apply(value);
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
        f.accept(value);
        return this;
    }

    @Override
    public Either<L, R> orElse(R value) {
        return Right.of(value);
    }

    @Override
    public Optional<L> left() {
        return Optional.of(value);
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
        return "Left(" + value + ")";
    }

    @Override
    public boolean equals(Object value) {
        if (value == this) {
            return true;
        }
        if (value instanceof Left) {
            @SuppressWarnings("unchecked")
            Left<L, R> valueLeft = (Left<L, R>) value;
            return this.value.equals(valueLeft.left().get());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
