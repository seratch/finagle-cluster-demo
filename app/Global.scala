import play.api._

import scalikejdbc._

object Global extends GlobalSettings {

  Class.forName("org.hsqldb.jdbc.JDBCDriver")
  ConnectionPool.singleton("jdbc:hsqldb:mem:finagle_hack","sa","")

  DB autoCommit { implicit session =>

    try {
      SQL("SELECT COUNT(1) FROM MESSAGE").map(rs => rs.long(1)).single.apply()
    } catch { case e =>
      SQL("""
      CREATE TABLE MESSAGE (
        ID BIGINT PRIMARY KEY NOT NULL,
        SERVER_NAME VARCHAR(16),
        CURRENT_STATUS VARCHAR(16) NOT NULL
      )
          """).execute.apply()
      SQL("""
      CREATE TABLE MESSAGE_HISTORY (
        ID BIGINT GENERATED ALWAYS AS IDENTITY,
        MESSAGE_ID BIGINT NOT NULL,
        STATUS VARCHAR(16) NOT NULL,
        SERVER_NAME VARCHAR(16),
        ERROR_MESSAGE VARCHAR(256)
      );
          """).execute.apply()
    }

  }

}
