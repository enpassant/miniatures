import scala.math._

case class Tank(x: Int, y: Int, d: Int = 0) {
  def előre(l: Int) = Tank(
    (x + cos(Pi * d / 180) * l).toInt,
    (y + sin(Pi * d / 180) * l).toInt,
    d)

  def hátra(l: Int) = előre(-l)

  def fordul(dr: Int) = Tank(x, y, (d + dr) % 360)
}

object Tank {
  def balra = 90
  def jobbra = -90

  val előre = (l: Int) => (tank: Tank) => tank.előre(l)
  val hátra = (l: Int) => (tank: Tank) => tank.előre(-l)
  val fordul = (dr: Int) => (tank: Tank) => tank.fordul(dr)

  def kiír(msg: Any) = println(msg)
}

object TankApp extends App {
  import Tank._

  val tank = Tank(50, 50)
  val tank2 = tank fordul balra előre 10 fordul jobbra hátra 10
  kiír(tank2)

  val mozgás = fordul(balra) andThen előre(10) andThen fordul(jobbra) andThen hátra(10)
  val tank3 = mozgás(tank)
  kiír(tank3)
}
