package statar.starter

import cats.Eval
import cats.Eval._

import statar.domain._
import statar.states0._
import statar.logic._

sealed trait WorldEdge

object SPOC {
  private implicit val worldEdge = new WorldEdge {} 

  val _repo = new AccountsRepo {
    def findAccountsByUserId(userId: UserId): Eval[List[Account]] = ???
  }
  val _cache = new AccountsCache {
    def flushByUserId(userId: UserId): Eval[Unit] = ???
  }
  val service = new AccounstService {
    def cache: AccountsCache = _cache
    def repo: AccountsRepo = _repo
  }

  val prg = for {
    _ <- service.loadAccounts
    _ <- service.doSomeAction
    _ <- service.flushAccounts
  } yield ()

  prg.run(HasUserId.huid(UserId("333")))
}