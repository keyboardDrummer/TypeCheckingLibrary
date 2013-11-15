package rewrite

import ast._
import org.junit.Assert._
import org.junit.Test
import ast.Variable
import ast.Let
import ast.If
import usages.Examples
import machine.Machine

class TestRewriter {


  @Test
  def testAddition() {
    val inputExpression: Expression = 3 + 2
    val rewrite = new Rewriter().rewrite(inputExpression)
    assertEquals(5: Expression, rewrite)
  }

  @Test
  def testAdditionInIf() {
    val variable: Variable = "unknown"
    val inputExpression: Expression = new If(variable, 3 + 2, 1)
    val rewrite = new Rewriter().rewrite(inputExpression)
    val expectation = new If(variable, 5, 1)
    assertEquals(expectation, rewrite)
  }

  @Test
  def testLet() {
    val inputExpression = new Let("x", 3, "x")
    val expectation: Expression = 3
    assertEquals(expectation, new Rewriter().rewrite(inputExpression))
  }

  @Test
  def testComplicatedLet() {
    val inputExpression = new Let("x", 3 + 2, ("x": Expression) + 5)
    val expectation: Expression = 3 + 2 + 5
    assertEquals(expectation, new Rewriter().rewrite(inputExpression))
  }

  @Test
  def testUnknownValueLet() {
    val inputExpression = new Let("x", (5: Expression) + 3 + "y", new Variable("x") + (new IntValue(2) + 4))
    val expectation: Expression = new Let("x", new IntValue(5 + 3) + "y", (8: Expression) + "y" + new IntValue(2 + 4))
    val newInputExpression: Expression = new Rewriter().rewrite(inputExpression)
    assertEquals(expectation, newInputExpression)
  }

  @Test
  def testFibonacci0() {
    val inputExpression = new Call(Examples.fibonacci, 0)
    val expectation: Expression = 1
    assertEquals(expectation, new Rewriter().rewrite(inputExpression))
  }

  @Test
  def testFibonacci4() {
    val inputExpression = new Call(Examples.fibonacci, 4)
    val expectation: Expression = 5
    val result = new Rewriter().rewrite(inputExpression)
    assertEquals(expectation, result)
  }

  @Test
  def testKnownIf() {
    val inputExpression = new If("x", new If(3, 2, 1), 0)
    val expectation = new If("x", 2, 0)
    val result = new Rewriter().rewrite(inputExpression)
    assertEquals(expectation, result)
  }

  @Test
  def testFibonnaciInIf() {
    val inputExpression = new If("x", new Call(Examples.fibonacci, 4), -1)
    val expectation: Expression = new If("x", 5, -1)
    val result = new Rewriter().rewrite(inputExpression)
    assertEquals(expectation, result)
  }

  @Test
  def testUnknownFibonacci() {
    val inputExpression = new Call(Examples.fibonacci, "x")
    val fibonacciBody = Examples.fibonacci.value
    val expectation: Call = new Call(new Let("fibonacci", fibonacciBody, "fibonacci"), "x")
    def evaluateWithX(expression: Expression) = new Machine().evaluate(new Let("x", 5, expression))
    val expectationResult = evaluateWithX(expectation)
    val rewrite = new Rewriter().rewrite(inputExpression)
    val rewriteResult = evaluateWithX(rewrite)
    assertEquals(expectationResult, rewriteResult)
    assertEquals(expectation, rewrite)
  }

  @Test
  def testVariableTheftLet() = {
    val inputExpression = new Let("x", 3, new Let("x", 2, 0) + "x")
    val result = new Rewriter().rewrite(inputExpression)
    val expectation: Expression = 3
    assertEquals(expectation, result)
  }

  @Test
  def testEqualsExpression() = {
    val inputExpression = new Equals(new IntValue(3) + 2, 5)
    val result = new Rewriter().rewrite(inputExpression)
    val expectation: Expression = true
    assertEquals(expectation, result)
  }
}
