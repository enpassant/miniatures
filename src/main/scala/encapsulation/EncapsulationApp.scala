package encapsulation

object EncapsulationApp extends App {
  import unit.DistanceUnit._
  import unit.DistanceMiles._
  import unit.TimeUnit._
  import unit.SpeedUnit._
  import unit.Formulas._

  //new unit.Distance(15)
  //createKm(1).meter

  println(
    s"10000 meter = ${asMile(createDistance(10000))} miles"
  )
  println(
    s"1 mile = ${asKm(createMile(1))} km"
  )
  println(
    s"50 km/h * 1800 sec = ${asKm(calcDistance(createKph(50), createTime(1800)))} km"
  )

  case class ID(value: String)

  def loadFile(id: ID): Array[Byte] = id.value.getBytes()
  def loadDatabase(id: ID): Array[Byte] = ("Database" + id.value).getBytes()

  def dataToHex(data: Array[Byte]): String = data.map("%02X" format _).mkString

  def process(id: ID, load: ID => Array[Byte]): String = {
    val data = load(id)
    dataToHex(data)
  }

  println(
    process(ID("test"), loadFile)
  )

  case class IDObject(id: ID, load: ID => Array[Byte])

  def processObject(idObject: IDObject): String = {
    val data = idObject.load(idObject.id)
    dataToHex(data)
  }

  println(
    processObject(IDObject(ID("test"), loadDatabase))
  )

  def createDatabaseID(id: String): IDObject = IDObject(ID(id), loadDatabase)

  println(
    processObject(createDatabaseID("test"))
  )
}
