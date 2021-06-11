package compilemacro20190311

import scala.language.experimental.macros
import scala.reflect.macros.{ParseException, TypecheckException, whitebox}

object Macro {
  def isCompile(c: whitebox.Context)(snippet: c.Expr[String]): c.Expr[Boolean] = {
    import c.universe._
    val Literal(Constant(code2check: String)) = snippet.tree
    try {
      c.typecheck(c.parse("{ " + code2check + " }"))
      reify { true }
    } catch {
      case e @ (_: TypecheckException | _: ParseException) =>
        reify { false }
    }
  }
}
