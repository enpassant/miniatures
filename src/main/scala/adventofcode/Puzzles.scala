package adventofcode

object Puzzles extends App {
  def vigenere(key: String, text: String) = {
    val count = 1 + 'Z' - 'A'
    val offsets = key.toUpperCase.map(ch => ch - 'A')
    val len = offsets.length
    text.toUpperCase.zipWithIndex.map { case (ch, i) =>
      ('A' + ((count + (ch - 'A') - offsets(i % len)) % count)).toChar
    }.mkString
  }

  //println(vigenere("EzAKulcsszo", "MenekuljetekMertJonAzEllenseg"))

  val eqCharToInt = (a: Char, b: Char) =>
    if (a == b) Integer.parseInt(a.toString) else 0

  def p1_1(input: String) =
    input.zip(input.tail :+ input.last).map(eqCharToInt.tupled).sum

  def p1_2(input: String) = {
    val half = input.length / 2
    input.zip(input.substring(half) + input.substring(0, half))
      .map(eqCharToInt.tupled).sum
  }

  def parseNumbers(input: String) = input.split("\n").map(
    _.trim.split("\\s").map(Integer.parseInt(_)))

  def p2_1(input: Array[Array[Int]]) = {
    input.map { ls => ls.max -ls.min }
  }.sum


  def p2_2(input: Array[Array[Int]]) = {
    (input.map { ls =>
      (for (x <- ls; y <- ls) yield if (x != y && x % y == 0) x / y else 0).sum
    }).sum
  }

  def fromPos(pos: Int) = {
    val n = Stream.from(1, 2).find(n => n * n >= pos).get
    val x = (n - 1) / 2
    val minNum = (n - 2) * (n - 2) + 1
    val sidePos = (pos - minNum) % (n - 1)
    val center = x - 1
    val y = sidePos - center
    (pos - minNum) / (n - 1) match {
      case 0 => (x, y)
      case 1 => (-y, x)
      case 2 => (-x, -y)
      case 3 => (y, -x)
    }
  }

  def toPos(x: Int, y: Int) = {
    val absX = Math.abs(x)
    val absY = Math.abs(y)
    val n = 2 * Math.max(absX, absY) + 1
    val min = (n - 2) * (n - 2) + 1
    val center = (n - 1) / 2 - 1
    if (absX > absY) {
      if (x >= 0) {
        min + (center + y)
      } else {
        min + 2 * (n - 1) + (center - y)
      }
    } else if (y >= 0) {
      min + n - 1 + (center - x)
    } else {
      min + 3 * (n - 1) + (center + x)
    }
  }

  def p3_1(input: Int) = {
    val n = Stream.from(1, 2).find(n => n * n >= input).get
    val x = (n - 1) / 2
    val minNum = (n - 2) * (n - 2) + 1
    val sidePos = (input - minNum) % (n - 1)
    val center = x - 1
    val y = Math.abs(sidePos - center)
    x + y
  }

  def spiralSum(pos: Int): Int = {
    if (pos == 1) 1
    else {
      val (x, y) = fromPos(pos)
      val values =
        for (i <- x-1 to x+1; j <- y-1 to y+1) yield {
          val newPos = toPos(i, j)
          if (newPos < pos) spiralSum(newPos)
          else 0
        }
      values.sum
    }
  }

  def spiralSumStream = {
    def loop(pos: Int): Stream[Int] = spiralSum(pos) #:: loop(pos + 1)
    1 #:: loop(2)
  }

  def p3_2(input: Int) = {
    spiralSumStream.find(_ > input)
  }

  def p4_1(input: String) = {
    (input.split("\n").map { line =>
      if ((line.split("\\s").foldLeft((Set.empty[String], false)) {
        case ((set, duplicate), word) =>
          if (duplicate) (set, duplicate)
          else (set + word, set.contains(word))
      })._2) 0 else 1
    }).sum
  }

  def p4_2(input: String) = {
    (input.split("\n").map { line =>
      if ((line.split("\\s").map(word => word.sorted)
          .foldLeft((Set.empty[String], false))
      {
        case ((set, duplicate), word) =>
          if (duplicate) (set, duplicate)
          else (set + word, set.contains(word))
      })._2) 0 else 1
    }).sum
  }

