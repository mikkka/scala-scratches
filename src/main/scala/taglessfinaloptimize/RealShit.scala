package taglessfinaloptimize

import cats._
import cats.data.Const
import cats.instances.all._
import cats.syntax.all._

object RealShit {
  trait Program[Alg[_[_]], A] {
    def apply[F[_]: Applicative](interpreter: Alg[F]) : F[A]
  }

  def optimize[Alg[_[_]], F[_]: Applicative, A, M: Monoid]
  (program: Program[Alg, A])
  (extract: Alg[Const[M, ?]])
  (restructure: M => F[A]): Alg[F] => F[A] = { interp =>
    val m = program(extract).getConst

    restructure(m)
  }
}
