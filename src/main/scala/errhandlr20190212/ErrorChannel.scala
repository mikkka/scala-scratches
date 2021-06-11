package errhandlr20190212

trait ErrorChannel[F[_], E <: Throwable] {
  def raise[A](e: E): F[A]
}

import cats.ApplicativeError

object ErrorChannel {
  def apply[F[_], E <: Throwable](implicit ev: ErrorChannel[F, E]): ErrorChannel[F, E] = ev

  implicit def instance[F[_], E <: Throwable](implicit F: ApplicativeError[F, Throwable]): ErrorChannel[F, E] =
    new ErrorChannel[F, E] {
      override def raise[A](e: E): F[A] = F.raiseError(e)
    }

  object syntax {
    implicit class ErrorChannelOps[F[_], E <: Throwable](e: E)(implicit val ch: ErrorChannel[F, E]) {
      def raise[A]: F[A] = ErrorChannel[F, E].raise[A](e)
    }
  }
}