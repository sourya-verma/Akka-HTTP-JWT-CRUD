package service

/*
 * Copyright (C) 2020-2021 Lightbend Inc. <https://www.lightbend.com>
 */

import utils.JsonFormat._
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import models.{LoginDetail, Student, University, UserCred}
import repositories.{StudentRepository, UniversityRepository, UserCredRepository}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.directives.RouteDirectives.complete

import utils.TokenAuthorization

import scala.io.StdIn


object AkkaHttpDemo {

//  // needed to run the route
  implicit val system = ActorSystem(Behaviors.empty, "Akka-HTTP-CRUD")
//  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.executionContext
  private val cors = new CORSHandler {}

  def main(args: Array[String]): Unit = {
    val route = {
      path("user" / "create") {
        options{
          cors.corsHandler(complete(StatusCodes.OK))
        }~
          post {
            entity(as[String]) { // post body parameter
              userRoute =>
                cors.corsHandler(complete {
                  val user = parse(userRoute).extract[UserCred]
                  UserCredRepository.create(user).map { res =>
                    res.toString
                  }
                })
            }
          }
      } ~
      path("user" / "list") {
        options{
          cors.corsHandler(complete(StatusCodes.OK))
        }~
          get {
              cors.corsHandler(complete {
                UserCredRepository.getAll().map { res =>
                  write(res)
                }
              })
            }
      } ~
        path("user"/"validate") {
          options{
            cors.corsHandler(complete(StatusCodes.OK))
          }~
          post {
            entity(as[String]) { loginRequest =>
              val user = parse(loginRequest).extract[LoginDetail]
              if (UserCredRepository.validate(user) != null) {
                val token = TokenAuthorization.generateToken(user.id, user.password)
                cors.corsHandler(complete((StatusCodes.OK, token)))
              } else {
                cors.corsHandler(complete(StatusCodes.Unauthorized))
              }
            }
          }
        }~
      path("student" / "list") {
        options{
          cors.corsHandler(complete(StatusCodes.OK))
        }~
        get {
          TokenAuthorization.authenticated { _ =>
            cors.corsHandler(complete {
              StudentRepository.getAll().map { res =>
                write(res)
              }
            })
          }
        }
      } ~
        path("student" / "create") {
          options{
            cors.corsHandler(complete(StatusCodes.OK))
          }~
          post {
            TokenAuthorization.authenticated { _ =>
              entity(as[String]) { // post body parameter
                studentRoute =>
                  cors.corsHandler(complete {
                    val student = parse(studentRoute).extract[Student]
                    StudentRepository.create(student).map { res =>
                      write(res)
                    }
                  })
              }
            }
          }
        } ~
        path("student" / "update") {
          options{
            cors.corsHandler(complete(StatusCodes.OK))
          }~
          put {
            TokenAuthorization.authenticated { _ =>
              entity(as[String]) { // post body parameter
                studentRoute =>
                  cors.corsHandler(complete {
                    val student = parse(studentRoute).extract[Student]
                    StudentRepository.update(student).map { res =>
                      res.toString
                    }
                  })
              }
            }
          }
        }~
        path("student" / "delete") {
          parameters('id.as[Int]) { id => // URL parameter
            options{
              cors.corsHandler(complete(StatusCodes.OK))
            }~
            delete {
              TokenAuthorization.authenticated { _ =>
                cors.corsHandler(complete {
                  StudentRepository.delete(id).map { res =>
                    res.toString
                  }
                })
              }
            }
          }
        } ~
        path("student" / "getbyid") {
          parameters('id.as[Int]) { id => // URL parameter
            options{
              cors.corsHandler(complete(StatusCodes.OK))
            }~
            get {
              TokenAuthorization.authenticated { _ =>
                cors.corsHandler(complete {
                  StudentRepository.getById(id).map { res =>
                    res.toString
                  }
                })
              }
            }
          }
        } ~
        path("university" / "list") {
          options{
            cors.corsHandler(complete(StatusCodes.OK))
          }~
          get {
            TokenAuthorization.authenticated { _ =>
              cors.corsHandler(complete {
                UniversityRepository.getAll().map { res =>
                  write(res)
                }
              })
            }
          }
        } ~
        path("university" / "create") {
          options{
            cors.corsHandler(complete(StatusCodes.OK))
          }~
          post {
            TokenAuthorization.authenticated { _ =>
              entity(as[String]) { // post body parameter
                universityRoute =>
                  cors.corsHandler(complete {
                    val university = parse(universityRoute).extract[University]
                    UniversityRepository.create(university).map { res =>
                      println("university " + university)
                      write(res)
                    }
                  })
              }
            }
          }
        } ~
        path("university" / "update") {
          options{
            cors.corsHandler(complete(StatusCodes.OK))
          }~
          put {
            TokenAuthorization.authenticated { _ =>
              entity(as[String]) { // post body parameter
                universityRoute =>
                  cors.corsHandler(complete {
                    val university = parse(universityRoute).extract[University]
                    UniversityRepository.update(university).map { res =>
                      res.toString
                    }
                  })
              }
            }
          }
        } ~
        path("university" / "delete") {
          parameters('id.as[Int]) { id => // URL parameter
            options {
              cors.corsHandler(complete(StatusCodes.OK))
            } ~
              delete {
                TokenAuthorization.authenticated { _ =>
                  cors.corsHandler(complete {
                    UniversityRepository.delete(id).map { res =>
                      res.toString
                    }
                  })
                }
              }
          }
        } ~
        path("university" / "getbyid") {
          parameters('id.as[Int]) { id => // URL parameter
            options{
              cors.corsHandler(complete(StatusCodes.OK))
            }~
            get {
              TokenAuthorization.authenticated { _ =>
                cors.corsHandler(complete {
                  UniversityRepository.getById(id).map { res =>
                    write(res)
                  }
                })
              }
            }
          }
        }
    }

    val bindingFuture = Http().newServerAt("localhost", 9000).bind(route)
    println(s"Server online at http://localhost:9000/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}

