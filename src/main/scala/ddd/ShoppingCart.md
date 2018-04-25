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

Milyen gondot okozhatnak ezek?
----

### Nézzük először az a) pontot!

Kifejezhető az az állapot, hogy a kosárhoz szám, szöveg vagy bármely nem Item típusú elemet adjunk, pl. így: shoppingCart.addItem(item.id) vagy shoppingCart.addItem(item.name). Ezek érvénytelen állapotok! Ha véletlenül (elírás vagy más okból) kifejezésre jut, akkor a program működésében hibát okozhat.

1. Egyik megoldás, hogy reménykedünk, hogy nem lesz ilyen. Ha mégis lesz, akkor reméljük, hogy hamar kiderül és minél kevesebb kárt okoz.
2. Másik megoldás, hogy vizsgáljuk, hogy érvénytelen állapotról van-e szó, ha igen, akkor hibát adunk. Ezzel az a probléma, hogy az ügyfél ilyen esetben olyan hibákat kaphat, amikkel nem nagyon tud mit kezdeni, pl. item.id-s elírás esetén: "Az 5 nem cikk, ezért nem lehet a kosárhoz adni!" vagy item.name-es elírás esetén: "Az iPhone nem cikk, ezért nem lehet a kosárhoz adni!".

### Nézzük a b) pontot!

Itt sajnos sokat nem tehetünk, dinamikusan típusos nyelveknél nem igazán lehet szétszedni az API-t az implementációtól.

### Nézzük a különös eseteket!

#### 1. Ha üres a kosár, akkor is megpróbálható egy cikk eltávolítása.

Itt vélhetően sok problémánk nem lesz, hiszen üres a kosár, így nem fogjuk megtalálni benne a törlendő elemet. Ami egy kis gondot okozhat, hogy a felhasználónak kicsit érthetetlen hibaüzenet megy.

#### 2. Ha üres a kosár, akkor is megpróbálható a fizetési funkció meghívása.

Itt már több gondunk adódhat, hiszen a _payment_ nincs definiálva, így súlyos elszállásokat kaphatunk, érthetetlen hiba üzenetekkel.

#### 3. Ha már fizetésre került a kosár, akkor is megpróbálható új cikk felvétele.

Ez súlyos üzleti kockázatokat rejt!

Nézzünk erre egy példát:
1. A vásárló a kosarába tesz egy 1000 Ft-os tollat,
2. kifizeti,
3. a már kifizetett kosárhoz hozzáad egy 500e Ft-os iPhone-t.

Ha ezután a szállításnál csak azt nézik, hogy kifizetésre került a kosár, ami igaz is, akkor kifogják szállítani az 500e Ft-os iPhone-t is, pedig a vásárló csak 1000 Ft-ot fizetett.

Ezzel 500e Ft-os kár fogja érni a boltot!

#### 4. Ha már fizetésre került a kosár, akkor is megpróbálható cikk eltávolítása.
#### 5. Ha már fizetésre került a kosár, akkor is megpróbálható újabb fizetés indítása.

Mit tehetünk, hogy a fenti problémákat elkerüljük?
----

- Szükséges a külön dokumentáció, ahol a fenti esetek mindegyikére ki kell térni részletesen, mit kell ellenőrizni, milyen hibát kell adni!
- Mivel a dokumentáció és a kód szétválik, ezért a dokumentáció és a kód eltérhet egymástól, nem lehet tudni, hogy melyik az _igaz_ és melyiket kellene a másikhoz igazítani!
- Minden fenti esetre szükséges az automatikus teszt, nehogy véletlenül előfordulhassanak a fentiek!
- A fenti különös esetek erős biztonsági kockázatok, ráadásul az ügyintézők életét is megnehezíthetik, illetve komoly pénzekbe kerülhet az működtető cégnek!
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
  sealed trait ShoppingCart
  sealed trait EmptyOrActiveCart extends ShoppingCart

  case object EmptyCart extends EmptyOrActiveCart
  case class ActiveCart(unpaidItems: List[Item]) extends EmptyOrActiveCart

  case class PaidCart(
    paidItems: List[Item],
    payment: Payment
  ) extends ShoppingCart

  type AddToEmpty = (EmptyCart.type, AddItem) => ActiveCart
  type AddToActive = (ActiveCart, AddItem) => ActiveCart
  type RemoveFromActive = (ActiveCart, RemoveItem) => EmptyOrActiveCart
  type PayActive = (ActiveCart, Pay) => PaidCart
}

