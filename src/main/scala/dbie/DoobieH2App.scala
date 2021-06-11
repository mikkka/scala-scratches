package dbie

import cats.effect.{Blocker, IO}
import util.strng._
import scala.concurrent.duration.{FiniteDuration, NANOSECONDS}

import cats._
import cats.arrow.Profunctor
import cats.data.NonEmptyList
import cats.implicits._
import cats.effect.syntax.bracket._

import doobie._
import doobie.syntax._
import doobie.implicits._
import doobie.util.analysis.Analysis
import doobie.util.compat.FactoryCompat
import doobie.util.log.{LogEvent, ExecFailure, ProcessingFailure, Success}
import doobie.util.pos.Pos

final case class Person(id: Long, name: String, age: Option[Short])

object ExQ {
  private val now: PreparedStatementIO[Long] =
    FPS.delay(System.nanoTime)

  def executeQuery[A, T](
      sql: String,
      a: A,
      k: ResultSetIO[T],
      logHandler: LogHandler
  )(implicit write: Write[A]): PreparedStatementIO[T] = {
    val args = write.toList(a)
    def diff(a: Long, b: Long) = FiniteDuration((a - b).abs, NANOSECONDS)
    def log(e: LogEvent) = FPS.delay(logHandler.unsafeRun(e))
    for {
      t0 <- now
      eet <- FPS.executeQuery
        .bracket(
          rs =>
            for {
              t1 <- now
              et <- FPS.embed(rs, k).attempt
              t2 <- now
            } yield (t1, et, t2)
        )(FPS.embed(_, FRS.close))
        .attempt
      tuple <- eet.liftTo[PreparedStatementIO].onError {
        case e =>
          for {
            t1 <- now
            _ <- log(ExecFailure(sql, args, diff(t1, t0), e))
          } yield ()
      }
      (t1, et, t2) = tuple
      t <- et.liftTo[PreparedStatementIO].onError {
        case e =>
          log(ProcessingFailure(sql, args, diff(t1, t0), diff(t2, t1), e))
      }
      _ <- log(Success(sql, args, diff(t1, t0), diff(t2, t1)))
    } yield t
  }
}

object DoobieH2App extends App {
  val pos = implicitly[Pos]

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val xa = Transactor.fromDriverManager[IO](
    "org.h2.Driver", // driver classname
    "jdbc:h2:~/test", // connect URL (driver-specific)
    "sa", // user
    "", // password
    Blocker.liftExecutionContext(ExecutionContexts.synchronous)
  )

  val initSceme = {
    val dropSql =
      sql42"""
        DROP TABLE IF EXISTS person
      """

    val createSql =
      sql42"""
        CREATE TABLE person (
          id   SERIAL,
          name VARCHAR NOT NULL UNIQUE,
          age  SMALLINT
        )
      """

    val createDDL = createSql.update
    val dropDDL = dropSql.update
    val createConnIo = createDDL.run
    val dropConnIo = dropDDL.run

    for {
      dropRes <- dropConnIo
      createRes <- createConnIo
    } yield (dropRes, createRes)
  }

  def insert1(name: String, age: Option[Short]): Update0 =
    sql42"insert into person (name, age) values ($name, $age)".update

  def select(id: Long): Query0[Person] =
    sql42"select * from person where id = ${id}".query[Person]

  def execWith(id: Long) =
    sql42"select * from person where id = ${id}"
      .execWith(???)

  println(initSceme.transact(xa).unsafeRunSync())
  println(insert1("Alice", Some(12)).run.transact(xa).unsafeRunSync)
  println(insert1("Bob", None).run.transact(xa).unsafeRunSync)
  println(insert1("Pisya", Some(13)).run.transact(xa).unsafeRunSync)

  val query = select(1)

  println(select(1).option.transact(xa).unsafeRunSync)

  val id = 3
  val frgmnt = sql42"select * from person where id = ${id}"

  val connectIO: ConnectionIO[Option[Person]] = 
    frgmnt.execWith(
      FPS.setQueryTimeout(100) *>  
      FPS.cancel *>
      ExQ.executeQuery[Long,Option[Person]](
        frgmnt.sql, 3, HRS.getOption[Person], LogHandler.nop
      ) 
    )

  println(connectIO.transact(xa).unsafeRunSync)
}
