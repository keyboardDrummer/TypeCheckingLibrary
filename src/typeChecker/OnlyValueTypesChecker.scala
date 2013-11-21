package typeChecker

import ast._
import scala.collection.mutable

object OnlyValueTypesChecker {
  def getType(expression: Expression) : Set[Expression] = {
    val result = new OnlyValueTypesChecker().checkType(expression)
    if (result.size > 1)
      throw new CannotApplyValueException()
    result
  }
}

class CannotApplyValueException extends RuntimeException

object Bottom extends Expression {}
class OnlyValueTypesChecker {
  val environment = new StackedMap[String,Expression]
  def checkType(expression: Expression) : Set[Expression] = expression match {
    case Variable(name) => environment(name) match {
      case Bottom => Set.empty
      case value => Set.apply(value)
    }
    case Call(callee, argument) => for (
      calleeType <- checkType(callee);
      argumentType <- checkType(argument);
      result <- calleeType match {
        case Closure(newEnvironment, Lambda(name,body)) => {
          environment.push()
          environment ++= newEnvironment
          environment.put(name, argumentType)
          val result = checkType(body)
          environment.pop()
          result
        }
        case _ => throw new CannotApplyValueException()
      }) yield result

    case lambda@Lambda(name, body) => {
      Set.apply(new Closure(environment.clone(), lambda))
    }
    case If(condition,thenExpression,elseExpression) => {
      checkInt(condition)
      checkType(thenExpression) ++ checkType(elseExpression)
    }
    case Let(name,value,body) => {
      environment.push()
      environment.put(name,Bottom)
      val valueTypes = checkType(value)
      environment.pop()
      valueTypes.flatMap(valueType => {
        environment.push()
        environment.put(name,valueType)
        val bodyType = checkType(body)
        checkType(value)
        environment.pop()
        bodyType
      })
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
  }

  def checkInt(condition: Expression) {
    checkType(condition).foreach({
      case IntValue(_) => {}
      case _ => throw new CannotApplyValueException()
    })
  }
}
