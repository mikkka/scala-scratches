package catsmeow20181002

import cats.MonadError
import cats.effect.{ExitCode, IO, IOApp}
import com.olegpy.meow.hierarchy._

import scala.util.Random
import cats.syntax.all._

object meow extends IOApp {
  case class CustomError(msg: String) extends Throwable

  def customHandle[F[_], A](f: F[A], fallback: F[A])(implicit ev: MonadError[F, CustomError]): F[A] =
    f.handleErrorWith(_ => fallback)

  val io: IO[Int] = IO(Random.nextInt(2)).flatMap {
    case 0 => IO.raiseError(CustomError("custo"))
    case 1 => IO.raiseError(new Exception("boom"))
  }

  override def run(args: List[String]): IO[ExitCode] = {
    customHandle(io, IO.pure(123)).as(ExitCode.Success)
  }
}
