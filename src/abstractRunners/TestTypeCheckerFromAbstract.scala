package abstractRunners

import org.junit.Test
import org.junit.Assert._
import ast._
import ast.Addition
import ast.Lambda
import usages.Examples
import abstractRunners.{TypeMismatchException, IntType, TypeCheckerFromAbstract, AbstractMachine}
import machine.VariableNotInScopeException

class TestTypeCheckerFromAbstract {

  @Test
  def testAddNumberAndLambda() {
    val expression = new Addition(3, new Lambda("x", 4))
    assertException(classOf[TypeMismatchException], () => new TypeCheckerFromAbstract().evaluate(expression))
  }

  @Test
  def callNonLambda()
  {
    val program = new Call(3,4)
    assertException(classOf[AbstractMachine#CannotCallNonLambda], () => new TypeCheckerFromAbstract().evaluate(program))
  }

  @Test
  def stableIntIf()
  {
    val program = new If(3,4,5)
    assertSame(IntType, new TypeCheckerFromAbstract().evaluate(program))
  }

  @Test
  def stableLambdaIf()
  {
    val program = new Call(new If(3,new Lambda("x","x"), new Lambda("y",7)),3)
    assertSame(IntType, new TypeCheckerFromAbstract().evaluate(program))
  }

  @Test
  def unstableIf()
  {
    val program = new If(3,3, new Lambda("x",3))
    assertException(classOf[Exception], () => new TypeCheckerFromAbstract().evaluate(program))
  }

  @Test
  def lambdaAsCondition()
  {
    val program = new If(new Lambda("x",3),3,4)
    assertException(classOf[AbstractMachine#IfPredicateMustBeAConstant], () => new TypeCheckerFromAbstract().evaluate(program))
  }

  @Test
  def variableNotInScopeException()
  {
    val program = new Variable("x")
    assertException(classOf[VariableNotInScopeException],() => new TypeCheckerFromAbstract().evaluate(program))
  }

  @Test
  def testAddNumbers() {
    val expression = new Addition(3,new Addition(5,3))
    assertSame(IntType, new TypeCheckerFromAbstract().evaluate(expression))
  }

  @Test
  def testFibonnaciRun() {
    val program = new Call(Examples.fibonacci,4)
    assertSame(IntType,new TypeCheckerFromAbstract().evaluate(program))
  }

  def assertException[E <: Class[_ <: Exception]](expected: E, f: () => Unit)
  {
    try
    {
      f()
      fail()
    } catch {
      case caught: Exception => assertEquals(expected,caught.getClass)
    }
  }
}
