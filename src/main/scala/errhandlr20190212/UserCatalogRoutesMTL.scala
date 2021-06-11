package errhandlr20190212

import cats.effect.Sync
import cats.syntax.all._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class UserCatalogRoutesMTL[F[_]: Sync](
  users: UserAlg[F, UserError],
  catalog: CatalogAlg[F, CatalogError]) extends Http4sDsl[F] {

  private val httpRoutes: HttpRoutes[F] = ???

  def routes(implicit
             H1: HttpErrorHandler[F, UserError],
             H2: HttpErrorHandler[F, CatalogError]): HttpRoutes[F] =
    H2.handle(H1.handle(httpRoutes))

}
