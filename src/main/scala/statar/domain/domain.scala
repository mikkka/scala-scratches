package statar.domain

case class UserId(id: String)
case class AccountId(id: String)
sealed trait Account
sealed trait Balance
