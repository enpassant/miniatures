package independence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class TightCoupling {
    public static String read() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            return br.readLine();
        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public static void write(String str) {
        System.out.println(str);
    }

    public static String capitalize(String str) {
        if (str.isEmpty()) return "UNKNOWN";
        else return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void main(String[] args) {
        write("Enter your name: ");
        String name = read();
        String capitalizedName = capitalize(name);
        String greeting = "Hello " + capitalizedName + "!";
        write(greeting);
    }
}

