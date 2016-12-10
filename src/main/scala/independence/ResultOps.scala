package independence

object Level extends Enumeration {
  type Level = Value
  val TRACE, DEBUG, INFO, WARN, ERROR, FATAL = Value
}
import Level._

case class Log(level: Level, place: String, message: () => String)
  extends Information
{
  override def toString = s"Log($level, $place, ${message()})"
}

trait DBStatement extends Information

case class DBPersist[T](entity: T) extends DBStatement
case class DBMerge[T](entity: T) extends DBStatement

case class Email(to: String, subject: String, message: String)
  extends Information

case class Sms(to: String, subject: String, message: String)
  extends Information

case class Validation(level: Symbol, field: String, message: String)
  extends Information

case class Validator[T](predicate: T => Boolean, cause: String)

object ResultOps {
  import Result._

  def log(level: Level, place: String, message: => String): Log = {
    lazy val msg = message
    Log(level, place, () => msg)
  }

  def log[T](level: Level, place: String, result: Result[T]) = {
    val message = () => result match {
      case GoodResult(v, i) => v.toString
      case BadResult(c, i) => c.toString
    }
    result.addInformation(Log(level, place, message))
  }

  def hasFatal[T](result: Result[T]): Result[T] = {
    val fatal = result.infos find {
      case v @ Validation(level, field, msg) if level == 'Fatal => true
      case _ => false
    }
    fatal match {
      case Some(_) => BadResult(TextError("Fatal validation"))
      case _ => result
    }
  }

  def validateField[T](
    value: T,
    field: String,
    validators: (Symbol, Validator[T])*): Result[Option[T]] =
  {
    validators.find {
      case (level, validator) => !validator.predicate(value)
    } map {
      case (l, v) => validate(value, v.predicate, Validation(l, field, v.cause))
    } getOrElse GoodResult(Some(value))
  }
}

