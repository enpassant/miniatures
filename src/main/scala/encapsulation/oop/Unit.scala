package encapsulation.oop

class Unit private[oop](
  private[oop] val value: Double,
  private[oop] val unit: String
) {
  override def toString(): String = s"$value $unit"
}
