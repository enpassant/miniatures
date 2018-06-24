# Hibakezelés

try-catch vs Either

## Enterprise hibakezelés

Enterprise szoftverfejlesztésnél a de facto standard hibakezelésre az **Exception**-ök használata.
Napi szinten használjuk, egyszerűnek és jónak tartjuk.

**Azonban a látszat csal!**

Sajnos, nem nyilvánvaló módon, rengeteg probléma forrása és a látszat ellenére, nagyon megnehezíti az életünket.

**Van-e más lehetőségünk?**

Szerencsére van, az **Either konstrukció** használata. A következőkben egy kicsit megismerjük az Either konstrukciót, majd összehasonlítjuk a try-catch és az Either előnyeit, hátrányait.

## Either

A cikknek nem célja az Either alapos ismertetése, csupán egy alapfokú ismeretet ad, amivel érthetővé válik a cikk további része.

Akik ismerik az Eithert, azok ezt a részt át is ugorhatják.

### Either API

```java
public interface Either<L, R> {
    <B> Either<L, B> map(Function<R, B> f);
    <B> Either<L, B> flatMap(Function<R, Either<L, B>> f);
    Either<L, R> forEach(Consumer<R> f);
    Either<L, R> forEachLeft(Consumer<L> f);
    Either<L, R> orElse(R value);
}
```

Az Either kétféle értéket tud tárolni, vagy egy *left* (baloldali) érteket vagy egy *right* (jobboldali, helyes) értéket. Mi a hibakezelés kapcsán a baloldali értékben a hibát fogjuk tárolni, a jobboldaliban a tényleges, helyes értéket.

### Főbb funkciói

#### Left.of

Készít egy új baloldali Eithert.

Pl.:
```java
Either<Failure, Integer> positiveNumberResult =
    Left.of(new NotPositive());
```

#### Right.of

Készít egy új jobboldali Eithert.

Pl.:
```java
Either<Failure, Integer> positiveNumberResult =
    Right.of(15);
```

#### Pozitív szám készítő

Készítsünk egy olyan segédfüggvényt, ami egy számból készít egy pozitív számot tartalmazó Eithert.

```java
public static Either<Failure, Integer> createPositive(int number) {
    return (number > 0) ?
        Right.of(number) :
        Left.of(new NotPositive());
}
```

#### ```<B> Either<L, B> map(Function<R, B> f);```

Átalakítja a jobboldali értéket a paraméterként megadott függvény segítségével és az eredményül kapott értéket adja vissza jobboldali Either értékként. Ha hiba van benne, akkor nem futtatja a függvényt, hanem az eredeti, hibát tartalmazó Eithert adja vissza.

Pl.:
```java
createPositive(-5).map(number -> number * 2);
// Eredménye: Left(NotPositive)
createPositive(15).map(number -> number * 2);
// Eredménye: Right(30)
```

#### ```<B> Either<L, B> flatMap(Function<R, Either<L, B>> f);```

Átalakítja a helyes értéket a paraméterként megadott függvény segítségével, majd ennek eredményét adja vissza. Ha hiba van benne, akkor nem futtatja a függvényt, hanem a hibát tartalmazó Eithert adja vissza.

Pl.:
```java
createPositive(-5).flatMap(number -> createPositive(number - 10));
// Eredménye: Left(NotPositive)
createPositive(5).flatMap(number -> createPositive(number - 10));
// Eredménye: Left(NotPositive)
createPositive(25).flatMap(number -> createPositive(number - 10));
// Eredménye: Right(15)
```

#### ```Either<L, R> forEach(Consumer<R> f);```

Helyes érték esetén futtatja a megadott consumer függvényt, hiba érték esetén nem futtatja a függvényt. Mindkét esetben az eredeti Eithert adja vissza.

Pl.:
```java
createPositive(-5).forEach(number -> logger.debug("Result: {}", number));
// Eredménye: Left(NotPositive) és nem írja a logot.
createPositive(15).forEach(number -> logger.debug("Result: {}", number));
// Eredménye: Right(15) és a logba a "Result: 15" kerül kiírásra.
```

#### ```Either<L, R> forEachLeft(Consumer<L> f);```

