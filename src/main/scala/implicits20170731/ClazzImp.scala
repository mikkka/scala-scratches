package implicits20170731

object ClazzImp extends App {
  implicit class ReachInt(val x: Int) extends AnyVal {
    def fib: Int =
      x match {
        case 0 | 1 => 1
        case i => (i - 1).fib + (i - 2).fib
      }
  }

  println(10.fib)
}
