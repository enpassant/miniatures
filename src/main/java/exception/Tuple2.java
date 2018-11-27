package exception;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class Tuple2<A, B> {
    private final A first;
    private final B second;

    public Tuple2(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Tuple2<A, B> of(A first, B second) {
        return new Tuple2<>(first, second);
    }

    @SafeVarargs
    public static <A, B> Map<A, B> toMap(Tuple2<A, B>... tuples) {
        return Arrays.stream(tuples)
            .collect(Collectors.toMap(
                Tuple2::getFirst,
                Tuple2::getSecond,
                (u, v) -> v,
                LinkedHashMap::new
            ));
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}
