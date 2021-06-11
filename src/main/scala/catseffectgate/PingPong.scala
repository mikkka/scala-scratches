package catseffectgate
import cats.Monad
import cats.effect.concurrent.MVar
import cats.effect._
import cats.effect.syntax.all._
import cats.implicits._
import scala.concurrent.duration._

object PingPong extends IOApp {
  type Push[A, F[_]] = A => F[Unit]

  def ping[F[_]: Console: Monad](inlet: F[Int], outlet: Push[Int, F])(
      implicit timer: Timer[F]): F[Unit] =
    for {
      message <- inlet
      _ <- Console[F].println(s"ping $message")
      _ <- timer.sleep(0.5 seconds)
      _ <- outlet(message + 1)
      _ <- ping[F](inlet, outlet)
    } yield ()

  def pong[F[_]: Console: Monad](inlet: F[Int], outlet: Push[Int, F])(
      implicit timer: Timer[F]): F[Unit] =
    for {
      message <- inlet
      _ <- Console[F].println(s"pong $message")
      _ <- timer.sleep(0.5 seconds)
      _ <- outlet(message + 1)
      _ <- pong[F](inlet, outlet)
    } yield ()

  def pingPong[F[_]: Concurrent: Console: Timer]: F[Unit] =
    for {
      ping2pong <- MVar.empty[F, Int]
      pong2ping <- MVar.empty[F, Int]
      pingFiber <- ping(ping2pong.take, pong2ping.put).start
      pongFiber <- pong(pong2ping.take, ping2pong.put).start
      _ <- pong2ping.put(0)
      _ <- pingFiber.join *> pongFiber.join
    } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    pingPong[IO] as ExitCode.Success
}

trait Console[F[_]] {
  def println(s: String): F[Unit]
}

object Console {
  def apply[F[_]](implicit console: Console[F]): Console[F] = console

  implicit def syncConsole[F[_]](implicit F: Sync[F]): Console[F] =
    s => F.delay(println(s))
}
