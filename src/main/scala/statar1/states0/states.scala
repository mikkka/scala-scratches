package statar1.states0

import statar1.domain._
import statar1.WorldEdge

import cats.Eval
import cats.Eval._
import cats.data.IndexedStateT

sealed trait UserIdS { 
  def userId: UserId
}

sealed trait AccountsS {
  def userId: UserId
  def accounts: List[Account]
}

sealed trait BalancesS {
  def userId: UserId
  def balances: Map[AccountId, Balance]
}

sealed trait AccountsBalancesS {
  def userId: UserId
  def accounts: List[Account]
  def balances: Map[AccountId, Balance]
}

sealed trait HasUserId[T] {
  def apply(t: T): UserId
} 

sealed trait HasAccounts[T] {
  def apply(t: T): List[Account] 
} 

sealed trait HasBalances[T] {
  def apply(t: T): Map[AccountId, Balance]
}

sealed trait `+Accounts`[A,B] {
  def apply(a: A, accounts: List[Account]): B
}

sealed trait `-Accounts`[A,B] {
  def apply(a: A): B
}

object Instances {
  type AppState[T1, T2, T3] = IndexedStateT[Eval, T1, T2, T3]

  private case class _UserIdS(userId: UserId) extends UserIdS
  private case class _AccountsS(userId: UserId, accounts: List[Account]) extends AccountsS
  private case class _BalancesS(userId: UserId, balances: Map[AccountId, Balance]) extends BalancesS

  def huid(uid: UserId)(implicit we: WorldEdge): UserIdS = _UserIdS(uid)

  implicit val UserIdSHasUserId = new HasUserId[UserIdS] {
    def apply(t: UserIdS): UserId = t.userId
  } 

  implicit val `UserIdS+Accounts` = new `+Accounts`[UserIdS,AccountsS] {
    def apply(a: UserIdS, accounts: List[Account]): AccountsS = _AccountsS(a.userId, accounts)
  } 

  implicit val AccountsSHasUserId = new HasUserId[AccountsS] {
    def apply(t: AccountsS): UserId = t.userId
  } 

  implicit val AccountsSHasAccounts = new HasAccounts[AccountsS] {
    def apply(t: AccountsS): List[Account] = t.accounts
  }

  implicit val `AccountsS-Accounts` = new `-Accounts`[AccountsS,UserIdS] {
    def apply(a: AccountsS): UserIdS = new _UserIdS(a.userId)
  }
}