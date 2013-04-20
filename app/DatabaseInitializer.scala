import play.api._
import scalikejdbc._
import scalikejdbc.SQLInterpolation._

class DatabaseInitializer(app: Application) extends Plugin {

  override def onStart() {
    DB autoCommit { implicit session =>
      try {
        sql"select count(1) from message".map(_.long(1)).single.apply()
      } catch {
        case e: Exception =>

          sql"""
         create table message (
           id bigint primary key not null,
           server_name varchar(16),
           current_status varchar(16) not null
         )
         """.execute.apply()

          sql"""
         create table message_history (
           id bigint generated always as identity,
           message_id bigint not null,
           status varchar(16) not null,
           server_name varchar(16),
           error_message varchar(256)
         );
         """.execute.apply()
      }
    }
  }

}
