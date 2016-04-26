package licit

import org.scalatest._
import scala.util.Random

class LicitSpec extends FunSpec with Matchers {
  describe("Értékelés:") {
    it("Mindenki ugyanarra az 1 parcellára pályázik") {
      val palyazatok = List(
        Palyazat(1, 1, 1000),
        Palyazat(1, 1, 5000),
        Palyazat(1, 1, 6000),
        Palyazat(1, 1, 3000)
      )
      val elvartMegoldas = List(palyazatok(2))
      val szamoltMegoldas = Licit.szamitMegoldas(palyazatok)
      szamoltMegoldas shouldBe elvartMegoldas
    }

    it("Mindenki 1 parcellára pályázik, de különbözőre") {
      val palyazatok = List(
        Palyazat(5, 5, 10000),
        Palyazat(2, 2, 5000),
        Palyazat(1, 1, 5000),
        Palyazat(4, 4, 6000),
        Palyazat(3, 3, 5000)
      )
      val rendezettPalyazatok = palyazatok.sortBy(_.parcellaVeg)
      val elvartMegoldas = rendezettPalyazatok
      val szamoltMegoldas = Licit.szamitMegoldas(palyazatok)
      szamoltMegoldas shouldBe elvartMegoldas
    }

    it("Többre is pályáznak, de nincs átfedés") {
      val palyazatok = List(
        Palyazat(5, 6, 10000),
        Palyazat(1, 2, 5000),
        Palyazat(3, 4, 6000),
        Palyazat(7, 8, 5000)
      )
      val rendezettPalyazatok = palyazatok.sortBy(_.parcellaVeg)
      val elvartMegoldas = rendezettPalyazatok
      val szamoltMegoldas = Licit.szamitMegoldas(palyazatok)
      szamoltMegoldas shouldBe elvartMegoldas
    }

    it("Egyetlen pályázó nyer, de marad eladatlan LICIT (pályáztak rá)") {
      val palyazatok = List(
        Palyazat(1, 5, 10000),
        Palyazat(2, 3, 3000),
        Palyazat(4, 5, 5000),
        Palyazat(4, 4, 4000)
      )
      val elvartMegoldas = List(palyazatok(0))
      val szamoltMegoldas = Licit.szamitMegoldas(palyazatok)
      szamoltMegoldas shouldBe elvartMegoldas
    }

    it("Többen nyernek") {
      val palyazatok = List(
        Palyazat(1, 5, 11000),
        Palyazat(2, 3, 5000),
        Palyazat(4, 5, 5000),
        Palyazat(4, 4, 6000)
      )
      val elvartMegoldas1 = List(palyazatok(0))
      val elvartMegoldas2 = List(
        palyazatok(1),
        palyazatok(3)
      )
      val szamoltMegoldas = Licit.szamitMegoldas(palyazatok)
      szamoltMegoldas should (be(elvartMegoldas1) or be(elvartMegoldas2))
    }

    it("Többen nyernek, a mindenre pályázó nem nyer") {
      val palyazatok = List(
        Palyazat(1, 5, 10000),
        Palyazat(2, 3, 5000),
        Palyazat(3, 5, 11000),
        Palyazat(4, 4, 6000)
      )
      val elvartMegoldas1 = List(palyazatok(2))
      val elvartMegoldas2 = List(
        palyazatok(1),
        palyazatok(3)
      )
      val szamoltMegoldas = Licit.szamitMegoldas(palyazatok)
      szamoltMegoldas should (be(elvartMegoldas1) or be(elvartMegoldas2))
    }

    it("Véletlen nagy teszt") {
      val PALYAZATOK_SZAMA = 100
      val MAX_PARCELLAK_SZAMA = 100
      val LEGHOSSZABB_INTERVALLUM = 5

      def keszitNyertesPalyazat(parcellaKezdet: Int) = {
        val parcellaVeg = parcellaKezdet + LEGHOSSZABB_INTERVALLUM - 1
        val ar = (Random.nextInt(100) + 1) * 100000
        Palyazat(parcellaKezdet, parcellaVeg, ar)
      }

      def keszitVesztesPalyazat(parcellaKezdet: Int) = {
        val intervallumHossz = Random.nextInt(LEGHOSSZABB_INTERVALLUM)
        val parcellaVeg = scala.math.min(parcellaKezdet + intervallumHossz, MAX_PARCELLAK_SZAMA)
        val ar = Random.nextInt(100) + 1000
        Palyazat(parcellaKezdet, parcellaVeg, ar)
      }

      def keszitVeletlenPalyazat(index: Int) = {
        val nyertes = (index % LEGHOSSZABB_INTERVALLUM) == 0
        if (nyertes) {
          keszitNyertesPalyazat(index)
        } else {
          keszitVesztesPalyazat(index)
        }
      }

      val palyazatok = ((0 until PALYAZATOK_SZAMA) map keszitVeletlenPalyazat).toList
      val nyertesPalyazatok = palyazatok.filter(_.ar >= 100000)
      val elvartMegoldas = nyertesPalyazatok
      val szamoltMegoldas = Licit.szamitMegoldas(palyazatok)
      szamoltMegoldas shouldBe elvartMegoldas
    }

    it("Többen nyernek, nagy teszt") {
      val PALYAZATOK_SZAMA = 100
      val MAX_PARCELLAK_SZAMA = 100
      val LEGHOSSZABB_INTERVALLUM = 5

      def keszitNyertesPalyazat(parcellaKezdet: Int) = {
        val parcellaVeg = parcellaKezdet + LEGHOSSZABB_INTERVALLUM - 1
        val ar = (Random.nextInt(100) + 1) * 100000
        Palyazat(parcellaKezdet, parcellaVeg, ar)
      }

      def keszitVesztesPalyazat(parcellaKezdet: Int) = {
        val intervallumHossz = Random.nextInt(LEGHOSSZABB_INTERVALLUM)
        val parcellaVeg = scala.math.min(parcellaKezdet + intervallumHossz, MAX_PARCELLAK_SZAMA)
        val ar = Random.nextInt(100) + 1000
        Palyazat(parcellaKezdet, parcellaVeg, ar)
      }

      def keszitVeletlenPalyazat(index: Int) = {
        val nyertes = (index % LEGHOSSZABB_INTERVALLUM) == 0
        if (nyertes) {
          keszitNyertesPalyazat(index)
        } else {
          keszitVesztesPalyazat(index)
        }
      }

      val palyazatok = ((0 until PALYAZATOK_SZAMA) map keszitVeletlenPalyazat).toList
      val nyertesPalyazatok = palyazatok.filter(_.ar >= 100000)
      val nyertesAr = (nyertesPalyazatok map (_.ar)).sum
      val masikNyertesPalyazat = Palyazat(1, MAX_PARCELLAK_SZAMA, nyertesAr)
      val elvartMegoldas1 = nyertesPalyazatok
      val elvartMegoldas2 = List(masikNyertesPalyazat)
      val szamoltMegoldas = Licit.szamitMegoldas(palyazatok)
      szamoltMegoldas should (be(elvartMegoldas1) or be(elvartMegoldas2))
    }
  }

