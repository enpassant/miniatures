package shape

object CalculatorPF extends App {
  def literal(v: Double): Double = v
  def add(a: Double, b: Double): Double = a + b

  def literalPP(v: Double) = { println(s"$v"); v }
  def addPP(a: Double, b: Double) = { println(s"$a + $b"); a + b }

  case class Calculator[T](
    literal: T => T,
    add: (T, T) => T
  )

  val doubleCalculator = Calculator(literal, add)
  val ppCalculator = Calculator(literalPP, addPP)

  def calc[T](expression: Calculator[T] => T)(calculator: Calculator[T]) = {
    expression(calculator)
  }

  val expression = calc[Double] { c =>
    val la = c.literal(10.2)
    val lb = c.literal(1.5)
    c.add(la, lb)
  } _

  println(expression(doubleCalculator))
  expression(ppCalculator)
}
