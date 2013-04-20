package models

import play.api.Logger

object MessageStore {

  val log = Logger("MessageStore")

  def all = Message.countAll()
  def historyAll = MessageHistory.countAll()
  def failures = Message.countByCurrentStatus("failure")

  def setAsSuccess(id: Long, server: String) = try {
    Message.find(id).map { message => message.copy(currentStatus = "success").save()
    }.getOrElse {
      Message.create(
        id = id,
        currentStatus = "success",
        serverName = Option(server)
      )
    }
    MessageHistory.create(
      errorMessage = None,
      messageId = id,
      serverName = Some(server),
      status = "success"
    )
  } catch {
    case e: Exception => log.error("failed to store", e)
  }

  def setAsFailure(id: Long, error: String) = try {
    Message.find(id).map { message => message.copy(currentStatus = "failure").save()
    }.getOrElse {
      Message.create(
        id = id,
        currentStatus = "failure",
        serverName = None
      )
    }
    MessageHistory.create(
      errorMessage = Some(error),
      messageId = id,
      serverName = None,
      status = "failure"
    )
  } catch {
    case e: Exception => log.error("failed to store", e)
  }

}