  def pr5_1(input: String) = {
    def loop(ls: Vector[Int], pos: Int, count: Int): Int = {
      if (pos >= ls.length || pos < 0) count
      else {
        val step = ls(pos)
        loop(ls.updated(pos, step + 1), pos + step, count + 1)
      }
    }

    loop(parseNumbers(input).flatten.toVector, 0, 0)
  }

  def pr5_2(input: String) = {
    def loop(ls: Vector[Int], pos: Int, count: Int): Int = {
      if (pos >= ls.length || pos < 0) count
      else {
        val step = ls(pos)
        loop(
          ls.updated(pos, step + (if (step >= 3) -1 else 1)),
          pos + step,
          count + 1
        )
      }
    }

    loop(parseNumbers(input).flatten.toVector, 0, 0)
  }

  def pr6_1(input: String) = {
    val numbers = input.trim.split("\\s").map(Integer.parseInt(_))
    val len = numbers.length

    def calcMax() = {
      numbers.zipWithIndex.foldLeft(-1, -1) {
        case ((m, mi), (v, i)) =>
          if (v > m) (v, i)
          else (m, mi)
      }
    }
    def reallocate() = {
      val (max, index) = calcMax()
      numbers.update(index, 0)
      (1 to max) foreach { i =>
        val pos = (index + i) % len
        val v = numbers(pos)
        numbers.update(pos, v + 1)
      }
    }

    def redistribution(history: Set[List[Int]]): Int = {
      reallocate()
      val ls = numbers.toList
      if (history.contains(ls)) {
        history.size
      }
      else {
        redistribution(history + ls)
      }
    }

    val num1 = redistribution(Set.empty[List[Int]]) + 1
    val ls = numbers.toList
    val num2 = redistribution(Set(ls))

    (num1, num2)
  }

  case class Program(name: String, weight: Int, programs: List[String])
  case class ProgramTotal(name: String, weight: Int, programs: List[ProgramTotal], total: Int)

  def pr7_1(input: String) = {
    def loop(ls: List[Array[String]], map: Map[String, Program]):
      Map[String, Program] = ls match
    {
      case Nil => map
      case xs :: tail =>
        val weight = Integer.parseInt(xs(1).substring(1, xs(1).length - 1))
        val programs = xs.drop(3).map(name =>
          if (name.last == ',') name.dropRight(1)
          else name
        )
        val program = Program(xs(0), weight, programs.toList)
        loop(tail, map + (xs(0) -> program))

    }

    val ls = input.split("\n").map(_.trim.split("\\s").map(_.trim)).toList
    val programs = loop(ls, Map.empty[String, Program])

    val roots = programs.values.toList.filter(p =>
      !programs.values.toList.flatMap(p => p.programs).contains(p.name)
    )
    //roots

    def calcTotal(name: String): ProgramTotal = {
      val program = programs(name)
      val ps: List[ProgramTotal] = program.programs.map(calcTotal(_))
      val total = ProgramTotal(program.name, program.weight, ps,
        program.weight + ps.map(_.total).sum)
      total
    }

    val total = calcTotal(roots(0).name)

    total.programs.map(p => (p.name, p.total)).foreach(println)

    val total2 = calcTotal("uduyfo")

    total2.programs.map(p => (p.name, p.weight, p.total)).foreach(println)
  }

  def pr8_1(input: String) = {
    var registers = Map.empty[String, Int]
    var max = 0

    def getReg(key: String) = registers.getOrElse(key, 0)
    def setReg(key: String, value: Int) = registers = registers + (key -> value)

    val ls = input.split("\n").map(_.trim.split("\\s").map(_.trim)).toList

    ls.foreach { line =>
      val condReg = line(4)
      val condValue = Integer.parseInt(line(6))
      val cond = line(5) match {
        case "<" => getReg(condReg) < condValue
        case ">" => getReg(condReg) > condValue
        case "<=" => getReg(condReg) <= condValue
        case ">=" => getReg(condReg) >= condValue
        case "==" => getReg(condReg) == condValue
        case "!=" => getReg(condReg) != condValue
      }
      if (cond) {
        val reg = line(0)
        val value = Integer.parseInt(line(2))
        val stmt = line(1) match {
          case "inc" => setReg(reg, getReg(reg) + value)
          case "dec" => setReg(reg, getReg(reg) - value)
        }
        if (getReg(reg) > max) max = getReg(reg)
      }
    }
    (registers.values.max, max)
  }

