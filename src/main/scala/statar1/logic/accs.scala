package statar1.logic

import cats.Eval
import cats.Eval._
import cats.data.IndexedStateT

import statar1.domain._
import statar1.states0._

trait AccountsRepo {
  def findAccountsByUserId(userId: UserId): Eval[List[Account]]
}

trait AccountsCache {
  def loadByUserId(userId: UserId)(
    real: UserId => Eval[List[Account]]): Eval[List[Account]]
  def flushByUserId(userId: UserId): Eval[Unit]
}

trait AccounstService {
  def repo: AccountsRepo
  def cache: AccountsCache

  def loadAccounts[T1, T2](
    implicit 
    huid: HasUserId[T1],
    plus: `+Accounts`[T1,T2]
  ): IndexedStateT[Eval, T1, T2, Unit] = 
    IndexedStateT.modifyF { x =>
      cache.loadByUserId(huid(x))(repo.findAccountsByUserId).map {accs =>
        plus(x, accs)
      }
    }

  def doSomeAction[T1](
    implicit
    huid: HasUserId[T1],
    hasAccs: HasAccounts[T1]
  ): IndexedStateT[Eval, T1, T1, Unit] = for {
    accs <- IndexedStateT.get.map(hasAccs(_))
  } yield println(s"do some side id da effect ${accs}")

  def flushAccounts[T1,T2](
    implicit 
    huid : HasUserId[T1],
    haccs: HasAccounts[T1],
    minus: `-Accounts`[T1,T2]
  ): IndexedStateT[Eval, T1, T2, Unit] = 
    IndexedStateT.modifyF{ha => 
      cache.flushByUserId(huid(ha)).map(_ => minus(ha)) 
    }
}