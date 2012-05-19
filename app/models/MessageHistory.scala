package models

import scalikejdbc._

case class MessageHistory(
  id: Long,
  messageId: Long,
  errorMessage: Option[String] = None,
  serverName: Option[String] = None,
  status: String) { 

  def save(): MessageHistory = MessageHistory.save(this)

  def destroy(): Unit = MessageHistory.delete(this)

}

object MessageHistory {

  val tableName = "MESSAGE_HISTORY"

  object columnNames {
    val id = "ID"
    val messageId = "MESSAGE_ID"
    val errorMessage = "ERROR_MESSAGE"
    val serverName = "SERVER_NAME"
    val status = "STATUS"
    val all = Seq(errorMessage, id, messageId, serverName, status)
  }

  val * = {
    import columnNames._
    (rs: WrappedResultSet) => MessageHistory(
      id = rs.long(id),
      messageId = rs.long(messageId),
      errorMessage = Option(rs.string(errorMessage)),
      serverName = Option(rs.string(serverName)),
      status = rs.string(status))
  }

  def find(id: Long)(implicit session: DBSession = AutoSession): Option[MessageHistory] = {
    SQL("""SELECT * FROM MESSAGE_HISTORY WHERE ID = /*'id*/1""")
      .bindByName('id -> id).map(*).single.apply()
  }

  def findAll()(implicit session: DBSession = AutoSession): List[MessageHistory] = {
    SQL("""SELECT * FROM MESSAGE_HISTORY""").map(*).list.apply()
  }

  def countAll()(implicit session: DBSession = AutoSession): Long = {
    SQL("""SELECT COUNT(1) FROM MESSAGE_HISTORY""").map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: String, params: (Symbol, Any)*)(implicit session: DBSession = AutoSession): List[MessageHistory] = {
    SQL("""SELECT * FROM MESSAGE_HISTORY WHERE """ + where)
      .bindByName(params:_*).map(*).list.apply()
  }

  def countBy(where: String, params: (Symbol, Any)*)(implicit session: DBSession = AutoSession): Long = {
    SQL("""SELECT count(1) FROM MESSAGE_HISTORY WHERE """ + where)
      .bindByName(params:_*).map(rs => rs.long(1)).single.apply().get
  }

  def create(
    errorMessage: Option[String] = None,
    messageId: Long,
    serverName: Option[String] = None,
    status: String)(implicit session: DBSession = AutoSession): MessageHistory = {
    val id = SQL("""
      INSERT INTO MESSAGE_HISTORY (
        ERROR_MESSAGE,
        MESSAGE_ID,
        SERVER_NAME,
        STATUS
      ) VALUES (
        /*'errorMessage*/'abc',
        /*'messageId*/1,
        /*'serverName*/'abc',
        /*'status*/'abc'
      )
      """)
      .bindByName(
        'errorMessage -> errorMessage,
        'messageId -> messageId,
        'serverName -> serverName,
        'status -> status
      ).updateAndReturnGeneratedKey.apply()
    MessageHistory(
      id = id,
      messageId = messageId,
      errorMessage = errorMessage,
      serverName = serverName,
      status = status)
  }

  def save(m: MessageHistory)(implicit session: DBSession = AutoSession): MessageHistory = {
    SQL("""
      UPDATE 
        MESSAGE_HISTORY
      SET 
        ERROR_MESSAGE = /*'errorMessage*/'abc',
        ID = /*'id*/1,
        MESSAGE_ID = /*'messageId*/1,
        SERVER_NAME = /*'serverName*/'abc',
        STATUS = /*'status*/'abc'
      WHERE 
        ID = /*'id*/1
      """)
      .bindByName(
        'errorMessage -> m.errorMessage,
        'id -> m.id,
        'messageId -> m.messageId,
        'serverName -> m.serverName,
        'status -> m.status
      ).update.apply()
    m
  }

  def delete(m: MessageHistory)(implicit session: DBSession = AutoSession): Unit = {
    SQL("""DELETE FROM MESSAGE_HISTORY WHERE ID = /*'id*/1""")
      .bindByName('id -> m.id).update.apply()
  }

}