  describe("szamitElozmenyek") {
    it("Egyetlen pályázó nyer, de marad eladatlan LICIT (pályáztak rá)") {
      val palyazatok = List(
        Palyazat(1, 1, 1000),
        Palyazat(1, 3, 5000),
        Palyazat(2, 3, 5000),
        Palyazat(4, 4, 6000),
        Palyazat(4, 5, 11000),
        Palyazat(1, 5, 10000)
      )
      val elozmeny0 = Elozmeny(palyazatok(0) :: Nil, 1000)
      val elozmeny1 = Elozmeny(palyazatok(1) :: Nil, 5000)
      val elozmeny2 = Elozmeny(palyazatok(2) :: elozmeny0.elozmenyPalyazatok, 6000)
      val elozmeny3 = Elozmeny(palyazatok(3) :: elozmeny2.elozmenyPalyazatok, 12000)
      val elozmeny4 = Elozmeny(palyazatok(4) :: elozmeny2.elozmenyPalyazatok, 17000)
      val elozmeny5 = Elozmeny(palyazatok(5) :: Nil, 10000)
      val elvartElozmenyek = List(
        elozmeny0,
        elozmeny1,
        elozmeny2,
        elozmeny3,
        elozmeny4,
        elozmeny5
      )
      val elozmenyek = Licit.szamitElozmenyek(palyazatok)
      elozmenyek.reverse shouldBe elvartElozmenyek
    }

    it("Többre is pályáznak, de nincs átfedés") {
      val palyazatok = List(
        Palyazat(1, 2, 5000),
        Palyazat(3, 4, 6000),
        Palyazat(5, 6, 10000),
        Palyazat(7, 8, 5000)
      )
      val elozmeny0 = Elozmeny(palyazatok(0) :: Nil, 5000)
      val elozmeny1 = Elozmeny(palyazatok(1) :: elozmeny0.elozmenyPalyazatok, 11000)
      val elozmeny2 = Elozmeny(palyazatok(2) :: elozmeny1.elozmenyPalyazatok, 21000)
      val elozmeny3 = Elozmeny(palyazatok(3) :: elozmeny2.elozmenyPalyazatok, 26000)
      val elvartElozmenyek = List(
        elozmeny0,
        elozmeny1,
        elozmeny2,
        elozmeny3
      )
      val elozmenyek = Licit.szamitElozmenyek(palyazatok)
      elozmenyek.reverse shouldBe elvartElozmenyek
    }
  }

