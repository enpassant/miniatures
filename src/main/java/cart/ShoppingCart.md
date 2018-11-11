Mire, hogyan jók a típusok?
====

Régóta tervezgettem már ezen blog megírását, de az utolsó lökést ez a bejegyzés adta meg:

> én típusmentes rendszerekben (PHP4, ES3) írtam baromi nagy dolgokat, amiket mások szerint anélkül nem is lehetett volna (dehogyisnem, többmillió soros rendszereink futottak benne stabilan).

**Ez a bejegyzés alapvetően igaz!**

A valóságban tényleg nem sokkal jobbak a statikus típusos nyelvekben írt programok; amilyen kevéssel több munka a típusok kiírása, annyi kevéssel teljesít jobban a dokumentálás, unit teszt és a hibaszám terén.

Miért van ez?
----

Azért, mert a programozók (egyik oldal sem) nem nagyon ismerik a típusokban rejlő lehetőségeket és azokat hogyan lehet jól kihasználni.

Vegyünk egy példát, vásárlói kosár kezelés, és nézzük meg ki, hogyan csinálná.

Először a dinamikusan típusos Javascriptben nézzük.
----

```javascript
class ShoppingCart {
    constructor() {
        this.items = [];
        this.payment = undefined;
    }

    addItem(item) {
        ...
    }
    removeItem(item) {
        ...
    }
    pay(payment) {
        ...
    }
}
```

Nézzük mik a főbb problémák a fenti kóddal!
----

a) Az items és payment member változókban, paraméterekben bármi lehet, szám, szöveg, kis cigány gyerek.

b) Az API és az implementáció egyben van, ami nehezíti az API megértését.

Vannak _különös esetek_:
1. Ha üres a kosár, akkor is megpróbálható egy cikk eltávolítása.
2. Ha üres a kosár, akkor is megpróbálható a fizetési funkció meghívása.
3. Ha már fizetésre került a kosár, akkor is megpróbálható új cikk felvétele.
4. Ha már fizetésre került a kosár, akkor is megpróbálható cikk eltávolítása.
5. Ha már fizetésre került a kosár, akkor is megpróbálható újabb fizetés indítása.

- Szükséges a külön dokumentáció, ahol a fenti esetek mindegyikére ki kell térni részletesen, mit kell ellenőrizni, milyen hibát kell adni!
- Mivel a dokumentáció és a kód szétválik, ezért a dokumentáció és a kód eltérhet egymástól, nem lehet tudni, hogy melyik az _igaz_ és melyiket kellene a másikhoz igazítani!
- Minden fenti esetre szükséges az automatikus teszt, nehogy véletlenül előfordulhassanak a fentiek!
- A fenti különös esetek erős biztonsági kockázatok, ráadásul az ügyintézők életét is megnehezíthetik, illetve komoly pénzekbe kerülhet az üzemeltető cégnek!
- Végül, az implementációnál is le kell kezelni minden különös esetet, kellenek az ellenőrzések és a hibaüzenetek!

Nézzünk egy jellemző megoldást a statikusan típusos Scalaban:
----

```scala
trait ShoppingCartAPI {
    def items: List[Item]
    def payment: Payment

    def addItem(item: Item)
    def removeItem(item: Item)
    def pay(payment: Payment)
}
```
Implementáció:
```scala
class ShoppingCart extends ShoppingCartAPI {
    ...
}
```

Nézzük meg mely problémákat oldotta meg a típusok használata!

Sajnos azt kell észrevennünk, hogy csak az _a)_ és _b)_ pontokat oldotta meg, a különös esetek mindegyike továbbra is problémás!

A fentiekből látszik, hogy egy jellemző dinamikusan típusos és egy jellemző erősen típusos megoldás nem sokban különbözik. *Ezért van igaza a hozzászólónak!*

Lehet ezt jobban csinálni?
----

> *Make illegal states unrepresentable - Yaron Minsky.*

Ha szem előtt tartjuk a fenti idézetet és segítségül hívjuk a típusokat, akkor bizony lehet jobban, sokkal jobban!

```scala
object ShoppingCartAPI {
  sealed trait ShoppingCart extends State
  sealed trait EmptyOrActiveCart extends ShoppingCart

  case object EmptyCart extends EmptyOrActiveCart
  case class ActiveCart(unpaidItems: List[Item]) extends EmptyOrActiveCart

  case class PaidCart(
    paidItems: List[Item],
    payment: Payment
  ) extends ShoppingCart

  type AddToEmpty = (EmptyCart.type, AddItem) => Either[Error, ActiveCart]
  type AddToActive = (ActiveCart, AddItem) => Either[Error, ActiveCart]
  type RemoveFromActive = (ActiveCart, RemoveItem) => Either[Error, EmptyOrActiveCart]
  type PayActive = (ActiveCart, Pay) => Either[Error, PaidCart]
}

```

Nézzük sorjában mi micsoda!

Az elején definiáljuk a vásárlói kosár állapotait (sealed = csak ezek az állapotok vannak, amik ebben a fájlban találhatóak):
1. EmptyCart, nem tartozik hozzá semmi, se cikk, se fizetési mód.
2. ActiveCart, a kifizetetlen cikkeket tárolja, de nincs fizetési mód.
3. PaidCart, a kifizetett cikkeket tárolja és a fizetési módot.

