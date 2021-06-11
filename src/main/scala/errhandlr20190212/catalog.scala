package errhandlr20190212

sealed trait CatalogError extends Exception
final case class ItemAlreadyExists(item: String) extends CatalogError
final case class CatalogNotFound(id: Long) extends CatalogError

final case class Item(name: String) extends AnyVal

abstract class CatalogAlg[F[_]: ErrorChannel[?[_], E], E <: Throwable] {
  def find(id: Long): F[List[Item]]
  def save(id: Long, item: Item): F[Unit]
}