package typeChecker

import ast._

class BottomUpTypeChecker {
  val variableTypes = new StackedMap[String,Type]()
  variableTypes.push()

  def checkEquals(first: Type, second: Type) : Unit = {
    first match {
      case VariableType(name) => {
        variableTypes.get(name) match {
          case Some(firstValue) => checkEquals(firstValue,second)
          case None => variableTypes.put(name,second)
        }
      }
      case _ => second match {
        case VariableType(_) => checkEquals(second,first)
        case _ => {
          (first,second) match {
            case (LambdaType(firstInput,firstOutput),LambdaType(secondInput,secondOutput)) => {
              checkEquals(firstInput,secondInput)
              checkEquals(firstOutput,secondOutput)
            }
            case (IntType,IntType) => {}
            case _ => throw new RuntimeException(s"type $first does not match type $second")
          }
        }
      }
    }
  }

  def getType(expression: Expression) : Type = expression match {
    case IntValue(_) => IntType
    case Call(callee,argument) => {
      variableTypes.push()
      val calleeType = getType(callee)
      val argumentType = getType(argument)
      val freshVariable = System.nanoTime()
      val outputType = new VariableType(freshVariable.toString)
      checkEquals(calleeType, new LambdaType(argumentType, outputType))
      variableTypes.pop()
      outputType
    }
    case Variable(name) => {
      new VariableType(name)
    }
    case If(condition,elseExpression,thenExpression) => {
      checkEquals(IntType, getType(condition))
      val elseType = getType(elseExpression)
      val thenType = getType(thenExpression)
      checkEquals(elseType, thenType)
      elseType
    }
    case Lambda(name, body) => {
      new LambdaType(new VariableType(name),getType(body))
    }
    case Let(name,value,body) => {
      variableTypes.push()
      val valueType = getType(value)
      checkEquals(new VariableType(name),valueType)
      val result = getType(body)
      variableTypes.pop()
      result
    }
    case Addition(first,second) => {
      checkEquals(getType(first),IntType)
      checkEquals(getType(second),IntType)
      IntType
    }
    case Equals(first,second) => {
      checkEquals(getType(first),IntType)
      checkEquals(getType(second),IntType)
      IntType
    }
  }

}
