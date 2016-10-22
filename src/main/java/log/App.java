package log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.function.Function;

class App {
    public static void main(final String[] args) {
        Logger.config(args);

        BufferedReader br = null;

        final Function<String, String> process = Logger.makeLogged(
            App.class, "process", Server::process);

        final Function<String, String> process2 = Logger.makeLogged(
            App.class, "process",
            Server::parse
        ).andThen(
            (o -> o.map(n -> n * n))
        ).andThen(Logger.makeLogged(App.class, "process",
            (o -> o.map(n -> "Response: " + n)))
        ).andThen(
            (o -> o.orElse("Parse error"))
        );

        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                final String request = br.readLine();

                if (request == null) {
                    System.out.println("Exit!");
                    break;
                }
                final String response = process.apply(request);
                System.out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
