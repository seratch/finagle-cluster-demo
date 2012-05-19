package models

import scalikejdbc._

case class Message(
  id: Long, 
  serverName: Option[String] = None, 
  currentStatus: String) { 

  def save(): Message = Message.save(this)

  def destroy(): Unit = Message.delete(this)

}

object Message {

  val tableName = "MESSAGE"

  object columnNames {
    val id = "ID"
    val serverName = "SERVER_NAME"
    val currentStatus = "CURRENT_STATUS"
    val all = Seq(id, serverName, currentStatus)
  }

  val * = {
    import columnNames._
    (rs: WrappedResultSet) => Message(
      id = rs.long(id),
      serverName = Option(rs.string(serverName)),
      currentStatus = rs.string(currentStatus))
  }

  def find(id: Long)(implicit session: DBSession = AutoSession): Option[Message] = {
    SQL("""SELECT * FROM MESSAGE WHERE ID = /*'id*/1""")
      .bindByName('id -> id).map(*).single.apply()
  }

  def findAll()(implicit session: DBSession = AutoSession): List[Message] = {
    SQL("""SELECT * FROM MESSAGE""").map(*).list.apply()
  }

  def countAll()(implicit session: DBSession = AutoSession): Long = {
    SQL("""SELECT COUNT(1) FROM MESSAGE""").map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: String, params: (Symbol, Any)*)(implicit session: DBSession = AutoSession): List[Message] = {
    SQL("""SELECT * FROM MESSAGE WHERE """ + where)
      .bindByName(params:_*).map(*).list.apply()
  }

  def countBy(where: String, params: (Symbol, Any)*)(implicit session: DBSession = AutoSession): Long = {
    SQL("""SELECT count(1) FROM MESSAGE WHERE """ + where)
      .bindByName(params:_*).map(rs => rs.long(1)).single.apply().get
  }

  def create(
    id: Long,
    serverName: Option[String] = None,
    currentStatus: String)(implicit session: DBSession = AutoSession): Message = {
    SQL("""
      INSERT INTO MESSAGE (
        ID,
        SERVER_NAME,
        CURRENT_STATUS
      ) VALUES (
        /*'id*/1,
        /*'serverName*/'abc',
        /*'currentStatus*/'abc'
      )
      """)
      .bindByName(
        'id -> id,
        'serverName -> serverName,
        'currentStatus -> currentStatus
      ).update.apply()
    Message(
      id = id,
      serverName = serverName,
      currentStatus = currentStatus)
  }

  def save(m: Message)(implicit session: DBSession = AutoSession): Message = {
    SQL("""
      UPDATE 
        MESSAGE
      SET 
        ID = /*'id*/1,
        SERVER_NAME = /*'serverName*/'abc',
        CURRENT_STATUS = /*'currentStatus*/'abc'
      WHERE 
        ID = /*'id*/1
      """)
      .bindByName(
        'id -> m.id,
        'serverName -> m.serverName,
        'currentStatus -> m.currentStatus
      ).update.apply()
    m
  }

  def delete(m: Message)(implicit session: DBSession = AutoSession): Unit = {
    SQL("""DELETE FROM MESSAGE WHERE ID = /*'id*/1""")
      .bindByName('id -> m.id).update.apply()
  }

}
