package macroz

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

object pprint {
  def prettyTree(raw: String): String = {
    var level = 0
    def indent = "  " * level
    val sparse = raw.map {
      case ',' => s",\n$indent".dropRight(1)
      case '(' =>
        level += 1
        s"(\n$indent"
      case ')' =>
        level -= 1
        s"\n$indent)"
      case other => other
    }.mkString
    sparse.replaceAll("""\(\s+\)""", "()")
  }
}

@compileTimeOnly("enable macro paradise to expand macro annotations")
class printa extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro printaMacro.impl
}

object printaMacro {
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val inputs = annottees.map(_.tree).toList
    val (_, expandees) = inputs match {
      case (param: ValDef) :: (rest @ (_ :: _)) => (param, rest)
      case (param: TypeDef) :: (rest @ (_ :: _)) => (param, rest)
      case _ => (EmptyTree, inputs)
    }
    println(pprint.prettyTree(showRaw(expandees)))
    val outputs = expandees
    c.Expr[Any](Block(outputs, Literal(Constant(()))))
  }
}

@compileTimeOnly("enable macro paradise to expand macro annotations")
class gena extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro genaMacro.impl
}

object genaMacro {
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val inputs = annottees.map(_.tree).toList
    val expandees = inputs match {
      case (x: TypeDef) :: Nil => scan4types(c)(x)
      case _ => inputs
    }

    println(pprint.prettyTree(showRaw(expandees)))
    println(expandees)

    c.Expr[Any](Block(expandees, Literal(Constant(()))))
  }  

  def scan4types(c: whitebox.Context)(tree: c.universe.Tree): List[c.universe.Tree] = {
    import c.universe._
    println("tree scan")
    tree.collect {
      case x @ c.universe.TypeDef(mods, name, Nil, c.universe.CompoundTypeTree(templ)) => 
        // println("typedef : " + name + " mods  " + mods + " params " + tparams + " templ " + templ)
        // println(showRaw(templ))
        genCase(c)(name, templ)
    }
  }

  val _STD_MEMBERS = Set(
    "synchronized", "$hash$hash", "$bang$eq", "$eq$eq", "ne", "eq", "finalize", 
    "wait", "wait", "wait", "notifyAll", "notify", "toString", "clone", "equals", 
    "hashCode", "getClass", "asInstanceOf", "isInstanceOf"
  )

  def genCase(c: whitebox.Context)(tpeName: c.TypeName, templ: c.universe.Template): c.universe.Tree = {
    import c.universe._
    val typed = templ.parents.map(c.typecheck(_, mode = c.TYPEmode))
    val members2impl: List[(TermName, Type)] = typed.map (x => x.tpe.members.collect {
        case meth if meth.isMethod 
          && meth.isAbstract 
          && !_STD_MEMBERS(meth.name.decodedName.toString())
          && meth.asMethod.paramLists.isEmpty => 
            meth.asMethod.name -> meth.asMethod.typeSignatureIn(x.tpe)
      }
    ).flatten.distinct

    // println("METH TO IMPL")
    // println(members2impl)
    // members2impl.foreach(x => println(x._2.toString + " : " + pprint.prettyTree(showRaw(x._2))))

    // val typeParams = members2impl.map(_._2).map(
    //   p => TypeDef(Modifiers(Flag.PARAM), ???, Nil, TypeBoundsTree(EmptyTree, EmptyTree))
    // )

    val ctorArgs = members2impl.map { case (argName, argType) =>
      val mods = Modifiers(Flag.CASEACCESSOR | Flag.PARAMACCESSOR)
      val valDef = q"$mods val $argName: $argType"
      ValDef(
        Modifiers(Flag.CASEACCESSOR | Flag.PARAMACCESSOR),
        argName,
        Ident(argType.typeSymbol), // somehow extract type name - Ident(x: TypeName)
        EmptyTree
      )
      valDef
    }

    // println()
    // println()
    // println()

    // println("templ")
    // println(showRaw(templ))
    // println(showRaw(templ.parents))

    q"final case class $tpeName (..$ctorArgs) extends ..${templ.parents}"
  }
}