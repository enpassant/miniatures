package licit

case class Palyazat(parcellaKezdet: Int, parcellaVeg: Int, ar: Int)
case class Elozmeny(elozmenyPalyazatok: List[Palyazat], osszAr: Int)

object Licit {
  def szamitMegoldas(palyazatok: List[Palyazat]): List[Palyazat] = {
    val rendezettPalyazatok = palyazatok.sortBy(_.parcellaVeg)
    val elozmenyek = szamitElozmenyek(rendezettPalyazatok)
    osszeallitMegoldas(elozmenyek)
  }

  def szamitElozmenyek(palyazatok: List[Palyazat]): List[Elozmeny] = {
    palyazatok.foldLeft(List.empty[Elozmeny])(keszitElozmeny)
  }

  def keszitElozmeny(osszesElozmeny: List[Elozmeny], palyazat: Palyazat): List[Elozmeny] = {
    val elozmeny = keresesElozmeny(osszesElozmeny, palyazat)
    val elozmenyPalyazatok = elozmeny map ( e => e.elozmenyPalyazatok) getOrElse Nil
    val osszAr = (elozmeny map (_.osszAr) getOrElse 0) + palyazat.ar
    Elozmeny(palyazat :: elozmenyPalyazatok, osszAr) :: osszesElozmeny
  }

  def keresesElozmeny(osszesElozmeny: List[Elozmeny], palyazat: Palyazat): Option[Elozmeny] = {
    osszesElozmeny.sortBy(-_.osszAr).find { pe =>
      palyazat.parcellaKezdet > pe.elozmenyPalyazatok.head.parcellaVeg }
  }

  def osszeallitMegoldas(osszesElozmeny: List[Elozmeny]): List[Palyazat] = {
    val csokkenoAruElozmenyek = osszesElozmeny.sortBy(-_.osszAr)
    csokkenoAruElozmenyek.head.elozmenyPalyazatok.reverse
  }
}
