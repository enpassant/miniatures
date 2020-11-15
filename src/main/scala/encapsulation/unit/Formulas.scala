package encapsulation.unit

object Formulas {
  def calcSpeed(distance: Distance, time: Time): Speed =
    new Speed(distance.meter / time.second)
  def calcTime(distance: Distance, speed: Speed): Time =
    new Time(distance.meter / speed.mps)
  def calcDistance(speed: Speed, time: Time): Distance =
    new Distance(speed.mps * time.second)
}
