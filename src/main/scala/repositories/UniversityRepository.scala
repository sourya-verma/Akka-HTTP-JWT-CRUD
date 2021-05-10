package repositories

import connection.{DBComponent, MySqlDBComponent}
import models.University

import scala.concurrent.Future

trait UniversityRepository extends UniversityTable {
  this: DBComponent =>

  import driver.api._

  def create(university: University): Future[Option[Int]] = db.run {
    (universityTableQuery returning universityTableQuery.map(_.id)) += university
  }


  def update(university: University): Future[Int] = db.run {
    universityTableQuery.filter(_.id === university.id.get).update(university)
  }

  def getById(id: Int): Future[Option[University]] = db.run {
    universityTableQuery.filter(_.id === id).result.headOption
  }


  def getAll(): Future[List[University]] = db.run {
    universityTableQuery.to[List].result
  }


  def delete(id: Int): Future[Int] = db.run {
    universityTableQuery.filter(_.id === id).delete
  }

//
//  def getUniversityStudentCount() = {
//    val ans = (for {
//      (s,u) <- studentTableQuery join(universityTableQuery) on(_.universityId === _.id)
//    } yield (s,u)).groupBy(_._2.name).map{
//      case (university, data) => (university, data.map(_._1.universityId).length)
//    }
//    ans.to[List].result
//  }
//

}

private[repositories] trait UniversityTable {
  this: DBComponent =>

  import driver.api._

  protected val universityTableQuery = TableQuery[UniversityTable]


  private[UniversityTable] class UniversityTable(tag: Tag) extends Table[University](tag, "university") {
    val id:Rep[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    val name:Rep[String] = column[String]("university_name")
    val location:Rep[String] = column[String]("location")

    def * = (name, location, id).mapTo[University]

  }

}

object UniversityRepository extends UniversityRepository with MySqlDBComponent


