import scala.math._

case class Tank(x: Int, y: Int, d: Int = 0) {
  def előre(l: Int) = Tank(
    (x + cos(Pi * d / 180) * l).toInt,
    (y + sin(Pi * d / 180) * l).toInt,
    d)

  def hátra(l: Int) = előre(-l)

  def fordul(dr: Int) = Tank(x, y, (d + dr) % 360)
}

object Tank extends App {
  def balra = 90
  def jobbra = -90

  def kiír(msg: Any) = println(msg)

  val tank = Tank(50, 50) fordul balra előre 10 fordul jobbra hátra 10
  kiír(tank)
}
