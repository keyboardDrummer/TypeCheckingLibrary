package typeChecker

import ast._
import scala.collection.mutable
import ast.Call
import ast.Variable
import ast.Let
import ast.IntValue
import ast.Equals
import ast.Addition
import ast.If
import ast.Lambda

object OnlyValueTypesChecker {
  def getType(expression: Expression) : Set[Expression] = {
    val result = new OnlyValueTypesChecker().checkType(expression)
    if (result.size > 1)
      throw new CannotApplyValueException()
    result
  }
}

class CannotApplyValueException extends RuntimeException

class OnlyValueTypesChecker {
  val environment = new StackedMap[String,Expression]
  def checkType(expression: Expression) : Set[Expression] = expression match {
    case Variable(name) => checkType(environment(name))
    case Call(callee, argument) => for (
      calleeType <- checkType(callee);
      argumentType <- checkType(argument);
      result <- calleeType match {
        case Closure(storedEnvironment, Lambda(name, body)) => {
          environment.push()
          environment ++= storedEnvironment
          environment.put(name, argumentType)
          val result = checkType(body)
          environment.pop()
          result
        }
        case _ => throw new CannotApplyValueException()
      }) yield result

    case lambda: Lambda => {
      Set.apply(new Closure(environment.clone(), lambda))
    }
    case If(condition,thenExpression,elseExpression) => {
      checkInt(condition)
      checkType(thenExpression) ++ checkType(elseExpression)
    }
    case Let(name,value,body) => {
      environment.push()
      environment.put(name,checkType(value))
      val result = checkType(body)
      environment.pop()
      result
    }
    case Equals(first,second) => {
      checkInt(first)
      checkInt(second)
      Set.apply(new IntValue(0))
    }
    case Addition(first,second) => {
      checkInt(first)
      checkInt(second)
      Set.apply(new IntValue(0))
    }
    case IntValue(_) => {
      Set.apply(new IntValue(0))
    }
    case closure: Closure => {
      Set.empty
    }
  }

  def checkInt(condition: Expression) {
    checkType(condition).foreach({
      case IntValue(_) => {}
      case _ => throw new CannotApplyValueException()
    })
  }
}
