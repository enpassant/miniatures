package nomock.app

import org.scalatest._

class UserCreationSpec extends FunSpec with Matchers {
  import  nomock.user.API._
  import  nomock.user.Infra._
  import  nomock.user.Check._

  val expectedUserName = "Teszt Elek"
  val expectedPassword = "t1tk0s"
  val expectedIdGood = verifiedId + "1"
  val expectedIdBad = "1"
  val expectedSavedUser = createTestSavedUser(
    expectedUserName,
    expectedPassword,
    expectedIdGood
  )

  describe("UserCreation.registerUser") {
    describe("when everything is ok") {
      it("should be savedUser response") {
        val userService = new TestUserService()
        val newUser = NewUser(expectedUserName, expectedPassword, expectedIdGood)
        val savedUserEither = UserCreation.registerUser(userService)(newUser)
        savedUserEither shouldBe Right(expectedSavedUser)
      }
    }
    describe("when login information is wrong") {
      it("username is empty") {
        val userService = new TestUserService()
        val newUser = NewUser("", expectedPassword, expectedIdGood)
        val savedUserEither = UserCreation.registerUser(userService)(newUser)
        savedUserEither shouldBe Left(invalidLoginInfo)
      }
      it("password is empty") {
        val userService = new TestUserService()
        val newUser = NewUser(expectedUserName, "", expectedIdGood)
        val savedUserEither = UserCreation.registerUser(userService)(newUser)
        savedUserEither shouldBe Left(invalidLoginInfo)
      }
    }
    describe("when fail the saving") {
      it("should be optimistic lock failure") {
        val userService = new TestUserService()
        val newUser = NewUser(
          expectedUserName,
          expectedPassword,
          expectedIdGood + savingFailure
       )
        val savedUserEither = UserCreation.registerUser(userService)(newUser)
        savedUserEither shouldBe Left(optimisticLockFailure)
      }
    }
    describe("when username is exists") {
      it("should be username is not valid failure") {
        val userService = new TestUserService()
        val newUser = NewUser(
          expectedUserName,
          expectedPassword,
          expectedIdGood
       )
        val savedUserEither = UserCreation.registerUser(userService)(newUser)
        savedUserEither shouldBe Right(expectedSavedUser)
        val secondSavedUserEither = UserCreation.registerUser(userService)(newUser)
        secondSavedUserEither shouldBe Left(usernameIsNotValid)
      }
    }
    describe("when id is bad") {
      it("should be wrong id message") {
        val userService = new TestUserService()
        val newUser = NewUser(expectedUserName, expectedPassword, expectedIdBad)
        val savedUserEither = UserCreation.registerUser(userService)(newUser)
        savedUserEither shouldBe Left(wrongId)
      }
    }
  }
}