  def pr9_1(input: String) = {
    def loop(s: String, count: Int, depth: Int, garbage: Boolean, canceled: Int): (Int, Int) =
      s.toList match
    {
      case Nil => (count, canceled)
      case '!' :: ch :: tail => loop(tail.mkString, count, depth, garbage, canceled)
      case '>' :: tail => loop(tail.mkString, count, depth, false, canceled)
      case ch :: tail if garbage => loop(tail.mkString, count, depth, true, canceled + 1)
      case '<' :: tail => loop(tail.mkString, count, depth, true, canceled)
      case '}' :: tail => loop(tail.mkString, count, depth - 1, garbage, canceled)
      case '{' :: tail => loop(tail.mkString, count + depth, depth + 1, garbage, canceled)
      case ch :: tail => loop(tail.mkString, count, depth, garbage, canceled)
    }
    loop(input, 0, 1, false, 0)
  }

  def pr10_1(input: String) = {
    val str = (0 to 255).toArray
    var pos = 0
    var size = 0
    val slen = str.length

    def loop(lengths: List[Int]): Int = lengths match {
      case Nil => 0
      case len :: tail =>
        for(i<-1 to len / 2) {
          val p1 = (pos + len - i) % slen
          val p2 = (pos + i - 1) % slen
          val v = str(p2)
          str(p2) = str(p1)
          str(p1) = v
        }
        pos = (pos + len + size) % slen
        size = size + 1
        //println(s"$pos, $size, ${str.toList}")
        loop(tail)
    }

    for(i<-1 to 64) {
      loop(input.split(",").map(Integer.parseInt(_)).toList)
    }
    //str(0) * str(1)
    val hash = str.grouped(16).toList.map { h =>
      ("0" + h.reduce(_ ^ _).toHexString).takeRight(2)
    }
    hash.reduce(_ + _)
  }

  def knotHash(input: String) = {
    val str = input.map(ch => 0 + ch).mkString(",") + ",17,31,73,47,23"
    pr10_1(str)
  }

  def pr11_1(input: String) = {
    val map = Map(
      "ne" -> (1,1),
      "se" -> (1,-1),
      "s" -> (0,-2),
      "sw" -> (-1,-1),
      "nw" -> (-1,1),
      "n" -> (0,2)
    )

    def distance(x: Int, y: Int) = {
      val (ax, ay) = (Math.abs(x), Math.abs(y))
      val min = Math.min(ax, ay)
      (min + (Math.max(ax, ay) - min + 0.5) / 2).toInt
    }

    def loop(str: String) = {
      val path = str.split(",")
      path.foldLeft((0, 0, 0)) { case ((x, y, max), p) =>
        val move = map(p)
        val (xn, yn) = (x + move._1, y + move._2)
        (xn, yn, Math.max(max, distance(xn, yn)))
      }
    }

    val (x, y, max) = loop(input)

    (distance(x, y), max)
  }

  def pr12_1(input: String) = {
    def loop(ls: List[Array[String]], map: Map[String, Program]):
      Map[String, Program] = ls match
    {
      case Nil => map
      case xs :: tail =>
        val programs = xs(1).split(", ").map(name =>
          if (name.last == ',') name.dropRight(1)
          else name
        )
        val program = Program(xs(0), 0, programs.toList)
        loop(tail, map + (xs(0) -> program))

    }

    val ls = input.split("\n").map(_.trim.split(" <-> ").map(_.trim)).toList
    val programs = loop(ls, Map.empty[String, Program])

    def filter(map: Map[String, Program], set: Set[String]): Set[String] = {
      val newItems = set.filter(i => map.contains(i)).flatMap(i => map(i).programs).toSet
      val newSet = set ++ newItems
      if (newSet != set) filter(map, newSet)
      else set
    }

    def groups(map: Map[String, Program], count: Int): Int = {
      if (map.isEmpty) count
      else {
        val first = map.head._1
        val group = filter(programs, Set(first))
        val newMap = map -- group
        groups(newMap, count + 1)
      }
    }

    groups(programs, 0)
  }

