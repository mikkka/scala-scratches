package gotsome20210531

object domain {
  trait User
  trait Admin {
    def asUser: User
  }
  case class Suslik(name: String)
}