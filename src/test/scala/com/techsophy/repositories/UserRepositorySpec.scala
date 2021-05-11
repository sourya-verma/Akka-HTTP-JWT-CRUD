package com.techsophy.repositories

import com.techsophy.connection.H2DBComponent
import org.scalatest._
import funsuite._
import models.{Student, UserCred}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import repositories.UserCredRepository



class UserRepositorySpec extends AnyFunSuite with UserCredRepository with H2DBComponent with ScalaFutures {

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  test("create on user repo") {
    val response = create(UserCred(32345, "sourya", "kumar", "123"))
    whenReady(response) { id =>
      assert(id === 1)
    }
  }

  test("read on user repo") {
    val response = getAll()
    whenReady(response) { list =>
      assert(list === List(UserCred(12345, "sourya", "kumar", "123"),UserCred(22345,"aditya","kumar", "234")))
    }
  }

//
//
//  test("validate on user repo") {
//    val response = getById(3)
//    whenReady(response) { student =>
//      assert(student === Some(Student("dharma", "d@abc", 101, Some(3))))
//    }
//  }
//
//  test("update on student table") {
//    val response = update(Student("dharma", "d@abc", 101, Some(2)))
//    whenReady(response) { id =>
//      assert(id === 1)
//    }
//  }
//
//  test("delete on student table") {
//    val response = delete(1)
//    whenReady(response) { id =>
//      assert(id === 1)
//    }
//  }

}