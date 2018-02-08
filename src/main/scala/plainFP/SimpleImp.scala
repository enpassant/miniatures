package plainFP

object SimpleImp {
  def runApp(implicit config: Config) = {
    val webServer = new WebServer(config.host, config.port)
    while (webServer.listen(process)) {}
    webServer.close
  }

  def process(request: Request)(implicit config: Config): Response = {
    val id = parseJson(request.message)

    val modifiedPerson = updateEntity(id)

    modifiedPerson map { person =>
      sendMail(person)
      Response(person.toString)
    } getOrElse Response("Error!")
  }

  def updateEntity(id: String)(implicit config: Config) = {
    val dbServer = new DBServer(config.dbUrl, config.dbUser, config.dbPassword)

    val personOpt = dbServer.getEntity[Person](id)
    val modified = personOpt map { person =>
      val modifiedPerson = modifyPerson(person)
      dbServer.updateEntity(modifiedPerson)

      modifiedPerson
    }

    dbServer.close

    modified
  }

  def modifyPerson(person: Person)(implicit config: Config) = {
    val modifiedPerson = person.copy(age = person.age + 1)
    if (config.personNameToUppercase) {
      modifiedPerson.copy(name = modifiedPerson.name.toUpperCase)
    } else {
      modifiedPerson
    }
  }

  def createMail(person: Person)(implicit config: Config) = {
    Mail(config.smtpFrom, person.email, "Person updated", person.toString)
  }

  def sendMail(person: Person)(implicit config: Config) = {
    val smtpServer = new SmtpServer(
      config.smtpServer,
      config.smtpUser,
      config.smtpPassword
    )

    val mail = createMail(person)
    smtpServer.sendMail(mail)

    smtpServer.close
  }

  def parseJson(json: String) = json
}

object SimpleImpApp extends App {
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

  SimpleImp.runApp
}