  def pr13_1(input: String) = {
    def pos(range: Int, s: Int) = {
      val mod = (range - 1) * 2
      val p = s % mod
      if (p < range) p else mod - p
    }

    val ranges: Map[Int, Int] = input.split("\n").map(_.trim.split(": ")
      .map(Integer.parseInt(_))).map(xs => (xs(0) -> xs(1))).toMap
    val maxRange = ranges.keys.max

    def loop(delay: Int): Int = {
      val severity = ((0 to maxRange) map { packet: Int =>
        val rangeOpt = ranges.get(packet)
        rangeOpt.map(range => if (pos(range, packet + delay) != 0) 0 else range * (packet + delay))
          .getOrElse(0)
      }).sum

      if (severity == 0) delay
      else loop(delay + 1)
    }

    loop(0)
  }

  def pr14_1(input: String) = {
    def loop(row: Int, sum: Int): Int = {
      if (row >= 128) sum
      else {
        val hash = knotHash(s"$input-$row")
        val binary = BigInt(hash, 16).toString(2)
        val count = binary.filter(_ == '1').length
        loop(row + 1, sum + count)
      }
    }

    loop(0, 0)
  }

  def pr14_2(input: String) = {
    def loop(row: Int, disk: Vector[String]): Vector[String] = {
      if (row >= 128) disk
      else {
        val hash = knotHash(s"$input-$row")
        val binary = ("00000000" + BigInt(hash, 16).toString(2)).takeRight(128)
        loop(row + 1, disk :+ binary)
      }
    }

    val disk = loop(0, Vector())
    val regions = disk.map(_.map(ch => if (ch == '1') -1 else 0).toArray).toArray

    var nextRegNum = 1

    def setAll(i: Int, j: Int): Unit = {
      if (regions(i)(j) != nextRegNum && regions(i)(j) != 0) {
        regions(i)(j) = nextRegNum
        if (j < 127) setAll(i, j + 1)
        if (j > 0) setAll(i, j - 1)
        if (i < 127) setAll(i + 1, j)
        if (i > 0) setAll(i - 1, j)
      }
    }

    (0 to 127) foreach { i =>
      (0 to 127) foreach { j =>
        if (regions(i)(j) == -1) {
          setAll(i, j)
          nextRegNum = nextRegNum + 1
        }
      }
    }

    nextRegNum - 1
  }

  def pr15_1(input: String) = {
    def generator(factor: BigInt, mult: BigInt)(prev: BigInt): BigInt = {
      val value = prev * factor % 2147483647
      if (value % mult == 0) value
      else generator(factor, mult)(value)
    }

    val genA = generator(16807, 4) _
    val genB = generator(48271, 8) _

    def loop(count: Int, prevA: BigInt, prevB: BigInt, found: Int): Int = {
      if (count <= 0) found
      else {
        val a = genA(prevA)
        val b = genB(prevB)
        val a16 = a.toString(2).takeRight(16)
        val b16 = b.toString(2).takeRight(16)
        val nextFound = found + (if (a16 == b16) 1 else 0)
        loop(count-1, a, b, nextFound)
      }
    }

    //loop(5000000, 289, 629, 0)
    loop(1056, 65, 8921, 0)
  }

  def pr16_1(input: String) = {
    val programs = ('a' to 'p').toVector

    def loop(str: List[String], ps: Vector[Char]): Vector[Char] = str match {
      case s :: ss if s.startsWith("s") =>
        val l = Integer.parseInt(s.substring(1))
        loop(ss, (ps.takeRight(l) ++ ps.dropRight(l)))
      case s :: ss if s.startsWith("x") =>
        val params = s.substring(1).split("/")
        val p1 = Integer.parseInt(params(0))
        val p2 = Integer.parseInt(params(1))
        loop(ss, ps.updated(p1, ps(p2)).updated(p2, ps(p1)))
      case s :: ss if s.startsWith("p") =>
        val params = s.substring(1).split("/")
        val p1 = ps.indexOf(params(0)(0))
        val p2 = ps.indexOf(params(1)(0))
        loop(ss, ps.updated(p1, ps(p2)).updated(p2, ps(p1)))
      case Nil => ps
    }

    val ss = input.split(",").toList

    val ps = loop(ss, programs)

    def loop2(count: Int, ps: Vector[Char]): (Int, Vector[Char]) = {
      if (count <= 0) (count, ps)
      else {
        val nps = loop(ss, ps)
        if (nps == programs) (count, ps)
        else loop2(count - 1, loop(ss, ps))
      }
    }

    loop2(40, programs)._2.mkString
  }

