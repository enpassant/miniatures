package exception;

public class CalcEither {
    public static Either<Failure, Integer> calc(int value) {
        final int a = calcA(value);
        return calcB(value)
            .map(b -> calcAB(a, b));
    }

    public static int calcA(int value) {
        return value * 5;
    }

    public static Either<Failure, Integer> calcB(int value) {
        final int divisor = (20 - calcA(10 - value));
        return divisor == 0 ?
            Left.of(Failure.of("/ by zero")) :
            Right.of(10000 / divisor);
    }

    public static int calcAB(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        final Either<Failure, Integer> result1 = calc(6);
        System.out.println("Result1: " + result1);

        final int result2 = calc(8).orElse(0).get();
        System.out.println("Result2: " + result2);

        final int result3 = calc(6).orElse(0).get();
        System.out.println("Result3: " + result3);
    }
}



