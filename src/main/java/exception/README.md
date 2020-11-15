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
    R orElse(R value);
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

#### ```R orElse(R value);```

Hibás érték esetén lecseréli a paraméterben megadott helyes értékre. Helyes érték esetén az eredeti Eithert adja vissza.

Pl.:
```java
createPositive(15).orElse(1);
// Eredménye: 15
createPositive(-5).orElse(1);
// Eredménye: 1.
```

## Példák az összehasonlításhoz

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

1. Betölti az adatbázis configot.
2. Az adatbázis config alapján betölti (létrehozza) a repositoryt. (Kapcsolódik adatbázishoz, példa lekérdezést végez, hogy minden rendben)
3. A repository alapján betölti adatbázisból a kommunikációs config-ot.
4. A repository és a kommunikációs config alapján futtatja a programot.
5. Ha a fenti pontok bármelyikénél hiba volt, akkor kiírja a hibát és leáll a program.

### Either

```java
Either<Failure, DatabaseConfig> databaseConfigResult =
    DatabaseConfig.load("conf/" + configFileName);

Either<Failure, Repository> repositoryResult = databaseConfigResult.flatMap(databaseConfig ->
    Repository.load(databaseConfig.driver, databaseConfig.connectionUrl));

Either<Failure, Repository> communicationConfigResult = repositoryResult.flatMap(repository ->
    CommunicationConfig.loadFromDB(repository);

communicationConfigResult.map(communicationConfig ->
    Main.run(repository, communicationConfig))
).forEachLeft(failure ->
digitlogger.error("Hiba: {}", failure)
);
```

## A látszat csal!

Ha megnézzük a két példát, akkor a try-catch verzió egyszerűbbnek, könnyebben olvashatónak tűnik.
Az Either-est látszólag elbonyolítják a lambdák, a flatMap-ek, a map és a forEachLeft.

Az egyszerű és a könnyen olvasható definíciójától függ, hogy ez tényleg így is van-e.
Ha úgy definiáljuk ezeket, hogy ami kinézetre egyszerűbb, könnyebben olvasható, mert sokszor találkozom vele és ismerős a felépítése, akkor tényleg a try-catch az egyszerűbb.

Egy másik lehetséges definíció erre, hogy amit könnyebb megírni, létrehozni.
Ennek a definíciónak is megvan a maga előnye: ezen technikák és eszközök nagyon jók prototípusok létrehozásához. Ilyenek pl. dinamikus típusosság, exception, Dependency Injection container (DIc), annotation, ORM.

Ismert, hogy általános projekteknél (nem prototípus), a kód megírásához képest, ami egyszer történik, a kód refaktorálása, módosítása ennek többszöröse, elég megnézni a verziókezelő rendszerünkben a módosítások számát, illetve ezekhez képest is a kód olvasása átlagosan tízszer annyiszor történik.

Ezek alapján egyszerűség szempontjából célszerűbb ezeket inkább előtérbe helyezni!

## Egyszerűbb és könnyebben olvasható definíciója

1. Minél kevesebb mindent kell a fejemben tartanom.
2. Minél kevesebb dolognak kell utánanéznem, hogy a tényleges működést megértsem.
3. Minél könnyebben tudom az adott kódot refaktorálni.
4. Minél könnyebben tudom módosítani, úgy, hogy közben nem rontom el itt vagy máshol a program működését.
5. Minél kevesebb hibalehetőség van.
6. Minél könnyebben tudom az adott kódot újra felhasználni.

Ezen definíció alapján viszont az Either-es megoldás sokkal egyszerűbb és könnyebben olvashatóbb. Nézzük meg ezeket részletesebben!

