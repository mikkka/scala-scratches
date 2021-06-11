package catseffectgate


import cats.Monad
import cats.effect.concurrent.MVar
import cats.effect._
import cats.effect.syntax.all._
import cats.implicits._

object Gates extends IOApp {
  def memo[I, T, F[_]](
    f: I => F[T],
    store: MVar[F, Map[I, MVar[F, T]]]
  )(implicit M: Monad[F], conc: Concurrent[F]): I => F[T] =
    {in: I =>
      for {
        map <- store.take
        res <- if (map.isDefinedAt(in))
                 for {
                   _ <- store.put(map)
                   stored <- map(in).read
                 } yield stored
               else for {
                 cell       <- MVar.empty[F, T]
                 _          <- store.put(map + (in -> cell))
//                 fiber      <- f(in).start
//                 calculated <- fiber.join
                 calculated <- f(in)
                 _          <- cell.put(calculated)
               } yield calculated
      } yield res
    }

  def someWork(x: Int): IO[Double] = IO.delay {
    println(s"got $x")
    x * x.toDouble
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val memzod = for {
      memoStore <- MVar.of[IO, Map[Int, MVar[IO, Double]]](Map.empty)
      memoized = memo(someWork _, memoStore)
    } yield memoized

    (for {
      f <- memzod
      fs = List(2,1,3,4,1,1,1,2,2,2,2,2,2,2,3,3,3,3,3,3,4,4,4,4,4,4,4,4).map(f)
      res <- fs.parSequence
      prnt <- IO.delay(println(res))
    } yield prnt) as ExitCode.Success
  }
}