package gotsome20210531

import zio._
import zio.interop.catz._

object live {
  import domain._
  import ops._
  import typeclass._
  import tf_prg._

  // type AdminEnv = Has[Admin] with ZEnv
  type AdminTask[+A] = ZIO[Admin, Throwable, A]
  
  // type UserEnv = Has[User] with ZEnv
  type UserTask[+A] = ZIO[User, Throwable, A]

  implicit val gotAdmin = new GotSome[AdminTask, Admin] {
    def get: AdminTask[Admin] = ZIO.access(identity)
  }

  implicit val gotUser = new GotSome[UserTask, User] {
    def get: UserTask[User] = ZIO.access(identity)
  }
  
  val liveAdminPrg = tf_prg.make.admin[AdminTask]
  val liveUserPrg = tf_prg.make.user[UserTask]

  liveAdminPrg.prg("ADMIN")
  liveUserPrg.prg(List("user1", "user2", "user3"))
}