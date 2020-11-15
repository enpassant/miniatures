package encapsulation.unit

class Time private[unit](private[unit] val second: Double)

object TimeUnit {
  def createTime(second: Double): Time = new Time(second)

  def asHour(time: Time): Double = time.second / 60
  def asSecond(time: Time): Double = time.second
}
