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

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn


trait AkkaHttpCrudApp extends TokenAuthorization {
  implicit val executionContext : ExecutionContextExecutor
  private val cors = new CORSHandler {}
  val userRepository : UserCredRepository
  val studentRepository : StudentRepository
  val universityRepository : UniversityRepository


    val route =
      path("user" / "create") {
        options{
          cors.corsHandler(complete(StatusCodes.OK))
        }~
          post {
            entity(as[String]) { // post body parameter
              userRoute =>
                cors.corsHandler(complete {
                  val user = parse(userRoute).extract[UserCred]
                  userRepository.create(user).map { res =>
                    res.toString
                  }
                })
            }
          }
      }~
      path("user" / "list") {
        options{
          cors.corsHandler(complete(StatusCodes.OK))
        }~
          get {
              cors.corsHandler(complete {
                userRepository.getAll().map { res =>
                  write(res)
                }
              })
            }
      }~
        path("user"/"validate") {
          options {
            cors.corsHandler(complete(StatusCodes.OK))
          } ~
            post {
              entity(as[String]) {
                userRoute =>
                  cors.corsHandler(complete {
                    val user = parse(userRoute).extract[LoginDetail]
                    userRepository.validate(user).map {
                      case Some(userInfo) =>
                        write(generateToken(userInfo.firstName, userInfo.lastName))
                      case None =>
                        write(StatusCodes.Unauthorized)
                    }
                  })
              }
            }
        }~
        path("user" / "update") {
          options{
            cors.corsHandler(complete(StatusCodes.OK))
          }~
            put {
                entity(as[String]) { // post body parameter
                  userRoute =>
                    cors.corsHandler(complete {
                      val user = parse(userRoute).extract[UserCred]
                      userRepository.update(user).map { res =>
                        res.toString
                      }
                    })
              }
            }
        }~
        path("user" / "delete") {
          parameters('id.as[Int]) { id => // URL parameter
            options {
              cors.corsHandler(complete(StatusCodes.OK))
            } ~
              delete {
                  cors.corsHandler(complete {
                    userRepository.delete(id).map { res =>
                      res.toString
                    }
                  })
              }
          }
        }~
        path("user" / "getbyid") {
          parameters('id.as[Int]) { id => // URL parameter
            options {
              cors.corsHandler(complete(StatusCodes.OK))
            } ~
              get {
                  cors.corsHandler(complete {
                    userRepository.getById(id).map { res =>
                      write(res)
                    }
                  })

              }
          }
        }~
      path("student" / "list") {
        options{
          cors.corsHandler(complete(StatusCodes.OK))
        }~
        get {
          authenticated { _ =>
            cors.corsHandler(complete {
              studentRepository.getAll().map { res =>
                write(res)
              }
            })
          }
        }
      }~
        path("student" / "create") {
          options{
            cors.corsHandler(complete(StatusCodes.OK))
          }~
          post {
            authenticated { _ =>
              entity(as[String]) { // post body parameter
                studentRoute =>
                  cors.corsHandler(complete {
                    val student = parse(studentRoute).extract[Student]
                    studentRepository.create(student).map { res =>
                      write(res)
                    }
                  })
              }
            }
          }
        }~
        path("student" / "update") {
          options{
            cors.corsHandler(complete(StatusCodes.OK))
          }~
          put {
           authenticated { _ =>
              entity(as[String]) { // post body parameter
                studentRoute =>
                  cors.corsHandler(complete {
                    val student = parse(studentRoute).extract[Student]
                    studentRepository.update(student).map { res =>
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
              authenticated { _ =>
                cors.corsHandler(complete {
                  studentRepository.delete(id).map { res =>
                    res.toString
                  }
                })
              }
            }
          }
        }~
        path("student" / "getbyid") {
          parameters('id.as[Int]) { id => // URL parameter
            options{
              cors.corsHandler(complete(StatusCodes.OK))
            }~
            get {
              authenticated { _ =>
                cors.corsHandler(complete {
                  studentRepository.getById(id).map { res =>
                    write(res)
                  }
                })
              }
            }
          }
        }~
        path("university" / "list") {
          options{
            cors.corsHandler(complete(StatusCodes.OK))
          }~
          get {
            authenticated { _ =>
              cors.corsHandler(complete {
                universityRepository.getAll().map { res =>
                  write(res)
                }
              })
            }
          }
        }~
        path("university" / "create") {
          options{
            cors.corsHandler(complete(StatusCodes.OK))
          }~
          post {
            authenticated { _ =>
              entity(as[String]) { // post body parameter
                universityRoute =>
                  cors.corsHandler(complete {
                    val university = parse(universityRoute).extract[University]
                    universityRepository.create(university).map { res =>
                      println("university " + university)
                      write(res)
                    }
                  })
              }
            }
          }
        }~
        path("university" / "update") {
          options{
            cors.corsHandler(complete(StatusCodes.OK))
          }~
          put {
           authenticated { _ =>
              entity(as[String]) { // post body parameter
                universityRoute =>
                  cors.corsHandler(complete {
                    val university = parse(universityRoute).extract[University]
                    universityRepository.update(university).map { res =>
                      res.toString
                    }
                  })
              }
            }
          }
        }~
        path("university" / "delete") {
          parameters('id.as[Int]) { id => // URL parameter
            options {
              cors.corsHandler(complete(StatusCodes.OK))
            } ~
              delete {
                authenticated { _ =>
                  cors.corsHandler(complete {
                    universityRepository.delete(id).map { res =>
                      res.toString
                    }
                  })
                }
              }
          }
        }~
        path("university" / "getbyid") {
          parameters('id.as[Int]) { id => // URL parameter
            options {
              cors.corsHandler(complete(StatusCodes.OK))
            } ~
              get {
                authenticated { _ =>
                  cors.corsHandler(complete {
                    universityRepository.getById(id).map { res =>
                      write(res)
                    }
                  })
                }
              }
          }
        }

}
object AkkaHTTP extends App with AkkaHttpCrudApp {
  val studentRepository : StudentRepository = StudentRepository
  val universityRepository : UniversityRepository = UniversityRepository
  val userRepository : UserCredRepository = UserCredRepository
  implicit val system = ActorSystem(Behaviors.empty, "Akka-HTTP-CRUD")
  implicit val executionContext:ExecutionContextExecutor = system.executionContext
    val bindingFuture = Http().newServerAt("localhost", 9000).bind(route)
    println(s"Server online at http://localhost:9000/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

}

