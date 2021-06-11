package errhandlr20190212

import cats.Monad
import cats.effect.{Concurrent, Sync}
import cats.effect.concurrent.Ref
import cats.syntax.all._

trait RefMaker[F[_]] {
  def of[A](a: A): F[Ref[F,A]]
}

object RefMaker {
  def apply[F[_]](implicit ev: RefMaker[F]): RefMaker[F] = ev

  implicit def fromSync[F[_]: Sync]: RefMaker[F] = new RefMaker[F] {
    override def of[A](a: A): F[Ref[F, A]] = Ref.of[F, A](a)
  }
}

object UserInterpreter {
  def mkUserAlg[F[_]: RefMaker: Monad](
    implicit error: ErrorChannel[F, UserError]
  ): F[UserAlg[F, UserError]] =
    RefMaker[F].of[Map[String, User]](Map.empty).map { state =>
      new UserAlg[F, UserError] {
        private def validateAge(age: Int): F[Unit] =
          if (age <= 0) error.raise(InvalidUserAge(age)) else ().pure[F]

        override def find(username: String): F[Option[User]] =
          state.get.map(_.get(username))

        override def save(user: User): F[Unit] =
          validateAge(user.age) *>
            find(user.username).flatMap {
              case Some(_) =>
                error.raise(UserAlreadyExists(user.username))
              //  error.raise(new Exception("asd"))
              //  Does not compile

              //  Sync[F].raiseError(new Exception(""))
              //  Should be considered an unrecoverable failure
              //  And it can be hidden
              case None =>
                state.update(_.updated(user.username, user))
            }

        override def updateAge(username: String, age: Int): F[Unit] =
          validateAge(age) *>
            find(username).flatMap {
              case Some(user) =>
                state.update(_.updated(username, user.copy(age = age)))
              case None =>
                error.raise(UserNotFound(username))
            }
      }
    }
}