  describe("keszitElozmeny") {
    describe("Egyetlen pályázó nyer, de marad eladatlan LICIT (pályáztak rá)") {
      val palyazatok = List(
        Palyazat(1, 1, 1000),
        Palyazat(1, 3, 5000),
        Palyazat(2, 3, 5000),
        Palyazat(4, 4, 6000),
        Palyazat(4, 5, 11000),
        Palyazat(1, 5, 10000)
      )
      val elozmeny0 = Elozmeny(palyazatok(0) :: Nil, 1000)
      val elozmeny1 = Elozmeny(palyazatok(1) :: Nil, 5000)
      val elozmeny2 = Elozmeny(palyazatok(2) :: elozmeny0.elozmenyPalyazatok, 6000)
      val elozmeny3 = Elozmeny(palyazatok(3) :: elozmeny2.elozmenyPalyazatok, 12000)
      val elozmeny4 = Elozmeny(palyazatok(4) :: elozmeny2.elozmenyPalyazatok, 17000)
      val elozmeny5 = Elozmeny(palyazatok(5) :: Nil, 10000)
      val osszesElozmeny = List(
        elozmeny0,
        elozmeny1,
        elozmeny2,
        elozmeny3,
        elozmeny4,
        elozmeny5
      )

      it("Üres előzménnnyel") {
        val elozmenyek = List.empty[Elozmeny]
        val elvartElozmenyek = osszesElozmeny.take(1).reverse
        val keszitettElozmeny = Licit.keszitElozmeny(elozmenyek, palyazatok(0))
        keszitettElozmeny shouldBe elvartElozmenyek
      }

      it("Egy előzménnnyel") {
        val elozmenyek = osszesElozmeny.take(1).reverse
        val elvartElozmenyek = osszesElozmeny.take(2).reverse
        val keszitettElozmeny = Licit.keszitElozmeny(elozmenyek, palyazatok(1))
        keszitettElozmeny shouldBe elvartElozmenyek
      }

      it("Kettő előzménnnyel") {
        val elozmenyek = osszesElozmeny.take(2).reverse
        val elvartElozmenyek = osszesElozmeny.take(3).reverse
        val keszitettElozmeny = Licit.keszitElozmeny(elozmenyek, palyazatok(2))
        keszitettElozmeny shouldBe elvartElozmenyek
      }

      it("Három előzménnnyel") {
        val elozmenyek = osszesElozmeny.take(3).reverse
        val elvartElozmenyek = osszesElozmeny.take(4).reverse
        val keszitettElozmeny = Licit.keszitElozmeny(elozmenyek, palyazatok(3))
        keszitettElozmeny shouldBe elvartElozmenyek
      }

      it("Négy előzménnnyel") {
        val elozmenyek = osszesElozmeny.take(4).reverse
        val elvartElozmenyek = osszesElozmeny.take(5).reverse
        val keszitettElozmeny = Licit.keszitElozmeny(elozmenyek, palyazatok(4))
        keszitettElozmeny shouldBe elvartElozmenyek
      }

      it("Öt előzménnnyel") {
        val elozmenyek = osszesElozmeny.take(5).reverse
        val elvartElozmenyek = osszesElozmeny.take(6).reverse
        val keszitettElozmeny = Licit.keszitElozmeny(elozmenyek, palyazatok(5))
        keszitettElozmeny shouldBe elvartElozmenyek
      }
    }
  }

