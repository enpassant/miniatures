# A függetlenség napja

avagy erős függőség, gyenge függőség vagy teljes függetlenség

Az alábbi blog bejegyzésben a program fejlesztés közben felmerülő függőségekről lesz szó,
aki más témájút várt, azt sajnos el kell keserítsem. A függőségek kapcsán szó lesz még az automatikus tesztelésekről is.

## Függőségek

Alapvetően háromféle függőséget különböztetünk meg, az alapján, hogy két osztályunk (vagy függvényünk) milyen mértékben kapcsolódik a másikhoz. Lehet erős a függőség, gyenge a függőség vagy akár függetlenek is lehetnek, ha semmilyen módon nem kapcsolódnak.

Nagyon sok helyen olvashatjuk, hogy a gyenge függőség a legjobb, erre alapul a Dependency Injection is. Természetesen nagyon sok esetben tényleg az lehet a legjobb, de sok esetben a gyenge függőséget átalakíthatjuk úgy, hogy ne legyen függőség vagy akár úgy is, hogy erős függőség legyen, de annak a jó tulajdonságaival.

Van amikor az erős függőség jobb, mint a gyenge?

## Erős függőség mikor hasznos?

Sokan észre sem vesszük, hogy milyen sok helyen alkalmazzuk az erős függőséget, sőt azt kell mondjam, hogy több helyen alkalmazunk erős függőséget, mint gyengét.

Például ciklus változót mindig erős függőséggel használunk (integer érték vagy osztály).
Miért?

1. Nem tervezzük, hogy lecserélnénk másra.
2. Sima érték (pure függvényeket alkalmazunk), ezért mindig determinisztikus a viselkedése, ezért semmi gond nem jelentkezik a tesztelésénél.

Ez fordítva is igaz, ha valamit nem akarunk lecserélni és sima érték, akkor erősen függhet tőle más, gondot nem fog okozni.

A használata viszont sokkal kényelmesebb:

1. nem kell interfészt készíteni hozzá,
2. nem kell feloldani a függőséget, mindig ott van és működésre kész,
3. nem kell különböző implementációkat készíteni hozzá (production, teszt),
4. nem kell mock/stub-olni a tesztelésnél,
5. a unit teszt az egyben integrációs teszt is, hiszen nem kell a függőségeinket feloldani, mert azok determinisztikusak.

Ezért az egyik megoldás, hogy jobbá tegyük a kódunkat, hogy ahol lehet, a gyenge függést okozó osztályunkat átalakítjuk értékké.
A másik megoldás, ha teljesen megszüntetjük a függőséget.

Nézzünk meg egy tipikus OOP példát!


## Példa MVC alkalmazás

Vegyünk egy MVC (Model View Control) alkalmazást, amelyiknek van egy olyan funkciója, hogy felvesz egy felhasználót névvel, email címmel, telefonszámmal. A felvételről szeretnénk emailt és SMS-t is küldeni, amennyiben megadja az email címet, telefonszámot.

Nézzük ennek a felépítését:

* UserView: a felhasználói adatok bevitelét végzi. Függ: UserController, Logger.
* UserController: vezérli a felhasználóval kapcsolatos tevékenységeket. Függ: UserView, UserService, Logger.
* UserService: a felhasználóval kapcsolatos szolgáltatásokat végzi. Függ: UserDao, MailSender, SmsSender, Logger.
* UserDao: a felhasználóval kapcsolatos adateléréseket végzi. Függ: tényleges adatkezelő osztály (pl. EntityManager), Logger.
* MailSender: email küldése. Függ: Logger.
* SmsSender: SMS küldése. Függ: Logger.
* Logger: naplózás.

A unit tesztek és az esetleges lecserélhetőség végett itt általában gyenge függőséget alkalmazunk és Dependency Injection-nel oldjuk fel. A unit tesztek miatt az egyes osztályokat mock/stubolnunk kell mindenhol.

A felhasználó felvitel egy lehetséges folyamata:

* A UserView elküldi a UserControllernek a megadott adatokat.
* A UserController validálja az adatokat, fatal probléma esetén megszakítja a folyamatot, a validációs hibák megjelenítése a UserView segítségével.
* A UserController meghívja a UserService felhasználó felviteli funkcióját a megadott adatokkal.
* A UserService a UserDao segítségével elvégzi az adatok tárolását.
* Ha sikeres volt minden és van email megadva, akkor a MailSender segítségével emailt küld.
* Ha sikeres volt minden és van telefonszám megadva, akkor az SmsSender segítségével Sms-t küld.
* A hibákat és a sikeres műveletekről szóló információkat visszaadja a hívónak.
* A UserController a hibákat és a sikeres műveletekről szóló információkat megjelenítheti a UserView segítségével.
* Minden osztályunk metódusai naplózhatnak információkat a Logger osztály segítségével.

A tényleges implementáció elkészítésétől most eltekintek, gondolom nagyon sok ilyet láttok minden nap, vagy akár az interneten is fellelhető nagyon sok hasonló.

Próbáljuk meg átalakítani!

