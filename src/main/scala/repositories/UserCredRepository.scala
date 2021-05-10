package repositories



import connection.{DBComponent, MySqlDBComponent}
import models.{LoginDetail, UserCred}

import java.sql.Date
import scala.concurrent.Future

trait UserCredRepository extends UserCredTable {
  this: DBComponent =>

  import driver.api._

  def create(user: UserCred): Future[Int] = db.run {
    userCredTableQuery += user
  }

  def validate(user: LoginDetail) =  db.run{
    userCredTableQuery.filter(usr=> (usr.id === user.id && usr.password=== user.password)).result.headOption
  }

  def update(user: UserCred): Future[Int] = db.run {
    userCredTableQuery.filter(_.id === user.id).update(user)
  }

  def getById(id: Int): Future[Option[UserCred]] = db.run {
    userCredTableQuery.filter(_.id === id).result.headOption
  }


  def getAll(): Future[List[UserCred]] = db.run {
    userCredTableQuery.to[List].result
  }


  def delete(id: Int): Future[Int] = db.run {
    userCredTableQuery.filter(_.id === id).delete
  }
}

private[repositories] trait UserCredTable {
  this: DBComponent =>

  import driver.api._

  protected val userCredTableQuery = TableQuery[UserCredTable]


  private[UserCredTable] class UserCredTable(tag: Tag) extends Table[UserCred](tag, "user_cred") {
    val id = column[Int]("id", O.PrimaryKey)
    val firstName:Rep[String] = column[String]("fname")
    val lastName:Rep[String] = column[String]("lname")
    val password:Rep[String] = column[String]("password")

    def * = (id, firstName, lastName, password).mapTo[UserCred]

  }

}

object UserCredRepository extends UserCredRepository with MySqlDBComponent


