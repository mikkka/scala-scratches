package taglessfinaloptimize

import cats._
import cats.data.Const
import cats.instances.all._
import cats.syntax.all._

import scala.util.Try

object Program {
  //CAN'T OPTIMZE THIS M-FCKR!
  /*
  def program0[M[_]: FlatMap, F[_]](a: String)(K: KVStore[M])(
      implicit P: Parallel[M, F]) =
    for {
      _ <- K.put("A", a)
      x <- (K.get("B"), K.get("C")).parMapN(_ |+| _)
      _ <- K.put("X", x.getOrElse("-"))
    } yield x
   */
  val interpreter = new KVStoreInterpreter
  val analysisInterpreter = new KVStoreAnalysisInterpreter

  def program[F[_]: Apply](mouse: String)(F: KVStore[F]): F[List[String]] =
    (F.get("Cats"), F.get("Dogs"), F.put("Mice", mouse), F.get("Cats"))
      .mapN((f, s, _, t) => List(f, s, t).flatten)

  def optimizedProgram[F[_]: Applicative](mouse: String)(
      F: KVStore[F]): F[List[String]] = {
    val (gets, puts) = program(mouse)(analysisInterpreter).getConst

    puts.toList.traverse { case (k, v) => F.put(k, v) } *> gets.toList
      .traverse(F.get)
      .map(_.flatten)
  }

  def monadicProgram[F[_]: Monad](F: KVStore[F]): F[Unit] =
    for {
      mouse <- F.get("Mice")
      list <- optimizedProgram(mouse.getOrElse("64"))(F)
      _ <- F.put("Birds", list.headOption.getOrElse("128"))
    } yield ()

  def main(args: Array[String]): Unit = {
    program("MightyMice")(interpreter)
    println(program("MightyMice")(analysisInterpreter))

    println("----------------")

    optimizedProgram("MightyMice")(interpreter)

    println("------MONADIC------")

    monadicProgram(interpreter)
  }
}
