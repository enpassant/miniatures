package exception;

public class Calc {
    private static int calcA(int value) {
        return value * 5;
    }

    private static int calcB(int value) {
        return 10000 / (20 - calcA(10 - value));
    }

    private static int calcAB(int a, int b) {
        return a + b;
    }

    private static int calc(int value) {
        return calcAB(
                calcA(value),
                calcB(value)
        );
    }

    private static int calc2(int value) {
        try {
            return calcAB(
                calcA(value),
                calcB(value)
            );
        } catch(Exception e) {
            return 0;
        }
    }


    public static void main(String[] args) {
        int result = calc(6);
        System.out.println("Result: " + result);
    }
}



