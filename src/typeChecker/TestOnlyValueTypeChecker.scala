package typeChecker

import org.junit.Assert._
import test.{TestMetaProgramming, TestSomeTypeChecker}
import ast._
import org.junit.Test
import ast.Lambda

class TestOnlyValueTypeChecker extends TestSomeTypeChecker {

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

  def assertCheckSuccess(expression: Expression): Unit =
    assert(OnlyValueTypesChecker.getType(expression).size == 1)

  def assertCheckFailure(expression: Expression): Unit =
    assertException(classOf[CannotApplyValueException], () => OnlyValueTypesChecker.getType(expression))

  @Test
  def typeCheckIdentity() {
    val identity = new Let("identity", new Lambda("x", "x"), new If(new Call("identity",1),"identity","identity"))
    val result = OnlyValueTypesChecker.getType(identity)
    val variable = TypeVariable(7)
    assertEquals(new Polymorphic(variable,new LambdaType(variable,variable)),result)
  }

  @Test
  def typeCheckIdentityInIf() {
    val identity = new Let("identity", new Lambda("x", "x"), new If(3,new Lambda("y", new IntValue(3) + "y"),"identity"))
    val result = OnlyValueTypesChecker.getType(identity)
    assertEquals(new LambdaType(IntType,IntType),result)
  }

  @Test
  def typeCheckIdentityInIf2() {
    val identity = new Let("identity", new Lambda("x", "x"), new If(3,"identity",new Lambda("y", new IntValue(3) + "y")))
    val result = OnlyValueTypesChecker.getType(identity)
    assertEquals(new LambdaType(IntType,IntType),result)
  }

  @Test
  def doubleUseOfIdentity() {
    val identity = new Let("identity", new Lambda("x", "x"), new If(3,new Call("identity",3),new Call(new Call("identity","identity"),4)))
    assertCheckSuccess(identity)
  }

  @Test
  def typeCheckMetaProgramming() {
    val constX = TestMetaProgramming.createConstX

    def callConstX(i: Integer) = new Call(constX,new IntValue(i))
    def applyArguments(callee: Expression, arguments: List[Expression]) = arguments.fold(callee)((acc,argument) => new Call(acc,argument))

    List.range(0,3).map((i) => {
      val input = applyArguments(callConstX(i), List.range(0, i+1).map(i => new IntValue(i)))
      val typ = OnlyValueTypesChecker.getType(input)
      assertCheckSuccess(input)
    })
  }
}