  def pr17_1(input: String) = {
    def loop(buffer: Vector[Int], pos: Int, count: Int, step: Int): Int = {
      if (count > 2017) {
        val p = (pos + 1) % buffer.length
        buffer(p)
      } else {
        val p = if (buffer.length == 0) 0 else (pos + step) % buffer.length
        loop(
          (buffer.take(p + 1) :+ count) ++ buffer.takeRight(buffer.length - p - 1),
          p + 1,
          count + 1,
          step
        )
      }
    }

    loop(Vector(0), 0, 1, 349)
  }

  def pr17_2(input: String) = {
    def loop(pos: Int, count: Int, step: Int, solution: Int): Int = {
      if (count > 50000000) {
        solution
      } else {
        val p = if (count == 0) 0 else (pos + step) % count
        val s = if (p == 0) count else solution
        loop(
          p + 1,
          count + 1,
          step,
          s
        )
      }
    }

    loop(0, 1, 349, 0)
  }

  import scala.util.Try

  def pr18_1(input: String) = {
    var registers = Map.empty[String, BigInt]
    var max = 0

    def getReg(key: String): BigInt = registers.getOrElse(key, BigInt(0))
    def setReg(key: String, value: BigInt) = registers = registers + (key -> value)

    val programs = input.split("\n").map(_.trim.split("\\s").map(_.trim).toList).toVector

    def getValue(str: String): BigInt = Try(BigInt(str)).getOrElse(getReg(str))

    def loop(pos: Int, freq: BigInt): BigInt = {
      if (freq > 0 || pos >= programs.length) freq
      else {
        val line = programs(pos)
        println(s"$line, $registers")
        val reg = line(1)
        val (outFreq, p) = line(0) match {
          case "snd" =>
            val regValue = getValue(reg)
            println(s"play: $regValue")
            setReg("_snd_", regValue)
            (freq, 1)
          case "set" =>
            val value = getValue(line(2))
            setReg(reg, value)
            (freq, 1)
          case "add" =>
            val value = getValue(line(2))
            val regValue = getValue(reg)
            setReg(reg, value + regValue)
            (freq, 1)
          case "mul" =>
            val regValue = getValue(reg)
            val regValue2 = getValue(line(2))
            setReg(reg, regValue * regValue2)
            (freq, 1)
          case "mod" =>
            val value = getValue(line(2))
            val regValue = getValue(reg)
            setReg(reg, regValue % value)
            (freq, 1)
          case "rcv" =>
            val regValue = getValue(reg)
            if (regValue > 0) (getValue("_snd_"), 1)
            else (freq, 1)
          case "jgz" =>
            val regValue = getValue(reg)
            val value = getValue(line(2))
            (freq, if (regValue > 0) value.toInt else 1)
        }

        loop(pos + p, outFreq)
      }
    }

    loop(0, 0)
  }

  def pr18_2(input: String) = {
    val registers = Array(Map.empty[String, BigInt], Map[String, BigInt]("p" -> 1))
    var max = 0

    def getReg(pr: Int)(key: String): BigInt =
      registers(pr).getOrElse(key, BigInt(0))
    def setReg(pr: Int)(key: String, value: BigInt) =
      registers.update(pr, registers(pr) + (key -> value))

    val programs = input.split("\n").map(_.trim.split("\\s").map(_.trim).toList).toVector
    val sent = Array(List.empty[BigInt], List.empty[BigInt])

    def getValue(pr: Int)(str: String): BigInt =
      Try(BigInt(str)).getOrElse(getReg(pr)(str))

    def loop(pos0: Int, pos1: Int, pr1send: Int): BigInt = {
      if (pos0 >= programs.length && pos1 >= programs.length) pr1send
      else {
        val (pr, line) = if (pos0 < programs.length && (
          programs(pos0)(0) != "rcv" || !sent(1).isEmpty))
          (0, programs(pos0))
          else if (pos1 < programs.length) (1, programs(pos1))
          else (2, programs(pos0))

        if (pr == 2 || (pr == 1 && sent(0).isEmpty)) pr1send
        else {
          //println(s"$line, ${registers(0)}, ${registers(1)}")
          //println(s"$line, ${sent(0)}, ${sent(1)}")
          val reg = line(1)
          val (pr1, p) = line(0) match {
            case "snd" =>
              val regValue = getValue(pr)(reg)
              sent.update(pr, sent(pr) :+ regValue)
              (if (pr == 1) pr1send + 1 else pr1send, 1)
            case "set" =>
              val value = getValue(pr)(line(2))
              setReg(pr)(reg, value)
              (pr1send, 1)
            case "add" =>
              val value = getValue(pr)(line(2))
              val regValue = getValue(pr)(reg)
              setReg(pr)(reg, value + regValue)
              (pr1send, 1)
            case "mul" =>
              val regValue = getValue(pr)(reg)
              val regValue2 = getValue(pr)(line(2))
              setReg(pr)(reg, regValue * regValue2)
              (pr1send, 1)
            case "mod" =>
              val value = getValue(pr)(line(2))
              val regValue = getValue(pr)(reg)
              setReg(pr)(reg, regValue % value)
              (pr1send, 1)
            case "rcv" =>
              val regValue = sent(1-pr)(0)
              setReg(pr)(reg, regValue)
              sent.update(1 - pr, sent(1 - pr).drop(1))
              (pr1send, 1)
            case "jgz" =>
              val regValue = getValue(pr)(reg)
              val value = getValue(pr)(line(2))
              (pr1send, if (regValue > 0) value.toInt else 1)
          }

          val (np0, np1) = if (pr == 0) (pos0 + p, pos1) else (pos0, pos1 + p)
          loop(np0, np1, pr1)
        }
      }
    }

    loop(0, 0, 0)
  }