Nagyon jó videó a témában, Venkat Subramaniam
[Az egyszerűség művészete](https://www.youtube.com/watch?v=R4C_JciDsuo)
című előadása. Ennek a témának én olyan fontosságot tulajdonítok, hogy Bsc-n is és Msc-n is legalább egy-egy félévet szánnék a megismerésére, megértésére és alkalmazásának megtanulására.

A fenti előadás nem szükséges ezen cikkhez, de annak megtekintése (akár többszöri), megértése nagyban javítja ennek a cikknek megértését is.

Ez a téma elég hatalmas, ebben a cikkben csak a hibakezelés részére koncentrálunk.

### Mi ad vissza hibát?

Ha megnézem a kódot, akkor Either esetén minden egyes függvényhívásról tudom, hogy adhat vissza hibát vagy sem. Ezt jelzi a visszatérési érték típusa, illetve a használt speciális függvény (pl. map, flatMap).

A try-catch esetén fogalmam sincs erről, csak annyit sejtek, hogy a try-catch-en belül valamelyik adhat vissza hibát, de ez sem biztos.

Miért gond ez?

Ha módosítani, refaktorálni akarom az adott kódot, akkor minden egyes sornál utána kell néznem a dokumentációban, hogy dob-e exceptiont. Még rosszabb, ha nincs jól vezetve, akkor a forrásokban kellene megnéznem teljes mélységben az összes függvény összes függvényét. Nem is elég ennek utánanéznem, hanem ezt mind meg is kell jegyezzem.

### _"Az ezer GoTo általi halál"_

Egy exception dobás tulajdonképpen ugyanaz, mintha egy goto utasítást hajtanánk végre.
Ami kicsit még rosszabbá teszi, hogy pontosan nincs is meghatározva, hogy hová fog ugrani, ráadásul több helyre is ugorhat.

Azt a függvényt, ahol dobjuk az exceptiont, több másik függvényből is hívhatjuk, azokat szintén, és így tovább. Nagyon bonyolult hívási gráfunk lehet, amelynek minden egyes ágán elkaphatjuk a dobott exceptiont. Ha megvan a hívási gráfunk és minden egyes lehetőséget végignéznénk, akkor még az is megnehezíti a dolgunkat, hogy nem feltétlen az adott exceptiont kapjuk el, hanem egy ősét. Tehát még az is egy nehezítő tényező, hogy tudnunk kell az exceptionök leszármazási hierarchiáját.

### Hiba elfedése

Ha egy try részen belül több utasítás is van, ami dobhatja a catch ágban elkapott hibát, akkor ezek elfedik egymást, nem tudhatjuk, hogy melyiket kapjuk el.
Ugyanez a helyzet akkor is, ha egy metódus szignatúrájában throws kulcsszóval jelezzük a hiba dobását.

**Mi a gond ezzel?**

Azért tesszük egy try blokkba az utasításokat (vagy jelezzük throws kulcsszóval), mert mindegy melyik dobja a hibát.

Ha a blokkban szereplő utasítások közül valamelyik nem dob olyan hibát, de később egy módosítás során már dob, akkor a fordítótól nem kapunk visszajelzést, hogy ezen részt át kellene nézzük, mert nem biztos, hogy úgy van kezelve ott a hiba, ahogyan számunkra megfelelő volna.

### Nem újrafelhasználható

### Checked exception

A Checked exception egy öszvér megoldás, az exception kezelés hátrányait csak kissé csökkenti, miközben hozza a direkt hibakezelés (either) hátrányait, az előnyeiből csak nagyon keveset.

[Ebben a cikkben](https://www.javaworld.com/article/3142626/core-java/are-checked-exceptions-good-or-bad.html) szépen össze vannak szedve a hátrányai és az előnyei a nem checked exceptionhöz képest.

### Példák

Sokkal könnyebben érthető, ha nézünk a fentiekre egy pár példát.

[Mi ad vissza hibát?](#mi-ad-vissza-hib-t-)

Induljunk el ezzel:

, ahol a `calc`, `calcA`, `calcB` és `calcAB` szignatúrája ilyen:
```java
    public static int calc(int value);
    public static int calcA(int value);
    public static int calcB(int value);
    public static int calcAB(int a, int b);
```

Ennyiből sajnos fogalmunk sincs, hogy dob-e valamelyik exceptiont. Ahhoz, hogy ezt megtudjuk végig kell nézzük a `calc`, `calcA`, `calcB` és `calcAB` forrását és az azokból hívott összes metódus forrását, és így tovább.

Legyenek a következő egyszerű megvalósítások:
```java
    public static int calc(int value) {
        final int a = calcA(value);
        final int b = calcB(value);
        return calcAB(a, b);
    }

    public static int calcA(int value) {
        return value * 5;
    }

    public static int calcB(int value) {
        return 10000 / (20 - calcA(10 - value));
    }

    public static int calcAB(int a, int b) {
        return a + b;
    }
```
Nagyon egyszerű megvalósítások, mindenhol csak az alap aritmetikai műveleteket alkalmaztuk.
Remek, akkor ezek biztosan nem adnak hibát!
A látszat ismét csal!
Az osztás művelet sajnot exception-t dob, ha nullával akarjuk osztani az osztandót.
Ha átírjuk a `calcB` függvényt ilyenre:
```java
    public static int calcB(int value) {
        final int divisor = (20 - calcA(10 - value));
        if (divisor == 0) {
            throw new ArithmeticException("/ by zero");
        }
        return 10000 / divisor;
    }
}
```
, akkor a működés pontosan ugyanaz marad, de sokkal szembeötlőbbé válik, hogy ez a függvény hibát dob bizonyos esetekben.

Még jobb, ha a szignatúrában (is) jelezzük, hogy ez a függvény hibát dob:
```java
    public static int calcB(int value) throws ArithmeticException {
        return 10000 / (20 - calcA(10 - value));
    }
}
```
, mert ilyenkor a függvény dokumentációjából is már látszik, nem kell a forrását megnézni.
Ugyanezt meg kell csinálnunk az összes olyan függvényen, amely ezt a függvényt hívja, a példánknál a `calc` függvényre is.

Nézzük meg ugyanezt `Either`-rel:
```java
    public static int calcA(int value);
    public static Either<Failure, Integer> calcB(int value);
    public static int calcAB(int a, int b);
    public static Either<Failure, Integer> calc(int value);
```
Már a szignatúrából szépen látszik, hogy melyik függvény ad vissza hibát és melyik nem.

Nézzük meg az implementációjukat:
```java
    public static Either<Failure, Integer> calc(int value) {
        final int a = calcA(value);
        return calcB(value)
            .map(b -> calcAB(a, b));
    }

    public static int calcA(int value) {
        return value * 5;
    }

    public static Either<Failure, Integer> calcB(int value) {
        final int divisor = (20 - calcA(10 - value));
        return divisor == 0 ?
            Left.of(Failure.of("/ by zero")) :
            Right.of(10000 / divisor);
    }

    public static int calcAB(int a, int b) {
        return a + b;
    }
```
Egy kicsit bonyolultabbnak tűnik a kód, de ez abból adódik, hogy itt mindenhol kezdünk valamit a hibával, míg az exception-nél hagytuk tovább dobódni.
