package statar.states

import shapeless._

object shapelgame {
  sealed trait HasUserId
  sealed trait HasAccounts
  sealed trait HasBalances

  val huid: HasUserId :: HNil = ???
  val huidA: HasUserId :: HasAccounts :: HNil = ???
  val huidB: HasUserId :: HasBalances :: HNil = ???
  val huidAB: HasUserId :: HasAccounts :: HasBalances :: HNil = ???


  def acceptWithAccount[S <: HList](s: S): Unit = ???
 
  acceptWithAccount(huid)
  acceptWithAccount(huidA)
}