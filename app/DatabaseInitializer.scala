import play.api._
import scalikejdbc._
import scalikejdbc.SQLInterpolation._

class DatabaseInitializer(app: Application) extends Plugin {

  override def onStart() {
    DB autoCommit { implicit session =>
      try {
        sql"SELECT COUNT(1) FROM MESSAGE".map(_.long(1)).single.apply()
      } catch {
        case e: Exception =>

          sql"""
         CREATE TABLE MESSAGE (
           ID BIGINT PRIMARY KEY NOT NULL,
           SERVER_NAME VARCHAR(16),
           CURRENT_STATUS VARCHAR(16) NOT NULL
         );
         """.execute.apply()

          sql"""
         CREATE TABLE MESSAGE_HISTORY (
           ID BIGINT GENERATED ALWAYS AS IDENTITY,
           MESSAGE_ID BIGINT NOT NULL,
           STATUS VARCHAR(16) NOT NULL,
           SERVER_NAME VARCHAR(16),
           ERROR_MESSAGE VARCHAR(256)
         );
         """.execute.apply()
      }
    }
  }

}