  def pr19_1(input: String) = {
    val diagram = input.split("\n").toVector
    val startPos = diagram(1).indexOf('|')
    val dirs = List((-1, 0), (1, 0), (0, -1), (0, 1))

    def getChar(x: Int, y: Int) = {
      if (diagram.length > y && diagram(y).length > x) diagram(y)(x) else ' '
    }

    def loop(x: Int, y: Int, dirX: Int, dirY: Int, path: String, step: Int):
      (String, Int) =
    {
      getChar(x, y) match {
        case '|' =>
          loop(x + dirX, y + dirY, dirX, dirY, path, step + 1)
        case '-' =>
          loop(x + dirX, y + dirY, dirX, dirY, path, step + 1)
        case '+' =>
          val (dX, dY) = (dirs find { case (dx, dy) =>
            (dx != dirX && dy != dirY && getChar(x - dx, y - dy) != ' ')
          }).get
          loop(x - dX, y - dY, -dX, -dY, path, step + 1)
        case letter if ('A' to 'Z').contains(letter) =>
          loop(x + dirX, y + dirY, dirX, dirY, path + letter, step + 1)
        case _ =>
          (path, step)
      }
    }

    loop(startPos, 1, 0, 1, "", 0)
  }

  case class Particle(index: Int, pX: Int, pY: Int, pZ: Int, vX: Int, vY: Int, vZ: Int, aX: Int, aY: Int, aZ: Int) {
    def minA = Math.abs(aX) + Math.abs(aY) + Math.abs(aZ)
    def minV = Math.abs(vX) + Math.abs(vY) + Math.abs(vZ)
    def minP = Math.abs(pX) + Math.abs(pY) + Math.abs(pZ)

    def less(p: Particle) = this.minA < p.minA ||
      (this.minA == p.minA && this.minV < p.minV) ||
      (this.minV == p.minV && this.minP < p.minP)

    def next = Particle(
      index,
      pX + (vX + aX),
      pY + (vY + aY),
      pZ + (vZ + aZ),
      (vX + aX),
      (vY + aY),
      (vZ + aZ),
      aX,
      aY,
      aZ)

    def samePos(p: Particle) = this.index != p.index &&
      this.pX == p.pX && this.pY == p.pY && this.pZ == p.pZ
  }

  def pr20_1(input: String) = {
    var pos = -1
    val vectorsReg = raw"p=<(.*\d+),(.*\d+),(.*\d+)>,\s*v=<(.*\d+),(.*\d+),(.*\d+)>,\s*a=<(.*\d+),(.*\d+),(.*\d+)>".r.unanchored
    val particles = (input.split("\n").drop(1).dropRight(1).map { line =>
      line match {
        case vectorsReg(pX, pY, pZ, vX, vY, vZ, aX, aY, aZ) =>
          pos = pos + 1
          Particle(
            pos,
            Integer.parseInt(pX), Integer.parseInt(pY), Integer.parseInt(pZ), Integer.parseInt(vX), Integer.parseInt(vY), Integer.parseInt(vZ), Integer.parseInt(aX), Integer.parseInt(aY), Integer.parseInt(aZ))
      }
    }).toVector

    def loop(parts: Vector[Particle], count: Int): Int = {
      if (count <= 0) parts.length
      else {
        val filtered = parts.filterNot(p => parts.exists(p2 => p.samePos(p2)))
        loop(filtered.map(_.next), count - 1)
      }
    }

    (particles.sortWith(_.less(_)).head, loop(particles, 1000))
  }

