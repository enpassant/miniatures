package zio

import scalaz.zio.{App, IO}
import scalaz.zio.console._

import java.io.IOException

object Fibonacci extends App {

  sealed trait AppError

  case object NoneException extends AppError
  case class AppException(exception: Exception) extends AppError

  def run(args: List[String]): IO[Nothing, ExitStatus] =
    myAppLogic.attempt.map(_.fold(_ => 1, _ => 0)).map(ExitStatus.ExitNow(_))

  def myAppLogic: IO[AppError, Unit] =
    for {
      _ <- putStrLn("Hello! Which fibonacci value should calculate?")
        .leftMap[AppError](AppException(_))
      n <- getStrLn
        .leftMap[AppError](AppException(_))
      index <- IO.syncException(n.toInt)
        .leftMap[AppError](AppException(_))
      value <- fib(index)
        .leftMap[AppError](_ => NoneException)
      _ <- putStrLn(s"$index. fibonacci value is $value")
        .leftMap[AppError](AppException(_))
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
