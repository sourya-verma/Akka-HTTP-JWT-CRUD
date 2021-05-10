package repositories

import connection.{DBComponent, MySqlDBComponent}
import models.Student

import java.sql.Date
import scala.concurrent.Future

trait StudentRepository extends StudentTable {
  this: DBComponent =>

  import driver.api._

  def create(student: Student): Future[Option[Int]] = db.run {
    (studentTableQuery returning studentTableQuery.map(_.id)) += student
  }


  def update(student: Student): Future[Int] = db.run {
    studentTableQuery.filter(_.id === student.id.get).update(student)
  }

  def getById(id: Int): Future[Option[Student]] = db.run {
    studentTableQuery.filter(_.id === id).result.headOption
  }


  def getAll(): Future[List[Student]] = db.run {
    studentTableQuery.to[List].result
  }


  def delete(id: Int): Future[Int] = db.run {
    studentTableQuery.filter(_.id === id).delete
  }
}

private[repositories] trait StudentTable {
  this: DBComponent =>

  import driver.api._

  protected val studentTableQuery = TableQuery[StudentTable]


  private[StudentTable] class StudentTable(tag: Tag) extends Table[Student](tag, "student") {
    val id:Rep[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    val name:Rep[String] = column[String]("name")
    val email:Rep[String] = column[String]("email")
    val universityId:Rep[Int] = column[Int]("university_id")
//    val dob:Rep[Date] = column[Date]("date_of_birth")

    def * = (name, email, universityId, id).mapTo[Student]

  }

}

object StudentRepository extends StudentRepository with MySqlDBComponent


