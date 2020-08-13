package nomock.user

import scala.util.{Either, Left, Right}

object Check {
  import API._
  import Failure._

  val invalidLoginInfo = BusinessError("username or password is empty")

  def checkUserLogin(userName: String, password: String): Either[Error, Login] = {
    if (userName == "" || password == "") {
      Left(invalidLoginInfo)
    } else {
      Right(new Login(userName, password))
    }
  }
}
