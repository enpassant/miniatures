package exception;

public class CalcEither {
    private static int calcA(int value) {
        return value * 5;
    }

    private static Either<String, Integer> calcB(int value) {
        int divisor = (20 - calcA(10 - value));
        return divisor == 0 ?
            Left.of("Divide by zero") :
            Right.of(10000 / divisor);
    }

    private static int calcAB(int a, int b) {
        return a + b;
    }

    private static int calc(int value) {
        return calcB(value)
            .map(b -> calcAB(calcA(value), b))
            .orElse(0)
            .get();
    }

    public static void main(String[] args) {
        int result = calc(8);
        System.out.println("Result: " + result);
    }
}



