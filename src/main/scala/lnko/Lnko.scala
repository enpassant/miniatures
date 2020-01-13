package lnko

import scala.math.{max, sqrt}

object Lnko extends App {
  def legkisebbOszto(szam: Int): Int =
    (2 to sqrt(szam).toInt)
      .find(i => szam % i == 0)
      .getOrElse(szam)

  def primtenyezo(szam: Int): List[Int] = {
    def loop(szam: Int, list: List[Int]): List[Int] = {
      if (szam <= 1) list
      else {
        val res = legkisebbOszto(szam)
        loop(szam / res, res :: list)
      }
    }
    if (szam == 1) List(1)
    else loop(szam, Nil)
  }

  def lnko_lkkt(szam1: Int, szam2: Int): (Int, Int) = {
    def loop(p1: List[Int], p2: List[Int], lnko: List[Int], lkkt: List[Int]): (Int, Int) = {
      (p1.headOption, p2.headOption) match {
        case (None, None) => (lnko.product, lkkt.product)
        case (Some(a), None) => loop(p1.tail, p2, lnko, a :: lkkt)
        case (None, Some(b)) => loop(p1, p2.tail, lnko, b :: lkkt)
        case (Some(a), Some(b)) if (a > b) => loop(p1.tail, p2, lnko, a :: lkkt)
        case (Some(a), Some(b)) if (a < b) => loop(p1, p2.tail, lnko, b :: lkkt)
        case (Some(a), Some(b)) => loop(p1.tail, p2.tail, a :: lnko, b :: lkkt)
      }
    }
    loop(primtenyezo(szam1), primtenyezo(szam2), Nil, Nil)
  }

  println("Egyszerű példa: legkisebb közös többszörös és legnagyobb közös osztó.\n")

  (1 to 10) foreach { i =>
      println(s"$i prímtényezői: ${primtenyezo(i)}")
  }

  val a = 240
  val b = 180
  println(s"\n$a prímtényezői: ${primtenyezo(a)}")
  println(s"$b primtényezői: ${primtenyezo(b)}\n")

  val kokt = lnko_lkkt(a, b)
  println(s"LNKO($a, $b) = ${kokt._1}")
  println(s"LKKT($a, $b) = ${kokt._2}")
}
