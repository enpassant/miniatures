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
1. Ha üres a kosár, akkor is megpróbálható eltávolítani belőle egy cikket.
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

  case object EmptyCart extends ShoppingCart
  case class ActiveCart(unpaidItems: List[Item]) extends ShoppingCart

  case class PaidCart(
    paidItems: List[Item],
    payment: Payment
  ) extends ShoppingCart

  case class AddItem(item: Item) extends Command
  case class RemoveItem(item: Item) extends Command
  case class Pay(payment: Payment) extends Command

  case class FirstItemAdded(item: Item) extends Event
  case class NextItemAdded(item: Item) extends Event
  sealed trait ItemRemoved extends Event
  case class AnItemRemoved(item: Item) extends ItemRemoved
  case class LastItemRemoved(item: Item) extends ItemRemoved
  case class Paid(payment: Payment) extends Event

  type AddToEmpty = (EmptyCart.type, AddItem) => Either[Error, FirstItemAdded]
  type AddToActive = (ActiveCart, AddItem) => Either[Error, NextItemAdded]
  type RemoveFromActive = (ActiveCart, RemoveItem) => Either[Error, ItemRemoved]
  type PayActive = (ActiveCart, Pay) => Either[Error, Paid]

  type HandleFirstItemAdded = (EmptyCart.type, FirstItemAdded) => ActiveCart
  type HandleNextItemAdded = (ActiveCart, NextItemAdded) => ActiveCart
  type HandleAnItemRemoved = (ActiveCart, AnItemRemoved) => ActiveCart
  type HandleLastItemRemoved = (ActiveCart, LastItemRemoved) => EmptyCart.type
  type HandlePaid = (ActiveCart, Paid) => PaidCart
}

```

Nézzük sorjában mi micsoda!

Az elején definiáljuk a vásárlói kosár állapotait (sealed = csak ezek az állapotok vannak, amik ebben a fájlban találhatóak):
1. EmptyCart, nem tartozik hozzá semmi, se cikk, se fizetési mód.
2. ActiveCart, a kifizetetlen cikkeket tárolja, de nincs fizetési mód.
3. PaidCart, a kifizetett cikkeket tárolja és a fizetési módot.

Ez után definiáljuk az egyes kiadható parancsokat (AddItem, RemoveItem, Pay).

Majd az egyes eseményeket, amik a parancsok hatására bekövetkezhetnek (FirstItemAdded, NextItemAdded, AnItemRemoved, LastItemRemoved, Paid).

A következő részben az egyes parancsokat végrehajtó függvénytípusokat definiáljuk.

Pl. AddToEmpty: a függvény EmptyCart állapotot és AddItem parancsot kap paraméterként, és vagy hibát ad vissza vagy a bekövetkező FirstItemAdded eseményt.

A következő részben az egyes eseményeket kezelő függvénytípusokat definiáljuk.

Pl. HandleLastItemRemoved: a függvény ActiveCart állapotot és LastItemRemoved eseményt kap paraméterként, amire EmptyCart állapotot ad vissza.

Nézzük meg mely problémákat oldotta meg a típusok ilyetén használata!
----

Nem szükséges külön dokumentáció, minden állapot, parancs, esemény, a parancsok hatása és az állapot változások részletesen benne vannak.

Mivel minden egyes állapotra definiálva van, hogy mely parancsot lehet rajta végrehajtani, milyen állapotokat adhat vissza, az egyes állapotokból mely események mely állapotokba visznek át, ezért nem fordulhatnak elő a _különös esetek_! *Érvénytelen állapotot nem lehet kifejezni!*

A fenitek miatt nem szükséges a különös esetekhez szükséges dokumentáció, automatikus teszt, nem kell ezekre az implementációnál figyelni, és megszűntek az azokból eredő hiba lehetőségek és biztonsági kockázatok!

Sajnos nem minden statikusan típusos nyelv ad ekkora segítséget a fentiekhez (pl. Java), mint a Scala, de azért van több is, használjuk azokat :-), sőt van olyan is, aminek a típusrendszere még egyszerűbb és kifejezőbb (pl. F#) !

A fenti cikk létrejöveteléhez nagyban hozzájárult a [fsharpforfunandprofit](http://fsharpforfunandprofit.com/) oldal. Ez egy igazi aranybánya! Akik nem szeretik és/vagy nem használják az F#-ot, azoknak is érdemes végigböngészni, rengeteg jó videó és cikk található rajta.

A hibakezelés, capability (képesség) alapú tervezés/programozás, FP egyszerűen és érthetően, és még további sok jó anyag található rajta; amik alapján akár további blogok is készülhetnek, természetesen Scala-ra adoptálva.

Happy typing! ;-)
