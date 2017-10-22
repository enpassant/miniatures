package ddd

trait Error
trait State
trait Command
trait Event

object Types {
  sealed trait Quantity {
    def volume: BigDecimal
    def unit: String
  }
  case class Pieces(volume: BigDecimal) extends Quantity { def unit = "pcs" }
  case class Volume(volume: BigDecimal) extends Quantity { def unit = "l" }
  case class Mass(volume: BigDecimal) extends Quantity { def unit = "kg" }

  type ID = String
  type String80 = String

  case class StateResult(state: State, events: List[Event])
  type ->[A, B] = PartialFunction[A, B]
  type CommandResult[Event] = Either[Error, List[Event]]
  type CommandHandler[S <: State, C <: Command, E <: Event] =
    (State, Command) -> CommandResult[Event]
  type EventHandler[S <: State, E <: Event, St <: State] =
    (State, Event) -> Either[Error, State]
  type Capability = (State, Command) -> Either[Error, StateResult]
  type GetCapabilities = State -> Map[String, Capability]

  def createCapability(
    commandHandler: CommandHandler[State, Command, Event],
    eventHandler: EventHandler[State, Event, State]): Capability =
  {
    case (state, command) if commandHandler.isDefinedAt(state, command) =>
      val fn = commandHandler andThen
        (r => r.right.flatMap {
          ls => eventHandler(state, ls.head).right.map {
            st => StateResult(st, ls)
          }
        })
      fn(state, command)
  }
}
