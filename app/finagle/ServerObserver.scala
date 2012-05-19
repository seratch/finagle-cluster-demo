package finagle

object ServerSubject {
  private val observers = new collection.mutable.ListBuffer[ServerObserver]
  def addObserver(o: ServerObserver) = observers.+=(o)
  def removeObserver(o: ServerObserver) = observers.-=(o)
  def notifyObservers(event: ServerEvent) = observers foreach (_.notify(event))
}

case class ServerEvent(id: Long, server: String, time: Long)

class ServerObserver {
  val events = new collection.mutable.ListBuffer[ServerEvent]
  def notify(event: ServerEvent): Unit = events.+=(event)
}
