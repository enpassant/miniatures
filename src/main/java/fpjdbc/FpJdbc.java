package fpjdbc;

import java.util.Arrays;

import exception.Either;
import exception.Failure;

final class FpJdbc {
    public static void main(String[] args) {
        System.out.println("FP JDBC");

        final Either<Failure, Repository> repositoryOrFailure = Repository.load(
            "org.h2.Driver",
            "jdbc:h2:mem:test",
            "SELECT 1"
        ).forEach(repository -> {
            fill(repository);

            final Either<Failure, Person> personOrFailure1 =
                repository.querySingle(
                    "SELECT id, name, age FROM person WHERE id = 2 and age < 30",
                    rs -> new Person(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age")
                    )
                );

            System.out.println("personOrFailure1: " + personOrFailure1);

            final Either<Failure, Person> personOrFailure2 =
                repository.querySingleAs(
                    Person.class,
                    "SELECT id, name, age FROM person p WHERE id = ? and age < ?",
                    2, 30
                );

            System.out.println("personOrFailure2: " + personOrFailure2);

            final Record record = Record.build(builder -> builder
                .field("id", 3)
                .field("name", "Jake Doe")
                .field("age", 13)
            );
            System.out.println("Record: " + record);

            final Either<Failure, Person> personOrFailure3 =
                record.as(Person.class);
            System.out.println("personOrFailure3: " + personOrFailure3);

            repository.close();
        });

        System.out.println(repositoryOrFailure);
    }

    private static void fill(Repository repository) {
        Arrays.asList(
            "CREATE TABLE person(id INT, name VARCHAR(30), age INT)",
            "INSERT INTO person VALUES(1, 'John Doe', 32)",
            "INSERT INTO person VALUES(2, 'Jane Doe', 28)"
        ).stream().forEach(repository::update);
    }

    public static class Person {
        private final int id;
        private final String name;
        private final int age;

        Person(final int id, final String name, final int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public String toString() {
            return "Person(" + id + ", " + name + ", " + age + ")";
        }
    }
}
