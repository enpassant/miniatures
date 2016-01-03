package com.example

trait Stack {
  type Elem

  def push(e: Elem): Unit
  def pop: Elem
}

trait AbstaractStackModel {
  type Elem
  var buffer = List.empty[Elem]

  def push(e: Elem): Unit = buffer = e :: buffer
  def pop: Elem = {
    val elem = buffer.head
    buffer = buffer.tail
    elem
  }
}

trait PrintStackModel {
  type Elem
  var buffer = List.empty[Elem]

  def push(e: Elem): Unit = {
    buffer = e :: buffer
    println(s"push($e)")
  }

  def pop: Elem = {
    val elem = buffer.head
    buffer = buffer.tail
    println(s"pop [$elem]")
    elem
  }
}

trait ConcreteStackModel[A] {
  type Elem = A
}

object StackMain extends App {
  val stack = new Stack with PrintStackModel with ConcreteStackModel[String]

  stack.push("7")
  stack.push("3")
  stack.push("6")
  stack.pop
  stack.pop
  stack.pop
}