```

Nézzük sorjában mi micsoda!

Az elején definiáljuk a vásárlói kosár állapotait (sealed = csak ezek az állapotok vannak, amik ebben a fájlban találhatóak):
1. EmptyCart, üres kosár, nem tartozik hozzá semmi, se cikk, se fizetési mód.
2. ActiveCart, aktív kosár, a kifizetetlen cikkeket tárolja, de nincs fizetési mód.
3. PaidCart, kifizetett kosár, a kifizetett cikkeket tárolja és a fizetési módot.

Ez után definiáljuk az egyes kiadható parancsokat:
1. AddToEmpty: hozzáad az üres kosárhoz egy cikket és egy ActiveCart állapotot kapunk vissza.
2. AddToActive: hozzáad az aktív kosárhoz egy újabb cikket és egy újabb  ActiveCart állapotot kapunk vissza.
3. RemoveFromActive: kivesz egy cikket az aktív kosárból és vagy egy EmptyCart vagy egy újabb ActiveCart állapotot kapunk vissza.
4. PayActive: kifizetésre kerül az aktív kosár és egy PaidCart állapotot kapunk vissza.

Nézzük meg mely problémákat oldotta meg a típusok ilyetén használata!
----

Nem szükséges külön dokumentáció, minden állapot, parancs és az állapot változások részletesen benne vannak. Olyan, mint egy szövegesen leírt UML diagram.

Mivel minden egyes állapotra definiálva van, hogy mely parancsot lehet rajta végrehajtani, milyen állapotokba vihetnek át, ezért nem fordulhatnak elő a _különös esetek_!

Nem lehetséges, hogy pl. egy már kifizetésre került kosárhoz új cikket adjanak, vagy töröljenek belőle vagy újra kifizessék (lásd különös esetek). Egyszerűen nem tud olyan kódot írni a kliens programozó, aki a ShoppingCartAPI-t használja, hogy az hibásan leforduljon.

A fenitek miatt nem szükséges a különös esetekhez szükséges dokumentáció, automatikus teszt, nem kell ezekre az implementációnál figyelni, és megszűnnek az azokból eredő hiba lehetőségek és biztonsági kockázatok!

Végszó
----

Sajnos nem minden típusos nyelv ad ekkora segítséget a fentiekhez, mint a Scala, van olyan is, aminek a típusrendszere még egyszerűbb és kifejezőbb (pl. F#, Haskell), de minden típusos nyelven (még a dinamikusokon is) a típusok segítségével lekorlátozhatjuk a reprezentálható állapotok körét, amivel:
1. csökken a szükséges dokumentáció,
2. csökken a szükséges automatikus tesztek száma,
3. az implementációnál nem kell arra figyelni, hogy milyen korlátozások vannak,
4. megszűnnek a hibás reprezentációból eredő hiba lehetőségek,
5. megszűnnek a hibás reprezentációból eredő biztonsági kockázatok,
6. megszűnnek a hibás reprezentációból eredő üzleti kockázatok,
7. az ügyfelet nem zargatjuk számukra érthetetlen hiba üzenetekkel.

Eleinte többletmunkának tűnik, de bőségesen visszahozza a befektetett energiát.

A fenti cikk létrejöveteléhez nagyban hozzájárult a [fsharpforfunandprofit](http://fsharpforfunandprofit.com/) oldal. Ez egy igazi aranybánya! Akik nem szeretik és/vagy nem használják az F#-ot, azoknak is érdemes végigböngészni, rengeteg jó videó és cikk található rajta.

A hibakezelés, capability (képesség) alapú tervezés/programozás, FP egyszerűen és érthetően, és még további sok jó anyag található rajta; amik alapján akár további blogok is készülhetnek.

Happy typing! ;-)
