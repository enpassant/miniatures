package plainFP

import scala.language.reflectiveCalls
import scala.util.Random

import Common._

object SimpleFP {
  def runApp(implicit config: Config) = {
    val webServer = new WebServer(config.host, config.port)
    val dbServer = new DBServer(config.dbUrl, config.dbUser, config.dbPassword)
    val smtpServer = new SmtpServer(
      config.smtpServer,
      config.smtpUser,
      config.smtpPassword
    )

    val getPerson = (id: String) => using(dbServer)(_.getEntity[Person](id))
    val updatePerson = (person: Person) =>
      using(dbServer)(_.updateEntity[Person](person))

    val createAndSendMail = (person: Person) => {
      val mail = createMail(config.smtpFrom)(person)
      using(smtpServer)(_.sendMail(mail))
      person
    }

    //val processOpt: Request => Either[Failure, Response] =
      //getMessage andThen parseJson andThen
      //getPerson ~~> modifyPerson(config.personNameToUppercase) ~~>
      //updatePerson ~> createAndSendMail ~> makeResponse

    val processOpt: Request => Either[Failure, Response] =
      getMessage andThen
      parseJson andThen
      getPerson andThen
      flatMap(modifyPerson(config.personNameToUppercase)) andThen
      flatMap(updatePerson) andThen
      map(createAndSendMail) andThen
      map(makeResponse)

    val process: Request => Response = request =>
      processOpt(request) match {
        case Right(response) => response
        case Left(failure) => Response(failure.toString)
      }

    using(webServer){ ws => while (ws.listen(process)) {} }
  }

  val getMessage = (request: Request) => request.message
  val makeResponse = (person: Person) => Response(person.toString)

  case object ModifyPersonFailed extends Failure

  def modifyPerson(toUpperCase: Boolean)(person: Person) = {
    val modifiedPerson = person.copy(age = person.age + 1)
    if (toUpperCase) {
      if (Random.nextInt(100) < 30) Left(ModifyPersonFailed)
      else Right(modifiedPerson.copy(name = modifiedPerson.name.toUpperCase))
    } else {
      Right(modifiedPerson)
    }
  }

  def createMail(from: String)(person: Person) = {
    Mail(from, person.email, "Person updated", person.toString)
  }

  def parseJson(json: String) = json

  def using[A <: { def close: Unit }, B](resource: A)(f: A => B): B = {
    val result = f(resource)
    resource.close
    result
  }
}

final case class ID private(val key: String)

object ID {
  private val keyPattern = raw"(\d{4}-\d{4}-\d{4})".r
  def apply(key: String) = key match {
    case keyPattern(k) => Some(new ID(k))
    case _ => None
  }
}

object SimpleFPApp extends App {
  println(ID("0123-4567-8901"))
  println(ID("122"))

  implicit val config = Config(
    "localhost",
    9000,
    "jdbc:..",
    "dbUser",
    "dbPassword",
    "smtp.local.com",
    "smtpUser",
    "smtpPassword",
    "simple@app.com",
    true
  )

  SimpleFP.runApp
}
