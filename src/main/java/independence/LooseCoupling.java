package independence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class LooseCoupling {
    static interface Reader {
        String read();
    }

    static class StdInReader implements Reader {
        public String read() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                return br.readLine();
            } catch (IOException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }

    static interface Writer {
        void write(String str);
    }

    static class StdOutWriter implements Writer {
        public void write(String str) {
            System.out.println(str);
        }
    }

    static class Greeting {
        private Reader reader;
        private Writer writer;

        public Greeting(Reader reader, Writer writer) {
            this.reader = reader;
            this.writer = writer;
        }

        public void greet() {
            writer.write("Enter your name: ");
            String name = reader.read();
            String capitalizedName = capitalize(name);
            String greeting = formatGreeting(capitalizedName);
            writer.write(greeting);
        }

        private static String capitalize(String str) {
            if (str.isEmpty()) return "UNKNOWN";
            else return str.substring(0, 1).toUpperCase() + str.substring(1);
        }

        public static String formatGreeting(String name) {
            return "Hello " + name + "!";
        }
    }

    public static void main(String[] args) {
        Greeting greeting = new Greeting(new StdInReader(), new StdOutWriter());
        greeting.greet();
    }
}

