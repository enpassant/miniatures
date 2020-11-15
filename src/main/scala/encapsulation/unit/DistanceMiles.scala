package encapsulation.unit

import DistanceUnit._

object DistanceMiles {
  def createMile(mile: Double): Distance = new Distance(mile * 1609.344)

  def asMile(distance: Distance): Double = distance.meter * 0.00062137119224
}
