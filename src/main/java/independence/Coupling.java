package independence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Coupling {
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

    public static void runMainTightCoupling() {
        write("Enter your name: ");
        String name = read();
        String capitalizedName = capitalize(name);
        String greeting = "Hello " + capitalizedName + "!";
        write(greeting);
    }

    public static void runMainIndependent() {
        write("Enter your name: ");
        String name = read();
        String greeting = runLogicIndependent(name);
        write(greeting);
    }

    public static String runLogicIndependent(String name) {
        String capitalizedName = capitalize(name);
        return "Hello " + capitalizedName + "!";
    }

    public static void runMainLooseCoupling(LogicLooseCoupling logic) {
        logic.runLogic();
    }

    static interface Reader {
        String read();
    }

    static class StdInReader implements Reader {
        public String read() {
            return Coupling.read();
        }
    }

    static interface Writer {
        void write(String str);
    }

    static class StdOutWriter implements Writer {
        public void write(String str) {
            Coupling.write(str);
        }
    }

    static class LogicLooseCoupling {
        private Reader reader;
        private Writer writer;

        public LogicLooseCoupling(Reader reader, Writer writer) {
            this.reader = reader;
            this.writer = writer;
        }

        public void runLogic() {
            writer.write("Enter your name: ");
            String name = reader.read();
            String capitalizedName = capitalize(name);
            String greeting = "Hello " + capitalizedName + "!";
            writer.write(greeting);
        }
    }

    public static void main(String[] args) {
        // Wiring
        LogicLooseCoupling logic =
            new LogicLooseCoupling(new StdInReader(), new StdOutWriter());

        final String type = (args.length < 1) ? "tight" : args[0];
        write("Coupling: " + type);

        switch (type) {
            case "tight":
                runMainTightCoupling();
                break;
            case "independent":
                runMainIndependent();
                break;
            case "loose":
                runMainLooseCoupling(logic);
                break;
        }
    }
}

