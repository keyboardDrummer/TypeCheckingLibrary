package test

import org.junit._
import Assert._
import ast._
import usages.Examples
import ast.Call
import ast.IntValue
import ast.Let

class TestMachineFromAbstract extends TestSomeMachine {
  def evaluateExpression(expression: Expression): Value = new MachineFromAbstract().evaluate(expression)
}