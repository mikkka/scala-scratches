package compilemacro20190311

import cats.syntax.eq._
import cats.instances.int._
//import cats.instances.double._

object Foo {
  def main(args: Array[String]): Unit = {
//    1 === 1           // should compile
//    10.5 === 10.5     // can't compile without cats.instances.double._
//    1 === 10.5        // can't compile at all

//    println(Should.compile("""val a: Int = 1"""))
//    println(Should.compile("""1 === 1"""))
//    println(Should.compile("""10.5 === 10.5"""))
//    println(Should.compile("""1 === 10.5"""))
  }
}
