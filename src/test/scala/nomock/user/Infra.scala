package nomock.user

import API._
import Failure._
import scala.util.{Either, Left, Right}

object Infra {
  val verifiedId = "Verified"
  val savingFailure = " SavingFailure"
  val wrongId = InfrastructureError("Wrong id")
  val usernameIsNotValid = InfrastructureError("username is not valid")
  val optimisticLockFailure = InfrastructureError("Optimistic lock failure")

  class TestUserService extends UserService {
    var userTable = Map[CheckedUserName, SavedUser]()

    object TestIDService extends IDService {
      def verifyID(id: String): Either[Error, VerifiedID] =
        if (id.startsWith("Verified")) Right(new VerifiedID(id))
        else Left(wrongId)
    }

    object TestUserRepository extends UserRepository {
      def checkUsername(username: String): Either[Error, CheckedUserName] =
        if (userTable.keySet.contains(username)) {
          Left(usernameIsNotValid)
        } else {
          Right(new CheckedUserName(username))
        }

      def saveUser(verifiedUser: VerifiedUser): Either[Error, SavedUser] =
        if (verifiedUser.verifiedId.endsWith(savingFailure)) {
          Left(optimisticLockFailure)
        } else {
          val savedUser = new SavedUser(verifiedUser)
          userTable = userTable + (verifiedUser.checkedLogin.checkedUserName -> savedUser)
          Right(savedUser)
        }
    }

    val iDService = TestIDService
    val users = TestUserRepository
  }

  def createTestSavedUser(username: String, password: String, id: String) =
    new SavedUser(
      new VerifiedUser(
        new CheckedLogin(
          new CheckedUserName(username),
          password
        ),
        new VerifiedID(id)
      )
    )
}
