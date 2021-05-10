package com.techsophy.repositories


import com.techsophy.connection.H2DBComponent
import org.scalatest._
import funsuite._
import models.University
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import repositories.UniversityRepository

import java.sql.Date


class UniversitySpec extends AnyFunSuite with UniversityRepository with H2DBComponent with ScalaFutures {

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))


  test("create on university table") {
    val response = create(University("hcu","hyderabad", Some(101)))
    whenReady(response) { id =>
      assert(id.get === 3)
    }
  }

  test("read on university table") {
    val response = getAll()
    whenReady(response) { result =>
      assert(result === List(University("hcu","hyderabad", Some(101)), University("jnu","delhi", Some(102))))
    }
  }



  test("getById on university table") {
    val response = getById(102)
    whenReady(response) { result =>
      assert(result.get === University("jnu","delhi", Some(102)))
    }
  }





  test("update on university table") {
    val response = update(University("nit","delhi", Some(102)))
    whenReady(response) { id =>
      assert(id === 1)
    }
  }

  test("delete on university table") {
    val response = delete(101)
    whenReady(response) { id =>
      assert(id === 1)
    }
  }


}
