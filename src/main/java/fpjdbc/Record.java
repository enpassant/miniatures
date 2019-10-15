package fpjdbc;

import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Consumer;

import exception.Either;
import exception.Failure;
import exception.Left;
import exception.Right;

public class Record {
    private final Map<String, Object> values;

    private Record(Map<String, Object> values) {
        this.values = values;
    }

    public Optional<Object> field(String name) {
        return Optional.ofNullable(values.get(name));
    }

    public <T> Either<Failure, T> as(Class<T> type) {
        try {
            Constructor constructors[] = type.getDeclaredConstructors();
            Constructor ctRet = constructors[0];
            //for (int i = 0; i < constructors.length; i++) {
                //Constructor ct = constructors[i];
                //System.out.println("name = " + ct.getName());
                //System.out.println("decl class = " +
                        //ct.getDeclaringClass());
                //Class pvec[] = ct.getParameterTypes();
                //for (int j = 0; j < pvec.length; j++)
                    //System.out.println("param #"
                            //+ j + " " + pvec[j]);
            //}
            Object arglist[] = values.values().toArray();
            return Right.of((T) ctRet.newInstance(arglist));
        } catch(Exception e) {
            return Left.of(
                Failure.of(e.getClass().getSimpleName(), Failure.EXCEPTION, e)
            );
        }
    }

    public static <T> Either<Failure, T> ofAs(ResultSet rs, Class<T> type) {
        return of(rs).flatMap(record -> record.as(type));
    }

    public static <T> ThrowingFunction<ResultSet, Either<Failure, T>, SQLException>
        expandAs(Class<T> type)
    {
        return rs -> ofAs(rs, type);
    }

    public static Record build(Consumer<Builder> factory) {
        final Builder builder = new Builder();
        factory.accept(builder);
        return builder.build();
    }

    public static Either<Failure, Record> of(ResultSet rs) {
        return Failure.tryCatch(() -> {
            final Map<String, Object> values = new LinkedHashMap<>();
            final ResultSetMetaData rsmd = rs.getMetaData();
            final int numberOfColumns = rsmd.getColumnCount();

            for (int i=1; i<=numberOfColumns; i++) {
                values.put(rsmd.getColumnLabel(i).toLowerCase(), rs.getObject(i));
            }

            return new Record(values);
        });
    }

    @Override
    public String toString() {
        final String fieldStr = values.entrySet()
            .stream()
            .map(entry -> entry.getKey() + " -> " + entry.getValue())
            .reduce((s1, s2) -> s1 + ", " + s2)
            .orElse("");

        return "Record(" + fieldStr + ")";
    }

    public static final class Builder {
        private final Map<String, Object> values = new LinkedHashMap<>();

        public Builder field(String name, Object value) {
            values.put(name, value);
            return this;
        }

        private Record build() {
            return new Record(values);
        }
    }
}
