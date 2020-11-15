package control

object ForFP {
  def forFn[S](
    init: S,
    predicate: S => Boolean,
    step: S => S,
    core: S => Unit
  ) = {
    def loop(state: S): Unit = {
      if (predicate(state)) {
        core(state)
        val nextState = step(state)
        loop(nextState)
      }
    }
    loop(init)
  }
}

object ForApp extends App {
  val data = List(1, 3, 5, 7, 8, 4, 9)
  ForFP.forFn[Int](0, _ < data.length, _ + 1, i => {
    println(data(i))
  })

  def forRange(s: Int, n: Int) =
    ForFP.forFn[Int](s, _ < n, _ + 1, _)

  forRange(0, data.length) { i =>
    println(data(i))
  }

  data.foreach(println)
}
