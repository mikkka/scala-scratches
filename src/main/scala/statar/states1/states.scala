package statar.states1

import statar.domain._
import statar.starter.WorldEdge


sealed trait UserId { 
  def userId: UserId
}

sealed trait Accounts {
  def userId: UserId
  def accounts: List[Account]
}

sealed trait Balances {
  def userId: UserId
  def balances: Map[AccountId, Balance]
}

sealed trait AccountsBalances {
  def userId: UserId
  def accounts: List[Account]
  def balances: Map[AccountId, Balance]
}

sealed trait HasUserId[T] {
  def userId(t: T): UserId
} 

sealed trait HasAccounts[T] {
  def accounts(t: T): List[Accounts] 
} 

sealed trait HasBalances[T] {
  def balances(t: T): Map[AccountId, Balance]
}

sealed trait `+Accounts`[A,B] {
  def apply(a: A): B
}

sealed trait `-Accounts`[A,B] {
  def apply(a: A): B
}