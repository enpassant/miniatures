# A legkisebb erő

Ha valaki egy leendő Csillagok Háborúja epizódra gondolt a cím alapján,
azokat el kell keserítsem, ismét programozás elméletről lesz szó.

Régóta foglalkoztatja a programozókat, hogy vajon mi alapján lehet megmondani,
hogy az egyik programrész vagy program design jobb, mint a másik.

Miközben _a válasz az életre, a világmindenségre, meg mindenre_ már megvan,

![42](https://upload.wikimedia.org/wikipedia/commons/5/56/Answer_to_Life.png)

aközben a fenti kérdésre eddig nem igazán volt válasz.

Egész pontosan már régóta megvan a válasz a kérdésre,
sőt ez nem csak a programozásra igaz, hanem tetszőleges tervezésre és az élet egyéb problémáira is.
A válasz már régóta megvan, de nem igazán vesszük észre. Ha összetalálkozunk vele az utcán, akkor simán elmegyünk mellette.
Ha egymás mellett jön a jól tervezett program és a rosszul tervezett, akkor látjuk, hogy mennyire más a kettő, de nem tudjuk, hogy miért más.

A válasz a kérdésre az egyszerűség.

## [Rule of least power](https://en.wikipedia.org/wiki/Rule_of_least_power)

* [Principle of Least Privilege](https://en.wikipedia.org/wiki/Principle_of_least_privilege)
* [Occam borotvája](https://en.wikipedia.org/wiki/Occam%27s_razor)
* [KISS: Kepp It Stupid Simple](https://en.wikipedia.org/wiki/KISS_principle)
* **Leonardo da Vinci** _"Simplicity is the ultimate sophistication"_,
* **Shakespeare** _"Brevity is the soul of wit"_,
* **Mies Van Der Rohe** _"Less is more"_,
* **Bjarne Stroustrup** _"Make Simple Tasks Simple!"_,
* **Antoine de Saint Exupéry** _"It seems that perfection is reached not when there is nothing left to add, but when there is nothing left to take away"_

A fentiek nagyjából ugyanazt fejezik ki más-más szemszögből.
Lényegük az, hogy **két megoldás közül azt érdemes választani, amelyik az egyszerűbb**.
Az egyszerűbbet úgy is mondhatjuk, hogy kevésbé powerful, vagyis kisebb az ereje, hatása, tudása.

Ezek szerint két program design közül az a jobb, amelyik az egyszerűbb.

## Miért is jobb az egyszerűbb?

Az egyszerűbbet
- könnyeb megérteni,
- könnyeb átalakítani (refaktorálni),
- könnyebb módosítani,
- könnyebb ellenőrizni a működését (tesztelés),
- készítésekor kevesebb hibát vétünk.

Talán már kicsit közelebb vagyunk a megoldáshoz, de még mindig nehéz megmondani, hogy mikor egyszerűbb az egyik, mint a másik.

A programtervezésénél a feladatot először kisebb részekre bontjuk, majd azokat még kisebbekre, egészen addig, amíg kóddá nem alakíthatjuk.
Ha az egyes részek az egyik design esetén mind egyszerűbb, mint a másik esetén, akkor az egész is egyszerűbb lesz.

Amikor készítjük a kódot, akkor is hasonlóan járhatunk el.
Ha valamit le akarunk kódolni, akkor általában több lehetőségünk is van, válasszuk mindig az egyszerűbb megoldást!

### A korlátozás felszabadít, a szabadság lekorlátoz

[Constraints Liberate, Liberties Constrain - Runar Bjarnason](https://www.youtube.com/watch?v=GqmsQeSzMdw)

**Ha egy szinten korlátozásokat teszünk**, **akkor egy másik szinten szabadságot nyerünk**.
Ez igaz fordítva is, **ha egy szinten szabadságot engedünk**, **akkor egy másik szinten korlátokba ütközünk**.

Ha az egyszerűbbet választjuk, ami kvesebb tudású, kevesebb erővel, hatással bír, akkor más szóval a korlátozottat használjuk.
Ha a nagyobb erejűt, nagyobb hatásút, többet tudót választjuk, akkor más szóval a szabadabbat választjuk.

Az életben erre nagyon jó példa a Lego vs Playmobil játékok.
A Lego játékokban az egyes elemek egyszerűek, keveset tudnak, korlátozott a tudásuk.
Egy másik szinten viszont szabadságot nyerünk, mert sokkal szabadabban tudjuk egymáshoz illeszteni az elemeket és nagyon sok mindent építhetünk belőlük.

A Playmobil játékoknál viszont egy egy elem részletesen kidolgozott, sokkal nagyobb tudással rendelkeznek, kész házak, autók, emberek.
Egy másik szinten viszont korlátokba ütközünk, mert sokkal kötöttebben tudjuk egymáshoz illeszteni az elemeket és nagyon kevés dolgot építhetünk belőlük.


Programozásnál, az előbbieket jól megfigyelhetjük a következő példáknál.

## Példák

| kicsi erő                          | < | NAGY ERŐ                           |
| ---------------------------------- | - | ---------------------------------- |
| erős függőség                      | < | laza függőség                      |
| monomorfikus függvény              | < | polimorfikus függvény              |
| kompozíció                         | < | öröklés                            |
| külön adat és függvény             | < | egységbe zárás                     |
| totális függvény                   | < | parciális függvény                 |
| determinisztikus függvény          | < | nem determinisztikus függvény      |
| mellékhatás mentes függvény        | < | mellékhatásos függvény             |
| immutable                          | < | mutable                            |
| lineáris kód végrehajtás           | < | nem lineáris kód végrehajtás       |

Elsőre nem feltétlen egyértelmű, hogy tényleg a baloldaliak egyszerűbbek, kisebb tudásúak, mint a jobb oldaliak.
Illetve, ha el is fogadjuk, hogy a baloldaliak egyszerűbbek is, de nem könnyű elfogadni, hogy igazak azok az állítások, amiket adtunk az egyszerűbbre.

### Erős függőség vs laza függőség

Sokunknak nagyon furcsa lehet, hogy az *erős függőséget* könnyebb tesztelni, mint a *laza függőséget*.

Azért sem könnyű sokszor eldönteni, hogy melyik az egyszerűbb, mert egyszerre több tulajdonság is igaz egy-egy dologra, némelyikben az egyik egyszerűbb, némelyikben a másik.

Pl. sokan azért gondolják jobban tesztelhetőnek a laza függőséget, mert szinte kizárólag mellékhatásos függvényekként vannak használatban.

Nem azért tesztelhető rosszul egy függvény, mert erős függősége van, hanem azért, mert az a függőség nem determinisztikus, parciális vagy mellékhatásos függvény hívás.

A [függőségeket vizsgáltam ebben a cikkemben](https://github.com/enpassant/miniatures/tree/master/src/main/java/independence).

### Monomorfizmus vs Polimorfizmus

Itt azt hiszem nem kérdés, hogy a polimorf függvény sokkal nagyobb tudású, mint a monomorf.
Mivel a (subtype) polimorfizmushoz szükség van öröklődésre (ez lehet interface alapú is) és egységbezárásra is, ezért ez nagyságrendekkel nagyobb tudású, mint egy nem egységbe zárt, öröklődéstől mentes monomorf függvény.

Ahogy itt is látjuk, sok esetben az egyik rossz tulajdonság hozza magával a másikat.

Az [OOP Polimorfizmus vs FP Monomorfizmus](https://github.com/enpassant/miniatures/wiki/OOP-Polimorfizmus-vs-FP-Monomorfizmus) cikkemben nagyon jól meg lehet figyelni, hogy milyen kényelmes a polimorfizmus kezdetben, ezt adja a nagy tudás.
Majd mennyire nehézzé válik a módosítás, átalakítás, de még az új funkciók hozzáadása is.

### Kompozíció vs öröklés

*Készítés alatt.*

### Külön adat és függvény vs egységbe zárás

*Készítés alatt.*

### Totális vs parciális függvény

A [totális vs parciális függvényekről](https://github.com/enpassant/miniatures/tree/master/src/main/java/total) itt írtam.

### Determinisztikus vs nem determinisztikus függvény

*Készítés alatt.*

### Mellékhatás mentes vs mellékhatásos függvény

*Készítés alatt.*

### Immutable vs mutable

*Készítés alatt.*

### Lineáris vs nem lineáris kód végrehajtás

*Készítés alatt.*

## További példák az egyszerűbbre

### Mellékhatásos függvény és nem lineáris kód végrehajtás

Cikkem a [Hibakezelésről](file:///projects/kalman/miniatures/src/main/java/exception/README.md#mi-ad-vissza-hib%C3%A1t?) megmutatja, hogy milyen gondok adódnak Exception-ös hibakezelésnél, vagyis amikor mellékhatással jár egy függvény és amikor nem lineáris a kód végrehajtás.
Illetve láthatjuk azt is, hogy mit nyerünk, ha mellékhatás nélküli hibakezelést használunk.

## FP vs OOP

Nézzük meg a listát FP vs OOP szemszögből!

Az öröklésen kívül mindegyik tulajdonságszerepel mindkét paradigma esetén.
A nagy különbség abban van, hogy az FP a bal oldali tulajdonságokat ajánlja, támogatja vagy akár ki is kényszeríti, addig az OOP a jobb oldali tulajdonságokkal teszi mindezt.

A Design patternek is főként a jobb oldali tulajdonságokra épülnek, így főként nem azért jók, mert egyszerűsítenek a programunkon.
Sokszor épp a rossz tulajdonságok miatti problémák megoldása miatt kényszerülünk a használatukra, lásd pl. Dependency Inversion.
Bonyolultan megoldott dolgok egyszerűsítését várjuk el, újabb bonyolult eszközök bevezetésével, miközben csak még bonyolultabbakká tesszük és valójában csak elfedjük a problémákat.
Egy-egy pici része lehet, hogy egyszerűsödik, de az egész az még sokkal bonyolultabb lesz.

OOP esetén is érdemes preferálnunk a bal oldali tulajdonságokat, még ha ez sok esetben nehézkes is.
Érdemes esetleg ezt támogató könyvtárat használni.
Érdemes kerülni a Dependency Injection-t, főként a DI vagy más néven IoC containert, az Annotációkat, az ORM-t és az Exception-t, lásd pl. [itt](https://hup.hu/node/161893).
Ezek mindegyike több nagy tudású tulajdonságot is magával hoz és még többre buzdít.

Érdemes megnézni egy-egy osztályunkat és látni fogjuk, hogy szinte mindegyik, a jobb oldali "rossz" tulajdonságok nagy részét tartalmazza.

## Procedurális vs OOP

Nézzük meg a listát a procedurális paradigma szemszögéből is!

Polimorfikus függvény, öröklés és egységbe zárás nincs benne, így azok helyett egyszerűbb megoldásokat használunk, általában erős függőséget használunk. Ezek a tulajdonságok, ahol vagy mindig vagy általában az egyszerűbb megoldásokat használjuk.
Az összes többi tulajdonságnál nincs egyik oldal sem támogatva, jellemzően az erősebb tulajdonságok vannak használva, de ez leginkább a programozókon múlik.

Összességében elmondható, hogy a procedurális paradigma használata is egyszerűbb programok készítését segíti elő az OOP-hez képest.

Sokan azt tartják, hogy nagy projektet, sok fejlesztővel csak OOP-val érdemes csinálni.
Erre az állításra nagyon könnyű ellenpéldát adni, pl. a Linux kernel, amit sok ezer fejlesztő fejleszt több évtizede.

## Minden problémára a megfelelő eszközt

Sokat halljuk ezt a kifejezést is és nagyon sok igazság van benne.

Célszerű megfogadni ezt a tanácsot!

Szoktam hallani OOP vs FP vs PP vonatkozásban is.
Ha nagyon akarjuk, akkor tekinthetjük egy eszköznek, de valójában ezek inkább eszközkészletek.
Mivel mindegyik esközkészlet általános célú, így a legtöbb feladatra alkalmasak.
Inkább a fejlesztők tudása, az adott problémára mennyire
Az OOP egy olyan eszközkészlet, amibe a legbonyolultabb eszközök vannak beválogatva, az FP-be pedig a legegyszerűbbek, a procedurálisba pedig ilyen is, olyan is.
