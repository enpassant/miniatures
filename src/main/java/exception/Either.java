package exception;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Either<L, R> {
    <B> Either<L, B> map(Function<R, B> f);
    <B> Either<B, R> mapLeft(Function<L, B> f);
    <B> Either<L, B> flatMap(Function<R, Either<L, B>> f);
    <B> Either<B, R> flatMapLeft(Function<L, Either<B, R>> f);
    <B> Either<L, B> flatten();
    Either<L, R> forEach(Consumer<R> f);
    Either<L, R> forEachLeft(Consumer<L> f);
    R orElse(R value);
    Optional<L> left();
    Optional<R> right();
    R get();

    public static <L, R> Either<L, R> ofOptional(L left, Optional<R> optional) {
        if (optional.isPresent()) {
            return Right.of(optional.get());
        } else {
            return Left.of(left);
        }
    }
}
