import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

import cats.syntax.all._
import cats.instances.all._

object ReverseApplicat {
  def action1: Future[Unit] = ???
  def action2: Future[Unit] = ???

  action1.onError({case _ => Future(println("kek"))})
  (action1.attempt, action2.attempt).mapN {
    case (Left(oracleError), Left(localError)) =>
      println("Can't update neither oracle confirmation storage nor local confirmation storage.")
      throw oracleError
    case _ => ()
  }: Future[Unit]
}
