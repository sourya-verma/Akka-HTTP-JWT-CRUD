package com.techsophy.service



import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.http.scaladsl.server._
import Directives._
import models.{LoginDetail, Student, University, UserCred}
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import repositories.{StudentRepository, UniversityRepository, UserCredRepository}
import service.AkkaHTTP.system
import service.AkkaHttpCrudApp
import utils.JsonFormat.write

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.FiniteDuration

class AkkaHttpDemoSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest with AkkaHttpCrudApp with MockitoSugar{
  val studentRepository = mock[StudentRepository]
  val userRepository = mock[UserCredRepository]
  val universityRepository = mock[UniversityRepository]

  implicit val routeTestTimeout = RouteTestTimeout(FiniteDuration(12,"seconds"))
  override implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val tokenValidate = Map("all"->"allow")
  override def authenticated: Directive1[Map[String, Any]] = provide(tokenValidate)
  "The service" should {

      "user login requests (POST, validate)" in {
        // tests:
        val login = LoginDetail(123454, "1545423546gjygvky")
        when(userRepository.validate(login)) thenReturn Future.successful(Some(UserCred(12345, "sourya", "kumar", "1ngfcjc23")))
        Post("/user/validate", write(login)) ~> route ~> check {
          status should ===(StatusCodes.OK)
          responseAs[String] shouldEqual """"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJzdE5hbWUiOiJzb3VyeWEiLCJsYXN0TmFtZSI6Imt1bWFyIn0.25wZecPzu6Ykh80M9fPK_Y6U4BmZBUOxk0gY_yNmfJw""""
        }
      }

    //----------------------------------------
    "user create requests (POST, create)" in {
      // tests:
      val user = UserCred(12345,"sourya", "sourya","123")
      when(userRepository.create(user)) thenReturn Future.successful(1)
      Post("/user/create", write(user)) ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """1"""
      }
    }

    "user read request(GET, getAll)" in {
      // tests:
      val users = List(UserCred(12345,"sourya", "kumar","123"))
      when(userRepository.getAll()) thenReturn ( Future.successful((users)))
      Get("/user/list") ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """[{"id":12345,"firstName":"sourya","lastName":"kumar","password":"123"}]"""

      }
    }

    "user read by id request(GET, getById)" in {
      // tests:
      val user = UserCred(12345,"sourya", "kumar","123")
      val id:Int = 12345
      when(userRepository.getById(id)) thenReturn ( Future.successful((Some(user))))
      Get("/user/getbyid?id="+id) ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """{"id":12345,"firstName":"sourya","lastName":"kumar","password":"123"}"""

      }
    }

    "user update requests (PUT, update)" in {
      // tests:
      val user = UserCred(12345,"sourya", "kumar","123")
      when(userRepository.update(user)) thenReturn Future.successful(1)
      Put("/user/update", write(user)) ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """1"""
      }
    }

    "user delete request(DELETE, delete)" in {
      // tests:
      val id:Int = 22
      when(userRepository.delete(id)) thenReturn ( Future.successful((1)))
      Delete("/user/delete?id="+id) ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """1"""

      }
    }

    //--------------------------------------

    "student create requests (POST, create)" in {
      // tests:
      val student = Student("Sourya","sourya.v@techsophy.com",101,Some(1))
      when(studentRepository.create(student)) thenReturn Future.successful(Some(2))
      Post("/student/create", write(student)) ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """2"""
      }
    }

    "student read request(GET, getAll)" in {
      // tests:
      val students = List(Student("Saurya","saurya@gmauks",21,Some(22)))
      when(studentRepository.getAll()) thenReturn ( Future.successful((students)))
      Get("/student/list") ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """[{"name":"Saurya","email":"saurya@gmauks","universityId":21,"id":22}]"""

      }
    }

    "student read by id request(GET, getById)" in {
      // tests:
      val student = Student("Saurya","saurya@gmauks",21,Some(22))
      val id:Int = 22
      when(studentRepository.getById(id)) thenReturn ( Future.successful((Some(student))))
      Get("/student/getbyid?id="+id) ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """{"name":"Saurya","email":"saurya@gmauks","universityId":21,"id":22}"""

      }
    }

    "student update requests (PUT, update)" in {
      // tests:
      val student = Student("Sourya","sourya.v@techsophy.com",101,Some(1))
      when(studentRepository.update(student)) thenReturn Future.successful(1)
      Put("/student/update", write(student)) ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """1"""
      }
    }

    "student delete request(DELETE, delete)" in {
      // tests:
//      val student = Student("Saurya","saurya@gmauks",21,Some(22))
      val id:Int = 22
      when(studentRepository.delete(id)) thenReturn ( Future.successful((1)))
      Delete("/student/delete?id="+id) ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """1"""

      }
    }


    "university create requests (POST, create)" in {
      // tests:
      val university = University("HCU","Hyderabad",Some(101))
      when(universityRepository.create(university)) thenReturn Future.successful(Some(102))
      Post("/university/create", write(university)) ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """102"""
      }
    }

    "university read request(GET, getAll)" in {
      // tests:
      val universities = List(University("HCU","Hyderabad",Some(101)))
      when(universityRepository.getAll()) thenReturn ( Future.successful((universities)))
      Get("/university/list") ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """[{"name":"HCU","location":"Hyderabad","id":101}]"""

      }
    }

    "university read by id request(GET, getById)" in {
      // tests:
      val university = University("HCU","Hyderabad",Some(101))
      val id:Int = 101
      when(universityRepository.getById(id)) thenReturn ( Future.successful((Some(university))))
      Get("/university/getbyid?id="+id) ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """{"name":"HCU","location":"Hyderabad","id":101}"""

      }
    }

    "university update requests (PUT, update)" in {
      // tests:
      val university = University("HCU","Hyderabad",Some(101))
      when(universityRepository.update(university)) thenReturn Future.successful(1)
      Put("/university/update", write(university)) ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """1"""
      }
    }

    "university delete request(DELETE, delete)" in {
      // tests:
      val id:Int = 22
      when(universityRepository.delete(id)) thenReturn ( Future.successful((1)))
      Delete("/university/delete?id="+id) ~> route ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual """1"""

      }
    }



    }

}