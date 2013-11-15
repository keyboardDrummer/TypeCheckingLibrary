package typeChecker

import org.junit.Assert._
import test.TestSomeTypeChecker
import ast.Expression

class TestBottomUpTypeChecker extends TestSomeTypeChecker {

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

  def assertCheckSuccess(expression: Expression): Unit = new BottomUpTypeChecker().getType(expression)

  def assertCheckFailure(expression: Expression): Unit =
    assertException(classOf[RuntimeException], () => new BottomUpTypeChecker().getType(expression))
}
