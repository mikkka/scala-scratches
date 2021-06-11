object WildcardsArgs {
  def f1[T, С[x] <: Seq[x]](v: С[T]) = ()

  def f2[T, С[_] <: Seq[_]](v: С[T]) = ()

  type Coll[A] = Vector[(String, A)]
  
  f1[Int, Vector](Vector(1, 2))
  // T === Int, C[x] === Vector[Int], Seq[x] === Seq[Int] 
  // Vector[Int] <: Seq[Int]

  // won't compile
  // f1[Int, Coll](Vector("kek" -> 1, "lol" -> 2): Coll[Int])
  // T === Int, C[x] === Vector[(String, Int)], Seq[x] === Seq[Int]
  //! Vector[(String, Int)] <: Seq[Int]

  f2[Int, Coll](Vector("kek" -> 1, "lol" -> 2): Coll[Int])
  // T === Int, C[_] === Vector[(String, _)], Vector[_] < Seq[_] 
}