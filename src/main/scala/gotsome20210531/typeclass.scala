package gotsome20210531

import scala.languageFeature.implicitConversions
import cats.Functor
import cats.syntax.functor._

object typeclass {
  trait GotSome[F[_], T] {
    def get: F[T]
  }  

  object GotSome {
    implicit def transformGotsSomes[F[_]: Functor, A1, A2](
      implicit 
        gotA1: GotSome[F, A1], 
        func: A1 => A2): GotSome[F, A2] = new GotSome[F, A2] {
          def get: F[A2] = gotA1.get.map(func)
        }
  }
}