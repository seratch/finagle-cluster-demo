package models

import scalikejdbc._
import scalikejdbc.SQLInterpolation._

case class MessageHistory(
  id: Long,
  messageId: Long,
  errorMessage: Option[String] = None,
  serverName: Option[String] = None,
  status: String)

object MessageHistory extends SQLSyntaxSupport[MessageHistory] {

  override val tableName = "message_history"

  def apply(mh: ResultName[MessageHistory])(rs: WrappedResultSet): MessageHistory = new MessageHistory(
    id = rs.long(mh.id),
    messageId = rs.long(mh.messageId),
    errorMessage = rs.stringOpt(mh.errorMessage),
    serverName = rs.stringOpt(mh.serverName),
    status = rs.string(mh.status)
  )

  val auto = AutoSession
  val mh = MessageHistory.syntax("mh")

  def find(id: Long)(implicit session: DBSession = auto): Option[MessageHistory] = {
    sql"select ${mh.result.*} from ${MessageHistory.as(mh)} where ${mh.id} = ${id}"
      .map(MessageHistory(mh.resultName)).single.apply()
  }

  def countAll()(implicit session: DBSession = auto): Long = {
    sql"select count(1) from ${MessageHistory.as(mh)}".map(_.long(1)).single.apply().get
  }

  def create(
    errorMessage: Option[String] = None,
    messageId: Long,
    serverName: Option[String] = None,
    status: String)(implicit session: DBSession = auto): MessageHistory = {

    val id = sql"""
      insert into ${MessageHistory.table} 
        (error_message, message_id, server_name, status) values 
        (${errorMessage}, ${messageId}, ${serverName}, ${status})
      """.updateAndReturnGeneratedKey.apply()

    MessageHistory(
      id = id,
      messageId = messageId,
      errorMessage = errorMessage,
      serverName = serverName,
      status = status)
  }

}
