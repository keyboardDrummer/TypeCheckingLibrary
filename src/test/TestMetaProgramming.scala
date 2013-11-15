package test

import org.junit.Test
import ast._
import org.junit.Assert._
import ast.Call
import ast.Addition
import ast.Lambda
import ast.Equals
import machine.Machine

class TestMetaProgramming {


  @Test
  def testEqualsMetaProgramming()
  {
    val additionLambda = new Lambda("x",new Lambda("y", new Addition("x","y")))
    val callLambda = new Lambda("x",new Lambda("y",new Call("x","y")))
    assertEquals(1 : Expression,new Machine().evaluate(new Equals(additionLambda, additionLambda)))
    assertEquals(0 : Expression,new Machine().evaluate(new Equals(additionLambda, callLambda)))
    assertEquals(1 : Expression,new Machine().evaluate(new Equals(callLambda, callLambda)))
  }

  @Test
  def createConstX()
  {
    val identity = new Lambda("x","x")
    val constXBody = new Lambda("x",new If(new Equals("x",0),"identity", new Lambda("y", new Call("constX",("x" : Expression) - 1))))
    val constX = new Let("identity",identity,new Let("constX", constXBody, "constX"))

    def callConstX(i: Integer) = new Machine().evaluate(new Call(constX,new IntValue(i)))
    def applyArguments(callee: Expression, arguments: List[Expression]) = arguments.fold(callee)((acc,argument) => new Call(acc,argument))

    List.range(0,3).foreach((i) => {
      val input = applyArguments(callConstX(i), List.range(0, i+1).map(i => new IntValue(i)))
      assertEquals(i : Expression,new Machine().evaluate(input))
    })
  }
}