  def pr21_1(input: String) = {
    val pattern = """.#.
..#
###"""
    val rules = (input.split("\n").map(_.trim.split("=>").map(_.trim).toSeq).map {
      ls  => (ls(0) -> ls(1)) }).toMap

    def toRuleFrom(pattern: String) = pattern.replace("\n", "/")
    def toPatternFrom(rule: String) = rule.replace("/", "\n")
    val transforms = Map(3 -> List(
      "012/345/678",
      "630/741/852",
      "852/741/630",
      "876/543/210",
      "258/147/036",
      "036/147/258",
      "210/543/876",
      "678/345/012"
    ), 2 -> List(
      "01/23",
      "20/31",
      "32/10",
      "13/02",
      "23/01",
      "10/32"
    ))
    def toPos(size: Int, ch: Char) = {
      val num = ch - '0'
      if (num >= size * 2) num + 2
      else if (num >= size) num + 1
      else num
    }

    def transform(pattern: String, tr: String, size: Int) = {
      val rule = toRuleFrom(pattern)
      tr.map {
        case '/' => '/'
        case num => rule(toPos(size, num))
      }
    }

    def loop(pattern: String, count: Int): Int = {
      if (count <= 0) {
        pattern.map {
          case '#' => 1
          case _ => 0
        }.sum
      } else {
        val image = pattern.split("\n")
        val len = image.length
        val size = if (len % 2 == 0) 2 else 3
        val rowCount = image.length / size
        val newImage = (0 until rowCount).map { row =>
          val columns = (0 until rowCount).map { col =>
            val p1 = (0 until size).map { pos =>
              image(row * size + pos).substring(col * size, (col + 1) * size)
            }.mkString("/")
            val transformeds = transforms(size).map(tr => transform(p1, tr, size))
            val transformed = transformeds.find(tr => rules.contains(tr))
            rules(transformed.getOrElse(""))
          }
          val empty: IndexedSeq[String] = (0 to size) map { i => "" }
          val grid = columns.foldLeft(empty) { (p, col) =>
            val p2 = toPatternFrom(col).split("\n")
            (0 to size) map { i => p(i) + p2(i) }
          }
          grid
        }
        val newPattern = newImage.flatten.mkString("\n")
        loop(newPattern, count - 1)
      }
    }

    loop(pattern, 18)
  }

  case class Virus(x: Int, y: Int, faceX: Int, faceY: Int)

  def pr22_1(input: String) = {
    def loop(
      grid: Map[(Int, Int), Int],
      count: Int,
      bursts: Int,
      virus: Virus
    ): Int = {
      if (count <= 0) bursts
      else {
        val state = grid.get(virus.x, virus.y).getOrElse(0)
        val newBursts = bursts + (if (state == 1) 1 else 0)
        val newVirus = state match {
          case 0 =>
            val faceX = -virus.faceY
            val faceY = virus.faceX
            Virus(virus.x + faceX, virus.y + faceY, faceX, faceY)
          case 1 =>
            virus.copy(x = virus.x + virus.faceX, y = virus.y + virus.faceY)
          case 2 =>
            val faceX = virus.faceY
            val faceY = -virus.faceX
            Virus(virus.x + faceX, virus.y + faceY, faceX, faceY)
          case 3 =>
            val faceX = -virus.faceX
            val faceY = -virus.faceY
            Virus(virus.x + faceX, virus.y + faceY, faceX, faceY)
        }
        val newGrid = grid + ((virus.x, virus.y) -> (state + 1) % 4)
        loop(newGrid, count - 1, newBursts, newVirus)
      }
    }

    val arr = input.split("\n").map(_.trim)
    val size = arr.length

    val grid = (for (i <- 0 until size; j <- 0 until size)
      yield ((j, -i) -> (if (arr(i)(j) == '#') 2 else 0))).toMap

    loop(grid, 10, 0, Virus(size/2, -size/2, 0, 1))
  }

