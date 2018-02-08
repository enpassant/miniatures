object Kamil extends App {
	val stream = Stream.continually(scala.io.StdIn.readLine).foldLeft(1)(
		(x,c)=>if("FLDT".contains(c)) x*2 else x)
	println(stream)
}
