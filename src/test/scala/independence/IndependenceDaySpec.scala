package independence

import org.scalatest._
import org.scalatest.Matchers._

class IndependenceDaySpec extends FunSpec with Matchers {
  import IndependenceDay._
  import Level._
  import ResultOps._

  describe("getUserId") {
    it("should give BadResult if no id") {
      val result = getUserId(None)
      result shouldEqual BadResult(TextError("No id cookie!"))
    }

    it("should give BadResult if id is not a number") {
      val result = getUserId(Option("alma"))
      result shouldEqual BadResult(TextError("Id cookie is not a number"))
    }

    it("should give BadResult if id is negative") {
      val result = getUserId(Option("-5"))
      result shouldEqual BadResult(TextError("Predicate does not hold for -5"))
    }

    it("should give BadResult if id is even") {
      val result = getUserId(Option("8"))
      result should matchPattern {
        case BadResult(TextError("User id must be odd"), _) =>
      }
    }

    it("should give GoodResult if id cookie is positive and odd") {
      val result = getUserId(Option("7"))
      result should matchPattern {
        case GoodResult(21, _) =>
      }
    }
  }

  describe("insertUser") {
    it("should give BadResult if id is negative") {
      val result = insertUser(Option("-7"), "als", "54", "asa")
      result should matchPattern {
        case BadResult(TextError("Predicate does not hold for -7"), _) =>
      }
      result.infos should contain allOf (
        Validation('Error, "userName", "wrong name format"),
        Validation('Error, "userPhone", "wrong phone format"),
        Validation('Warning, "userEmail", "wrong email format"))
    }

    it("should give BadResult if all parameter is empty") {
      val result = insertUser(Option("7"), "", "", "")
      result should matchPattern {
        case BadResult(TextError("Fatal validation"), _) =>
      }
    }

    it("should give BadResult if name is wrong, others empty") {
      val result = insertUser(Option("7"), "Elek", "", "")
      result should matchPattern {
        case BadResult(TextError("Missing user name"), _) =>
      }
    }

    it("should give BadResult if name is good, others empty") {
      val result = insertUser(Option("7"), "Teszt Elek", "", "")
      result should matchPattern {
        case GoodResult(_, _) =>
      }
      result.infos should contain allOf (
        Validation('Error, "userPhone", "required"),
        Validation('Error, "userEmail", "required"),
        DBPersist(DBUser(21, "Teszt Elek")))
    }

    it("should give BadResult if name and phone good, mail wrong") {
      val result = insertUser(Option("7"), "Teszt Elek", "(20)234-5678", "mail")
      result should matchPattern {
        case GoodResult(_, _) =>
      }
      result.infos should contain allOf (
        Validation('Warning, "userEmail", "wrong email format"),
        DBPersist(DBUser(21, "Teszt Elek")),
        DBPersist(DBContact(21, "phone", "(20)234-5678")),
        Sms("(20)234-5678", "User inserted", "Teszt Elek user has inserted"))
    }

    it("should give GoodResult if everything is right") {
      val result = insertUser(
        Option("7"),
        "Teszt Elek",
        "(20)234-5678",
        "teszt@elek.hu")
      result should matchPattern {
        case GoodResult(_, _) =>
      }
      result.infos should contain allOf (
        Log(INFO, "id * 3", ""),
        Log(INFO, "persistUser", ""),
        DBPersist(DBUser(21, "Teszt Elek")),
        DBPersist(DBContact(21, "phone", "(20)234-5678")),
        DBPersist(DBContact(21, "email", "teszt@elek.hu")),
        Email("teszt@elek.hu", "User inserted", "Teszt Elek user has inserted"),
        Sms("(20)234-5678", "User inserted", "Teszt Elek user has inserted"))
    }
  }
}