Hibás érték esetén futtatja a megadott consumer függvényt, helyes érték esetén nem futtatja a függvényt. Mindkét esetben az eredeti Eithert adja vissza.

Pl.:
```java
createPositive(-5).forEachLeft(failure -> logger.debug("Failure: {}", failure));
// Eredménye: Left(NotPositive) és a logba a "Failure: NotPositive" kerül kiírásra.
createPositive(15).forEachLeft(failure -> logger.debug("Failure: {}", failure));
// Eredménye: Right(15) és nem írja a logot.
```

#### ```Either<L, R> orElse(R value);```

Hibás érték esetén lecseréli a paraméterben megadott helyes értékre. Helyes érték esetén az eredeti Eithert adja vissza.

Pl.:
```java
createPositive(15).orElse(1);
// Eredménye: Right(15)
createPositive(-5).orElse(1);
// Eredménye: Right(1).
```

## Példák az összehasonlításhoz

### Either

```java
Either<Failure, DatabaseConfig> databaseConfigOpt =
    DatabaseConfig.load("conf/" + configFileName);

Either<Failure, Repository> repositoryOpt =
    databaseConfigOpt.flatMap(
        databaseConfig -> Repository.load(
            databaseConfig.driver,
            databaseConfig.connectionUrl));

repositoryOpt.flatMap(repository ->
    CommunicationConfig.loadFromDB(repository).map(
        communicationConfig ->
            Main.run(repository, communicationConfig))
).forEachLeft(failure -> logger.error("Hiba: {}", failure));
```

1. Betölti az adatbázis configot.
2. Az adatbázis config alapján betölti (létrehozza) a repositoryt. (Kapcsolódik adatbázishoz, példa lekérdezést végez, hogy minden rendben)
3. A repository alapján betölti adatbázisból a kommunikációs config-ot.
4. A repository és a kommunikációs config alapján futtatja a programot.
5. Ha a fenti pontok bármelyikénél hiba volt, akkor kiírja a hibát és leáll a program.

### try-catch

```java
try {
    DatabaseConfig databaseConfig =
        DatabaseConfig.load("conf/" + configFileName);
    Repository repository =
        Repository.load(databaseConfig.driver, databaseConfig.connectionUrl);
    CommunicationConfig communicationConfig =
        CommunicationConfig.loadFromDB(repository);
    Main.run(repository, communicationConfig);
} catch (Exception e) {
    logger.error("Hiba: {}", e);
}
```

## A látszat csal!

Ha megnézzük a két példát, akkor a try-catch verzió egyszerűbbnek, könnyebben olvashatónak tűnik.
Az Either-est látszólag elbonyolítják a lambdák, map-ek, flatMap-ek és a forEachLeft.

Az egyszerű és a könnyen olvasható definíciójától függ, hogy ez tényleg így is van-e.
Ha úgy definiáljuk ezeket, hogy ami kinézetre egyszerűbb, könnyebben olvasható, mert sokszor találkozom vele és ismerős a felépítése, akkor tényleg a try-catch az egyszerűbb.

## Egyszerűbb és könnyebben olvasható definíciója

1. Minél kevesebb mindent kell a fejemben tartanom.
2. Minél kevesebb dolognak kell utánanéznem, hogy a tényleges működést megértsem.
3. Minél könnyebben tudom az adott kódot refaktorálni.
4. Minél könnyebben tudom módosítani, úgy, hogy közben nem rontom el itt vagy máshol a program működését.
5. Minél kevesebb hibalehetőség van.
6. Minél könnyebben tudom az adott kódot újra felhasználni (compose).

Ezen definíció alapján viszont az Either-es megoldás sokkal egyszerűbb és könnyebben olvashatóbb. Nézzük meg ezeket részletesebben!

### Az egyszerűség vizsgálata

Ha megnézem a kódot, akkor Either esetén minden egyes függvényhívásról tudom, hogy adhat vissza hibát vagy sem. Ezt jelzi a visszatérési érték típusa, illetve a használt speciális függvény (pl. map, flatMap, forEachLeft).

A try-catch esetén fogalmam sincs erről, csak annyit sejtek, hogy a try-catch-en belül valamelyik adhat vissza hibát, de ez sem biztos.


