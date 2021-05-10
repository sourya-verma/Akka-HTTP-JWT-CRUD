package connection


import slick.jdbc.MySQLProfile

trait MySqlDBComponent extends DBComponent {

  val driver = MySQLProfile

  import driver.api._

  val db: Database = MySqlDB.connectionPool

}

private[connection] object MySqlDB {

  import slick.jdbc.MySQLProfile.api._

  val connectionPool = Database.forConfig("mysql")

}