package errhandlr20190212

final case class User(username: String, age: Int)
final case class UserUpdateAge(age: Int)

abstract class UserAlg[F[_]: ErrorChannel[?[_], E], E <: Throwable] {
  def find(username: String): F[Option[User]]
  def save(user: User): F[Unit]
  def updateAge(username: String, age: Int): F[Unit]
}

sealed trait UserError extends Exception
final case class UserAlreadyExists(username: String) extends UserError
final case class UserNotFound(username: String) extends UserError
final case class InvalidUserAge(age: Int) extends UserError