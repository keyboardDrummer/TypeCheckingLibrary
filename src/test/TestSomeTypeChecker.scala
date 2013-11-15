package test

import org.junit.Test
import org.junit.Assert._
import ast._
import ast.Addition
import ast.Lambda
import usages.Examples
import abstractRunners.{TypeMismatchException, IntType, TypeCheckerFromAbstract, AbstractMachine}
import machine.VariableNotInScopeException

abstract class TestSomeTypeChecker {
   def assertCheckSuccess(expression: Expression)
   def assertCheckFailure(expression: Expression)

   @Test
   def testAddNumberAndLambda() {
     assertCheckFailure(new Addition(3, new Lambda("x", 4)))
   }

   @Test
   def callNonLambda()
   {
     assertCheckFailure(new Call(3, 4))
   }

   @Test
   def stableIntIf()
   {
     assertCheckSuccess(new If(new If(3, 4, 5), 6, 7))
   }

   @Test
   def stableLambdaIf()
   {
     assertCheckSuccess(new Call(new If(3, new Lambda("x", "x"), new Lambda("y", 7)), 3))
   }

   @Test
   def unstableIf()
   {
     assertCheckFailure(new If(3, 3, new Lambda("x", 3)))
   }

   @Test
   def lambdaAsCondition()
   {
     assertCheckFailure(new If(new Lambda("x", 3), 3, 4))
   }

   @Test
   def testAddNumbers() {
     assertCheckSuccess(new Addition(3, new Addition(5, 3)))
   }

   @Test
   def testFibonnaciRun() {
     assertCheckSuccess(new Call(Examples.fibonacci, 4))
   }
 }
