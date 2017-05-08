object Duck {
  def Quack() = {}
  def Walk() = {}
  def Fly() = {}
}

object OtherDuck {
  def Quack() = {}
  def Walk() = {}
}

object ThirdDuck {
  def Quack() = {}
  def Walk() = {}
  def Sit() = {}
  def Stand() = {}
}

object Bird {
  type Fn = () => Unit

  def M(quack: Fn, walk: Fn, fly: Option[Fn], sit: Option[Fn], stand: Option[Fn]) {
    quack()
    walk()
    fly.foreach(_())
    walk()
    quack()
    sit.foreach(_())
    quack()
    quack()
    stand.foreach(_())
    quack()
  }

  M(Duck.Quack, Duck.Walk, Some(Duck.Fly), None, None)
  M(OtherDuck.Quack, OtherDuck.Walk, None, None, None)
  M(ThirdDuck.Quack, ThirdDuck.Walk, None, Some(ThirdDuck.Sit), Some(ThirdDuck.Stand))
}

