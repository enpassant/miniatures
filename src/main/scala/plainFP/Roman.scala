package plainFP

object Roman extends App {
  val romanNumber = "MCMLXIX"
  val digit = List('I', 'V', 'X', 'L', 'C', 'D', 'M')
  val digitValue = List(1, 5, 10, 50, 100, 500, 1000)
  val indexes = romanNumber.map(ch => digit.indexOf(ch))

  val pairs = indexes.zip(indexes.drop(1) :+ 0)
  val values = pairs.map(pair =>
      if (pair._1 >= pair._2)
        digitValue(pair._1)
      else
        -digitValue(pair._1))
  val arab = values.sum
  println("Arab: " + arab)
}
