package statar1

import cats.Eval
import cats.Eval._

import statar1.domain._
import statar1.states0._
import statar1.logic._
import statar1.states0.Instances._

sealed trait WorldEdge

object SPOC {
  private implicit val worldEdge = new WorldEdge {}

  val _repo = new AccountsRepo {
    def findAccountsByUserId(userId: UserId): Eval[List[Account]] = ???
  }

  val _cache = new AccountsCache {
    def loadByUserId(userId: UserId)(
        real: UserId => Eval[List[Account]]
    ): Eval[List[Account]] = ???
    def flushByUserId(userId: UserId): Eval[Unit] = ???
  }

  val service = new AccounstService {
    def cache: AccountsCache = _cache
    def repo: AccountsRepo = _repo
  }

  val prg: AppState[UserIdS, UserIdS, Unit] = for {
    _ <- service.loadAccounts[UserIdS, AccountsS]
    _ <- service.doSomeAction[AccountsS]
    _ <- service.flushAccounts[AccountsS, UserIdS]
  } yield ()

  prg.run(huid(UserId("333")))
}
