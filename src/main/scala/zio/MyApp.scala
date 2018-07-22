package zio

import scalaz.zio.{App, IO, Void}
import scalaz.zio.console._

import java.io.IOException

object MyApp extends App {

  def run(args: List[String]): IO[Void, ExitStatus] =
    myAppLogic.attempt.map(_.fold(_ => 1, _ => 0)).map(ExitStatus.ExitNow(_))

  def myAppLogic: IO[IOException, Unit] =
    for {
      _ <- putStrLn("Hello! What is your name?")
      n <- getStrLn
      _ <- putStrLn("Hello, " + n + ", good to meet you!")
    } yield ()
}
