package nonemptylist20170927

object NonEmpty extends App {
  type NonEl[T] = ::[T]
  def nonEl[T](x: T, xs: List[T]) = ::(x, xs)

  def length[T](xs: ::[T]) = xs.length

  val nonEmp = List(1, 2, 3)
  val nonEmp2 = 1 :: 2 :: 3 :: Nil
  val nonEmp3 = ::(1, 2 :: 3 :: Nil)
  val emp = List.empty[Int]

  nonEl(1, 2 :: 3 :: Nil) match {
    case a : NonEl[Int] => length(a)
  }

//  println(length(nonEmp))
//  println(length(nonEmp2))
  println(length(nonEmp3))
  println(length(nonEl(1, 2 :: 3 :: Nil)))
//  println(length(emp))
}
