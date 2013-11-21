package abstractRunners

import ast._
import test.TestSomeMachine

class TestMachineFromAbstract extends TestSomeMachine {
  def evaluateExpression(expression: Expression): Value = new MachineFromAbstract().evaluate(expression)
}