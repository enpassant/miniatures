package encapsulation.unit

class Distance private[unit](private[unit] val meter: Double)

object DistanceUnit {
  def createDistance(meter: Double): Distance = new Distance(meter)

  def createKm(km: Double): Distance = new Distance(km * 1000)

  def asMeter(distance: Distance): Double = distance.meter
  def asKm(distance: Distance): Double = distance.meter / 1000
}
