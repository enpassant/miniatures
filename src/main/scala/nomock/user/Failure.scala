package nomock.user

import scala.util.{Either, Left, Right}

object Failure {
  trait Error {
    def message: String
  }

  case class BusinessError(message: String) extends Error
  case class InfrastructureError(message: String) extends Error
}
