package exception;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Either<L, R> {
    <B> Either<L, B> map(Function<R, B> f);
    <B> Either<L, B> flatMap(Function<R, Either<L, B>> f);
    <B> Either<L, B> flatten();
    Either<L, R> forEach(Consumer<R> f);
    Either<L, R> forEachLeft(Consumer<L> f);
    Either<L, R> orElse(R value);
    Optional<L> left();
    Optional<R> right();
    R get();
}
