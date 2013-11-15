package abstractRunners

import scala.collection._
import scala.Some
import ast._
import scala.Some
import scala.Some
import scala.Some
import scala.Some
import ast.Let
import scala.Some
import ast.Lambda
import ast.Call
import ast.Variable
import ast.If
import machine.VariableNotInScopeException

abstract class AbstractMachine {
  def environment = stack.reverse.reduce((acc, frame) => acc ++ frame)

  trait ClosureLike {
    def env: EnvType
    def lambda: Lambda

    override def toString = env.keys + ": " + lambda.toString
    override def hashCode = lambda.hashCode
    override def equals(that: Any) = that.equals(lambda)
  }

  class CannotCallNonLambda(callee: AbstractValue) extends RuntimeException(s"Cannot call value ${callee.toString} that is not a lambda.")
  class IfPredicateMustBeAConstant(predicate: AbstractValue) extends RuntimeException(s"If predicate ${predicate.toString} must evaluate to a constant.")

  type AbstractValue
  type Closure <: AbstractValue with ClosureLike
  type EnvType = mutable.Map[String, Either[Expression, AbstractValue]]
  val stack: mutable.Stack[EnvType] = new mutable.Stack()
  stack.push(new mutable.HashMap())

  def createClosure(env: EnvType, lambda: Lambda): Closure

  def valueToAbstractValue(value: Value): AbstractValue

  def evaluateOperation(operation: Operation, arguments: List[AbstractValue]): AbstractValue
  def evaluateIf(iff: If) : AbstractValue
  def evaluate(expression: Expression): AbstractValue = expression match {
    case value: Value => valueToAbstractValue(value)

    case Call(callee, argument) => {
      val calleeValue = evaluate(callee)
      val argumentValue = evaluate(argument)
      calleeValue match {
        case closure: ClosureLike => {
          val env = closure.env
          val lambda = closure.lambda
          stack.push(env ++ mutable.HashMap.apply(lambda.variable -> Right.apply(argumentValue)))
          val result = evaluate(lambda.body)
          stack.pop()
          result
        }
        case _ => throw new CannotCallNonLambda(calleeValue)
      }
    }
    case iff : If => evaluateIf(iff)

    case lambda: Lambda => createClosure(environment, lambda)

    case Let(variable, value, body) => {
      stack.push(mutable.HashMap.apply(variable -> Left.apply(value)))
      val result = evaluate(body)
      stack.pop()
      result
    }

    case Variable(name) => {
      stack.find(env => env.contains(name)) match {
        case Some(env) => env.get(name).get match {
          case Right(value) => value
          case Left(unevaluated) => {
            val result = evaluate(unevaluated)
            env.put(name, Right.apply(result))
            result
          }
        }
        case _ => throw new VariableNotInScopeException(name)
      }
    }

    case operation: Operation => {
      val values: immutable.List[AbstractValue] = operation.arguments.map(evaluate)
      evaluateOperation(operation, values)
    }
  }
}

