package family

import org.scalatest._

class FamilySpec extends FunSpec with Matchers {
  import Family._

  describe("Family. Husband") {
    it("should greet anyone formally") {
      val moduls = List.empty[Modul]
      val greet = composeGreet(moduls)
      greet(Husband, Albert) shouldBe "How do you do!"
    }
    it("should greet his friends") {
      val moduls = List(HusbandModul)
      val greet = composeGreet(moduls)
      greet(Husband, Albert) shouldBe "Hey budd!"
    }
  }
}
