package control

trait For[S] {
  def init: S
  def predicate(s: S): Boolean
  def step(s: S): S
  def core(s: S)

  def run() = {
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

object ForOOApp extends App {
  val data = List(1, 3, 5, 7, 8, 4, 9)
  (new For[Int] {
    def init = 0
    def predicate(state: Int) = state < data.length
    def step(state: Int) = state + 1
    def core(state: Int) = {
      println(data(state))
    }
  }).run()
}
