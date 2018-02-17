public class IntOperation {
    private static int add(int a, int b) {
        return a + b;
    }

    private static int mul(int a, int b) {
        return a * b;
    }

    @FunctionalInterface
    interface Operation {
        int apply(int a, int b);
    }

    public static int run(Operation operation, int a, int b) {
        return operation.apply(a, b);
    }

    public static void main(String[] args) {
        //int result = run(DecimalFormat::add, 10, 14);
        int result = run((a, b) -> a * b, 10, 14);
        System.out.println("Result: " + result);
    }
}



