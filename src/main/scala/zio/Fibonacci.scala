package zio

import scalaz.zio.{App, IO, Void}
import scalaz.zio.console._

import java.io.IOException

object Fibonacci extends App {

  sealed trait AppError

  case object NoneException extends AppError
  case class AppException(exception: Exception) extends AppError

  def run(args: List[String]): IO[Void, ExitStatus] =
    myAppLogic.attempt.map(_.fold(_ => 1, _ => 0)).map(ExitStatus.ExitNow(_))

  def myAppLogic: IO[IOException, Unit] =
    for {
      _ <- putStrLn("Hello! Which fibonacci value should calculate?")
      n <- getStrLn
      index <- IO.syncException(n.toInt).leftMap(_ => new IOException())
      value <- fib(index).leftMap(_ => new IOException())
      _ <- putStrLn(s"$index. fibonacci value is $value")
    } yield ()

  def fib(n: Int): IO[Void, Int] =
    if (n <= 1) IO.point(1)
    else for {
      fiber1 <- fib(n - 2).fork
      fiber2 <- fib(n - 1).fork
      v2     <- fiber2.join
      v1     <- fiber1.join
    } yield v1 + v2
}
