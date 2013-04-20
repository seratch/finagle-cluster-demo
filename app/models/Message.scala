package models

import scalikejdbc._
import scalikejdbc.SQLInterpolation._

case class Message(
    id: Long,
    serverName: Option[String] = None,
    currentStatus: String) {

  def save(): Message = Message.save(this)

}

object Message extends SQLSyntaxSupport[Message] {

  override val tableName = "message"

  def apply(m: ResultName[Message])(rs: WrappedResultSet): Message = new Message(
    id = rs.long(m.id),
    serverName = rs.stringOpt(m.serverName),
    currentStatus = rs.string(m.currentStatus)
  )

  val auto = AutoSession
  val m = Message.syntax("m")

  def find(id: Long)(implicit session: DBSession = auto): Option[Message] = {
    sql"select ${m.result.*} from ${Message as m} where ${m.id} = ${id}"
      .map(Message(m.resultName)).single.apply()
  }

  def countAll()(implicit session: DBSession = auto): Long = {
    sql"select count(1) from ${Message.table}".map(_.long(1)).single.apply().get
  }

  def countByCurrentStatus(currentStatus: String)(implicit session: DBSession = auto): Long = {
    sql"select count(1) from ${Message.as(m)} where ${m.currentStatus} = ${currentStatus}"
      .map(_.long(1)).single.apply().get
  }

  def create(
    id: Long,
    serverName: Option[String] = None,
    currentStatus: String)(implicit session: DBSession = auto): Message = {

    sql"""
    insert into ${Message.table} 
      (id, server_name, current_status) 
      values 
      (${id}, ${serverName}, ${currentStatus})
    """.update.apply()

    Message(
      id = id,
      serverName = serverName,
      currentStatus = currentStatus)
  }

  def save(e: Message)(implicit session: DBSession = auto): Message = {
    sql"""
      update ${Message.as(m)} 
      set 
        ${m.id} = ${e.id},
        ${m.serverName} = ${e.serverName},
        ${m.currentStatus} = ${e.currentStatus}
      where 
        ${m.id} = ${e.id}
      """.update.apply()
    e
  }

}
