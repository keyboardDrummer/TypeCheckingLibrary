package ast

import scala.collection._
import scala.Some

case class VariableNotInScopeException(name: String) extends RuntimeException
{
  override def toString() = "Cannot find variable " + name.toString
}

class Machine {
  def environment = stack.reverse.reduce((acc,frame) => acc ++ frame)

  val stack: mutable.Stack[mutable.Map[String, Expression]] = new mutable.Stack()
  stack.push(new mutable.HashMap())

  def evaluate(expression: Expression): Value = expression match {
    case value: Value => value

    case Call(callee, argument) => {
      val calleeValue = evaluate(callee)
      val argumentValue = evaluate(argument)
      calleeValue match {
        case closure: Closure => {
          val env = closure.env
          val lambda = closure.lambda
          stack.push(mutable.HashMap[String, Expression]() ++ env + (lambda.variable -> argumentValue))
          val result = evaluate(lambda.body)
          stack.pop()
          result
        }
        case value => throw new CannotCallNonLambda(value)
      }
    }
    case If(condition, thenExpression, elseExpression) => {
      val conditionValue = evaluate(condition)
      conditionValue match {
        case value: IntValue => evaluate(if (value.value > 0) thenExpression else elseExpression)
        case value => throw new IfPredicateMustBeAConstant(value)
      }
    }
    case lambda: Lambda => new Closure(environment, lambda)

    case Let(variable, value, body) => {
      stack.push(new mutable.HashMap[String,Expression]() + (variable -> value))
      val result = evaluate(body)
      stack.pop()
      result
    }

    case Variable(name) => {
        stack.find(env => env.contains(name)) match {
        case Some(env) => env.get(name).get match {
          case value: Value => value
          case expression: Expression => {
            if (expression.equals(new Variable(name)))
              throw new RuntimeException()
            val result = evaluate(expression)
            env.put(name, result)
            result
          }
        }
        case _ => throw new VariableNotInScopeException(name)
      }
    }

    case operation: Operation => {
      val values = operation.arguments.map(evaluate)
      operation.apply(values)
    }
  }

  class CannotCallNonLambda(callee: Value) extends RuntimeException("Cannot call a value that is not a lambda.")
  class IfPredicateMustBeAConstant(predicate: Value) extends RuntimeException("If predicate must evaluate to a constant.")
}


