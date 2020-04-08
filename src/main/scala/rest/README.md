# RESTful rendszer készítése

Rengeteg írás született már, hogyan készítsünk RESTful rendszert.
Sajnos, amiket eddig láttam azok 99%-ban nem RESTful API-kat írnak le.
Részletesen elemzik, hogyan alakítsuk ki az útvonalakat (URI path), miként nevezzük el az erőforrásainkat, mikor milyen HTTP metódusokat (POST, PUT, DELETE, PATCH, ...) használjunk, de ezek részletkérdések és általában csak a rossz irányba visznek minket.

Szeretnék rendet tenni a fejekben, ezért mindenkit arra kérek, hogy próbáljon nyitottan állni hozzá és semmiképp se vegyék személyes támadásnak, még akkor se, ha eddig Ők a 99%-ba tartoztak, és azt a REST API felfogást vallják. Remélem a cikk végére és egy kis emésztés után, változik a felfogásuk ;-). Ha nem, az se baj!

A következő cikkben megmutatom a helyes utat ...

## REST és RESTful definíciója

A [Wikipédián](https://hu.wikipedia.org/wiki/REST) ([angolul](https://en.wikipedia.org/wiki/Representational_state_transfer)) remekül le van írva a REST, ezért arra fogunk támaszkodni.

A REST (Representational State Transfer) egy szoftverarchitektúra típus, elosztott kapcsolat (loose coupling), nagy, internet alapú rendszerek számára, amilyen például a világháló.

Azokat a rendszereket, amelyek eleget tesznek a REST megszorításainak, "RESTful"-nak nevezik.

### REST megszorításai

- **Kliens-szerver architektúra.**
    A kliensek el vannak különítve a szerverektől egy egységes interfész által. Az érdekeltségek ilyen nemű szétválasztása azt jelenti, például, hogy a kliensek nem foglalkoznak adattárolással, ami a szerver belső ügye marad, és így a kliens kód hordozhatósága megnő. A szerverek nem foglalkoznak a felhasználói felülettel vagy a kliens állapotával, így a szerverek egyszerűbbek és még skálázhatóbbak lehetnek. A szerverek és kliensek áthelyezhetőek és fejleszthetőek külön-külön is, egészen addig amíg az interfész nem változik meg.
- **Állapotmentesség.**
    A kliens-szerver kommunikáció tovább korlátozott az által, hogy a szerveren nem tárolják a kliens állapotát a kérések között. Minden egyes kérés bármelyik klienstől tartalmazza az összes szükséges információt a kérés kiszolgálásához, és minden állapotot a kliens tárol. A szerver lehet állapottartó; ez a korlátozás csupán azt követeli meg, hogy a szerver oldali erőforrás-állapotok URL által címezhetőek legyenek. Ez nem csak a szerver felügyeletét teszi lehetővé, de megbízhatóbbá teszi őket a hálózati meghibásodásokkal szemben, valamint tovább fokozza a skálázhatóságot.
- **Gyorsítótárazhatóság.**
    Mint ahogy a világhálón, a kliensek itt is képesek gyorsító-tárazni a válaszokat. A válaszoknak ezért impliciten vagy expliciten tartalmazniuk kell, hogy gyorsítótárazhatóak-e vagy sem. Így elkerülhető, hogy a kliens téves vagy elavult adatokat használjon fel újra. Egy jól menedzselt gyorsítótár lehetővé teszi, hogy teljesen megkerüljünk egyes kliens-szerver interakciókat, továbbá megnöveli a rendszer skálázhatóságát és a teljesítményét.
- **Réteges felépítés.**
    Egy kliens általában nem tudja megmondani, hogy direkt csatlakozott-e a végpont szerverhez, vagy közvetítő segítségével. A közvetítő szerverek megnövelhetik a rendszer skálázhatóságát terheléseloszlás kiegyenlítéssel és megosztott gyorsítótárak használatával.
- **Igényelt kód (opcionális).**
    A szerverek képesek időlegesen kiterjeszteni vagy testre szabni egy kliens funkcionalitását, programrészek átadásával, amelyeket a kliens futtatni képes. Ide tartoznak az előre fordított komponensek (pl. Java appletek) és a kliensoldali szkriptek (pl. JavaScript).
- **Egységes interfész.**
Az egységes interfész kliens és szerver között egyszerűsíti és kettéválasztja az architektúrát, és lehetővé teszi, hogy egymástól függetlenül fejlődjenek az egyes részek. Az interfész négy irányadó elve alább kerül részletezésre.

#### Egységes interfész

- **Erőforrások azonosítása.**
    Egyéni erőforrások azonosítása a kérésekben történik, például URI-k használatával HTTP-alapú REST rendszereknél. A források maguk koncepcionálisan elkülönítettek a reprezentációktól, melyeket a kliens kap. Például a szerver nem küldi el az adatbázisát, hanem néhány HTML, XML vagy JSON dokumentumot, melyek az adatbázis néhány rekordját reprezentálják, UTF-8-ban kódolva, a kérés adataitól és a szerver implementációjától függően.
- **Erőforrások manipulációja ezeken a reprezentációkon keresztül.**
    Ha egy kliens rendelkezik egy erőforrás-reprezentációval, beleértve minden csatolt metaadatot, akkor elegendő információja van az erőforrás módosításához vagy törléséhez a szerverről, feltéve, ha van engedélye hozzá.
- **Önleíró üzenetek.**
    Minden egyes üzenet elegendő információt tartalmaz az üzenet feldolgozásához. Például a média típusát, hogy a kliens tudja, hogyan jelenítse meg az erőforrást.
- **Hipermédia, mint az alkalmazásállapot motorja.**
    A kliensek csakis azokon az állapotokon mehetnek át, amelyeket a szerver által küldött hipermédia tartalmaz hivatkozások alakjában. Pár egyszerű belépési pont kivételével a kliens nem feltételezi egyik művelet meglétét sem.

## REST API tervezése

Az alábbi részben megvizsgáljuk, hogy a fenti megszorítások alapján miként tervezzük meg a REST API-nkat. Ha a fenti megszorítások mindegyike teljesül, akkor biztosra vehetjük, hogy RESTful lett az API-nk és élvezhetjük áldásos tulajdonságait.

### Kliens-szerver architektúra

Itt sok megvizsgálni való nincs, ketté kell vágjuk az alkalmazásunkat egy kliens és egy szerver részre.

### Állapotmentesség

A megszorítás leírásánál szépen le van írva, hogy mire figyeljünk.
A legnagyobb gond az szokott lenni, hogy sessiont használnak.
A session a kliens állapotát tárolja.
Ne csináljuk!

### Gyorsítótárazhatóság

Ez a rész is el van hanyagolva, pedig a teljesítmény szempontjából nagyon fontos lehet, hogy a skálázhatóságot mind a kliens mind a szerver oldalról kezeljük.
A szerver oldalon a REST alkalmazás állítsa be a cache-elhető erőforrásokra a [cache beállításokat](https://developer.mozilla.org/en-US/docs/Web/HTTP/Caching), ezeket a beállításokat pedig a kliens vegye figyelembe és használja!

### Réteges felépítés

Itt sincs sok vizsgálni való.
Ha minden megszorítást alkalmazunk, akkor élvezhetjük a réteges felépítés előnyeit.

### Igényelt kód (opcionális)

Ebbe se merülnék bele, főleg mivel opcionális.
Általában ritkán van rá szükség.

### Egységes interfész

A legfontosabb pont és a legtöbb vitára adó.
Nézzük részletesen!

#### Erőforrások azonosítása

Ez a pont arról szól, hogy minden erőforrást tudjunk azonosítani és azon keresztül elérni.
HTTP-s REST megvalósítás esetén erre az URI a legalkalmasabb.
Ezt a pontot általában félreértik, így ezt szokták tenni: az összes erőforrást meghatározzák, szépen kigondolt útvonalakkal ellátják és ezt, mint REST API-t a kliensek felé megadják.

- Meghatározzuk az erőforrásokat. Ez egy jó dolog!
- Szépen kigondolt útvonalakkal látjuk el ezeket. Fölösleges, de nincs vele nagy gond.
- Ezt adjuk át a kliensnek, mint REST API. **Ez teljesen rossz dolog!** Ne tegyük!

Miért ne tegyük?
Erre választ kapunk a *Hipermédia, mint az alkalmazásállapot motorja* részben.

##### Mi lehet erőforrás?

Itt is sok a félreértés.
Bármi lehet erőforrás!
Semmilyen kitétel nincs rá.
Lehet objektum, adat, szolgáltatás, de akár lehet parancs is, bármi.
Összefoglalóan, erőforrás tetszőleges koncepció (fogalom) lehet, amit el tudunk nevezni.

Eljárások is lehetnek erőforrások? Igen, azokat is el tudjuk nevezni.

Akkor ez nem REST, hanem RPC!

Igen ez RPC, de egyben REST is, ha mindenben megfelel a REST definícióinak, megszorításainak, erőforrásként tekintünk rá, van azonosítója, azon keresztül elérhetjük és/vagy módosíthatjuk és/vagy törölhetjük.
Ez azt jelenti, hogy készíthetünk olyan REST alkalmazást, aminek egy része vagy az egész RPC is. Másként fogalmazva az RPC-nek és a REST-nek van közös metszete.

Erre még később visszatérünk.

#### Erőforrások manipulációja ezeken a reprezentációkon keresztül

A definíció önmagáért beszél, nincs mit hozzáfűzni.

#### Önleíró üzenetek

A definíció önmagáért beszél, nincs mit hozzáfűzni.

#### Hipermédia, mint az alkalmazásállapot motorja.

Ez az egyik legfontosabb pont!
Nézzük újra meg a definícióját!

*A kliensek csakis azokon az állapotokon mehetnek át, amelyeket a szerver által küldött hipermédia tartalmaz hivatkozások alakjában. Pár egyszerű belépési pont kivételével a kliens nem feltételezi egyik művelet meglétét sem.*

Ez az egyik legfontosabb pont, ennek ellenére egyik kitételét sem szokták betartani.

- Eleve a szerver nem szokta elküldeni a hipermédia hivatkozásokat.
- Ha van, amit el is küld, a kliens akkor sem csak azokon az állapotokon mehet át, hanem olyanokon is, amiket nem küldött el a szerver.
- Végül, nem csak pár egyszerű belépési pontot feltételez a kliens, hanem a korábban látott, az összes erőforrásra meghatározott útvonal felsorolást.

Ez egy szükséges pont, nem opcionális.
Tehát amelyik rendszer ezt nem teljesíti az nem is RESTful!

##### Miért ez az egyik legfontosabb pont?

- Ha így csináljuk, akkor a kliens és a szerver valóban egymástól külön fejleszthető.
Amint beteszünk a szerverbe új funkciókat, kiveszünk régieket, módosítjuk az erőforrások azonosítóit, ..., a kliens módosítása nélkül azonnal használhatóvá válik.
- Nem fog gondot okozni a verziókezelés sem.
Ha egy új verziót nem ismer valamelyik kliens, akkor sincs semmi gond, működik a régivel, a régi funkcionalitással.
Amint az a kliens is ismeri az új verziót, azon nyomban használni is tudja.
Ez fordítva is igaz, a kliens már ismeri az új verziót, de nem fog a szervertől hipermédia hivatkozásokat kapni az új verzióra, amíg a szerverbe azt bele nem fejlesztik.
Magyarán teljesen mindegy, hogy az új verzió kezelése előbb kerül be a kliensekbe vagy előbb a szerverbe, de semmiképpen sem kell, hogy ez egyszerre történjen!
- Könnyen skálázhatunk is a segítségével. Bizonyos erőforrásokat áttehetünk prémium szerverekre (változik az URI-ja), hogy a prémium ügyfeleknek jobb élményt nyújtsunk, anélkül, hogy a kliens programokon változtatni kellene.
- Nagyon sok hiba származik abból, hogy a kliens rossz útvonalon próbálkozik. Pl. kimarad egy útvonal részlet, rossz a sorrendjük, vagy elmarad egy URL enkodolás.
- Csökkenthető az érvénytelen állapot átmenetek száma. Pl. a korábban említett **/api/orders/3f2418ca/pay** hivatkozást csak akkor küldi a szerver, ha még nem volt az adott rendelés kifizetve. Így elkerülhető, hogy még egyszer megpróbálja a kliens kifizetni, ha már ki volt fizetve.

##### Mit ismerjen a kliens?

Ha a kliens nem ismeri az erőforrás azonosítók felépítését, akkor honnan tudja, hogy miket kérdezhet le és hogyan módosítsa az erőforrást?

- Minimum kell ismernie a REST rendszerünk **belépési pont**ját (1), pl. **/api**
- Ha el tud érni egy erőforrást (pl. a belépési pontot), akkor az erőforrás használatával (*GET*, *POST*, ...), vagy csak az opciók (*HEAD*) lekérdezésével, visszakapja az adott erőforrással **rel**ációban (2) lévő hipermédia hivatkozásokat, illetve minden hivatkozáshoz az **erőforrás média típusát** (3).

Tehát a kliensnek ezeket és csak ezeket kell ismernie:
1. Alkalmazás beépési pontját.
2. Az egyes erőforrás reprezentációk **rel**ációját más erőforrásokhoz.
3. Egyes **erőforrások média típusának** tartalmát.

Nem szükséges minden relációt és médiatípust ismernie, amiket ismer, azokat tudja használni, a többi olyan, mintha nem is lenne a kliens számára.

Pl. Ha a fenti **/api/orders/3f2418ca** erőforrást használjuk, akkor a válaszban kaphatunk egy ilyen hivatkozást, Link: <https://.../api/orders/3f2418ca/pay>; rel="pay"; type="payment/card".
A *pay* relációból tudja, hogy azon URI adja meg a fizetés erőforrását, a *type*-ból pedig tudja, hogy kártyás fizetés típusú objektumot kell átadjon. Ha ismeri a *pay* relációt és ismeri *"payment/card"* média típust is, akkor el tudja végeztetni a rendelés kifizetését, pl. a felhasználó számára meg tudja jeleníteni a fizetési adatokat, majd kitöltésük esetén elküldi a megadott URI-ra.

## Egyéb gondolatok

### Erőforrás azonosítás

A "hagyományos" REST API-k esetén hatalmas problémát jelent az erőforrások azonosítása, még nagyobbat, ha át akarjuk nevezni vagy átszervezni ezeket.

Itt ilyen probléma nincs, bárhogy elnevezhető egy-egy erőforrás, sőt ajánlott nem könnyen beazonosítható azonosítók használata, pl. egy UUID használata.

*/api/7a79169c-a5a2-4b96-86c6-c0dc3d23f2dc* vs */api/orders/4*

- Vajon melyik erőforrás azonosító esetén van könnyebb dolga egy támadónak?
- Vajon melyik erőforrás azonosító esetén használja bizonyosan a kliens a hipermédia linkeket?

### Verziókezelés

Szintén nagy hasfájásokat okoz a "hagyományos" elgondolásnál.
Itt nincs semmi gond vele.
Ha a korábban látott *payment/card* média típus megváltozna (nem alulról és felülről kompatibilis módon), akkor egyszerűen bevezetünk egy *payment/card2* típust.
Amelyik kliens már ismeri, azok tudnak majd fizetni kártyával, amelyek nem azok nem.
Természetesen az új mellett ideiglenesen meghagyhatjuk a régi típust is, így addig a régivel is tudnak fizetni a kliensek.

### HTTP metódusok

Itt is ölre menő viták szoktak lenni, hogy mikor melyiket használjuk.

#### Melyik HTTP metódusokat célszerű használni?

Elsősorban a biztonságos metódusokat célszerű, mivel azok nem változtatják meg a szerver állapotát.

Ha meg kell változtassuk a szerver állapotát, akkor célszerű idempotens metódusokat használni.
Az idempotens azt jelenti, hogy ha egyszer küldjük el az üzenetet a szervernek, akkor is ugyanaz lesz a szerver állapota, mint amikor többször egymás után küldjük el ugyanazt az üzenetet.

Miért jobb az idempotens művelet?

Nézzük ezt meg a POST (nem idempotens) és a PUT (idempotens) példáján!

Vegyünk fel egy új rendelést!

- **POST** /api/orders/, ami létrehoz egy rendelést, visszakapjuk, hogy a 4-es számú rendelést hozta létre.
- **PUT** /api/orders/4, ez szintén a 4-es számú rendelést hozza létre vagy, ha már létezik, akkor felülírja.

Elsőre a **PUT** tűnik veszélyesebbnek.
Felülírja, ha már létezik?
Könnyen lehet, hogy egy másik kliens is épp akkor veszi fel a 4-es rendelést, ekkor előfordulhat, hogy az egyik kliens felülírja a másikét.
Ez így van, ez egy fontos probléma.

Szerencsére ezt könnyen orvosolhatjuk!

1. Egy metaadattal jelezhetjük, hogy új felvételét akarjuk csak megengedni, pl. **PUT** /api/orders/4?new=true
Ebben az esetben ez első üzenet feldolgozásakor felveszi 4-es azonosítóval az új rendelést, majd a következő kliens üzeneténél látja, hogy már van ilyen és visszautasítja a felvételt.
Ez annyiból jó megoldás, hogy elkerültük a felülírást, de nem engedjük mindkét kliensnek a felvételt, rossz esetben kezdheti újra a visszautasított kliens.
2. Azonosításra használhatunk UUID-t (némely esetben HASH-t), ami garantálja, hogy egyedi lesz az azonosító.
A mi rendszerünkben mindenképp egyedi lesz, ha UUID 1 v. 2 eljárást alkalmazunk, de az egész világon is elég nagy valószínűséggel. Emlékezzünk, hogy REST rendszerben, a hivatkozásokat a szervertől kapja a kliens, így ezt a hivatkozást is, amivel újat tud felvenni, tehát a kliensnek nem kell (és nem is tud) törődni az UUID generálással.

A nagyobb biztonság kedvéért a két megoldást kombinálhatjuk is.

A másik probléma az, ha az üzenetre nem érkezik válasz, (pl. timeout, megszakad a kapcsolat).
Ilyenkor a kliens nem tudja, hogy a szerver feldolgozta-e az üzenetet, jelen esetben, hogy felvette-e a rendelést. **POST** esetén az is, hogy ha fel is vette, akkor mi lett az azonosítója.

Idempotens műveleteknél (**PUT**) nincs ilyenkor nagy gond, mert újra elküldjük az üzenetet, amíg nem kapunk rá választ. Mivel idempotens, ezért ugyanaz a hatása az egyszeri küldésnek és a többszörinek is.

Nem idempotens műveleteknél (**POST**) viszont bajban vagyunk. Ha nem küldjük újra, akkor lehet, hogy egyet sem vett fel. Ha újra küldjük, akkor lehet, hogy többet is felvesz. Erre a problémára viszont nem igazán van megoldás.

A fentiek alapján én ezeket a metódusokat ajánlom használni:
- **GET**. Erőforrás lekérdezésére. Biztonságos, cache-elhető és idempotens.
- **PUT**. Erőforrás felvételére és módosítására. Idempotens.
- **DELETE**. Erőforrás törlésére. Idempotens.
- **HEAD**. Erőforrás fejléc, pl. hivatkozások, lekérésére. Biztonságos, cache-elhető és idempotens.

Én a többi metódust nem javaslom használni, a többi nem idempotens és kiváltható a fentiekkel.

#### Távoli eljárás hívás (RPC)

Nagy probléma az is, hogy melyik HTTP metódust használjuk, ha nem felvétel, módosítás, törlés műveletről van szó, hanem tetszőleges más eljárásról.

Vegyünk egy példa eljárást:
```
    launchMissile(date, target)
```

Ehhez felvehetünk egy `/api/procedures/launchMissile` erőforrást, ami pl. `application/json+LaunchMissileParameter` médiatípust tud fogadni.
Összeállítja a kliens a megfelelő jsont (date és target mezőkkel), majd azt elküldi **POST**-tal a fenti URI-ra.

Habár ez is REST, de a szellemiségével azért nem teljesen egyezik, így csak szükséges esetben használjuk!

#### Távoli függvény hívás

Hasonló, mint az RPC, de itt egy létező erőforráson szeretnénk valamilyen függvényt végezni, vagy olyan függvényt alkalmazni, aminek az eredménye egy erőforrás lesz.
Azért nem eljárásnak hívom, hanem függvénynek, mert van bemenete és van kimenete is.

Ennek megvalósítására több lehetőségünk is van:
1. A módosítandó erőforrás gyereke lesz a végrehajtandó függvény erőforrás, a body részben pedig a függvény paraméterezése megy át.
Pl. `/api/orders/3f2418ca/pay` lesz a gyerek erőforrás, az üzenet törzsben pedig a paraméterek mennek át az annak megfelelő médiatípus formában (pl. `PaymentParameters`).
2. A módosítandó erőforrás címére küldjük a végrehajtandó függvény médiatípust (pl. `Payment`).
3. A létrejövő erőforrás címére küldjük a végrehajtandó függvény médiatípust (pl. `Payment`).

### Visszatérési érték

Itt is sok szokott lenni a kérdőjel.
Itt nincs határozott álláspontom.
Szerintem két megoldás is elfogadható, de én inkább az elsőt javaslom:
- Minden esetben használjuk a HTTP visszatérési kódokat (200, 404, ...).
- A http protokollra vonatkozó visszatérési értékek esetén használjuk azokat, az alkalmazásra vonatkozó problémák esetén pedig magában a visszaadott erőforrásban adjuk meg.
Ennek az az egyik előnye, hogy ha más protokollra állnánk át, akkor az könnyen menne; a másik az, hogy elkülönül az alkalmazás hiba a protokoll hibáktól, így könnyebb a szerver alkalmazásban is ezt kezelni.

### Számlakezelési feladat példa

A következő tevékenységeket kell tudnia:
- számla felvétele,
- számla adatainak lekérése,
- számla törlése,
- számla elnevezés módosítása,
- utalás számlára,
- összeg levonása.

Ezt a következő API-val lehet leírni:

Belépési pont (_root_): /api

Átmenetek:

| Context       | rel      | metódus | Content-Type médiatípus | Accept médiatípus |
|---------------|----------|---------|-------------------------|-------------------|
| _root_        | accounts | GET     |                         | List[Account]     |
| List[Account] | new      | PUT     | Account                 | Account           |
| List[Account] | item     | GET     |                         | Account           |
| Account       | delete   | DELETE  |                         | Account           |
| Account       | edit     | PUT     | Name                    | Account           |
| Account       | edit     | PUT     | Deposit                 | Account           |
| Account       | edit     | PUT     | Withdraw                | Account           |

Médiatípusok:
- Account(name: Name, number: AccountNumber, balance: Double)
- Name(value: String[80])
- Deposit(sourceAccount: AccountNumber, amount: Double)
- Withdraw(targetAccount: AccountNumber, amount: Double)

Egyéb adattípus:
- AccountNumber: [0-9]{16,24}
