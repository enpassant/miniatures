package tower

import scala.collection.immutable.TreeSet
import scala.collection.parallel.CollectionConverters._
import scala.util.Random

case class Slice(wall: Int = 0, water: Int = 0, air: Int = 0) {
  def leakInto(target: Slice) = {
    if (air != 0) this else {
      val newWater = Math.max(target.wall + target.water - wall, 0)
      copy(air = water - newWater, water = newWater)
    }
  }
}

case class World(towerHeights: Vector[Int]) {
  val worldHeight = towerHeights.max
  val noWall = Vector(Slice(air = worldHeight))
  val slices =
    noWall ++ (towerHeights map { h => Slice(h, worldHeight - h) }) ++ noWall

  @scala.annotation.tailrec
  final def drainLeft(slices: Vector[Slice], index: Int, cont: Boolean)
    : Vector[Slice] =
  {
    if (cont && index < slices.length - 1) {
      val newSlice = slices(index + 1).leakInto(slices(index))
      drainLeft(
        slices.updated(index + 1, newSlice),
        index + 1,
        newSlice.air > 0
      )
    } else slices
  }

  @scala.annotation.tailrec
  final def drainRight(slices: Vector[Slice], index: Int, cont: Boolean)
    : Vector[Slice] =
  {
    if (cont && index >= 0) {
      val newSlice = slices(index).leakInto(slices(index + 1))
      drainRight(
        slices.updated(index, newSlice),
        index - 1,
        newSlice.air > 0
      )
    } else slices
  }

  def drain() = {
    val leftSlices = drainLeft(slices, 0, true)
    drainRight(leftSlices, leftSlices.length - 2, true)
  }
}

case class Plateau(height: Int, width: Int)

case class Glob(
  left: Vector[Plateau],
  top: Plateau,
  right: Vector[Plateau],
  water: Int)

object Glob {
  def buildGlobs(ls: Vector[Int]) = ls.map(
    v => Glob(Vector(), Plateau(v, 1), Vector(), 0)
  )

  def rainfall(ls: Vector[Glob]) = ls.par.reduce(add(_, _)).water

  def threeWaySplit(plateaus: Vector[Plateau], height: Int):
    (Vector[Plateau], Option[Int], Vector[Plateau]) =
  {
    plateaus match {
      case Vector() =>
        (Vector(), None, Vector())
      case Vector(plateau) =>
        if (plateau.height < height) {
          (plateaus, None, Vector())
        } else if (plateau.height > height) {
          (Vector(), None, plateaus)
        } else {
          (Vector(), Some(plateau.width), Vector())
        }
      case _ =>
        val (y, z) = plateaus.splitAt(plateaus.length / 2)
        if (height < z(0).height) {
          val (p, q, r) = threeWaySplit(y, height)
          (p, q, r ++ z)
        } else {
          val (p, q, r) = threeWaySplit(z, height)
          (y ++ p, q, r)
        }

    }
  }

  def add(x: Glob, y: Glob) = {
    if (x.top.height < y.top.height) {
      val (less, equal, greater) = threeWaySplit(y.left, x.top.height)
      Glob(
        (x.left :+
          Plateau(x.top.height,
            x.top.width + width(x.right) + width(less) + equal.getOrElse(0)
          )) ++ greater,
        y.top,
        y.right,
        x.water +
          fill(x.right, x.top.height) +
          fill(less, x.top.height) +
          y.water
      )
    } else if (x.top.height > y.top.height) {
      val (less, equal, greater) = threeWaySplit(x.right, y.top.height)
      Glob(
        x.left,
        x.top,
        (y.right :+
          Plateau(y.top.height,
            equal.getOrElse(0) + width(less) + width(y.left) + y.top.width)
          ) ++ greater,
        x.water +
          fill(less, y.top.height) +
          fill(y.left, y.top.height) +
          y.water
      )
    } else {
      Glob(
        x.left,
        Plateau(
          x.top.height,
          x.top.width + width(x.right) + width(y.left) + y.top.width
        ),
        y.right,
        x.water +
          fill(x.right, x.top.height) +
          fill(y.left, x.top.height) +
          y.water
      )
    }
  }

  def width(plateaus: Vector[Plateau]) = plateaus.foldLeft(0) {
    (s, x) => s + x.width
  }

  def fill(plateaus: Vector[Plateau], height: Int) = plateaus.foldLeft(0) {
    (s, x) => s + x.width * (height - x.height)
  }
}

object Tower extends App {
  def rainfall2(ls: Vector[Int]) = {
    val tm = TreeSet(ls:_*)(Ordering[Int].on(x => x))
    tm.sum
  }

  def zip3[A](fn: (A, A, A) => A)(l1 : Seq[A], l2 : Seq[A],l3 : Seq[A]) : Seq[A] =
    l1.zip(l2).zip(l3).map { case ((a, b), c) => fn(a, b, c) }

  //val towers = Vector(2,6,3,5,2,8,1,4,2,2,5,3,5,7,4,1)
  //val towers = Vector(5,3,7,2,6,4,5,9,1,2)
  val towers = Stream.fill(10000000)(Random.nextInt(200)).toVector
  //val globs = Glob.buildGlobs(towers)
  val start = System.nanoTime
  def maxl = (towers: Seq[Int]) => towers.tail.scanLeft(towers.head)(Math.max)
  def maxr = (towers: Seq[Int]) => towers.init.scanRight(towers.last)(Math.max)
  //def mins = (maxl: Seq[Int], maxr: Seq[Int]) => (maxl, maxr).zipped.map(Math.min)
  //val diffs = (mins: Seq[Int], towers: Seq[Int]) => (mins, towers).zipped.map(_ - _)
  //val rainfall = diffs(mins(maxl(towers), maxr(towers)), towers).sum

  //val rainfall = zip3((l: Int, r: Int, x: Int) => l.min(r) - x)(maxl(towers), maxr(towers), towers).sum
  val world = World(towers)
  val slices = world.drain()
  val rainfall = slices.foldLeft(0)((s, t) => s + t.water)
  //println(Glob.rainfall(globs))
  val end = System.nanoTime
  println(rainfall)
  //println(rainfall2(towers))
  println(s"${(end - start)/1000000}")
}
