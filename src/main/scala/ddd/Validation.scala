package ddd

trait Failure
trait Warning extends Failure
trait Error extends Failure
trait Fatal extends Failure

sealed abstract class Validation[Value] extends Product with Serializable {
  def map[T](f: Value => T): Validation[T] = this match {
    case Passed(value, warnings) => Passed(f(value), warnings)
    case _ => this.asInstanceOf[Validation[T]]
  }

  def flatMap[T](f: Value => Validation[T]): Validation[T] = this match {
    case Passed(value, warnings) =>
      f(value) match {
        case Passed(v, ws) => Passed(v, warnings ++ ws)
        case Failed(errors) => Failed[T](warnings ++ errors)
      }
    case _ => this.asInstanceOf[Validation[T]]
  }

  def getOrElse(other: Value) = this match {
    case Passed(value, warnings) => value
    case _ => other
  }

  def noError: Validation[Value] = this match {
    case Passed(value, warnings)
      if warnings.exists(f => f.isInstanceOf[Error]) =>
        Failed(warnings)

    case _ =>
        this
  }
}

case class Passed[Value](value: Value, warnings: List[Failure] = Nil)
  extends Validation[Value]
case class Failed[Value](errors: List[Failure]) extends Validation[Value]

object Validation {
  def apply[Value](value: Value): Validation[Value] = Passed(value)
  def apply[Value](value: Value, failure: Failure): Validation[Value] =
    failure match {
      case error: Fatal => Failed(List(error))
      case warning: Failure => Passed(value, List(warning))
  }
}

case class TooLong(name: String) extends Warning
case class TooBig(quantity: BigDecimal) extends Warning
