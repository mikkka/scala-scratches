package app20171107

trait Functor[F[_]] {
  def map[A, B](f: A => B)(fa: F[A]): F[B]
}
trait Applicative[F[_]] extends Functor[F] {
  def pure[A](a: A): F[A]
  def ap[A, B](ff: F[A => B], fa: F[A]): F[B]

  def map2[A, B, R](f: (A, B) => R)(fa: F[A], fb: F[B]): F[R] = {
    ap(map(f.curried)(fa), fb)
  }

  def map3[A, B, C, R](f: (A, B, C) => R)(fa: F[A], fb: F[B], fc: F[C]): F[R] = {
    ap(ap(map(f.curried)(fa), fb), fc)
  }

  def map4[A, B, C, D, R](f: (A, B, C, D) => R)(fa: F[A], fb: F[B], fc: F[C], fd: F[D]): F[R] = {
    ap(ap(ap(map(f.curried)(fa), fb), fc), fd)
  }
}