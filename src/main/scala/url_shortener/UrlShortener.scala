package url_shortener

import java.util.Base64
import scala.math.BigInt
import scala.util.Random

object UrlShortener extends App {
  (1000000001 to 1000000010) map {
    i => i ^ 0x3a75dc68
  } map {
    i => new Random(147).shuffle(i.toBinaryString.toList)
  } map {
    s => Integer.parseInt(s.mkString, 2)
  } map {
    i => Base64.getEncoder.encodeToString(BigInt(i).toByteArray)
  } foreach println
}

