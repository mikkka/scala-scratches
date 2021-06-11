package statar.logic

import cats.Eval
import cats.Eval._
import cats.data.IndexedStateT

import statar.domain._
import statar.states0._

trait AccountsRepo {
  def findAccountsByUserId(userId: UserId): Eval[List[Account]]
}

trait AccountsCache {
  def flushByUserId(userId: UserId): Eval[Unit]
}

trait AccounstService {
  def repo: AccountsRepo
  def cache: AccountsCache

  def loadAccounts: IndexedStateT[Eval, HasUserId, HasAccounts, Unit] = 
    IndexedStateT.modifyF { huid =>
      repo.findAccountsByUserId(huid.userId).map { accs =>
        huid + accs  
      }
    }

  def doSomeAction: IndexedStateT[Eval, HasAccounts, HasAccounts, Unit] = for {
    hasAccs <- IndexedStateT.get
  } yield ???

  def flushAccounts: IndexedStateT[Eval, HasAccounts, HasUserId, Unit] = 
    IndexedStateT.modifyF{ha => 
      cache.flushByUserId(ha.userId).map(_ => ha --)
    }
}