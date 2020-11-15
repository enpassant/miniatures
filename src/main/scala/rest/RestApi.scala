package rest

trait MediaTypeStructure

case class StringMTStructure(content: String) extends MediaTypeStructure
case class CollectionMTStructure(contentMT: MediaType) extends MediaTypeStructure

case class MediaType(value: String, structure: MediaTypeStructure)

sealed trait HttpMethod

case object GET extends HttpMethod
case object HEAD extends HttpMethod
case object PUT extends HttpMethod
case object DELETE extends HttpMethod

case class RelLink(
  rel: String,
  method: HttpMethod,
  in: Option[MediaType],
  out: MediaType
)

case class Link(
  uri: String,
  relLink: RelLink
)

case class Transition(
  context: List[MediaType],
  relLink: RelLink
)

trait Message {
}

case class ParsedUri(id: String, params: Map[String, String])

case class Action[R](
  transition: Transition,
  parseUri: String => ParsedUri,
  fn: Message => R
)

case class RestApi(entryPoint: String, transitions: Transition*)

object RestApi extends App {
  val account = MediaType(
    "Account",
    StringMTStructure("Account(name: Name, number: AccountNumber, balance: Double)")
  )
  val accountList = MediaType(
    "List[Account]",
    CollectionMTStructure(account)
  )
  val name = MediaType(
    "Name",
    StringMTStructure("Name(value: String[80])")
  )
  val deposit = MediaType(
    "Deposit",
    StringMTStructure("Deposit(sourceAccount: AccountNumber, amount: Double)")
  )
  val withdraw = MediaType(
    "Withdraw",
    StringMTStructure("Withdraw(targetAccount: AccountNumber, amount: Double)")
  )

  val accountApi = RestApi(
    "/api",
    Transition(Nil, RelLink("accounts", GET, None, accountList)),
    Transition(List(accountList), RelLink("new", PUT, Option(account), account)),
    Transition(List(accountList), RelLink("item", GET, None, account)),
    Transition(List(account), RelLink("delete", DELETE, None, account)),
    Transition(List(account), RelLink("edit", PUT, Option(name), account)),
    Transition(List(account), RelLink("edit", PUT, Option(deposit), account)),
    Transition(List(account), RelLink("edit", PUT, Option(withdraw), account))
  )

  println(accountApi)
}
