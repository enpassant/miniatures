package independence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class TightAndIndeendentCoupling {
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

    public static String formatGreeting(String name) {
        return "Hello " + name + "!";
    }

    public static void main(String[] args) {
        write("Enter your name: ");
        String name = read();
        String greeting = createGreeting(name);
        write(greeting);
    }

    public static String createGreeting(String name) {
        String capitalizedName = capitalize(name);
        return formatGreeting(capitalizedName);
    }
}

