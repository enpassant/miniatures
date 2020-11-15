package com.example

import scala.language.implicitConversions

case class IntValue(x: Int) {
  def less(b: Int): Option[Int] =
    if (x < b) Some(b) else None
}

object Icon extends App {
  implicit def int2Integer(x: Int) = IntValue(x)
  val x = 8
  println(
    for {
      expr <- 2 less x
    } yield (expr * 2)
  )
}
