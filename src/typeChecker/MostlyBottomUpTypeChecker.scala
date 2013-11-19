//package typeChecker
//
//import ast._
//
//class MostlyBottomUpTypeChecker {
//  val variableTypes = new StackedMap[String, Type]()
//  variableTypes.push()
//
//  def checkIsAssignableTo(target: Type, value: Type): Unit = {
//    evaluateType(value) match {
//      case TypeVariable(name) => {
//        target match {
//          case TypeVariable(targetName) if name == targetName =>
//          case _ => variableTypes.put(name, target)
//        }
//      }
//      case newValue => {
//        if (target.isInstanceOf[TypeVariable])
//        {
//          checkIsAssignableTo(newValue,target)
//          return
//        }
//        val newTarget = evaluateType(target)
//        (newTarget, newValue) match {
//          case (LambdaType(firstName, firstInput, firstOutput), LambdaType(secondName, secondInput, secondOutput)) => {
//            variableTypes.push()
//            val variableInstance = getFreshVariable
//            if (variableTypes.get(firstName).isEmpty)
//            {
//              variableTypes.put(firstName,variableInstance)
//            }
//            if (variableTypes.get(secondName).isEmpty)
//            {
//              variableTypes.put(secondName,variableInstance)
//            }
//            checkIsAssignableTo(firstInput, secondInput)
//            checkIsAssignableTo(secondOutput, firstOutput)
//            variableTypes.pop()
//          }
//          case (IntType, IntType) => {}
//          case _ => throw new RuntimeException(s"type $target does not match type $value")
//        }
//      }
//    }
//  }
//
//  def checkEquals(first: Type, second: Type): Unit = {
//    checkIsAssignableTo(first, second)
//    // checkIsAssignableTo(second, first)
//  }
//
//  def getType(expression: Expression): Type = {
//    val innerType = getTypeInner(expression)
//    evaluateType(innerType)
//  }
//
//  def evaluateType(innerType: Type): Type = {
//    innerType match {
//      case innerType@TypeVariable(name) => variableTypes.get(name).fold[Type](innerType)(evaluateType)
//      case LambdaType(name, input, output) => new LambdaType(name, evaluateType(input), evaluateType(output))
//      case _ => innerType
//    }
//  }
//
//  def getTypeInner(expression: Expression): Type = expression match {
//    case IntValue(_) => IntType
//    case Call(callee, argument) => {
//      variableTypes.push()
//      val calleeType = getType(callee)
//      val argumentType = getType(argument)
//      val outputType = getFreshVariable
//      checkIsAssignableTo(new LambdaType(outputType.name, argumentType, outputType), calleeType)
//      val result = evaluateType(outputType)
//      variableTypes.pop()
//      result
//    }
//    case Variable(name) => {
//      new TypeVariable(name)
//    }
//    case If(condition, elseExpression, thenExpression) => {
//      checkIsAssignableTo(IntType, getType(condition))
//      val elseType = getType(elseExpression)
//      val thenType = getType(thenExpression)
//      checkEquals(elseType, thenType)
//      elseType
//    }
//    case Lambda(name, body) => {
//      new LambdaType(name, new TypeVariable(name), getType(body))
//    }
//    case Let(name, value, body) => {
//      variableTypes.push()
//      val valueType = getType(value)
//      checkIsAssignableTo(valueType, new TypeVariable(name))
//      val result = getType(body)
//      variableTypes.pop()
//      result
//    }
//    case Addition(first, second) => {
//      checkIsAssignableTo(IntType, getType(first))
//      checkIsAssignableTo(IntType, getType(second))
//      IntType
//    }
//    case Equals(first, second) => {
//      checkIsAssignableTo(IntType, getType(first))
//      checkIsAssignableTo(IntType, getType(second))
//      IntType
//    }
//  }
//
//
//  def getFreshVariable: TypeVariable = {
//    val freshVariable = System.nanoTime()
//    val outputType = new TypeVariable(freshVariable.toString)
//    outputType
//  }
//}