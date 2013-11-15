package usages

import ast._
import ast.Expression._
import ast.If
import ast.Lambda

object Examples {
  val and = function("a","b")(new If("a",new If("b",1,0),0))
  val or = function("a","b")(new If("a",1,"b"))
  def fibonacci =
  {
    val name = "fibonacci"
    val condition = "i" - 1
    val recursiveCall = call(name,"i" - 1) + call(name,"i" - 2)
    new Let(name, function("i")(new If(condition,recursiveCall,1)),name)
  }
}
