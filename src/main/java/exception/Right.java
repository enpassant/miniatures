package exception;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Right<L, R> implements Either<L, R> {
    private final R value;

    private Right(R r) {
        value = r;
    }

    public static <L, R> Right<L, R> of(R r) {
        return new Right<>(r);
    }

    @Override
    public <B> Either<L, B> map(Function<R, B> f) {
        return new Right<>(f.apply(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Either<B, R> mapLeft(Function<L, B> f) {
        return (Either<B, R>) this;
    }

    @Override
    public <B> Either<L, B> flatMap(Function<R, Either<L, B>> f) {
        return f.apply(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Either<B, R> flatMapLeft(Function<L, Either<B, R>> f) {
        return (Either<B, R>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Either<L, B> flatten() {
        return (Either<L, B>) value;
    }

    @Override
    public Either<L, R> forEachLeft(Consumer<L> f) {
        return this;
    }

    @Override
    public Either<L, R> forEach(Consumer<R> f) {
        f.accept(value);
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
        return Optional.of(value);
    }

    @Override
    public R get() {
        return value;
    }

    @Override
    public String toString() {
        return "Right(" + value + ")";
    }

    @Override
    public boolean equals(Object value) {
        if (value == this) {
            return true;
        }
        if (value instanceof Right) {
            @SuppressWarnings("unchecked")
            Right<L, R> valueRight = (Right<L, R>) value;
            return this.value.equals(valueRight.right().get());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
