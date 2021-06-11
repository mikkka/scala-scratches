package catsmeow20181002
import cats.effect.{ExitCode, IO, IOApp}

import cats.syntax.all._
import com.olegpy.meow.hierarchy._

import org.http4s.implicits._
import org.http4s.server.blaze._

object App extends IOApp {
  implicit val userHttpErrorHandler: HttpErrorHandler[IO, UserError] = new UserHttpErrorHandler[IO]

  val routesMtl = UserInterpreter.create[IO].map { UserAlgebra =>
    new UserRoutesMTL[IO](UserAlgebra).routes
  }

  override def run(args: List[String]): IO[ExitCode] = {
    routesMtl.flatMap { r =>
      BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(r.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
    }
  }
}