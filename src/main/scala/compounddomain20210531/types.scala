package compounddomain20210531

object types {
  abstract class Card
  abstract class Account

  object Card {
    case class Brief()
    sealed trait WithBrief { _: Card =>
      def brief: Brief
    }

    case class FinInfo()
    sealed trait WithFinInfo { _: Card =>
      def finInfo: FinInfo
    }

    case class Name()
    sealed trait WithName { _: Card =>
      def name: Name
    }
  }

  object Account {
    sealed trait Product
    case class KVKProduct() extends Product
    case class DebitProduct() extends Product
    sealed trait WithProduct[+T <: Product] extends Account {
      def product: T
    }

    sealed trait Brief 
    case class KVKBrief() extends Brief
    case class DebitBrief() extends Brief
    sealed trait WithBrief[+T <: Brief] extends Account {
      def brief: T
    }

    sealed trait Details
    case class KVKDetails() extends Details
    case class DebitDetails() extends Details
    sealed trait WithDetails[+T <: Details] extends Account {
      def details: T
    }

    sealed trait FinInfo
    case class DebitFinInfo() extends FinInfo
    case class KVKFinInfo() extends FinInfo
    sealed trait WithFinInfo[+T <: FinInfo] extends Account {
      def finInfo: T
    }

    case class Name()
    sealed trait WithName { _: Account =>
      def name: Name
    }

    sealed trait Cards[+T <: Card] {  
      def list: List[T]
    }

    sealed trait WithCards[+T <: Card] { _: Account =>
      def cards: Cards[T]
    }

    // sealed trait Kek extends Account with Card.WithName // ill typed
    // sealed trait Con extends Account with Card with Card.WithName
  }      
}

object concrete {
  import types._

  trait FullCardPerks extends Card 
    with Card.WithBrief
    with Card.WithFinInfo
    with Card.WithName 

  case class FullCard(
    brief: Card.Brief,
    finInfo: Card.FinInfo,
    name: Card.Name
  ) extends FullCardPerks

  case class FullCards(list: List[FullCard]) extends Account.Cards[FullCardPerks]

  trait FullDebitAccoutPerks extends Account
    with Account.WithProduct[Account.DebitProduct]
    with Account.WithBrief[Account.DebitBrief]
    with Account.WithDetails[Account.DebitDetails]
    with Account.WithFinInfo[Account.DebitFinInfo]
    with Account.WithCards[Card with Card.WithBrief with Card.WithName with Card.WithFinInfo]
    with Account.WithName

  case class FullDebitAccount(
    product: Account.DebitProduct,
    brief: Account.DebitBrief,
    details: Account.DebitDetails,
    finInfo: Account.DebitFinInfo,
    cards: FullCards,
    name: Account.Name
  ) extends FullDebitAccoutPerks// it's card account

  trait FullKVKAccountPerks extends Account 
    with Account.WithProduct[Account.KVKProduct]
    with Account.WithBrief[Account.KVKBrief]
    with Account.WithDetails[Account.KVKDetails]
    with Account.WithFinInfo[Account.KVKFinInfo]
    with Account.WithName 

  case class FullKVKAccount(
    product: Account.KVKProduct,
    brief: Account.KVKBrief,
    details: Account.KVKDetails,
    finInfo: Account.KVKFinInfo,
    name: Account.Name
  ) extends FullKVKAccountPerks // it's non-card account
}