  describe("keresesElozmeny") {
    describe("Többre is pályáznak, átfedéssel") {
      val palyazatok = List(
        Palyazat(1, 1, 2000),
        Palyazat(1, 3, 5000),
        Palyazat(2, 3, 6000),
        Palyazat(3, 3, 1000),
        Palyazat(4, 4, 6000),
        Palyazat(1, 5, 10000)
      )
      val elozmeny0 = Elozmeny(palyazatok(0) :: Nil, 2000)
      val elozmeny1 = Elozmeny(palyazatok(1) :: Nil, 5000)
      val elozmeny2 = Elozmeny(palyazatok(2) :: elozmeny0.elozmenyPalyazatok, 8000)
      val elozmeny3 = Elozmeny(palyazatok(3) :: elozmeny0.elozmenyPalyazatok, 3000)
      val elozmeny4 = Elozmeny(palyazatok(4) :: elozmeny2.elozmenyPalyazatok, 12000)
      val elozmeny5 = Elozmeny(palyazatok(5) :: Nil, 10000)
      val osszesElozmeny = List(
        elozmeny0,
        elozmeny1,
        elozmeny2,
        elozmeny3,
        elozmeny4,
        elozmeny5
      )

      it("Üres listában keresés") {
        val elozmenyek = List.empty[Elozmeny]
        val elvartElozmeny = None
        val elozmeny = Licit.keresesElozmeny(elozmenyek, palyazatok(0))
        elozmeny shouldBe elvartElozmeny
      }

      it("Olyan keresése, aminek nincs előzménye") {
        val elozmenyek = osszesElozmeny.take(1)
        val elvartElozmeny = None
        val elozmeny = Licit.keresesElozmeny(elozmenyek, palyazatok(0))
        elozmeny shouldBe elvartElozmeny
      }

      it("Olyan keresése, aminek nincs előzménye, hosszú intervallum") {
        val elozmenyek = osszesElozmeny.take(5)
        val elvartElozmeny = None
        val elozmeny = Licit.keresesElozmeny(elozmenyek, palyazatok(5))
        elozmeny shouldBe elvartElozmeny
      }

      it("Olyan keresése, aminek egy előzménye van") {
        val elozmenyek = osszesElozmeny.take(2)
        val elvartElozmeny = Some(elozmeny0)
        val elozmeny = Licit.keresesElozmeny(elozmenyek, palyazatok(2))
        elozmeny shouldBe elvartElozmeny
      }

      it("Olyan keresése, aminek több előzménye van, előrébb van az értékesebb.") {
        val elozmenyek = osszesElozmeny.take(3)
        val elvartElozmeny = Some(elozmeny0)
        val elozmeny = Licit.keresesElozmeny(elozmenyek, palyazatok(3))
        elozmeny shouldBe elvartElozmeny
      }

      it("Olyan keresése, aminek több előzménye van, hátrébb van az értékesebb.") {
        val elozmenyek = osszesElozmeny.take(4)
        val elvartElozmeny = Some(elozmeny2)
        val elozmeny = Licit.keresesElozmeny(elozmenyek, palyazatok(4))
        elozmeny shouldBe elvartElozmeny
      }
    }
  }

  describe("osszeallitMegoldas") {
    it("Többre is pályáznak, átfedéssel") {
      val palyazatok = List(
        Palyazat(1, 1, 1000),
        Palyazat(1, 3, 5000),
        Palyazat(2, 3, 5000),
        Palyazat(4, 4, 6000),
        Palyazat(4, 5, 11000),
        Palyazat(1, 5, 10000)
      )
      val elozmenyek = Licit.szamitElozmenyek(palyazatok)
      val elvartMegoldas = List(
        palyazatok(0),
        palyazatok(2),
        palyazatok(4)
      )
      val megoldas = Licit.osszeallitMegoldas(elozmenyek)
      megoldas shouldBe elvartMegoldas
    }

    it("Egyetlen pályázó nyer, de marad eladatlan LICIT (pályáztak rá)") {
      val palyazatok = List(
        Palyazat(1, 5, 10000),
        Palyazat(2, 3, 3000),
        Palyazat(4, 5, 5000),
        Palyazat(4, 4, 4000)
      )
      val elozmenyek = Licit.szamitElozmenyek(palyazatok)
      val elvartMegoldas = List(palyazatok(0))
      val megoldas = Licit.osszeallitMegoldas(elozmenyek)
      megoldas shouldBe elvartMegoldas
    }
  }
}
