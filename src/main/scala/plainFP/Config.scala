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
}

object ConfigApp extends App {
  import ConfigAPI._

  println(readPerson(Config("John Doe", 20)))
  println(readPerson(Config("Incognito", 99)))
  println(readPerson(Config("John Doe", 170)))
}