## Alkalmazás átalakítása

A felhasználó felvitel folyamatában több olyan osztály metódus hívása is szerepel, amiknek a végrehajtását elodázhatjuk. Például a naplózó műveletek végrehajtása ugyanolyan jó a folyamat végén, mint közben. Hasonlóan elodázható az email, SMS küldés, a validációs hibák megjelenítése, még akár az adatbázis műveletek végrehajtása is.

A megoldás roppant egyszerű, az adott művelet végrehajtása helyett, létrehozzuk az adott művelet végrehajtásához szükséges adatot és ezeket gyűjtjük a folyamat végéig. A folyamat végén, attól függően, hogy mi lett a végeredmény, szépen végrehajtathatjuk az összegyűjtött műveleteket, azok egy részét vagy akár semmit sem.

Mit nyerünk ezzel a megoldással?

A folyamat végéig csak adatok áramolnak, ezért könnyen megoldható pure függvényekkel.
Nem akarjuk lecserélni a az egyes függvényeinket, mert a tényleges lecserélendő utasításokat csak a folyamat után hajtatjuk végre. Az ottani végrehajtást viszont könnyedén cserélhetjük.

A fenti két ok miatt erős függőséget használhatunk, ezért a korábban leírt előnyeit kapjuk.

A folyamat végén ott van adatként minden, a naplózandó adatok, adatkezelő utasítások adatai, küldendő email-ek, sms-ek adatai, validációs adatok, ezért:

* a tesztek leegyszerűsödnek,
* a tesztek sokkal több mindent tudnak tesztelni, pl. azt is könnyedén, hogy a tanúsításhoz szükséges naplózások megtörténnek-e,
* egy teszttel rengeteg unit teszt kiváltható,
* a tesztek megírhatók black box módon.

Sokkal rugalmasabb lesz a kódunk. Pl. szeretnénk megoldani, hogy:

- ha nincs hiba, akkor ténylegesen csak a minimum Warning bejegyzéseket naplózza;
- ha Warning hiba van, akkor csak a minimum Info bejegyzéseket;
- ha Error hiba van, akkor csak a minimum Debug bejegyzéseket;
- ha Fatal hiba van, akkor minden bejegyzést.

Ezt a hagyományos programnál igen nehezen tudnánk megoldani, míg iennél a folyamat végén ott van minden adatunk, volt-e hiba, milyen hiba volt, miket szeretnénk naplózni, és ezek alapján könnyedén megcsinálhatjuk.

Hiba esetén nem csak részletesebb naplóbejegyzéseket kapunk, hanem ott vannak az egész végrehajtás során született információk is, azokat is naplózhatjuk, így még sokkal könnyebbé válhat egy esetleges hiba javítása.

Mivel főként pure függvényeket használunk, ezért sokkal kisebb lesz az állapottér, emiatt kisebb állapotteret kell tesztelni, könnyebb lesz átlátni, megérteni a kódot, így kevesebb hibát is fogunk véteni és gyorsabban is fogunk haladni a fejlesztéssel, módosításokkal.

A megoldás egyszerű, de amikor elkezdjük használni, akkor rájövünk, hogy egy csomó boilerplate kódot kell írnunk, amik nagyon megnehezítik a kód megértését.

## Problémák az átalakítással

Egy függvény visszatérési értékénél két eset lehetséges:

1. minden rendben történt, ekkor vissza kell adnunk ezt a tényt (GoodResult), az értéket (value), amit a függvény kiszámolt, valamint egyéb információkat (informations), pl. azon utasítások adatait, amiket a folyamat végén akarunk végrehajtani;
2. hiba történt, ekkor vissza kell adnunk ezt a tényt (BadResult), a hiba adatait (error), valamint egyéb információkat (informations).

Az ezt követő függvény hívásánál már figyelembe kell vegyük, hogy történt-e hiba, illetve az onnan visszakapott információkkal bővítenünk kell az eddigieket.
Kb. így nézne ki egy program részlet (sematikus algoritmus):

``` java
  result = validateName(userName)
  if (result.isGood) {
    validatedUserName = result.value
    result2 = validateEmail(userEmail).addInformationsFrom(result)
    if (result2.isGood) {
      validatedUserEmail = result2.value
      ...
    } else {
      error = result2.error
    }
  } else {
    error = result.error
  }
```

Ennek kezelésére a használt nyelvtől függően több lehetőségünk van. Pl. Java esetén segédosztályokkal és lambda függvényekkel egyszerűsíthetünk a helyzetünkön.

Azon nyelvek esetén, amelyek támogatják a monad-ok létrehozását (pl. Scala, C#) még könnyebb dolgunk lesz.
Egy egyszerű Result monad készítésével, amelynek két megjelenési formája lehet GoodResult és BadResult, könnyen kombinálhatjuk a Result-ot visszaadó függvényeinket.
Ez Scalaban kb. így nézne ki:

``` scala
  val result = for {
    validatedUserName <- validateName(userName)
    validatedUserEmail <- validateEmail(userEmail)
  } yield (validatedUserName, validatedUserEmail)
```
