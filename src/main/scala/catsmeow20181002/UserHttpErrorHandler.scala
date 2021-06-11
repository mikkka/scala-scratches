package catsmeow20181002

import cats.MonadError

import io.circe.syntax._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl


class UserHttpErrorHandler[F[_]](implicit M: MonadError[F, UserError]) extends HttpErrorHandler[F, UserError] with Http4sDsl[F] {
  private val handler: UserError => F[Response[F]] = {
    case InvalidUserAge(age) => BadRequest(s"Invalid age $age".asJson)
    case UserAlreadyExists(username) => Conflict(username.asJson)
    case UserNotFound(username) => NotFound(username.asJson)
  }

  override def handle(routes: HttpRoutes[F]): HttpRoutes[F] =
    RoutesHttpErrorHandler(routes)(handler)
}