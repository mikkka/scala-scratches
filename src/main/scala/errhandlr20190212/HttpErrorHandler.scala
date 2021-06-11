package errhandlr20190212

import cats.ApplicativeError
import cats.data.{Kleisli, OptionT}
import org.http4s._
import cats.syntax.all._

trait HttpErrorHandler[F[_], E <: Throwable] {
  def handle(routes: HttpRoutes[F]): HttpRoutes[F]
}

object RoutesHttpErrorHandler {
  def apply[F[_], E <: Throwable](routes: HttpRoutes[F])(
    handler: E => F[Response[F]])(implicit ev: ApplicativeError[F, E]): HttpRoutes[F] =
    Kleisli { req: Request[F] =>
      OptionT {
        routes.run(req).value.handleErrorWith { e => handler(e).map(Option(_)) }
      }
    }
}

object HttpErrorHandler {
  def apply[F[_], E <: Throwable](implicit ev: HttpErrorHandler[F, E]) = ev

  def mkInstance[F[_], E <: Throwable](
    handler: E => F[Response[F]]
  )(implicit ev: ApplicativeError[F, E]): HttpErrorHandler[F, E] =
    (routes: HttpRoutes[F]) => RoutesHttpErrorHandler(routes)(handler)
}