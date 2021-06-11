package asclazz20170927

import scala.reflect.ClassTag

trait Foo {
  private def as[T <: Foo : ClassTag]: Option[T] = this match {
    case t: T => Some(t)
    case _ => None
  }

  def asBar = as[Bar]
  def asBaz = as[Baz]
  def asGer = as[Ger]
}

class Bar extends Foo
class Baz extends Foo
class Ger extends Foo

object FooApp extends App {
  println((new Bar).asBar)
  println((new Bar).asBaz)
  println((new Bar).asGer)
}