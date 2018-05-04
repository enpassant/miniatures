# Problémák a függőséggel

Kezdő programozóként még nem nagyon törődtünk a függőségekkel.
Nem akartuk lecserélni a függőségeket másra, nem akartuk unit tesztelni a kódunkat, nem hallottunk még a pure-impure dolgokról, és nem tudtuk, hogy a statikus függvények rosszak, valamint azt, hogy a strong/tight coupling (erős, szoros függőség) az nagyon rossz.

Ha egy egyszerű, név bekérő és üdvözlő programot akartunk írni, akkor azt pl. így csináltuk:

```java
    public static void main(String[] args) {
        write("Enter your name: ");
        String name = read();
        String capitalizedName = capitalize(name);
        String greeting = "Hello " + capitalizedName + "!";
        write(greeting);
    }

    public static String read() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            return br.readLine();
        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public static void write(String str) {
        System.out.println(str);
    }

    public static String capitalize(String str) {
        if (str.isEmpty()) return "UNKNOWN";
        else return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
```

A programunk rövid és könnyen érthető, jól látszik, hogy mit csinál a `read`, `write`, `capitalize` függvény, és ezek alapján mit csinál a programunk.

Aztán megismertük az OOP világát, az egységbe zárást, a rétegekre bontást, a unit teszteket és a SOLID alapelveket.

A programunk ezek alapján már így néz ki:
```java
    interface Reader {
        String read();
    }
```

```java
    class StdInReader implements Reader {
        public String read() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                return br.readLine();
            } catch (IOException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }
```

```java
    interface Writer {
        void write(String str);
    }
```

```java
    class StdOutWriter implements Writer {
        public void write(String str) {
            System.out.println(str);
        }
    }
```

```java
    class Greeting {
        private Reader reader;
        private Writer writer;

        public Greeting(Reader reader, Writer writer) {
            this.reader = reader;
            this.writer = writer;
        }

        public void greet() {
            writer.write("Enter your name: ");
            String name = reader.read();
            String capitalizedName = capitalize(name);
            String greeting = "Hello " + capitalizedName + "!";
            writer.write(greeting);
        }

        private String capitalize(String str) {
            if (str.isEmpty()) return "UNKNOWN";
            else return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }
```

```java
    public static void main(String[] args) {
        // Wiring
        Greeting greeting = new Greeting(new StdInReader(), new StdOutWriter());

        greeting.greet();
    }
```

A programunk kétszer akkora lett, de megérte, mert sokkal jobban szeparált, megfelel a SOLID elveknek, szép OOP program és könnyen unit tesztelhető.
Ha a programunk lényegi részét szeretnénk unit tesztelni, akkor csak létrehozunk egy Greeting objektumot, mockoljuk a `Reader`, `Writer` osztályokat.
Így ahelyett, hogy a tényleges, side effectes read-write hívódna; szimulált, konzerv viselkedést tudunk leprogramozni. A mock `Reader` egy konzerv inputot ad, a mock `Writer`-rel pedig ellenőrizzük, hogy helyes outputot kapunk. **Remek! ;-)**

Egy ideig nagyon örülünk a szép új kódjainknak, de ahogy telik múlik az idő és egyre többen, egyre nagyobb programokat írunk, egyre nehezebbé válik a módosítás, egyre másra küzdünk a függőségek összekötésével.

Aztán rátalálunk az IoC (DI) container-ekre, amikkel nagyon egyszerűvé válik a függőségek összekötése.

Egy idő után azt vesszük észre, hogy még nehezebbé válik a program módosítása, még nehezebben értjük meg, hogy mi mire van és milyen hatással.

**Miért lehet ez?**

Elemezzük egy kicsit a szép, DI programunkat.
*Valóban egyszerű, könnyen érthető lett?*

**Sajnos nem igazán!**

Sajnos sok boilerplate kód került be, amik megnehezítik a megértést, de ugye ez az ár, amit vállaltunk, és erről tudunk is.

**Miről nem tudunk, mi nem nyilvánvaló?**

