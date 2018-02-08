package com.example

object Fibonacci extends App {
  def fibonacci(n: Int) = {
    def fib(current: Int, next: Int, index: Int): Int = {
      if (index <= 1) next
      else fib(next, current + next, index - 1)
    }
    fib(0, 1, n)
  }

  def tribonacci(n: Int) = {
    def trib(v1: Int, v2: Int, v3: Int, index: Int): Int = {
      if (index <= 1) v3
      else trib(v2, v3, v1 + v2 + v3, index - 1)
    }
    trib(0, 1, 1, n)
  }

  println(
    (1 to 10) map { n =>
      s"fibonacci($n)=${fibonacci(n)}"
    }
  )

  println(
    (1 to 10) map { n =>
      s"tribonacci($n)=${tribonacci(n)}"
    }
  )
}
