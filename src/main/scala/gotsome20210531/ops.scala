package gotsome20210531

import typeclass._
import domain._

import cats.syntax.all._
import cats.Applicative

object ops {
  trait AdminOps[F[_]] {
    def update(id: Suslik)(implicit adm: F GotSome Admin): F[Unit]
  }

  object AdminOps {
    def apply[F[_] : Applicative]: AdminOps[F] = new AdminOps[F] {
      def update(id: Suslik)(implicit adm: GotSome[F,Admin]): F[Unit] = ().pure[F]
    }
  }

  trait UserOps[F[_]] {
    def read(id: String)(implicit user: F GotSome User): F[Suslik]
  }

  object UserOps {
    def apply[F[_] : Applicative]: UserOps[F] = new UserOps[F] {
      def read(id: String)(implicit user: GotSome[F,User]): F[Suslik] = Suslik(s"suslik $id").pure[F]
    }
  }
}