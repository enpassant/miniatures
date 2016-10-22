package log;

import java.util.Optional;

class Server {
    private static final Logger logger = Logger.getLogger(Server.class);

    public static String process(final String request) {
        //if (logger.isDebugEnabled()) {
            //logger.debug("request: " + request);
        //}

        final int number = Integer.parseInt(request);
        final String output = Integer.toString(number * number);
        //if (logger.isDebugEnabled()) {
            //logger.debug("response: " + output);
        //}

        return output;
    }

    public static Optional<Integer> parse(final String request) {
        try {
            final int number = Integer.parseInt(request);
            return Optional.of(number);
        } catch(Exception e) {
            return Optional.empty();
        }
    }
}
