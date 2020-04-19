package shape

import org.scalatest._

class ShapePFSpec extends FunSpec with Matchers {
  import ShapePF._

  describe("Draw") {
    it("circle") {
      draw(Circle(Point(3, 7), 10)) shouldBe
        "Drawing circle at (3, 7) Radius: 10"
    }

    it("rectangle") {
      draw(Rectangle(Point(3, 7), 10,20)) shouldBe
        "Drawing rectangle at (3, 7) Width 10, Height 20"
    }

    it("ellipse") {
      draw(Ellipse(Point(3, 7), 10,20)) shouldBe
        "Drawing ellipse at (3, 7) Width 10, Height 20"
    }

    it("rounded rectangle") {
      drawWithRoundRectangle(Rectangle(Point(3, 7), 10,20,15)) shouldBe
        "Drawing rounded rectangle at (3, 7) Width 10, Height 20, Smoothing 15"
    }

    it("rounded rectangle with smoothing 100% (aka ellipse)") {
      drawWithRoundRectangle(Rectangle(Point(3, 7), 10,20,100)) shouldBe
        "Drawing ellipse at (3, 7) Width 10, Height 20"
    }
  }

  describe("Side count") {
    it("circle should be 1") {
      sideCount(Circle(Point(3, 7), 10)) shouldBe 1
    }

    it("rectangle should be 4") {
      sideCount(Rectangle(Point(3, 7), 10,20)) shouldBe 4
    }

    it("ellipse should be 1") {
      sideCount(Ellipse(Point(3, 7), 10,20)) shouldBe 1
    }

    it("rounded rectangle should be 1") {
      sideCount(Rectangle(Point(3, 7), 10,20,15)) shouldBe 1
    }

    it("rounded rectangle with smoothing 100% (aka ellipse) should be 1") {
      sideCount(Rectangle(Point(3, 7), 10,20,100)) shouldBe 1
    }
  }
}
