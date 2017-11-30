package plainFP

object ConfigAPI {
  case class Config(name: String, age: Int)
  case class Name(first: String, last: String)
  case class Age(age: Int)
  case class Person(name: Name, age: Age)

  def readName(name: String): Option[Name] = {
    val parts = name.split(" ")
    if (parts.length >= 2) Some(Name(parts(0), parts.tail.mkString(" ")))
    else None
  }

  def readAge(age: Int): Option[Age] = {
    if (1 <= age && age <= 150) Some(Age(age))
    else None
  }

  def readPerson(config: Config): Option[Person] = {
    for {
      name <- readName(config.name)
      age <- readAge(config.age)
    } yield Person(name, age)
  }

  // or better:
  def optMap2[A, B, C](f: (A, B) => C)(optA: Option[A], optB: Option[B]) =
    for {a <- optA; b <- optB} yield f(a, b)
}

object ConfigApp extends App {
  import ConfigAPI._

  val config1 = Config("John Doe", 20)
  println(readPerson(config1))
  println(optMap2(Person)(readName(config1.name), readAge(config1.age)))
  println(optMap2(Tuple2[Name, Age])(readName(config1.name), readAge(config1.age)))
  val config2 = Config("Incognito", 99)
  println(readPerson(config2))
  println(optMap2(Person)(readName(config2.name), readAge(config2.age)))
  val config3 = Config("John Doe", 170)
  println(readPerson(config3))
  println(optMap2(Person)(readName(config3.name), readAge(config3.age)))
}
