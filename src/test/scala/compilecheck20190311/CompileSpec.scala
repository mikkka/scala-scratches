package compilecheck20190311

import org.scalatest._
import Matchers._

class CompileSpec extends FlatSpec {
  "bad code" should "not compile" in {
    "val a: String = 1" shouldNot compile
  }
}