package family

object Family {
  trait FamilyMember
  trait Friend
  type Greet = PartialFunction[(FamilyMember, Friend), String]
  object Husband extends FamilyMember
  object Albert extends Friend
  object Joe extends Friend
  val composePF = (g1: Greet, g2: Greet) => g2 orElse g1
  val greetDef: Greet = { case (fm, f) => s"How do you do!" }
  val friends = List(Albert)
  val accept = (friend: Friend) => friends.contains(friend)
  val greetHusbandHisFriend: Greet = { case (Husband, friend) if (accept(friend)) => "Hey budd!" }
  trait GreetFn {
    def greet: Greet
  }
  trait Modul
  val composeGreet = (moduls: List[Modul]) =>
    moduls.collect { case g: GreetFn => g.greet }.foldLeft(greetDef)(composePF)
  object HusbandModul extends Modul with GreetFn {
    def greet = greetHusbandHisFriend
  }
}
