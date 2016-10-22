package log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Function;

class Logger {
    private static String loggedClassName;
    private final Class currentClass;

    public static void config(final String[] args) {
        if (args.length >= 1) {
            loggedClassName = args[0];
        }
    }

    public static Logger getLogger(final Class loggedClass) {
        return new Logger(loggedClass);
    }

    private Logger(final Class currentClass) {
        this.currentClass = currentClass;
    }

    public boolean isDebugEnabled() {
        boolean isEnabled = currentClass.getName().equals(loggedClassName);
        debugInternal("isDebugEnabled", "" + isEnabled);
        return currentClass.getName().equals(loggedClassName);
    }

    public void debug(final String method, final String message) {
        if (isDebugEnabled()) {
            debugInternal(method, message);
        }
    }

    private void debugInternal(final String method, final String message) {
        System.err.println("[" + currentClass.getName()
            + "." + method + "] " + message);
    }

    public static <I, O> Function<I, O> makeLogged(
        final Class loggedClass,
        final String method,
        final Function<I, O> fn)
    {
        final Logger logger = Logger.getLogger(loggedClass);
        if (logger.isDebugEnabled()) {
            return new Function<I, O>() {
                public O apply(final I input) {
                    StringBuilder sb = new StringBuilder();

                    final long start = System.nanoTime();
                    sb.append("Input: ").append(input);
                    try {
                        final O output = fn.apply(input);
                        sb.append(". Output: ").append(output);
                        return output;
                    } catch(Exception e) {
                        sb.append(". Exception: ").append(e.getMessage());
                        throw e;
                    } finally {
                        final long end = System.nanoTime();
                        sb.append(". Running time: ")
                            .append((end - start))
                            .append(" ns");
                        logger.debugInternal(method, sb.toString());
                    }
                }
            };
        } else {
            return fn;
        }
    }
}
