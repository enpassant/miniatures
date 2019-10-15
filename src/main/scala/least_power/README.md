# A legkisebb erő

Ha valaki egy leendő Csillagok Háborúja epizódra gondolt a cím alapján,
azokat el kell keserítsem, ismét programozás elméletről lesz szó.

Régóta foglalkoztatja a programozókat, hogy vajon mi alapján lehet megmondani,
hogy az egyik program(rész) vagy program design jobb, mint a másik.

Miközben _a válasz az életre, a világmindenségre, meg mindenre_ már megvan,

![42](https://upload.wikimedia.org/wikipedia/commons/5/56/Answer_to_Life.png)

aközben a fenti kérdésre eddig nem igazán volt válasz.

Egész pontosan már régóta megvan a válasz a kérdésre,
sőt ez nem csak a programozásra igaz, hanem tetszőleges tervezésre és
    az élet egyéb problémáira is.
A válasz már régóta megvan, de nem igazán vesszük észre. Ha összetalálkozunk vele az utcán, akkor simán elmegyünk mellette.

A válasz a kérdésre az egyszerűség.

## Rule of least power

* Principle of Least Privilege
* Occam's razor
* KISS (Kepp It Stupid Simple)
* **Leonardo da Vinci** _"Simplicity is the ultimate sophistication"_,
* **Shakespeare** _"Brevity is the soul of wit"_,
* **Mies Van Der Rohe** _"Less is more"_,
* **Bjarne Stroustrup** _"Make Simple Tasks Simple!"_,
* **Antoine de Saint Exupéry** _"It seems that perfection is reached not when there is nothing left to add, but when there is nothing left to take away"_

[Constraints Liberate, Liberties Constrain - Runar Bjarnason](https://www.youtube.com/watch?v=GqmsQeSzMdw)

## Nézzünk példákat!

| kicsi erő                          | < | NAGY ERŐ                           |
| ---------------------------------- | - | ---------------------------------- |
| konkrét                            | < | absztrakt                          |
| nem polimorfikus függvény          | < | polimorfikus függvény              |
| kompozíció                         | < | öröklés                            |
| külön adat és függvény             | < | egységbe zárás                     |
| totális függvény                   | < | parciális függvény                 |
| determinisztikus függvémy          | < | nem determinisztikus függvémy      |
| pure függvény                      | < | impure függvény                    |
| immutable változó                  | < | mutable változó                    |
| lineáris kód végrehajtás           | < | vezérlési szerkezetek              |
| single level of abstraction        | < | több szint                         |
| egy paraméteres függvény           | < | több paraméteres függvény          |
| saját függvény                     | < | library                            |
| library függőség nélkül            | < | library függőségekkel              |
| library                            | < | framework                          |
