package typeChecker

import ast._
import scala.collection.mutable

class OnlyValueTypesChecker {
  val environment = new StackedMap[String,Expression]
  def checkType(expression: Expression) : List[Expression] = expression match {
    case Variable(name) => List.apply(environment(name))
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
        case _ => throw new RuntimeException()
      }) yield result

    case Lambda(name, body) => {
      List.apply(expression)
    }
    case If(condition,thenExpression,elseExpression) => {
      checkInt(condition)
      checkType(thenExpression) ++ checkType(elseExpression)
    }
    case Let(name,value,body) => {
      environment.push()

      environment.pop()
    }
    case Equals(first,second) => {
      checkInt(first)
      checkInt(second)
      List.apply(new IntValue(0))
    }
  }

  def checkInt(condition: Expression) {
    checkType(condition).foreach({
      case IntValue(_) => {}
      case _ => throw new RuntimeException()
    })
  }
}
