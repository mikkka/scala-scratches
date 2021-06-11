package gotsome20210531

import cats.syntax.all._
import cats.instances.list._
import cats.Monad
import cats.Applicative

object tf_prg {
  import domain._
  import ops._
  import typeclass._

  implicit val fromOdmenToUser: Admin => User = _.asUser

  class AdminPrg[F[_]: Monad](
    userOps: UserOps[F],
    adminOps: AdminOps[F]
  )(implicit val adm: F GotSome Admin) {
    def prg(id: String): F[Unit] = 
      userOps.read(id) >>= {
        s => adminOps.update(s)}
  }

  class UserPrg[F[_]: Applicative](
    userOps: UserOps[F]
  )(implicit usr: F GotSome User) {
    def prg(id: List[String]): F[List[Suslik]] = 
      id.traverse(userOps.read)
  }


  object make {
    def admin[F[_]: Monad](implicit adm: F GotSome Admin): AdminPrg[F] = 
      new AdminPrg[F](UserOps[F], AdminOps[F])

    def user[F[_]: Monad](implicit usr: F GotSome User): UserPrg[F] =
      new UserPrg[F](UserOps[F])
  }
}