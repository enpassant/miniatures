package shape

import org.scalatest._

class ShapeSpec extends FunSpec with Matchers {
  import Shape._

  describe("Draw") {
    it("circle") {
      draw(Circle(10), Point(3, 7)) shouldBe
        "Drawing circle at (3, 7) Radius: 10"
    }

    it("rectangle") {
      draw(Rectangle(10,20), Point(3, 7)) shouldBe
        "Drawing rectangle at (3, 7) Width 10, Height 20"
    }

    it("ellipse") {
      draw(Ellipse(10,20), Point(3, 7)) shouldBe
        "Drawing ellipse at (3, 7) Width 10, Height 20"
    }

    it("rounded rectangle") {
      draw(Rectangle(10,20,15), Point(3, 7)) shouldBe
        "Drawing rounded rectangle at (3, 7) Width 10, Height 20, Smoothing 15"
    }

    it("rounded rectangle with smoothing 100% (aka ellipse)") {
      draw(Rectangle(10,20,100), Point(3, 7)) shouldBe
        "Drawing ellipse at (3, 7) Width 10, Height 20"
    }
  }

  describe("Side count") {
    it("circle should be 1") {
      sideCount(Circle(10)) shouldBe 1
    }

    it("rectangle should be 4") {
      sideCount(Rectangle(10,20)) shouldBe 4
    }

    it("ellipse should be 1") {
      sideCount(Ellipse(10,20)) shouldBe 1
    }

    it("rounded rectangle should be 1") {
      sideCount(Rectangle(10,20,15)) shouldBe 1
    }

    it("rounded rectangle with smoothing 100% (aka ellipse) should be 1") {
      sideCount(Rectangle(10,20,100)) shouldBe 1
    }
  }
}