  def pr23_1(input: String) = {
    var registers = Map.empty[String, BigInt]
    var max = 0

    def getReg(key: String): BigInt = registers.getOrElse(key, BigInt(0))
    def setReg(key: String, value: BigInt) = registers = registers + (key -> value)

    val programs = input.split("\n").map(_.trim.split("\\s").map(_.trim).toList).toVector

    def getValue(str: String): BigInt = Try(BigInt(str)).getOrElse(getReg(str))

    def loop(pos: Int, freq: BigInt): BigInt = {
      if (pos >= programs.length) freq
      else {
        val line = programs(pos)
        //println(s"$line, $registers")
        val reg = line(1)
        val (outFreq, p) = line(0) match {
          case "set" =>
            val value = getValue(line(2))
            setReg(reg, value)
            (freq, 1)
          case "sub" =>
            val value = getValue(line(2))
            val regValue = getValue(reg)
            setReg(reg, regValue- value)
            (freq, 1)
          case "mul" =>
            val regValue = getValue(reg)
            val regValue2 = getValue(line(2))
            setReg(reg, regValue * regValue2)
            (freq + 1, 1)
          case "jnz" =>
            val regValue = getValue(reg)
            val value = getValue(line(2))
            (freq, if (regValue != 0) value.toInt else 1)
        }

        loop(pos + p, outFreq)
      }
    }

    loop(0, 0)
  }

  def pr23_2(input: String) = {
    var h = 0
    def count(x: Int): Unit = {
      for (i <- 2 until x) {
        if (x % i == 0) {
          h = h + 1
          return
        }
      }
    }

    (108400 to 125400 by 17) foreach { x =>
      count(x)
    }
    h
  }

  def pr24_1(input: String) = {
    val connections = input.split("\n").map(_.trim.split("/").
      map(Integer.parseInt(_))).map(ls => (ls(0) -> ls(1))).toList

    def loop(cs: List[(Int, Int)], end: Int): Int =
    {
      cs.map(x => x match {
        case (s, e) if (s == end) =>
          loop(cs.filter(_ != x), e) + 100000 + s + e
        case (s, e) if (e == end) =>
          loop(cs.filter(_ != x), s) + 100000 + s + e
        case _ =>
          0
      }).max
    }

    loop(connections, 0)
  }

  case class Condition(
    write: Int,
    move: Int,
    cont: String
  )

  case class State(conditions: Vector[Condition])

  def pr25_1(states: Map[String, State], state: State, step: Int) = {
    val tape = Map.empty[Int, Int]

    def read(tape: Map[Int, Int], key: Int): Int = tape.getOrElse(key, 0)
    def write(tape: Map[Int, Int], key: Int, value: Int) = tape + (key -> value)

    def loop(tape: Map[Int, Int], state: State, step: Int, pos: Int): Int = {
      if (step <= 0) tape.values.sum
      else {
        val value = read(tape, pos)
        val condition = state.conditions(value)
        val nextTape = write(tape, pos, condition.write)
        loop(nextTape, states(condition.cont), step - 1, pos + condition.move)
      }
    }

    loop(tape, state, step, 0)
  }

  val states = Map(
    "A" -> State(Vector(Condition(1, 1, "B"), Condition(0, -1, "B"))),
    "B" -> State(Vector(Condition(1, -1, "A"), Condition(1, 1, "A")))
  )

  val states2 = Map(
    "A" -> State(Vector(Condition(1, 1, "B"), Condition(0, -1, "C"))),
    "B" -> State(Vector(Condition(1, -1, "A"), Condition(1, 1, "D"))),
    "C" -> State(Vector(Condition(1, 1, "A"), Condition(0, -1, "E"))),
    "D" -> State(Vector(Condition(1, 1, "A"), Condition(0, 1, "B"))),
    "E" -> State(Vector(Condition(1, -1, "F"), Condition(1, -1, "C"))),
    "F" -> State(Vector(Condition(1, 1, "D"), Condition(1, 1, "A")))
  )

  val input = """
"""

  val input2 = """
"""

  println(
    //pr25_1(states, states("A"), 6)
    pr25_1(states2, states2("A"), 12919244)
  )
}
