package encapsulation.unit

class Speed private[unit](private[unit] val mps: Double)

object SpeedUnit {
  def createSpeed(mps: Double): Speed = new Speed(mps)

  def createKph(kph: Double): Speed = new Speed(kph / 3.6)

  def asMps(speed: Speed): Double = speed.mps
  def asKph(speed: Speed): Double = speed.mps * 3.6
}
