import java.util.Scanner;

public class PP {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int Z = scanner.nextInt();
        for (int i = 0; i < Z; ++i) {
            int n = scanner.nextInt();
            int j = 2;
            for (int k = 0; k < n; j++) {
                if (isPrime(j) && isPalindrome(j)) {
                    k++;
                }
            }
            j--;
            int product = calcProduct(j);
            int l = 2;
            for (int k = 0; k < product; l++) {
                if (isPrime(l)) {
                    k++;
                }
            }
            l--;
            System.out.println(j + " " + l);
        }
    }

    public static int calcProduct(int n) {
        int p = 1;
        String s = Integer.toString(n);
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != 0) {
                Character ch = s.charAt(i);
                String chStr = Character.toString(ch);
                p = p * Integer.parseInt(chStr);
            }
        }
        return p;
    }

    public static boolean isPalindrome(int n) {
        String s = "" + n;
        return s.equals(new StringBuilder(s).reverse().toString());
    }

    public static boolean isPrime(int n) {
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}



