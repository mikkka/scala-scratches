package taglessfinaloptimize

import cats.data.Const
import cats.syntax.option._

import scala.util.{Success, Try}

trait KVStore[F[_]] {
  def get(key: String): F[Option[String]]
  def put(key: String, a: String): F[Unit]
}

class KVStoreInterpreter extends KVStore[Try] {
  def get(key: String): Try[Option[String]] = {
    println(s"get $key")
    Success((key.toUpperCase).some)
  }

  def put(key: String, a: String): Try[Unit] = {
    println(s"set $key : $a")
    Success(())
  }
}

class KVStoreAnalysisInterpreter
    extends KVStore[Const[(Set[String], Map[String, String]), ?]] {


  override def get(key: String): Const[(Set[String], Map[String, String]), Option[String]] = Const((Set(key), Map.empty))
  override def put(key: String, a: String): Const[(Set[String], Map[String, String]), Unit] = Const((Set.empty, Map(key -> a)))
}
