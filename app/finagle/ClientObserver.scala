package finagle

object ClientSubject {
  private val observers = new collection.mutable.ListBuffer[ClientObserver]
  def addObserver(o: ClientObserver) = observers.+=(o)
  def removeObserver(o: ClientObserver) = observers.-=(o)
  def notifyObservers(event: ClientEvent) = observers foreach (_.notify(event))
}

case class ClientEvent(message: String)

class ClientObserver {
  val events = new collection.mutable.ListBuffer[ClientEvent]
  def notify(event: ClientEvent): Unit = events.+=(event)
}
