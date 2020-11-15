package zio

import zio.{App, ExitCode, IO, random}
import zio.blocking.effectBlocking
import zio.console._

import java.io.IOException

object MyApp extends App {

  def run(args: List[String]) =
    myAppLogic.fold(_ => ExitCode(1), _ => ExitCode(0))

  val finalizer = IO.effectTotal(println("Sleep interrupted"))

  def myAppLogic =
    for {
      r <- random.nextInt
      fiber <- effectBlocking({ Thread.sleep(5000); r})
        .ensuring(finalizer)
        .fork
      _ <- putStrLn("Hello! What is your name?")
      n <- getStrLn
      _ <- putStrLn("Hello " + n + ", good to meet you!")
      v <- fiber.interrupt
      _ <- putStrLn("Value: " + v)
    } yield ()
}
