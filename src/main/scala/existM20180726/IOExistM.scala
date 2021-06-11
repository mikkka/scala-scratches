package existM20180726

import cats._
import cats.implicits._
import cats.effect.IO

object IOExistM extends App {
  def func(x: Int) = {
    IO {
      println(x)

      if (x % 2 == 0) true
      else false
    }
  }

  val xs = List(1,2,3,4,5,6).map(func)
  xs.existsM(identity).unsafeRunSync()
}
