package test

import org.junit._
import Assert._
import ast._
import usages.Examples
import ast.Call
import ast.IntValue
import ast.Let

abstract class TestSomeMachine { //TODO test something like x = x + 1

  def evaluateExpression(expression: Expression) : Value

  @Test
  def testFibonacci() = {
    val expectedResults = Array(1, 1, 2, 3, 5, 8, 13)
    for(expectation <- expectedResults.zipWithIndex)
    {
      val index = expectation._2
      val result = evaluateExpression(new Call(Examples.fibonacci,index))
      assertEquals(new IntValue(expectation._1),result)
    }
  }

  @Test
  def testVariableTheftLet() =
  {
    val expression = new Let("x", 3, new Let("x", 2, 0) + "x")
    val result = evaluateExpression(expression)
    assertEquals(3 : Expression,result)
  }

  @Test
  def testEqualsInteger()
  {
    val expression = new Equals(3: Expression, 3: Expression)
    val expectation : Expression = 1
    val result = evaluateExpression(expression)
    assertEquals(expectation,result)
  }

  @Test
  def testNestedBindings()
  {
    val expression = new Call(new Let("x",0,new Let("y",1,new Lambda("z",new Variable("x") + "y" + "z"))),2)
    val result = evaluateExpression(expression)
    val expectation : Expression = 3
    assertEquals(expectation,result)
  }

  @Test
  def testNestedCollision()
  {
    val expression = new Call(new Let("x",0,new Let("x",1,new Lambda("y",new Variable("x") + "y"))),2)
    val result = evaluateExpression(expression)
    val expectation : Expression = 3
    assertEquals(expectation,result)
  }
}