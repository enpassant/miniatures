# Totális függvények

Sokak által ismert a függvények és az eljárások (utasítások) közötti különbség. Az is ismert, hogy milyen előnyei vannak a függvényeknek az eljárásokkal szemben, amiért célszerűbb inkább azokat használni.
Az már kevésbé ismert, hogy a függvényeket két nagy csoportba sorolhatjuk, parciális és totális. Ebben a cikkben megvizsgáljuk a parciális és totális függvények tulajdonságait, és azt, hogy miért célszerűbb utóbbiakat alkalmazni.

## Függvények

A [függvényekről a wikipédián](https://hu.wikipedia.org/wiki/F%C3%BCggv%C3%A9ny_(matematika)) sok mindent megtudhatunk, de minket most csak a parciális és totális függvények definíciója érdekel.

A programozás kapcsán mi a következő definíciókat fogjuk használni:
- **függvény**: olyan leképezés, amely input érték(ek)hez output érték(ek)et rendel.
- **totális függvény**: olyan leképezés, amely minden inputhoz pontosan egy outputot rendel.
- **parciális függvény**: olyan függvény, ami nem totális. Másképp, van legalább egy input, amihez nincs output meghatározva.

## Parciális függvény

Nézzünk egy példát a parciális függvényre! Írjunk egy függvényt, ami kiszámolja, hogy egy szám egy másik számnak hány százaléka!

```
private static int percent(int a, int b) {
    return a * 100 / b;
}
```

Elsőre fel sem tűnik, hogy ez a függvény parciális, de ha jobban megvizsgáljuk, akkor észrevesszük, hogy `b = 0` esetén nem kapunk vissza outputot, hanem a függvényünk kivételt fog dobni (ArithmeticException).

A parciális függvények előnyei:
- könnyű megírni,
- tömör (egyszerűnek látszik),
- ismerős, gyakran találkozunk ilyen megoldással.

A hátrányai nem annyira szembeötlőek, emiatt nem is annyira ismert. Akkor vehetjük észre, ha próbáljuk az ilyen kódot felhasználni, megérteni, tesztet írni rá vagy módosítani.

A teszt írása és a felhasználása egy tőről fakad, hiszen a teszt a kód használatára mutat példát. Nézzünk akkor példát a használatára!

A [Pair osztály](https://github.com/enpassant/miniatures/blob/master/src/main/java/total/Pair.java) segítségével, hozzunk létre szám párokat:
```
static final List<Pair<Integer, Integer>> pairs = Arrays.asList(
    Pair.of(6, 2),
    Pair.of(2, 6),
    Pair.of(2, 0),
    Pair.of(2, 8)
);
```

Ezekre számoljuk ki a százalékokat és írassuk ki:

```
pairs.stream()
    .map(Total::percent)
    .forEach(System.out::println);
```

Nagyon szép és egyszerű megoldás, de sajnos a `Pair.of(2, 0)` értéknél kivételt dob, így a feldolgozás megszakad.

Kapjuk el a kivételt, hogy minden adatot fel tudjunk dolgozni:

```
pairs.stream()
    .map(pair -> {
        try {
            return Optional.of(Total.percent(pair));
        } catch (ArithmeticException e) {
            return Optional.empty();
        }
    })
    .forEach(System.out::println);
```

A le nem kezelt input problémát okoz a függvényünk készítésénél, mert nem tudjuk, hogy mit kezdjünk vele. Vissza nem tudunk adni a megadott értékkészletből egyetlen elemet sem. Ha kivételt adunk vissza, akkor pedig a hívó szembesülhet azzal, hogy nem tud mit kezdeni az adott kivétellel.

Ez különösen akkor problémás, amikor a beérkező hibás érték és a függvényünk, amelyiknek ezzel kezdenie kellene valamit, között több függvény hívás is van, mivel a visszaadott válasz minden egyes lépésben problémát fog okozni.

Ha például van egy hívási láncunk: `a -> b -> c -> d -> e`, ahol a rendszerbe az `a`-nál került be a hibás érték (pl. felhasználói megadás, vagy külső service hívással), az érték feldolgozása az `e`-nél történik, akkor a visszaadott kivétel a `d`, `c`, `b` és `a` pontban is gondot okozhat.

Amint látjuk, a parciális függvény használata nem egyszerű, mert figyelni kell a le nem kezelt esetekre, ezért a tesztelés sem egyszerű, a megértés sem az, így a módosítás sem az, ráadásul könnyen hibás működést kaphatunk.

### Totális függvény

Alakítsuk át a parciális függvényünket totálissá. Ezt kétféleképp tehetjük meg.

#### Output körének bővítése

Egyik megoldás az, hogy a le nem kezelt inputhoz rendelünk output értéket.

```
private static Optional<Integer> percentOptional(int a, int b) {
    if (b == 0) {
        return Optional.empty();
    } else {
        return Optional.of(a * 100 / b);
    }
}
```

Nézzük meg, hogy az így totálissá vált függvényünk használata egyszerűbb lett-e!

```
pairs.stream()
    .map(Total::percentOptional)
    .forEach(System.out::println);
```

Egyszerűbb használni, így egyszerűsödik a teszt is, de külön le kell teszteljük mind a két ágat, hogy helyes bemenetre is és helytelenre is jó értéket ad-e vissza.
Az a probléma itt is megmarad, hogy a hívó szembesülhet azzal, hogy nem tud mit kezdeni a visszaadott hiba értékkel.

#### Input körének szűkítése

Másik megoldás a parciális függvény totálissá tételéhez az, hogy az érvénytelen input értéket, megfelelő típussal, kizárjuk a megadható értékek köréből.
Ezt az elvet (Make illegal state unrepresentable) nem csak az inputoknál, hanem mindenhol használhatjuk, ahol adatokat reprezentálunk.
Készítsünk egy [NotZeroInt](https://github.com/enpassant/miniatures/blob/master/src/main/java/total/NotZeroInt.java) osztályt, amivel csak `nem nulla értékű egész számok` reprezentálhatók.
Ennek használatával így néz ki a százalék számoló függvényünk:

```
private static int percentStrict(int a, NotZeroInt b) {
    return a * 100 / b.number;
}
```

Nézzük meg az így totálissá vált függvényünk használatát:

```
pairs.stream()
    .map(pair -> pair.rightOptMap(NotZeroInt::of))
    .map(pairOpt -> pairOpt.map(Total::percentStrict))
    .forEach(System.out::println);
```

Egy nagyon kicsit bonyolultabb lett a használata, mivel a hívónak gondoskodnia kell arról, hogy a megfelelő típusú értéket előállítsa és az esetleges hibát, ami ott és akkor keletkezik, azt megfelelően lekezelje.

Ha a hibás értékeket csak egyszerűen kihagyjuk az eredményből, akkor még szembeötlőbb a használat egyszerűsége.

```
pairs.stream()
    .map(pair -> pair.rightOptMap(NotZeroInt::of))
    .filter(Optional::isPresent)
    .map(Optional::get)
    .map(Total::percentStrict)
    .forEach(System.out::println);
```

Ebből a használata mindössze ennyi: `.map(Total::percentStrict)`.

A tesztelés nagyon egyszerű lett, hiszen csak egyetlen esetet kell tesztelnünk. Itt nem jelentkezik a korábbi probléma, hiszen a hívó nem kap vissza hibaértéket.

Ha a korábban látott hívási láncot nézzük (`a -> b -> c -> d -> e`), akkor mindjárt az `a` pontnál át kell alakítanunk a bejött értéket a szigorúbb típusúvá, így már azonnal kezdenünk kell valamit a hibás értékkel, amit könnyedén meg is tudunk tenni (pl. a felhasználónak vagy a külső service-nek jelezzük a problémát).
Lefelé már csak a szükséges típus megy le, így nem fog gondot okozni se az `e`-nél, sem a visszaadott értékekkel a `d`, `c`, `b` és `a` pontokban.

## Összefoglalás

Elsődlegesen olyan totális függvényeket használjunk, ahol az input köre le van szűkítve a legális állapotokra, ha ez nem megy valamiért, akkor a visszatérési értékek körét bővítsük, hogy totális függvényünk legyen!

Ami elsőre többlet munkának tűnik (`NotZeroInt` létrehozása), az többszöresen megtérül az egyszerűbb feldolgozásnál, az egyszerűbb teszteknél, az érthetőbb, hibamentesebb és jól karbantartható kódnál!
