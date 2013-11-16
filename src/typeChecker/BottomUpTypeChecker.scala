package typeChecker

import ast._

class BottomUpTypeChecker {
  val variableTypes = new StackedMap[String, Type]()
  variableTypes.push()

  def checkIsAssignableTo(target: Type, value: Type): Unit = {
    evaluateType(value) match {
      case VariableType(name) => {
        target match {
          case VariableType(targetName) if name == targetName =>
          case _ => variableTypes.put(name, target)
        }
      }
      case newValue =>
        if (target.isInstanceOf[VariableType])
        {
          checkIsAssignableTo(value,target)
          return
        }
        (evaluateType(target), newValue) match {
        case (LambdaType(firstInput, firstOutput), LambdaType(secondInput, secondOutput)) => {
          checkIsAssignableTo(firstInput, secondInput)
          checkIsAssignableTo(secondOutput, firstOutput)
        }
        case (IntType, IntType) => {}
        case _ => throw new RuntimeException(s"type $target does not match type $value")
      }
    }
  }

  def checkEquals(first: Type, second: Type): Unit = {
    checkIsAssignableTo(first, second)
    // checkIsAssignableTo(second, first)
  }

  def getType(expression: Expression): Type = {
    val innerType = getTypeInner(expression)
    evaluateType(innerType)
  }

  def evaluateType(innerType: Type): Type = {
    innerType match {
      case innerType@VariableType(name) => variableTypes.get(name).fold[Type](innerType)(evaluateType)
      case LambdaType(input, output) => new LambdaType(evaluateType(input), evaluateType(output))
      case _ => innerType
    }
  }

  def getTypeInner(expression: Expression): Type = expression match {
    case IntValue(_) => IntType
    case Call(callee, argument) => {
      variableTypes.push()
      val calleeType = getType(callee)
      val argumentType = getType(argument)
      val freshVariable = System.nanoTime()
      val outputType = new VariableType(freshVariable.toString)
      checkIsAssignableTo(new LambdaType(argumentType, outputType), calleeType)
      val result = evaluateType(outputType)
      variableTypes.pop()
      result
    }
    case Variable(name) => {
      new VariableType(name)
    }
    case If(condition, elseExpression, thenExpression) => {
      checkIsAssignableTo(IntType, getType(condition))
      val elseType = getType(elseExpression)
      val thenType = getType(thenExpression)
      checkEquals(elseType, thenType)
      elseType
    }
    case Lambda(name, body) => {
      new LambdaType(new VariableType(name), getType(body))
    }
    case Let(name, value, body) => {
      variableTypes.push()
      val valueType = getType(value)
      checkIsAssignableTo(valueType, new VariableType(name))
      val result = getType(body)
      variableTypes.pop()
      result
    }
    case Addition(first, second) => {
      checkIsAssignableTo(IntType, getType(first))
      checkIsAssignableTo(IntType, getType(second))
      IntType
    }
    case Equals(first, second) => {
      checkIsAssignableTo(IntType, getType(first))
      checkIsAssignableTo(IntType, getType(second))
      IntType
    }
  }

}
