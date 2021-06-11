package eio

import cats.MonadError
import cats.effect.IO
import cats.syntax.applicativeError._
import cats.syntax.functor._
import cats.syntax.monadError._

import scala.reflect.ClassTag

object EIO {
  type EIO[E <: Throwable, A] = IO[A] {
    type Error = E
  }

  /*
    def io[E <: Throwable]: EIOPA[E] = new EIOPA[E]

    class EIOPA[E <: Throwable] {
      def apply[A](io: IO[A]): EIO[E, A] =  io.asInstanceOf[EIO[E, A]]
    }
  */

  def apply[E <: Throwable, A](io: IO[A]): EIO[E, A] =  io.asInstanceOf[EIO[E, A]]

  implicit def instance[E <: Throwable : ClassTag]: MonadError[EIO[E, ?], E] =
    new MonadError[EIO[E, ?], E] {
      private val ioMonad: MonadError[IO, Throwable] = MonadError[IO, Throwable]

      override def flatMap[A, B](fa: EIO[E, A])(f: A => EIO[E, B]) = EIO(fa.flatMap(f))
      override def tailRecM[A, B](a: A)(f: A => EIO[E, Either[A, B]]) = EIO(ioMonad.tailRecM(a)(f))
      override def raiseError[A](e: E) = EIO(ioMonad.raiseError(e))
      override def handleErrorWith[A](fa: EIO[E, A])(f: E => EIO[E, A]) =
        EIO((fa: IO[A]).recoverWith { case e: E => f(e) })
      override def pure[A](x: A) = EIO(IO.pure(x))
    }
}

object EIORun {
  import EIO.instance
  sealed trait MyError extends Exception with Product with Serializable{
    override def toString = productPrefix
  }
  case object TooSmall extends MyError
  case object TooBig extends MyError

  def runShit[F[_]](fio: F[Int])(implicit F: MonadError[F, MyError]): F[String] =
    fio.ensure(TooSmall)(_ >= 10)
      .ensure(TooBig)(_ <= 100)
      .map(_.toString)

  def main(args: Array[String]): Unit = {
    /*
        for(i <- List(1, 10, 100, 1000))
          println(runShit(EIO[MyError,Int](IO.pure(i))).attemptT.value.unsafeRunSync())
    */

    for(i <- List(1, 10, 100, 1000))
      println(runShit(EIO[MyError,Int](IO.raiseError(new RuntimeException("olo")))).attemptT.value.unsafeRunSync())
  }
}