Nézzük meg a kódunk lényegi részét (`greet`)! Ha azt valaki megnézi, akkor tudja, hogy mit csinál az adott rész?
Sajnos nem, és ez elsőre nem nyilvánvaló!
**A `Reader` és a `Writer` sorokról nem tudjuk, hogy mit csinálnak ténylegesen.** Sejtelmünk lehet, de amíg a futás során fel nem oldódik egy konkrét implementációban, addig bármit csinálhat, akár rakétákat is indíthat. Sokkal rosszabb a helyzet, mint az első programunknál, mert ott elnavigálhattunk a konkrét működésre és láttuk, hogy stdin-ről olvasnak, és stdout-ra írnak.
Ennél a kódnál még annyiból jobb a helyzet, hogy a program belépési pontjától meg tudjuk keresni a függőségek összekötését (Wiring), és abból kibogozhatjuk, hogy melyik laza függőségnek épp mi lesz a megvalósítása. Ám amikor ez a gombolyag már nagyon nagy vagy amikor el van rejtve egy IoC container-ben, akkor erre esélyünk se nagyon lesz.

Mit tehetünk?

Fogjuk meg az első programunkat és alakítsunk rajta egy kicsit:
```java
    public static void main(String[] args) {
        write("Enter your name: ");
        String name = read();
        String greeting = createGreeting(name);
        write(greeting);
    }

    public static String createGreeting(String name) {
        String capitalizedName = capitalize(name);
        return "Hello " + capitalizedName + "!";
    }
```

Ezzel a kis módosítással kiemeltük a programunk lényegi részét egy pure függvénybe (`createGreeting`), amit nagyon egyszerűen tudunk mockok nélkül is unit tesztelni. Pont ugyanolyan hatékonysággal, csak sokkal egyszerűbben, hiszen a szükséges inputot mock objektum nélkül, direktben adjuk át és az ellenőrzendő outputot szintén mock obkjektum nélkül, direktben kapjuk meg.

* Tehát a unit tesztelhetőség rendben van.
* A rétegezés is nagyjából rendben van, hiszen kívül, egy helyen vannak a külvilággal érintkező részek és belül, szeparálva az üzleti logika.
* Az érthetőség is jó, sőt még jobb lett, mint az első esetben volt, mivel az üzleti logika ki van emelve és az önmagában is vizsgálható.
* Amit veszítettünk az a `write` és `read` futás közbeni cserélhetősége. Cserélni tudjuk, hiszen csak a `read` és `write` függvényeket kell átírni.

Ha megfigyeljük a programunkat, akkor még két erős függőséget vehetünk észre benne:

1. `capitalize`,
2. üdvözlet összeállítás (`"Hello " + capitalizedName + "!"`)

Az erős függés ellenére fel sem merült, hogy ezek nehezíthetik a tesztelést.
Miért?

*Azért, mert a tesztelést a side effect-ek nehezítik, az erősen függő, pure kódok nem!*

Mi a tanulság a fentiek alapján?

**A loose coupling (laza függőség) egyetlen előnye a futás közbeni cserélhetőség. Ha nincs szükséged futás közbeni cserélhetőségre, akkor nem szükséges a DI.**

Tartsd fejben: **YAGNI!**

Az igazán nagyon károsak az IoC containerek. Ami a nagy előnyük, azzal végeznek igazi pusztítást: *leveszik a programozó válláról a terhet az összekötések magadásáról és elrejtik ennek bonyolultságát előle*.
**Ha csak nem fizetnek érte, soha ne használj IoC containert! ;-)**

## References:

* [Dependency Injection is EVIL - Tony Marston](http://www.tonymarston.net/php-mysql/dependency-injection-is-evil.html)

* [Dependency Injection Makes Your Code Worse - Daniel Alexiuc](https://dzone.com/articles/dependency-injection-makes)

* [From Dependency injection to dependency rejection - Mark Seemann - Youtube](https://www.youtube.com/watch?v=cxs7oLGrxQ4)

* [Dependency rejection - Mark Seemann](http://blog.ploeh.dk/2017/02/02/dependency-rejection/)

* [When does Dependency Injection become an anti-pattern? - David Lundgren](http://davidscode.com/blog/2015/04/17/when-does-dependency-injection-become-an-anti-pattern/)
