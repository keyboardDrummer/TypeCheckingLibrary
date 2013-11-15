package abstractRunners

import org.junit._
import Assert._
import ast._
import usages.Examples
import ast.Call
import ast.IntValue
import ast.Let
import abstractRunners.MachineFromAbstract
import test.TestSomeMachine

class TestMachineFromAbstract extends TestSomeMachine {
  def evaluateExpression(expression: Expression): Value = new MachineFromAbstract().evaluate(expression)
}