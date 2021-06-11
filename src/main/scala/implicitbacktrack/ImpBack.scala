package implicitbacktrack

object ImpBack {
trait FatherOf[Parent, Child]

  /** [[Parent]] / [[Child]] relationship, mother side. */
  trait MotherOf[Parent, Child]

  def fact[P, C](): FatherOf[P, C] = new FatherOf[P, C] {}
  def fact[P, C]()(implicit d: DummyImplicit): MotherOf[P, C] = new MotherOf[P, C] {}

  trait Bob;   trait Bill;  trait Stacy;  trait Ben
  trait Buffy; trait Sarah; trait Philip; trait Julie

  implicit val fact0:  Stacy MotherOf Bob   = fact()
  implicit val fact1: Philip FatherOf Bob   = fact()
  implicit val fact2:    Bob FatherOf Bill  = fact()
  implicit val fact3:   Bill FatherOf Buffy = fact()
  implicit val fact4:    Bob FatherOf Ben   = fact()
  implicit val fact5:  Julie MotherOf Ben   = fact()
  implicit val fact6:    Ben FatherOf Sarah = fact()

  trait IsAncestor[Ancestor, Descendant]

  object IsAncestor {
    def apply[A, D](implicit i: IsAncestor[A, D]): IsAncestor[A, D] = i

    implicit def directFather[A, D]
      (implicit e: FatherOf[A, D]) = new IsAncestor[A, D] {}

    // implicit def directMother[A, D]
    //   (implicit e: MotherOf[A, D]) = new IsAncestor[A, D] {}

    implicit def fatherSideRelation[A, D, Z]
      (implicit e: FatherOf[A, Z], i: IsAncestor[Z, D]) = new IsAncestor[A, D] {}

    implicit def motherSideRelation[A, D, Z]
      (implicit e: MotherOf[A, Z], i: IsAncestor[Z, D]) = new IsAncestor[A, D] {}
  }
  
  // IsAncestor[Stacy, Bob]
  // IsAncestor[Stacy, Bill]
  // IsAncestor[Bob, Buffy]
  // IsAncestor[Stacy, Buffy]

  IsAncestor[Bob, Buffy](
    IsAncestor.fatherSideRelation(
      implicitly[Bob FatherOf Bill],
      implicitly[Bill IsAncestor Buffy]
    )
  )
}
