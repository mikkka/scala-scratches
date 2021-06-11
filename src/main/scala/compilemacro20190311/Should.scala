package compilemacro20190311
import scala.language.experimental.macros

object Should {
  def compile(snippet: String): Boolean = macro Macro.isCompile
}
