package shape

import org.scalatest._

class ShapeOOSpec extends FunSpec with Matchers {
  import ShapeOO._

  describe("Draw") {
    it("circle") {
      Circle(10) draw Point(3, 7) shouldBe
        "Drawing circle at (3, 7) Radius: 10"
    }

    it("rectangle") {
      Rectangle(10,20) draw Point(3, 7) shouldBe
        "Drawing rectangle at (3, 7) Width 10, Height 20"
    }

    it("ellipse") {
      Ellipse(10,20) draw Point(3, 7) shouldBe
        "Drawing ellipse at (3, 7) Width 10, Height 20"
    }

    it("rounded rectangle") {
      Rectangle(10,20,15) draw Point(3, 7) shouldBe
        "Drawing rounded rectangle at (3, 7) Width 10, Height 20, Smoothing 15"
    }

    it("rounded rectangle with smoothing 100% (aka ellipse)") {
      Rectangle(10,20,100) draw Point(3, 7) shouldBe
        "Drawing ellipse at (3, 7) Width 10, Height 20"
    }
  }

  describe("Side count") {
    it("circle should be 1") {
      Circle(10).sideCount shouldBe 1
    }

    it("rectangle should be 4") {
      Rectangle(10,20).sideCount shouldBe 4
    }

    it("ellipse should be 1") {
      Ellipse(10,20).sideCount shouldBe 1
    }

    it("rounded rectangle should be 1") {
      Rectangle(10,20,15).sideCount shouldBe 1
    }

    it("rounded rectangle with smoothing 100% (aka ellipse) should be 1") {
      Rectangle(10,20,100).sideCount shouldBe 1
    }
  }
}
