package utils

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}
import akka.http.scaladsl.server.Directives.{optionalHeaderValueByName, provide, reject}
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}

trait TokenAuthorization {

  private val secretKey = "super_secret_key"
  private val header = JwtHeader("HS256")

  def generateToken(fname: String, lname: String): String = {
    val claims = JwtClaimsSet(
      Map(
        "firstName" -> fname,
        "lastName" -> lname
      )
    )
    JsonWebToken(header, claims, secretKey)
  }

  def isEmpty(x: String) = x == null || x.trim.isEmpty


  def authenticated: Directive1[Map[String, Any]] = {

    optionalHeaderValueByName("Authorization").flatMap { tokenFromUser =>
      tokenFromUser match {
        case None => complete(StatusCodes.Unauthorized -> "Token Missing")
        case _=> {
          val jwtToken = tokenFromUser.get.split(" ")

          jwtToken(1) match {
            case token if isTokenExpired(token) => {
              println("inside expire token")
              complete(StatusCodes.Unauthorized -> "Session expired.")
            }

            case token if JsonWebToken.validate(token, secretKey) => {
              println("inside token passed")
              provide(getClaims(token))
            }
            case _ => {
              complete(StatusCodes.Unauthorized -> "Invalid Token")
            }
          }

        }
      }

    }
  }

  private def isTokenExpired(jwt: String): Boolean =
    getClaims(jwt).get("expiredAt").exists(_.toLong < System.currentTimeMillis())

  private def getClaims(jwt: String): Map[String, String] =
    JsonWebToken.unapply(jwt) match {
      case Some(value) => value._2.asSimpleMap.getOrElse(Map.empty[String, String])
      case None => Map.empty[String, String]

    }


}

