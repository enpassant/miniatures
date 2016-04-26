package shape

import org.scalatest._

class ShapeSimpleSpec extends FunSpec with Matchers {
  import ShapeSimple._

  describe("Draw") {
    it("circle") {
      draw(Point(3,7), createCircle(10)) shouldBe
        "Drawing circle at (3, 7) Radius: 10"
    }

    it("rectangle") {
      draw(Point(3,7), createRectangle(10, 20)) shouldBe
        "Drawing rectangle at (3, 7) Width 10, Height 20"
    }

    it("ellipse") {
      draw(Point(3,7), createEllipse(10, 20)) shouldBe
        "Drawing ellipse at (3, 7) Width 10, Height 20"
    }

    it("rounded rectangle") {
      draw(Point(3,7), createRoundedRectangle(10, 20, 15)) shouldBe
        "Drawing rounded rectangle at (3, 7) Width 10, Height 20, Smoothing 15"
    }

    it("rounded rectangle with smoothing 100% (aka ellipse)") {
      draw(Point(3,7), createRoundedRectangle(10, 20, 100)) shouldBe
        "Drawing ellipse at (3, 7) Width 10, Height 20"
    }
  }

  describe("Side count") {
    it("circle should be 1") {
      sideCount(createCircle(10)) shouldBe 1
    }

    it("rectangle should be 4") {
      sideCount(createRectangle(10, 20)) shouldBe 4
    }

    it("ellipse should be 1") {
      sideCount(createEllipse(10, 20)) shouldBe 1
    }

    it("rounded rectangle should be 1") {
      sideCount(createRoundedRectangle(10, 20, 15)) shouldBe 1
    }

    it("rounded rectangle with smoothing 100% (aka ellipse) should be 1") {
      sideCount(createRoundedRectangle(10, 20, 100)) shouldBe 1
    }
  }
}
