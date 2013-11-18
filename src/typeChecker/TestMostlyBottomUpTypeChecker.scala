//package typeChecker
//
//import org.junit.Assert._
//import test.{TestMetaProgramming, TestSomeTypeChecker}
//import ast._
//import org.junit.Test
//import typeChecker.LambdaType
//import ast.Lambda
//import typeChecker.TypeVariable
//
//class TestMostlyBottomUpTypeChecker extends TestSomeTypeChecker {
//
//  def assertException[E <: Class[_ <: Exception]](expected: E, f: () => Unit)
//  {
//    try
//    {
//      f()
//      fail()
//    } catch {
//      case caught: Exception => assertEquals(expected,caught.getClass)
//    }
//  }
//
//  def assertCheckSuccess(expression: Expression): Unit =
//    assert(!new MostlyBottomUpTypeChecker().getType(expression).isInstanceOf[TypeVariable])
//
//  def assertCheckFailure(expression: Expression): Unit =
//    assertException(classOf[RuntimeException], () => new MostlyBottomUpTypeChecker().getType(expression))
//
//  @Test
//  def typeCheckIdentity() {
//    val identity = new Let("identity", new Lambda("x", "x"), new If(new Call("identity",1),"identity","identity"))
//    val result = new MostlyBottomUpTypeChecker().getType(identity)
//    result match {
//      case LambdaType(_, input,output) => input == output && input.isInstanceOf[TypeVariable]
//      case _ => fail()
//    }
//  }
//
//  @Test
//  def typeCheckIdentityInIf() {
//    val identity = new Let("identity", new Lambda("x", "x"), new If(3,"identity",new Lambda("y", new IntValue(3) + "y")))
//    val result = new MostlyBottomUpTypeChecker().getType(identity)
//    result match {
//      case LambdaType(_, input,output) => input == output && input == IntType
//      case _ => fail()
//    }
//  }
//
//  @Test
//  def doubleUseOfIdentity() {
//    val identity = new Let("identity", new Lambda("x", "x"), new If(3,new Call("identity",3),new Call(new Call("identity","identity"),4)))
//    assertCheckSuccess(identity)
//  }
//
//  @Test
//  def typeCheckMetaProgramming() {
//    val constX = TestMetaProgramming.createConstX
//
//    def callConstX(i: Integer) = new Call(constX,new IntValue(i))
//    def applyArguments(callee: Expression, arguments: List[Expression]) = arguments.fold(callee)((acc,argument) => new Call(acc,argument))
//
//    List.range(0,3).map((i) => {
//      val input = applyArguments(callConstX(i), List.range(0, i+1).map(i => new IntValue(i)))
//      val typ = new MostlyBottomUpTypeChecker().getType(input)
//      assertCheckSuccess(input)
//    })
//  }
//}
