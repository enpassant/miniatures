package total;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Total {
    static final List<Pair<Integer, Integer>> pairs = Arrays.asList(
        Pair.of(6, 2),
        Pair.of(2, 6),
        Pair.of(2, 0),
        Pair.of(2, 8)
    );

    private static int percent(int a, int b) {
        return a * 100 / b;
    }

    private static int percent(Pair<Integer, Integer> pair) {
        return percent(pair.left, pair.right);
    }

    private static Optional<Integer> percentOptional(int a, int b) {
        if (b == 0) {
            return Optional.empty();
        } else {
            return Optional.of(a * 100 / b);
        }
    }

    private static Optional<Integer> percentOptional(Pair<Integer, Integer> pair) {
        return percentOptional(pair.left, pair.right);
    }

    private static int percentStrict(int a, NotZeroInt b) {
        return a * 100 / b.number;
    }

    private static int percentStrict(Pair<Integer, NotZeroInt> pair) {
        return percentStrict(pair.left, pair.right);
    }

    public static void main(String[] args) {
        pairs.stream()
            .map(pair -> {
                try {
                    return Optional.of(Total.percent(pair));
                } catch (ArithmeticException e) {
                    return Optional.empty();
                }
            })
            .forEach(System.out::println);

        pairs.stream()
            .map(Total::percentOptional)
            .forEach(System.out::println);

        pairs.stream()
            .map(pair -> pair.rightOptMap(NotZeroInt::of))
            .map(pairOpt -> pairOpt.map(Total::percentStrict))
            .forEach(System.out::println);

        pairs.stream()
            .map(pair -> pair.rightOptMap(NotZeroInt::of))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(Total::percentStrict)
            .forEach(System.out::println);
    }
}