Ez után definiáljuk az egyes kiadható parancsokati:
1. AddToEmpty: hozzáad az üres kosárhoz egy cikket és vagy hibát kapunk, vagy az ActiveCart állapotot.
2. AddToActive: hozzáad az aktív kosárhoz egy újabb cikket és vagy hibát kapunk, vagy egy újabb  ActiveCart állapotot.
3. RemoveFromActive: kivesz egy cikket az aktív kosárból és vagy hibát kapunk, vagy egy EmptyCart vagy egy újabb ActiveCart állapotot.
4. PayActive: kifizetésre kerül az aktív kosár és vagy hibát kapunk, vagy a PaidCart állapotot.

Nézzük meg mely problémákat oldotta meg a típusok ilyetén használata!
----

Nem szükséges külön dokumentáció, minden állapot, parancs és az állapot változások részletesen benne vannak.

Mivel minden egyes állapotra definiálva van, hogy mely parancsot lehet rajta végrehajtani, milyen állapotokba vihetnek át, ezért nem fordulhatnak elő a _különös esetek_!

A fenitek miatt nem szükséges a különös esetekhez szükséges dokumentáció, automatikus teszt, nem kell ezekre az implementációnál figyelni, és megszűntek az azokból eredő hiba lehetőségek és biztonsági kockázatok!

Érvénytelen állapotot nem lehet kifejezni! /_Magyarázat_/
----

Az első példánál kifejezhető volt az az állapot, hogy a kosárhoz szám, szöveg vagy bármely nem Item típusú elemet adjunk, pl. így: shoppingCart.addItem(item.id) vagy shoppingCart.addItem(item.name). Ezek érvénytelen állapotok! Ha véletlenül (elírás vagy más okból) kifejezésre jut, akkor a program működésében hibát okozhat.

1. Egyik megoldás, hogy reménykedünk, hogy nem lesz ilyen. Ha mégis lesz, akkor reméljük, hogy hamar kiderül és minél kevesebb kárt okoz.
2. Másik megoldás, hogy vizsgáljuk, hogy érvénytelen állapotról van-e szó, ha igen, akkor hibát adunk. Ezzel az a probléma, hogy az ügyfél ilyen esetben olyan hibákat kaphat, amikkel nem nagyon tud mit kezdeni, pl. item.id-s elírás esetén: "Az 5 nem cikk, ezért nem lehet a kosárhoz adni!" vagy item.name-es elírás esetén: "Az iPhone nem cikk, ezért nem lehet a kosárhoz adni!".
3. A harmadik megoldás az, hogy az érvénytelen állapotot nem engedjük kifejezhetővé tenni. A most látott esetre ilyenkor megoldás a második példa, ahol az addItem csak Item típust fogad el. A korábban látott item.id-s és item.name-es elírásokra egyszerűen fordítási hibákat fogunk kapni, mert azok az állapotok nem kifejezhetők.

A második példa sem szűkítette le az állapotteret eléggé, a különös esetek érvénytelen állapotok, amik kifejezhetők voltak. Így előfordulhat, hogy egy már kifizetésre került kosárhoz újabb cikket adjanak. Pl. kifejezhető így:
```scala
shoppingCart.addItem(pen);
shoppingCart.pay(mastercard);
shoppingCart.addItem(iPhone);
```

Így egy 500Ft-os kifizetett kosárhoz hozzáadható az 500eFt-os iPhone (előző 1. pont). Ahhoz, hogy ezt elkerüljük az egyes metódusokat kiegészíthetjük vizsgálatokkal és hibaüzenetekkel (előző 2. pont).
Avagy ezeket az érvénytelen állapotokat is kifejezhetetlenné tesszük (előző 3. pont), így jutunk el a 3. példához.
Az utolsó példa esetén nem lehetséges, hogy pl. egy már kifizetésre került kosárhoz új cikket adjanak, vagy töröljenek belőle vagy újra kifizessék (lásd különös esetek). Egyszerűen nem tud olyan kódot írni a kliens programozó, aki a ShoppingCartAPI-t használja, hogy az hibásan leforduljon.


Végszó
----

Sajnos nem minden típusos nyelv ad ekkora segítséget a fentiekhez, mint a Scala, van olyan is, aminek a típusrendszere még egyszerűbb és kifejezőbb (pl. F#, Haskell), de minden típusos nyelven (még a dinamikusokon is) a típusok segítségével lekorlátozhatjuk a reprezántalható állapotok körét, amivel:
1. csökken a szükséges dokumentáció,
2. csökken a szükséges automatikus tesztek száma,
3. az implementációnál nem kell arra figyelni, hogy milyen korlátozások vannak,
4. megszűnnek a hibás reprezentációból eredő hiba lehetőségek,
5. megszűnnek a hibás reprezentációból eredő biztonsági kockázatok,
6. az ügyfelet nem zargatjuk számukra érthetetlen hibaüzenetekkel.

Eleinte többletmunkának tűnik, de bőségesen visszahozza a befektetett energiát.

A fenti cikk létrejöveteléhez nagyban hozzájárult a [fsharpforfunandprofit](http://fsharpforfunandprofit.com/) oldal. Ez egy igazi aranybánya! Akik nem szeretik és/vagy nem használják az F#-ot, azoknak is érdemes végigböngészni, rengeteg jó videó és cikk található rajta.

A hibakezelés, capability (képesség) alapú tervezés/programozás, FP egyszerűen és érthetően, és még további sok jó anyag található rajta; amik alapján akár további blogok is készülhetnek.

Happy typing! ;-)
