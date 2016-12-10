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
2. Sima érték (pure függvényeket alkalmazunk), ezért mindig determinisztukus a viselkedése, ezért semmi gond nem jelentkezik a tesztelésénél.

Ez fordítva is igaz, ha valamit nem akarunk lecserélni és sima érték, akkor erősen függhet tőle más, gondot nem fog okozni.

A használata viszont sokkal kényelmesebb:

1. nem kell interfészt készíteni hozzá,
2. nem kell feloldani a függőséget, mindig ott van és müködésre kész,
3. nem kell különböző implementációkat készíteni hozzá (production, teszt),
4. nem kell mock/stub-olni a tesztelésnél.

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

A felhasználó fevitel egy lehetséges folyamata:

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

