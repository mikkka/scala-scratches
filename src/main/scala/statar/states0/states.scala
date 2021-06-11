package statar.states0

import statar.domain._
import statar.starter.WorldEdge

sealed trait HasUserId { 
  def userId: UserId
  def + (accounts: List[Account]): HasAccounts
  def + (balances: Map[AccountId, Balance]): HasBalances
}

sealed trait HasAccounts {
  def userId: UserId
  def accounts: List[Account]

  def + (balances: Map[AccountId, Balance]): HasAccountsBalances
  def -- : HasUserId
}

sealed trait HasBalances {
  def userId: UserId
  def balances: Map[AccountId, Balance]
  
  def + (accounts: List[Account]): HasAccountsBalances
  def -- : HasUserId
}

sealed trait HasAccountsBalances {
  def userId: UserId
  def accounts: List[Account]
  def balances: Map[AccountId, Balance]

  def `-accounts`: HasBalances
  def `-balances`: HasAccounts
}

sealed trait WithAccounts
sealed trait WithNoAccounts

object HasUserId {
  private case class _HasUserId(userId: UserId) extends HasUserId  {
    def + (accounts: List[Account]): HasAccounts = 
      _HasAccounts(userId, accounts)
    
    def + (balances: Map[AccountId, Balance]): HasBalances = 
      _HasBalances(userId, balances)
  }

  def huid(uid: UserId)(implicit we: WorldEdge): HasUserId = _HasUserId(uid)

  private case class _HasAccounts(
      userId: UserId,
      accounts: List[Account]
  ) extends HasAccounts {
    def + (balances: Map[AccountId, Balance]) = 
      _HasAccountsBalance(userId, accounts, balances)

    def -- = _HasUserId(userId)
  }

  private case class _HasBalances(
      userId: UserId,
      balances: Map[AccountId, Balance]
  ) extends HasBalances {
    def + (accounts: List[Account]) = 
      _HasAccountsBalance(userId, accounts, balances)

    def -- = _HasUserId(userId)
  }

  private case class _HasAccountsBalance(
      userId: UserId,
      accounts: List[Account],
      balances: Map[AccountId, Balance]
  ) extends HasAccountsBalances {
    def `-accounts`: HasBalances = _HasBalances(userId, balances)
    def `-balances`: HasAccounts = _HasAccounts(userId, accounts)
  } 
}

