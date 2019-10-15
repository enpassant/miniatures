package fpjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import exception.Either;
import exception.Failure;
import exception.Left;
import exception.Right;

public class Repository implements AutoCloseable {
    private String connectionUrl;
    private Connection conn;

    private Repository(Connection conn, String connectionUrl) {
        this.conn = conn;
        this.connectionUrl = connectionUrl;
    }

    public void close() {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            //logger.error("close error", e);
        }
    }

    public void openConnection() {
        try {
            if (conn.isClosed()) {
                //logger.error("Az adatbázis kapcsolat megszakadt, újrakapcsolódás.");
                conn = DriverManager.getConnection(connectionUrl);
            }
        } catch (SQLException e) {
            //logger.error("openConnection error", e);
        }
    }

    public static Either<Failure, Repository> load(
        String driver,
        String connectionUrl,
        String testSql
    ) {
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName(driver);

            conn = DriverManager.getConnection(connectionUrl);
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(testSql);
            rs.close();
            return Right.of(new Repository(conn, connectionUrl));
        } catch (Exception e) {
            //logger.error("load error", e);
            return Left.of(
                Failure.of(e.getClass().getSimpleName(), Failure.EXCEPTION, e)
            );
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se) {
            }
        }
    }

/*
    public <T> Either<Failure, Stream<T>> query(
            String sql,
            ThrowingFunction<ResultSet, T, SQLException> createObject
            ) {
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);

            Stream.Builder<T> builder = Stream.builder();
            while(rs.next()) {
                T createdObject = createObject.apply(rs);
                builder.accept(createdObject);
            }
            rs.close();

            return Right.of(builder.build());
        } catch (Exception e) {
            logger.error("Query error", e);
            return Left.of(new Failures.SqlQueryFailed());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                logger.error("Query close error", e);
            }
        }
            }
*/

/*
    public <T> Either<Failure, T> querySingleAs(
        Class<T> type,
        String sql
    ) {
        return querySingle(sql, Record.expandAs(type)).flatten();
    }

    public <T> Either<Failure, T> querySingle(
        String sql,
        ThrowingFunction<ResultSet, T, SQLException> createObject
    ) {
        Statement stmt = null;

        try {
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);

            T createdObject = null;
            while (rs.next()) {
                createdObject = createObject.apply(rs);
                if (createdObject != null) {
                    break;
                }
            }
            rs.close();
            return (createdObject == null) ?
                Left.of(Failure.of("SqlQueryFailed")) :
                Right.of(createdObject);
        } catch (Exception e) {
            //logger.error("Query single error", e);
            return Left.of(
                Failure.of(e.getClass().getSimpleName(), Failure.EXCEPTION, e)
            );
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                //logger.error("Query single close error", e);
            }
        }
    }
*/

/*
    public <T> Either<Failure, Stream<T>> queryPrepared(
            String sql,
            ThrowingConsumer<PreparedStatement, SQLException> prepare,
            ThrowingFunction<ResultSet, T, SQLException> createObject
            ) {
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            prepare.accept(stmt);

            ResultSet rs = stmt.executeQuery();

            Stream.Builder<T> builder = Stream.builder();
            while(rs.next()) {
                T createdObject = createObject.apply(rs);
                builder.accept(createdObject);
            }
            rs.close();

            return Right.of(builder.build());
        } catch (Exception e) {
            logger.error("Query prepared error", e);
            return Left.of(new Failures.SqlQueryFailed());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                logger.error("Query prepared close error", e);
            }
        }
            }
*/

    public <T> Either<Failure, T> querySingle(
        String sql,
        ThrowingFunction<ResultSet, T, SQLException> createObject
    ) {
        ThrowingConsumer<PreparedStatement, SQLException> prepare = ps -> {};

        return querySinglePrepared(
            sql,
            prepare,
            createObject
        );
    }

    public <T> Either<Failure, T> querySingleAs(
        Class<T> type,
        String sql,
        Object... params
    ) {
        ThrowingConsumer<PreparedStatement, SQLException> prepare = ps -> {
            for (int i=0; i<params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        };

        return querySinglePrepared(
            sql,
            prepare,
            Record.expandAs(type)
        ).flatten();
    }

    public <T> Either<Failure, T> querySinglePreparedAs(
        Class<T> type,
        String sql,
        ThrowingConsumer<PreparedStatement, SQLException> prepare
    ) {
        return querySinglePrepared(sql, prepare, Record.expandAs(type)).flatten();
    }

    public <T> Either<Failure, T> querySinglePrepared(
        String sql,
        ThrowingConsumer<PreparedStatement, SQLException> prepare,
        ThrowingFunction<ResultSet, T, SQLException> createObject
    ) {
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            prepare.accept(stmt);

            ResultSet rs = stmt.executeQuery();

            T createdObject = null;
            while (rs.next()) {
                createdObject = createObject.apply(rs);
                if (createdObject != null) {
                    break;
                }
            }
            rs.close();
            return (createdObject == null) ?
                Left.of(Failure.of("SqlQueryFailed")) :
                Right.of(createdObject);
        } catch (Exception e) {
            //logger.error("Query single error", e);
            return Left.of(
                Failure.of(e.getClass().getSimpleName(), Failure.EXCEPTION, e)
            );
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                //logger.error("Query single close error", e);
            }
        }
    }

    public Either<Failure, Integer> update(String sql) {
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.executeUpdate();

            ResultSet generatedKeysRS = stmt.getGeneratedKeys();

            Right<Failure, Integer> result =
                Right.of(generatedKeysRS.next() ? generatedKeysRS.getInt(1) : 0);

            generatedKeysRS.close();

            return result;
        } catch (Exception e) {
            //logger.error("Update error", e);
            return Left.of(
                Failure.of(e.getClass().getSimpleName(), Failure.EXCEPTION, e)
            );
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                //logger.error("Update prepared close error", e);
            }
        }
    }

    public Either<Failure, Integer> update(
        String sql,
        ThrowingConsumer<PreparedStatement, SQLException> prepare
    ) {
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            prepare.accept(stmt);

            stmt.executeUpdate();

            ResultSet generatedKeysRS = stmt.getGeneratedKeys();

            Right<Failure, Integer> result =
                Right.of(generatedKeysRS.next() ? generatedKeysRS.getInt(1) : 0);

            generatedKeysRS.close();

            return result;
        } catch (Exception e) {
            //logger.error("Update error", e);
            return Left.of(
                Failure.of(e.getClass().getSimpleName(), Failure.EXCEPTION, e)
            );
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                //logger.error("Update prepared close error", e);
            }
        }
    }

/*
    public <T> Either<Failure, T> runInTransaction(
            Supplier<Either<Failure, T>> supplier
            ) {
        try {
            conn.setAutoCommit(false);
            Either<Failure, T> result = supplier.get();

            if (result.right().isPresent()) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return result;
        } catch (SQLException e) {
            logger.error("A tranzakció nem futott le.", e);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                logger.error("A tranzakció visszavonás sem futott le.", e);
            }
            return Left.of(new Failures.SqlQueryFailed());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Az autocommit visszaállítsa nem futott le", e);
            }
        }
    }
*/
}
