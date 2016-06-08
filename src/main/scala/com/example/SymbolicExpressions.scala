package com.example

object SymbolicExpressions {
  type Variable = () => Unit
  type Context = Variable => Float
  type Expression = Context => Float
  type Operator = (Expression, Expression) => Expression

  def variable = () => ()

  val value = (variable: Variable) => (context: Context) => context(variable)

  val constant = (value: Float) => (context: Context) => value

  val set = (variable: Variable, value: Float, expression: Expression) =>
    (context: Context) =>
  {
    val newContext: Context = {
      case `variable` => value
      case v => context(v)
    }
    expression(newContext)
  }

  val operator = (floatOperator: (Float, Float) => Float) =>
    (a: Expression, b: Expression) =>
      (context: Context) =>
  {
    floatOperator(a(context), b(context))
  }

  val plus: Operator = operator(_ + _)
  val minus: Operator = operator(_ - _)
  val multiply: Operator = operator(_ * _)
  val divide: Operator = operator(_ / _)

  val sqrt = (x: Expression) => (context: Context) => math.sqrt(x(context)).toFloat
}

object ExpressionSample extends App {
  import SymbolicExpressions._

  val a = variable
  val b = variable
  val c = variable
  val d = variable

  val expression = set(d, 1f, plus(minus(multiply(divide(value(a), value(b)), value(c)), value(d)), sqrt(constant(25f))))

  val context = Map(a -> 1f, b -> 2f, c -> 3f, d -> 4f)
  println(expression(context))
}
