package higher_order_unification

// https://issues.scala-lang.org/browse/SI-2712

object Test {
  def meh0[M[_], A](x: M[A]): A = ???
  def meh[M[_], A](x: M[A]): M[A] = x

  val mehArg: Function1[Int, Int] = { (x: Int) => x}
//  val mehRet0: String = meh0(mehArg) // should solve ?M = [X] X => X and ?A = Int ...
  val mehRet: Function1[Int, Int] = meh(mehArg) // should solve ?M = [X] X => X and ?A = Int ...

  //  val mehArg: Tuple3[Int,Int,Double] = (14,88,42)
  //  val mehRet: Double = meh(mehArg) // should solve ?M = [X] X => X and ?A = Int ...
}