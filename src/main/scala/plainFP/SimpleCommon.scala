package plainFP

import scala.language.higherKinds
import scala.util.Random

case class Config(
  host: String,
  port: Int,

  dbUrl: String,
  dbUser: String,
  dbPassword: String,

  smtpServer: String,
  smtpUser: String,
  smtpPassword: String,
  smtpFrom: String,

  personNameToUppercase: Boolean
)

case class Person(id: String, name: String, email: String, age: Int)
extends Entity

case class Request(message: String)
case class Response(message: String)

class WebServer(host: String, port: Int) {
  var count = 0

  def close = { println("WebServer has closed") }

  def listen(callback: Request => Response): Boolean = {
    val request = Request(count.toString)
    println(s" => $request")
    val response = callback(Request(count.toString))
    count = count + 1
    println(s"<= $response")
    count < 3
  }
}

trait Entity
case object GetEntityFailed extends Failure
case object UpdateEntityFailed extends Failure

class DBServer(url: String, user: String, password: String) {
  def close = { println("DBServer has closed") }

  def getEntity[T <: Entity](id: String): Either[Failure, T] = {
    val entity = Person(
      id,
      "John Doe",
      "john@doe.com",
      27 + Integer.parseInt(id)
    ).asInstanceOf[T]
    println(s"getEntity: id $id entity $entity")
    if (Random.nextInt(100) < 10) Left(GetEntityFailed)
    else Right(entity)
  }

  def updateEntity[T <: Entity](entity: T): Either[Failure, T] = {
    println(s"updateEntity: $entity")
    if (Random.nextInt(100) < 20) Left(UpdateEntityFailed)
    else Right(entity)
  }
}

case class Mail(from: String, to: String, subject: String, content: String)

case object SendMailFailed extends Failure

class SmtpServer(smtpServer: String, smtpUser: String, smtpPassword: String) {
  def close = { println("SmtpServer has closed") }

  def sendMail(mail: Mail): Either[Failure, Boolean] = {
    println(s"sendMail: $mail")
    if (Random.nextInt(100) < 20) Left(SendMailFailed)
    else Right(true)
  }
}

trait Failure

object Common {
  def map[A, B](f: A => B): Either[Failure, A] => Either[Failure, B] = {
    x => x.map(f)
  }

  def flatMap[A, B](f: A => Either[Failure, B]):
    Either[Failure, A] => Either[Failure, B] =
  {
    x => x.flatMap(f)
  }

  //implicit class Function1Option2[-T1, R](f: T1 => Either[Failure, R])
    //extends Function1[T1, Either[Failure, R]]
  //{
    //def apply(x: T1): Either[Failure, R] = f(x)

    //def ~>[A](g: (R) => A): (T1) => Either[Failure, A] = x => apply(x).map(g)
    //def ~~>[A](g: (R) => Either[Failure, A]): (T1) => Either[Failure, A] =
      //x => apply(x).flatMap(g)
  //}
}
