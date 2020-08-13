package nomock.app

import scala.util.{Either, Left, Right}

object UserCreation {
  import nomock.user.API._
  import nomock.user.Check._
  import nomock.user.Failure._

  def registerUser(s: UserService)(newUser: NewUser): Either[Error, SavedUser] = {
    return for {
      // Business validation
      login <- checkUserLogin(newUser.username, newUser.password)
      // Validate there is no such username
      checkedUserName <- s.users.checkUsername(login.username)
      checkedLogin = CheckedLogin.of(checkedUserName, login)
      // Call an external (government?) service to verify the ID number
      verifiedID <- s.iDService.verifyID(newUser.iDNumber)
      verifiedUser = VerifiedUser(checkedLogin, verifiedID)
      // Save the user in the persistent store
      savedUser <- s.users.saveUser(verifiedUser)
    } yield savedUser
  }
}
