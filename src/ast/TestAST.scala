package ast

import org.junit.Test
import org.junit.Assert._
import ast._
import ast.Variable
import ast.Let
import ast.Addition
import ast.If

class TestAST {

  @Test
  def testVariableEquals() {
    assertEquals(new Variable("a"), new Variable("a"))
  }

  @Test
  def testAdditionEquals() {
    assertEquals((4 : Expression) + 3,(4: Expression) + 3)
  }

  @Test
  def testIfAdditionEquals() {
    val expression = new If(1, new Addition(3, 2), 4)
    val expression2 = new If(1, new Addition(3, 2), 4)
    assertEquals(expression, expression2)
  }

  @Test
  def testLetEquals() {
    val otherExpression = new Let("x", (8: Expression) + "y", (8: Expression) + "y" + 6)
    val otherExpression2 = new Let("x", (8: Expression) + "y", (8: Expression) + "y" + 6)
    assertEquals(otherExpression, otherExpression2)
  }
}
