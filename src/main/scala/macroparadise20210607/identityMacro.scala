package macroparadise20210607

import macroz._

object olo {
  // @printa
  final case class FooBarBaz(
    foo: Int, bar: String, baz: Map[String, kek.Ger]
  ) extends kek.Foo with kek.Bar with kek.Baz

  // @printa
  final case class FooBar(
    foo: Int, bar: String
  ) extends kek.Foo with kek.Bar

}

package kek {
  case class Ger(puk: Double)
  
  sealed trait Foo {
    def foo: Int
    def concrete: Unit = println(s"foo $foo")
  }

  sealed trait Bar {
    def bar: String
  }

  sealed trait ABaz[K, V] { self : Foo =>
    def baz: Map[String, Ger]
  }

  sealed trait Baz extends ABaz[String, Ger] { self : Foo => }

}

object test {
  @gena 
  type FooBar = kek.Foo with kek.Bar

  // @gena 
  type BarFoo = kek.Bar with kek.Foo
  
  @gena 
  type FooBarBaz = kek.Foo with kek.Bar with kek.Baz
}

object wassap extends App {
  val fooBar: kek.Foo = test.FooBar(foo = 123, bar = "hello")
  println(fooBar)

  fooBar match {
    case test.FooBar(x, y) => println((x, y))
  }
}