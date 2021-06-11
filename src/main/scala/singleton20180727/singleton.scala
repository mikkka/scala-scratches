/*
import scala.reflect.api.Im

trait InstanceImpl[T, I <: T with Singleton] extends ImplDef {
  def repr(implicit ev: Tag[I],
           w: Witness.Aux[I]): valuedef.ImplDef.InstanceImpl =
    valuedef.ImplDef.InstanceImpl(RuntimeDIUniverse.SafeType.get[I], w.value)
}


object InstanceImpl {
  def apply[T <: AnyRef](impl: T with Singleton): InstanceImpl[T, impl.type] =
    new InstanceImpl[T, impl.type] {}
}
*/
