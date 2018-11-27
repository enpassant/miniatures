package exception;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Failure {
    public static final String EXCEPTION = "EXCEPTION";
    protected final String code;
    protected final Map<String, Object> params;

    private Failure(String code, Map<String, Object> params) {
        this.code = code;
        this.params = params;
    }

    public static Failure of(String code) {
        return new Failure(code, new HashMap<>());
    }

    public static Failure of(String code, String key, Object value) {
        Map<String, Object> params = new HashMap<>();
        params.put(key, value);
        return new Failure(code, params);
    }

    @SafeVarargs
    public static Failure of(String code, Tuple2<String, Object>... tuples) {
        return new Failure(code, Tuple2.toMap(tuples));
    }

    public static <E extends Exception, R> Either<Failure, R> tryCatch(
        SupplierCatch<E, R> process
    ) {
        try {
            return Right.of(process.get());
        } catch(Exception e) {
            return Left.of(
                Failure.of(e.getClass().getSimpleName(), EXCEPTION, e)
            );
        }
    }

    public static <E extends Exception, R> Either<Failure, R> tryCatch(
        String code,
        SupplierCatch<E, R> process
    ) {
        try {
            return Right.of(process.get());
        } catch(Exception e) {
            return Left.of(
                Failure.of(code, EXCEPTION, e)
            );
        }
    }

    public static <E extends Exception, R> Optional<R> tryCatchOptional(
        SupplierCatch<E, R> process
    ) {
        try {
            return Optional.of(process.get());
        } catch(Exception e) {
            return ignoreException(e, Optional.empty());
        }
    }

    public String getCode() {
        return code;
    }

    public Set<String> getParamNames() {
        return params.keySet();
    }

    @SuppressWarnings("unchecked")
    public <T> T getParamValue(String paramName) {
        return (T) params.get(paramName);
    }

    public String format(String pattern) {
        return MessageFormat.format(
            pattern,
            params.values().toArray(new Object[0])
        );
    }

    @Override
    public String toString() {
        Optional<String> paramStrOpt = params.entrySet()
            .stream()
            .map(entry -> entry.getKey() + " -> " + entry.getValue())
            .reduce((s1, s2) -> s1 + ", " + s2);

        return "Failure(" + code + ", " + paramStrOpt + ")";
    }

    public static <E extends Exception, R> R ignoreException(E e, R r) {
        e.getCause();
        return r;
    }

    @FunctionalInterface
    public static interface SupplierCatch<E extends Exception, R> {
        R get() throws E;
    }
}
