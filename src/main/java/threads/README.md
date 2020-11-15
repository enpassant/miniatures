# Szálak

A következőkben a szálakról (Thread), szálak kezeléséről és a szálkészletekről (ThreadPool) lesz szó.
Elsősorban a JVM alatti szálakról lesz szó, de sok a hasonlóság más rendszerekben alkalmazottakkal.

## Mikor használjunk szálakat?

1. Ha fontos a teljesítmény és szeretnénk kihasználni a mai sok magos processzorokban rejlő lehetőségeket.
2. Ha konkurrensen akarunk kezelni erőforrásokat.
3. Ha egyik sem illik ránk, de olyan keretrendszer (pl. alkalmazásszerver) alá fejlesztünk, ami több szálat használ, akkor akarva akaratlanul is több szálat fogunk használni.

## Mikor futtassunk több szálat?

### Blokkoló utasítások (I/O)

Ha egy feladatot (task) futtatunk, akkor időnként előfordul, hogy blokkoló utasítást hajtunk végre. Ilyen például a legtöbb adatkezelés vagy más I/O utasítás. Ilyenkor az adott szál végrehajtása blokkolódik, gyakorlatilag semmit sem fog csinálni a programunk a válasz megérkezéséig. Ilyenkor célszerű egy másik szálon más feladatot végeztetni.

### Több mag esetén

Ha több mag van a processzorunkban, akkor érdemes legalább annyi szálon futtatni a feladatokat, amennyi mag van a processzorunkban.

## Mennyi szálat futtassunk?

Régen az volt az arany szabály, hogy ahány mag van a processzorunkban annyit plusz egy vagy kettőt. Ezt az elméletet már túlhaladtuk.

1. Ha CPU intenzív feladatot futtatunk, azokból pont annyit érdemes, amennyi mag van a processzorunkban. Ha kevesebbet futtatunk, akkor nincs kihasználva processzor, ha többet, akkor meg a konteksztus váltások miatt lassabb lesz a futtatás.
2. Ha blokkoló feladatot futtatunk, azokhoz plusz egy szálat érdemes létrehozni. Amíg blokkol, addig a többi szálat szépen futtathatja. Több száz blokkoló utasítás egymás melletti futtatása esetén akár több százat is érdemes.
3. Ha nem blokkoló I/O (NIO) válaszát kérdezgetjük le, erre és csak erre a kis időre elég pontosan egy magas prioritású szál. Ezen az egy szálon az összes ilyen aszinkron I/O kérdezgethető (polling).

## Hogyan érdemes futtatni a szálakat?

Érdemes kimenni a szálstrandra és igénybe venni a szálmedencéket! ;-)

Erre a legjobb a szálkészleteket (ThreadPools) igénybe venni.
Alapvetően két fajta szálkészletet különböztetünk meg:
1. Maximálisan meghatározott elemszámú. (pl. 1 vagy ahány magunk van).
2. Korlátlan elemszámú.

Ha egy szálkészleten keresztül futtatunk egy feladatot, akkor
1. ha nincs hozzá felhasználható szál, akkor készít egyet és berakja a szálkészletbe,
2. ha van, akkor kivesz egyet

és azt használja.

Az adott feladat végrehajtása után visszateszi a szálat a készletbe, így a már felkonfiguráltat gyorsan igénybe tudja venni. Meg lehet határozni egy időtartamot is, ameddig ha nincs használva, akkor szüntesse meg az adott szálat.

## Milyen szálkészletet érdemes használni?


