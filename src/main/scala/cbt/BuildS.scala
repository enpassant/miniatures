import shapeless._ ; import syntax.singleton._ ; import record._

object BuildS {
  val build =
    ("x" ->> 5) ::
    ("y"  ->> 6) ::
    ("z" ->> (() => 5)) ::
    ("zz" ->> (() => 5)) ::
    HNil

  val build2 = build + ("z" ->> (() => build("x") + build("y")))
}
