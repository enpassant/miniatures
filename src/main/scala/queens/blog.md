N Királynő-probléma
----

avagy *"A lustaság fél egészség*"

tl;dr

Biztosan mindenki ismeri az N királynő-problémát, aki nem, annak álljon itt a [Wikipédia definíciója](https://hu.wikipedia.org/wiki/Nyolckir%C3%A1lyn%C5%91-probl%C3%A9ma):

A nyolckirálynő-probléma egy sakkfeladvány, lényege a következő: hogyan illetve hányféleképpen lehet 8 királynőt (vezért) úgy elhelyezni egy 8×8-as sakktáblán, hogy a sakk szabályai szerint ne üssék egymást. Ehhez a királynő/vezér lépési lehetőségeinek ismeretében az kell, hogy ne legyen két bábu azonos sorban, oszlopban vagy átlóban.

A nyolckirálynő-probléma egy példa az ennél általánosabb „n királynő problémára”, ami azt a kérdést veti fel, hányféleképpen lehet lerakni n darab királynőt egy n×n-es táblán.

A [Wikipédia angol nyelvű verziójában](https://en.wikipedia.org/wiki/Eight_queens_puzzle) különböző megoldási javaslatokat is látunk, pl.:

1. Brute-force keresés, ahol legeneráljuk az összes olyan esetet, ahogyan a 8 királynőt fel lehet helyezni a táblára, majd kiszűrjük közülük a helyes megoldásokat.
2. Javított brute-force keresés, ahol minden sorba csak egy királynőt teszünk. Számunkra ez lesz az egyik érdekes megoldás, mert elég könnyű megcsinálni, de nem elég hatékony.
3. A másik számunkra érdekes megoldás a visszalépéses (backtrack) mélységi keresés. Ez még hatékonyabb, mint a 2. megoldás, de nehezebb megcsinálni.

## Visszalépéses mélységi keresés algoritmusa

1. Az első királynőt helyezzük az első sor első oszlopába.
2. Ha a felhelyezett királynő
  - ütésben van a korábban felhelyezett királynőkkel, akkor, ha
    + van még oszlop, akkor tegyük egy oszloppal odébb és ismételjük a 2. pontot
    + ha nincs, akkor ezt a királynőt vegyük le ebből a soról,
        - ha vissza tudunk lépni egy sort, akkor  lépjünk vissza egy sort és az ott lévő királynőt tegyük egy oszloppal odébb, majd ismételjük a 2. pontot.
        - ha nem tudunk, akkor nincs (több) megoldás.
  - nincs ütésben a korábban felhelyezett királynőkkel, akkor lépjünk a következő sorra, helyezzünk fel az első oszlopba egy új királynőt és ismételjük meg a 2. pontot.

### 4x4-es királynő probléma

Mivel egy sorba csak egy királynő kerülhet, ezért a (rész)megoldásunkat megadhatjuk egy listával, ahol minden egyes elem azt mutatja, hogy az adott sorban hányadik oszlopban szerepel a királynő. Ha 0-tól kezdjük az indexelést, akkor az induló állást így jelölhetjük: (), az 1. pontban levőt így (0), egy megoldást pedig így (1, 3, 0, 2), ezt nézzük meg a sakktáblán is:

0 | 1 | 2 | 3
:-: | :-: | :-: | :-:
  | ♕ |  |   
  |   |    | ♕ |
♕ |   |  |   
  |   | ♕ |   

#### Algoritmus futása

Menjünk végig az algoritmuson és jegyezzük fel az egyes részmegoldásokat!

Lépés | (rész)megoldás
----  | ---:
Indulás | (0)
A következő sorban nem jó a 0. pozícióban sem, az 1.-ben sem így marad a | (0, 2)
A következő sorban egyik oszlopba se helyezhetjük a vezért, ezért visszalépünk és továbbrakjuk a vezérünket | (0, 3)
A következő sorban nem jó a 0. oszlop, de jó az 1. | (0, 3, 1)
Sajnos az utolsó sorban nem jó megint egyik sem visszalépünk a 2. sorra. |
Ott sem jó sem a 2., sem a 3. oszlop, ezért visszalépünk az. 1. sorra. |
Ott már nincs több oszlop, ezért visszalépünk a 0. sorra, ott továbbrakjuk a vezért | (1)
A következő sorban jó a 3. oszlop | (1, 3)
A következő sorban mindjárt a 0. jó | (1, 3, 0)
És jön az első megoldásunk | (1, 3, 0, 2) *
Ha folytatjuk az algoritmust, akkor vissza kell lépni egészen az első sorig | (2)
Következő sorból jó a 0. | (2, 0)
Következő sorból jó a 3. | (2, 0, 3)
Következő sorból jó a 1. Újabb megoldás | (2, 0, 3, 1) *
Visszalépünk az elejére | (3)
Következő sorból jó a 0. | (3, 0)
Következő sorból jó a 2. | (3, 0, 2)
Visszalépés kétszer | (3, 1)
Visszalépünk egészen a 0. sor elé | vége

Vége az algoritmusnak, nincs több megoldás, összesen kettőt találtunk.

Ezt az algoritmust nem nagyon egyszerű megírni és megérteni. Egy C++ példát láthatunk a korábban említett [Wikipédia definíciójánál](https://hu.wikipedia.org/wiki/Nyolckir%C3%A1lyn%C5%91-probl%C3%A9ma).

Ha valaki a Javascript-et kedveli, akkor a következő [JSFiddle](https://jsfiddle.net/enpassant/k2jnmjjx/)-nél, a _var queens_ -től kezdődő résznél találhat.

### Javított brute-force keresés

Sokkal könnyeb dolgunk van, ha a javított brute-force keresést használjuk. Főleg akkor, ha nem mondjuk meg részletesen, hogyan csinálja, csak azt, hogy mit.

Lássuk az algoritmust és egyből utána a Scala kódját!

```
Az összes (rész)megoldást úgy kapjuk n oszlopra és i sorra, hogy
 ha i <= 0, akkor egy üres megoldásunk van, tehát: (())
 különben az összes részmegoldást n oszlopra és i-1 sorra terjesszük ki egy új (n oszlopból álló) sorral
```
Scala
``` scala
def allSolution(n: Int, i: Int): Seq[Seq[Int]] =
  if (i <= 0) Seq(Seq()) else allSolution(n, i-1) flatMap extendSolution(n)
```
Nézzük meg mit jelent egy (rész)megoldás kiterjesztése!
```
A megoldás kiterjesztése (n oszlopból álló) megegyezik azzal, hogy
  az n oszlop közül kiszűrjük azokat, amik felvehetők és ezeket az eddigi megoldáshoz fűzzük
```
A Scala-s megoldásban praktikus okokból itt nem a végére rakjuk az új, felvehető oszlop indexeket, hanem az elejére.
``` scala
def extendSolution(n: Int)(qs: Seq[Int]) =
  (0 until n) filter okToAdd(qs) map (q => q +: qs)
```
Felvehető oszlop
```
Felvehető egy vezér ((rész)megoldás esetén, az új sor egy q. oszlopába) megegyezik azzal, hogy
  a részmegoldás minden vezérének pozíciója nem fenyegeti az új sor, q. oszlopába felveendő vezért
```
``` scala
def okToAdd(qs: Seq[Int])(q: Int) =
  qs zip (1 to qs.length) forall notThreaten(q, 0).tupled
```
Magyarázat az előzőhöz: mivel a (rész)megoldásoknál csak az oszlopszámot tároljuk el, emiatt hozzá kell párosítani a sorszámot is (zip). Praktikus okokból itt sem a végére vesszük fel az új sort, hanem az eddigiek elé (0. sor)
Végül nézzük mikor nem fenyeget egy vezér egy másikat!
```
Egy vezér (qi, i) pozícióban  nem fenyeget egy másikat (qj, j) pozícióban, ha
  nincsenek ugyanabban az oszlopban és nincsenek ugyanabban az átlóban
```
``` scala
val notThreaten = (qi: Int, i: Int) => (qj: Int, j: Int) =>
  (qi != qj) && math.abs(qi - qj) != math.abs(i - j)
```

Mivel praktikus okokból a 0. sorba vesszük fel az új sort, emiatt az utóbbi függvényben, mivel i mindig 0, ezért ezt a paramétert elhagyhatjuk és ezáltal leegyszerűsödik a két utolsó függvényünk, de nézzük a teljes algoritmust egyben:
``` scala
def allSolution(n: Int, i: Int): Stream[Seq[Int]] =
  if (i <= 0) Stream(Seq()) else allSolution(n, i-1) flatMap extendSolution(n)

def extendSolution(n: Int)(qs: Seq[Int]) =
  (0 until n) filter okToAdd(qs) map (q => q +: qs)

def okToAdd(qs: Seq[Int])(q: Int) =
  qs zip (1 to qs.length) forall notThreaten(q).tupled

val notThreaten = (qi: Int) => (qj: Int, j: Int) =>
  (qi != qj) && math.abs(qi - qj) != j
```

Ha lefuttatjuk és kiíratjuk az egyes (rész)megoldásokat, akkor látjuk, hogy pontosan ugyanazokat a részmegoldásokat készítette el, mint a backtrack-es, de annyival rosszabb a hatásfoka, hogy ha csak az első (pár) megoldásra van szükségünk, akkor is csak a futás legvégén lesz meg, míg a backtrack-es már sokkal előbb megtalálja az elsőt.

Mondhatják egyesek, hogy *"Jó dolog ez a mit a hogyan helyett, könnyebb is megérteni, könnyebb is megírni, de sokkal rosszabb a hatásfoka"*.
Sajnos ez a rossz hír, a jó hír viszont az, hogy sokkal hatékonyabbá tehető egy nagyon kis módosítással.

## *A lustaság fél egészség*

Mi a különbség a jó programozó és a rossz programozó között?

A jó programozó **LUSTA**! ;-)
Ez egyébként sok más gondolkodást igénylő szakmára igaz, pl. a matematikusra is.

### Mit is jelent az, hogy lusta?

Most azt nevezzük lustának, ha valaki a rá bízott feladat végrehajtását addig az utolsó időpontig elhalasztja, ameddig csak lehetséges. Pl. ha valaki kap egy két nap időráfordítást igénylő feladatot és van rá öt napja, hogy elkészítse, akkor három napig pihen, majd az utolsó két napban megcsinálja. A szorgalmas ezzel szemben azonnal megcsinálja és három napig pihen utána.

### Miért is jó, ha lusta?

Ha pont annyi vagy kevesebb feladatot kap valaki, mint amennyit el tud adott idő alatt végezni, akkor nincs nagy különbség a lusta és szorgalmas között, azt leszámítva, hogy az egyik az elején pihen, a másik a végén, de mind a kettő elvégzi a munkáját rendesen.
Ha viszont több feladatot kap, mint amennyit el tud végezni, akkor pedig mind a kettő ugyanannyit késik, mint a másik. Hol itt a különbség?

#### A különbség az elvégzett munka sorrendjében van.

Míg a szorgalmas szépen csinálja egymás után a feladatait, amíg nem végez, a lusta ezzel szemben mindig csak azt a feladatát csinálja, ami már végképp nem tűr halasztást, így a lusta a kitűzött idő alatt a legfontosabb munkáit megcsinálta, míg a szorgalmas nem. Hasonlóan, mint a visszalépéses keresés algoritmus és a javított brute-force algoritmus. A visszalépéses olyan, mint a lusta, már a 8. lépésben megtalálja az első megoldást, a javított brute-force pedig olyan, mint a szorgalmas, csak az utolsó lépésekben.

Ha ezt felismertük, akkor már könnyű dolgunk van. Tegyük a javított brute-force algoritmust lustává és máris készen vagyunk. A pure functional nyelveken (mint a Haskell) eleve lusta kiértékelésűek a collection-ök, Scala-ban azonban alapértelmezetten nem, kivéve a Stream-et, illetve minden immutable collection a view metódussal egyszerűen azzá tehető. Használjuk a Streamet, mert az még "cache"-el is!:
```scala
  def allSolution(n: Int, i: Int): Stream[Seq[Int]] =
    if (i <= 0) Stream(Seq()) else allSolution(n, i-1) flatMap extendSolution(n)

  def extendSolution(n: Int)(qs: Seq[Int]) =
    Stream.range(0, n) filter okToAdd(qs) map (q => q +: qs)

  def okToAdd(qs: Seq[Int])(q: Int) =
    qs zip (1 to qs.length) forall notThreaten(q).tupled

  val notThreaten = (qi: Int) => (qj: Int, j: Int) =>
    (qi != qj) && math.abs(qi - qj) != j
```

Ha így futtatjuk le az algoritmusunkat, akkor eredményül egy Stream-et kapunk, de úgy, hogy egyetlen árva lépést sem csinál meg az algoritmusból. Ez valóban lusta lett ;-)
``` scala
  lazy val solutions = allSolution(4, 4) //semmit nem csinál
```

Ha elkérjük az első eredményt, akkor pontosan azokat a lépéseket hajtja végre, amiket a backtrack-nél is láttunk, de csak addig, amíg meg nem találja az első megoldást.
``` scala
  lazy val solutions = allSolution(4, 4) // semmit nem csinál
  solutions.take(1).toList
```
Ha ismét elkérnénk az elsőt, akkor megint csak nem csinál semmit, hanem visszaadja azt, amit már kiszámolt.

Ha másodszorra két megoldást kérünk el, akkor folytatja a keresést ott, ahol abbahagyta egészen addig, amíg meg nem találja a második megoldást.
``` scala
  lazy val solutions = allSolution(4, 4) //semmit nem csinál
  solutions.take(1).toList
  solutions.take(1).toList // semmit nem csinál
  solutions.take(2).toList
```

Ennek a megoldásnak még további előnye, hogy rugalmasan felhasználható. Láttuk, hogy könnyen elkérhetjük az első, a második, vagy akár az öesszes megoldást. Ezen túlmenően még olyan extrém elvárást is könnyen megoldhatunk, mint pl. olyan három megoldás érdekel, amelyiknél az 1., 5. és 7. sorban levő királynő oszloppozícióinak szorzata nagyobb, mint 80:

``` scala
  lazy val solutions = allSolution(8, 8)
  solutions filter (s => s(1) * s(5) * s(7) > 80) take 3
```
Ezt az imperatív megoldásnál csak nagy átalakítások után tudnánk elérni.

#### Előnyei az imperatív megoldással szemben

- Rövid, egyszerű megoldás
- Könnyen érthető
- Könnyen elkészíthető (ehhez azért jártasság is kell és sok gyakorlás, hogy az ember agya ráálljon erre a gondolkodás módra)
- Rugalmasan módosítható, felhasználható
- A részeredmények újrafelhasználhatók

#### Mivel ez sem silver bullet, így vannak hátrányai is

- Még, ha ugyanannyi lépésből is megtalálja a megoldást, akkor is több idő alatt teszi meg.
- Nagyobb a memória használata, mivel az egyes félbehagyott, elvégzendő funkciókat is tárolnia kell.

---------------------------
A források elérhetők a [github-on](https://github.com/enpassant/miniatures/tree/master/src/main/scala/queens), illetve a Javascriptes verzió a korábban is említett [JSFiddle](https://jsfiddle.net/enpassant/k2jnmjjx/)-n.
