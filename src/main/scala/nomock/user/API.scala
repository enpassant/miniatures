package nomock.user

import scala.util.{Either, Left, Right}

object API {
  import Failure._

  type VerifiedID = String

  trait IDService {
    def verifyID(id: String): Either[Error, VerifiedID]
  }

  case class NewUser private[user] (
    val username: String,
    val password: String,
    val iDNumber: String
  )

  type CheckedUserName = String

  case class Login private[user] (val username: String, val password: String)

  case class CheckedLogin private[user] (
    val checkedUserName: CheckedUserName,
    val password: String
  )
  object CheckedLogin {
    def of(checkedUserName: CheckedUserName, login: Login) =
      new CheckedLogin(checkedUserName, login.password)
  }

  case class VerifiedUser (
    checkedLogin: CheckedLogin,
    verifiedId: VerifiedID
  )

  case class SavedUser private[user] (
    val verifiedUser: VerifiedUser
  )

  trait UserRepository {
    def checkUsername(username: String): Either[Error, CheckedUserName]
    def saveUser(verifiedUser: VerifiedUser): Either[Error, SavedUser]
  }

  trait UserService {
    def iDService: IDService
    def users: UserRepository
  }
}
