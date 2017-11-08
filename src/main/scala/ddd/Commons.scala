package ddd

trait State
trait Command
trait Event

case object NoCommand extends Command

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
  type CommandResult[Event] = Either[Failure, List[Event]]
  type CommandHandler[S <: State, C <: Command, E <: Event] =
    (State, Command) -> CommandResult[Event]
  type EventHandler[S <: State, E <: Event, St <: State] =
    (State, Event) -> Either[Failure, State]
  type Capability =
    Command -> Either[Failure, StateResult]
  type GetCapabilities =
    State -> Map[String, Capability]

  def createCapability(
    state: State,
    commandHandler: CommandHandler[State, Command, Event],
    eventHandler: EventHandler[State, Event, State]): Capability =
  {
    case command if commandHandler.isDefinedAt(state, command) =>
      val fn = commandHandler andThen
        (r => r.right.flatMap {
          ls => eventHandler(state, ls.head).right.map {
            st => StateResult(st, ls)
          }
        })
      fn(state, command)
  }

  def createCapability(
    state: State,
    command: Command,
    commandHandler: CommandHandler[State, Command, Event],
    eventHandler: EventHandler[State, Event, State]): Capability =
  {
    case cmd => createCapability(state, commandHandler, eventHandler)(command)
  }
}
