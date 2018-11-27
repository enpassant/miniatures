package exception;

public class Calc {
    public static int calc(int value) throws ArithmeticException {
        final int a = calcA(value);
        final int b = calcB(value);
        return calcAB(a, b);
    }

    public static int calcA(int value) {
        return value * 5;
    }

    public static int calcB(int value) throws ArithmeticException {
        return 10000 / (20 - calcA(10 - value));
    }

    public static int calcAB(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        try {
            final int result1 = calc(6);
            System.out.println("Result1: " + result1);
        } catch (Exception e) {
            System.out.println("Result1: " + e);
        }

        try {
            final int result2 = calc(8);
            System.out.println("Result2: " + result2);
        } catch (Exception e) {
            System.out.println("Result2: " + e);
        }

        int result3;
        try {
            result3 = calc(6);
        } catch (Exception e) {
            result3 = 0;
        }
        System.out.println("Result3: " + result3);
    }
}



