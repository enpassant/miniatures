package independence;

class Samples {
    public static Integer addIntegers(Integer a, Integer b) {
        return a + b;
    }

    public static Number addNumbers(Number a, Number b) {
        return a.intValue() + b.intValue();
    }

    public static void main(String[] args) {
        for (int i=0; i<10; i++) {
            System.out.println("Hello World!");
        }
        //Integer i = addIntegers(4, 5);
        //System.out.println(i);

        //Number n = addNumbers(4.2, 5.4);
        //System.out.println(n);
    }
}

