package com.example

object Decoration extends App {
  trait Logger {
    def debug(msg: String)
    def error(msg: String)
  }

  def log[A, B](logger: Logger)(fn: A => B): A => B = {
    input =>
      try {
        logger.debug(s"Start. <= $input")
        val start = System.nanoTime
        val output = fn(input)
        val time = (System.nanoTime - start) / 1000000
        logger.debug(s"End $time ms. => $output")
        output
      } catch {
        case e: Exception =>
          logger.error(e.toString)
          throw e
      }
  }

  trait DB {
    def begin
    def commit
    def rollback
    def select(sql: String)
  }

  class TestDB extends DB {
    def begin = println("[begin]")
    def commit = println("[commit]")
    def rollback = println("[rollback]")
    def select(sql: String) = println(s"[select] $sql")
  }

  def transaction[T](fn: DB => T): DB => T = {
    db =>
      try {
        db.begin
        val result = fn(db)
        db.commit
        result
      } catch {
        case e: Exception =>
          db.rollback
          throw e
      }
  }

  class TestLogger extends Logger {
    def debug(msg: String) = println(s"[Debug] $msg")
    def error(msg: String) = println(s"[Error] $msg")
  }

  def testLogger = new TestLogger
  def testDB = new TestDB

  val selectPerson = log(testLogger) {
    transaction {
      db =>
        db.select("select * from person")
        Thread.sleep(103)
        //throw new RuntimeException("Error")
        "Alex Smith"
    }
  }

  selectPerson(testDB)
}
