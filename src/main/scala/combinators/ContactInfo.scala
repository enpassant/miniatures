package combinators

case class ContactInfo(name: String, email: String)

object ContactInfoService {
  def toContactInfoList(
    csv: Seq[String],
    nameRequired: Boolean,
    emailRequired: Boolean): Seq[ContactInfo] = {
    csv
      .map(_.split(';'))
      .map(tokens =>
        (tokens.headOption.getOrElse(""),
          tokens.drop(1).headOption.getOrElse("")))
      .flatMap {
        case (name, email) =>
          if ((name == "" && nameRequired) || (email == "" && emailRequired)) {
            None
          } else {
            Some(ContactInfo(name, email))
          }
      }
  }

  type Converter = PartialFunction[(String, String), Option[ContactInfo]]

  def toContactInfoList2(
    csv: Seq[String],
    converter: Converter): Seq[ContactInfo] = {
    csv
      .map(_.split(';'))
      .map(tokens =>
        (tokens.headOption.getOrElse(""),
          tokens.drop(1).headOption.getOrElse("")))
      .flatMap {
        case (name, email) => converter(name, email)
      }
  }

  def makeContactInfo: Converter = {
    case (name, email) => Some(ContactInfo(name, email))
  }

  def noEmptyName: Converter = {
    case ("", _) => None
  }

  def noEmptyEmail: Converter = {
    case (_, "") => None
  }
}

object ContactInfoApp extends App {
  import ContactInfoService._

  val csv = List(
    ";",
    "Teszt Elek;",
    ";teszt@elek.hu",
    "Teszt Elek;teszt@elek.hu"
  )

  println(toContactInfoList(csv, true, true))
  println(toContactInfoList(csv, false, true))
  println(toContactInfoList(csv, true, false))
  println(toContactInfoList(csv, false, false))

  println(
    toContactInfoList2(
      csv,
      noEmptyName orElse noEmptyEmail orElse makeContactInfo)
  )
  println(toContactInfoList2(csv, noEmptyEmail orElse makeContactInfo))
  println(toContactInfoList2(csv, noEmptyName orElse makeContactInfo))
  println(toContactInfoList2(csv, makeContactInfo))
}
