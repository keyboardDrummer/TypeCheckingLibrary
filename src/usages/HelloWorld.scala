package usages

import usages.Examples._
import ast.Expression._
import machine.Machine

object HelloWorld {
  def main(args: Array[String]) {
    println("result = " + new Machine().evaluate(call(fibonacci, 4)))
  }
}

