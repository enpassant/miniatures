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

  def readPerson(name: String, age: Int): Option[Person] = {
    for {
      n <- readName(name)
      a <- readAge(age)
    } yield Person(n, a)
  }
}

object ConfigApp extends App {
  import ConfigAPI._

  val config1 = Config("John Doe", 20)
  println(readPerson(config1.name, config1.age))
  val config2 = Config("Incognito", 99)
  println(readPerson(config2.name, config2.age))
  val config3 = Config("John Doe", 170)
  println(readPerson(config3.name, config3.age))
}
