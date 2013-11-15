package usages

import scala.collection.immutable.TreeMap
import usages.Examples
import usages.Examples._
import ast._
import ast.Expression._

object HelloWorld {
  def main(args: Array[String]) {
    println("result = " + new Machine().evaluate(call(fibonacci, 4)))
  }
}

