package compounddomain20210531

object usages extends App {
  import types._
  import concrete._

  val debit =  
    FullDebitAccount(
      product = Account.DebitProduct(),
      brief = Account.DebitBrief(),
      details = Account.DebitDetails(),
      finInfo = Account.DebitFinInfo(),
      cards = FullCards(List.empty),
      name = new Account.Name {}
    )

  val kvk =  
    FullKVKAccount(
      product = Account.KVKProduct(),
      brief = Account.KVKBrief(),
      details = Account.KVKDetails(),
      finInfo = Account.KVKFinInfo(),
      name = new Account.Name {}
    )

  def produce1: List[Account 
    with Account.WithProduct[Account.Product]] = List(debit, kvk)

  def produce2: Account 
    with Account.WithProduct[Account.Product]
    with Account.WithName
    with Account.WithCards[Card with Card.WithName] = debit
  
  def consume1(accs: List[Account with Account.WithFinInfo[_]]) =
    accs.map(_.finInfo match {
      case _: Account.DebitFinInfo => ()
      case _: Account.KVKFinInfo   => ()
    }
  )

  def consume2(a: Account with Account.WithCards[Card with Card.WithFinInfo]) = 
    a.cards.list.foreach(x => println(x.finInfo))

  consume1(List(debit, kvk))
  consume2(debit)
  // consume2(kvk) // ill typed

  def collectAccsWithNames(accs: List[Account]): 
    List[Account with Account.WithName] = 
      accs.collect {case acc: Account with Account.WithName => acc}

  // is it a problem?
  def unsoundMethod(acc: Account): Account with Card.WithName = throw new Exception(s"fuck ${acc}") // ok typed? 

  // println(List(acc))
  println(collectAccsWithNames(List(debit)))

  trait ShortInfo {
    def debits: List[Account with Account.WithProduct[Account.DebitProduct]]
    def kvks: List[Account with Account.WithProduct[Account.KVKProduct]]
  }
  
  trait BriefInfo extends ShortInfo {
    def debits: List[Account with Account.WithProduct[Account.DebitProduct] with Account.WithBrief[Account.DebitBrief]]
    def kvks: List[Account with Account.WithProduct[Account.KVKProduct] with Account.WithBrief[Account.KVKBrief]]
  }

  trait FullInfo extends BriefInfo {
    def debits: List[FullDebitAccoutPerks]
    def kvks: List[FullKVKAccountPerks]
  }

  val fullInfo = new FullInfo {
    override def debits: List[FullDebitAccoutPerks] = List(debit)
    
    override def kvks: List[FullKVKAccountPerks] = List(kvk)
  }

  def consumeShorts(accs: ShortInfo): Unit = println(accs)
  def consumeBriefs(accs: BriefInfo): Unit = println(accs)
  def consumeFulls(accs: FullInfo): Unit = println(accs)

  consumeShorts(fullInfo)
  consumeBriefs(fullInfo)
  consumeFulls(fullInfo)

  import shapeless._

  type SHORT = 
    Account with Account.WithProduct[Account.DebitProduct] with Account.WithName with Account.WithCards[Card with Card.WithName] :+: 
    Account with Account.WithProduct[Account.KVKProduct] with Account.WithName :+: CNil

  def consumeShort(acc: SHORT) = println(acc)

  consumeShort(Inl(debit))
  consumeShort(Inr(Inl(kvk)))
  
  println("unordered match")
  List(debit, kvk).collect {
    case x: Account with Account.WithName with Account.WithBrief[_] => println(x)
  }
}
