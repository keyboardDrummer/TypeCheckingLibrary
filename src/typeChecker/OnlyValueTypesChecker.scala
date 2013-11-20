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

class OnlyValueTypesChecker {
  val environment = new StackedMap[String,Expression]
  def checkType(expression: Expression) : Set[Expression] = expression match {
    case Variable(name) => Set.apply(environment(name))
    case Call(callee, argument) => for (
      calleeType <- checkType(callee);
      argumentType <- checkType(argument);
      result <- calleeType match {
        case Lambda(name, body) => {
          environment.push()
          environment.put(name, argumentType)
          val result = checkType(body)
          environment.pop()
          result
        }
        case _ => throw new CannotApplyValueException()
      }) yield result

    case Lambda(name, body) => {
      Set.apply(expression)
    }
    case If(condition,thenExpression,elseExpression) => {
      checkInt(condition)
      checkType(thenExpression) ++ checkType(elseExpression)
    }
    case Let(name,value,body) => {
      environment.push()
      environment.put(name,value)
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
  }

  def checkInt(condition: Expression) {
    checkType(condition).foreach({
      case IntValue(_) => {}
      case _ => throw new CannotApplyValueException()
    })
  }
}
