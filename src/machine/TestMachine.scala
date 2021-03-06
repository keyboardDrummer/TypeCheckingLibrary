package machine

import org.junit._
import Assert._
import ast._
import usages.Examples
import ast.Call
import ast.IntValue
import ast.Let
import machine.Machine
import test.TestSomeMachine

class TestMachine extends TestSomeMachine {

  def evaluateExpression(expression: Expression): Value = new Machine().evaluate(expression)
}