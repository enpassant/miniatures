package independence

import scala.Enumeration
import scala.reflect._
import scala.util.{ Try, Success, Failure, Either, Left, Right }
import Level._

trait Fault {
  def level: Level
}

trait FieldFault extends Fault {
  def field: String
}

case class ExceptionError(cause: Throwable, level: Level = ERROR) extends Fault

case class PredicateDoesNotHoldFor[T](value: T, level: Level = FATAL)
  extends Fault

trait Information

sealed trait Result[+T] {

  def infos: Vector[Information]

  def ++[T](that: Result[T]): Result[T]

  def flatMap[B](fn: T => Result[B]): Result[B]

  def map[B](fn: T => B): Result[B] = flatMap(t => Result(fn(t)))

  def check(p: T => Boolean, makeCause: T => Fault): Result[T] = this match {
    case r @ GoodResult(v, i) if (p(v)) => r
    case r @ GoodResult(v, i) => BadResult(makeCause(v), i)
    case r @ BadResult(c, i) => r
  }

  def filter(p: T => Boolean): Result[T] =
    check(p, value => PredicateDoesNotHoldFor(value))

  def withFilter(p: T => Boolean): Result[T] = filter(p)

  def addInformation(info: Information): Result[T] = this match {
    case r @ GoodResult(v, i) => r.copy(infos = infos :+ info)
    case r @ BadResult(c, i) => r.copy(infos = infos :+ info)
  }

  def isGood = this match {
    case GoodResult(_, _) => true
    case _ => false
  }

  def processInfos[I:ClassTag,R](processor: Vector[I] => Result[R]): Result[R] =
    this match {
      case GoodResult(_, _) =>
        processAllInfos(processor)
      case b @ BadResult(_, _) => b
  }

  def processAllInfos[I:ClassTag,R](processor: Vector[I] => Result[R]):
  Result[R] = {
    val infos = this.infos collect {
      case info: I => info
    }
    this ++ processor(infos)
  }
}

case class GoodResult[T](value: T, infos: Vector[Information] = Vector())
  extends Result[T]
{
  def flatMap[B](fn: T => Result[B]): Result[B] = {
    fn(value) match {
      case r @ GoodResult(v, i) => r.copy(infos = infos ++ i)
      case r @ BadResult(c, i) => r.copy(infos = infos ++ i)
    }
  }

  def ++[T](that: Result[T]): Result[T] = that match {
    case r @ GoodResult(v, i) => r.copy(infos = infos ++ i)
    case r @ BadResult(c, i) => r.copy(infos = infos ++ i)
  }
}

case class BadResult(cause: Fault, infos: Vector[Information] = Vector())
  extends Result[Nothing]
{
  def flatMap[B](fn: Nothing => Result[B]): Result[B] = this

  def ++[T](that: Result[T]): Result[T] = this
}

object Result {
  def apply[T](value: T) = GoodResult(value)

  def fromOption[T](opt: Option[T], cause: Fault) = opt match {
    case Some(value) => GoodResult(value)
    case None => BadResult(cause)
  }

  def fromTry[T](value: => T, cause: Option[Fault] = None) = Try(value) match {
    case Success(value) => GoodResult(value)
    case Failure(throwable) =>
      BadResult(cause getOrElse ExceptionError(throwable))
  }

  def fromEither[T](either: Either[Fault, T], cause: Option[Fault] = None) =
    either match {
      case Right(value) => GoodResult(value)
      case Left(left) =>
        BadResult(cause getOrElse left)
  }

  def validate[T](value: T, p: T => Boolean, info: Information):
    Result[Option[T]] =
  {
    if (p(value)) {
      GoodResult(Some(value))
    } else {
      GoodResult(None, Vector(info))
    }
  }
}
