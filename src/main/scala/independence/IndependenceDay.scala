package independence

import scala.util.Try

object IndependenceDay extends App {
  import Level._
  import ResultOps._

  case class User(
    name: Option[String],
    phone: Option[String],
    email: Option[String])
  case class DBUser(id: Long, name: String)
  case class DBContact(id: Long, mode: String, value: String)

  case class Required(field: String, level: Level = ERROR) extends FieldFault
  case class WrongNameFormat(field: String, level: Level = ERROR)
    extends FieldFault
  case class WrongPhoneFormat(field: String, level: Level = ERROR)
    extends FieldFault
  case class WrongEmailFormat(field: String, level: Level = ERROR)
    extends FieldFault

  case class IdCookieIsMissing(level: Level = ERROR) extends Fault
  case class IdCookieIsNotNumber(level: Level = ERROR) extends Fault
  case class UserIdMustBeOdd(level: Level = ERROR) extends Fault
  case class MissingUserName(level: Level = ERROR) extends Fault
  case class MissingDbStatement(level: Level = ERROR) extends Fault

  def validatorRequired = Validator((n: String) => !n.isEmpty, "required")
  def regexValidator(regex: String, cause: String) =
    Validator((n: String) => n.matches(regex), cause)
  def validatorName = regexValidator("\\w+\\s+\\w+", "wrong name format")
  def validatorPhone = regexValidator("""\(\d+\)\d+-\d+""", "wrong phone format")
  def validatorEmail = regexValidator("\\w+@\\w+\\.\\w+", "wrong email format")

  def validateUser(name: String, phone: String, email: String) = {
    for {
      userName <- validateField(name, "userName",
        ('Fatal, validatorRequired),
        ('Error, validatorName)
      )
      userPhone <- validateField(phone, "userPhone",
        ('Error, validatorRequired),
        ('Error, validatorPhone)
      )
      userEmail <- validateField(email, "userEmail",
        ('Error, validatorRequired),
        ('Warning, validatorEmail)
      )
    } yield User(userName, userPhone, userEmail)
  }

  def insertUser(
    idCookieOpt: Option[String],
    name: String,
    phone: String,
    email: String) =
  {
    for {
      user <- log(DEBUG, "validateUser",
        hasFatal(
          validateUser(name, phone, email)
        )
      )
      userId <- log(DEBUG, "getUserId",
        getUserId(idCookieOpt)
      )
      persistedUser <- log(INFO, "persistUser",
        persistUser(userId, user)
      )
      messages <- sendMessages(
        "User inserted",
        s"${name} user has inserted",
        persistedUser collect { case dbContact: DBContact => dbContact })
    } yield persistedUser
  }

  def getUserId(idCookieOpt: Option[String]) = {
    for {
      idCookie <- Result.fromOption(idCookieOpt, Some(IdCookieIsMissing()))
      id <- Result.fromTry(idCookie.toLong, Some(IdCookieIsNotNumber()))
      if (id > 0)
      tripledId <- log(INFO, "id * 3", Result(id * 3))
      userId <- Result.fromEither(isUserIdOdd(tripledId))
    } yield userId
  }

  def isUserIdOdd(id: Long) = if (id % 2 == 1) {
    Right(id)
  } else {
    Left(UserIdMustBeOdd())
  }

  def persistUser(id: Long, user: User) = user.name match {
    case Some(name) =>
      val dbPhone = user.phone map { phone => DBContact(id, "phone", phone) }
      val dbEmail = user.email map { email => DBContact(id, "email", email) }
      val dbUser = Some(DBUser(id, name))
      val dbEntities = List(dbUser, dbPhone, dbEmail).flatten
      val dbStatements = dbEntities.map { e => DBPersist(e) }
      GoodResult(dbEntities, dbStatements)

    case _ =>
      BadResult(MissingUserName())
  }

  def sendMessages(
    subject: String,
    message: String,
    dbContacts: List[DBContact]) =
  {
    val messages = dbContacts map {
      case DBContact(_, "phone", phone) => Sms(phone, subject, message)
      case DBContact(_, "email", email) => Email(email, subject, message)
    }
    GoodResult(messages, messages)
  }

  def processResult(result: Result[_]) = {
    val validationResult = result.processAllInfos(processValidation)
    val transactionResult = validationResult.processInfos(runTransaction)
    val emailResult = transactionResult.processInfos(sendEmail)
    val smsResult = emailResult.processInfos(sendSms)
    val finalResult =
      if (smsResult.isGood) {
        smsResult.processInfos(processLog(WARN))
      } else {
        Console.err.println(smsResult)
        smsResult.processAllInfos(processLog(TRACE))
      }
    println()

    finalResult
  }

  def sendSms(smses: List[Sms]) = {
    if (smses.isEmpty) {
      Result(())
    } else {
      val tryTransaction = Try {
        smses foreach { sms => println(s"Send $sms") }
      }
      Result.fromTry(tryTransaction)
    }
  }

  def sendEmail(emails: List[Email]) = {
    if (emails.isEmpty) {
      Result(())
    } else {
      val tryTransaction = Try {
        emails foreach { email => println(s"Send $email") }
      }
      Result.fromTry(tryTransaction)
    }
  }

  def runTransaction(dbStatements: List[DBStatement]) = {
    if (dbStatements.isEmpty) {
      BadResult(MissingDbStatement())
    } else {
      val tryTransaction = Try {
        println("Begin transaction")

        dbStatements foreach { dbStatement => println(s"Execute $dbStatement") }

        println("End transaction")
      }
      Result.fromTry(tryTransaction)
    }
  }

  def processValidation(validations: List[Validation]) = {
    validations foreach { case Validation(level, field, message) =>
      println(s"Set $field field color by level $level. Message: $message")
    }
    Result(())
  }

  def processLog(minLevel: Level)(logs: List[Log]) = {
    logs foreach {
      case log @ Log(level, place) if level >= minLevel =>
        println(s"/LOG/ [$level] |$place|.${log.message()}")
      case _ =>
    }
    Result(())
  }

  processResult(getUserId(None))
  processResult(getUserId(Option("alma")))
  processResult(getUserId(Option("-5")))
  processResult(getUserId(Option("8")))
  processResult(getUserId(Option("7")))

  processResult(insertUser(Option("7"), "", "", ""))
  processResult(insertUser(Option("7"), "Elek", "", ""))
  processResult(insertUser(Option("7"), "Teszt Elek", "", ""))
  processResult(insertUser(Option("7"), "Teszt Elek", "(20)234-5678", "mail"))
  processResult(insertUser(Option("-7"), "Teszt Elek", "(20)234-5678", "teszt@elek.hu"))
  processResult(insertUser(Option("-7"), "", "54", "asa"))
  processResult(insertUser(Option("7"), "Teszt Elek", "(20)234-5678", "teszt@elek.hu"))
}
