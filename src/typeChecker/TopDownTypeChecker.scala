package usages

import ast._
import typeChecker._
import scala.collection.immutable
import typeChecker.LambdaType
import ast.Let
import ast.Lambda
import typeChecker.TypeVariable
import ast.Call
import ast.Variable
import ast.IntValue
import ast.Equals
import ast.Addition
import ast.If


class TypesDoNotMatchException(expected: typeChecker.Type, actual: typeChecker.Type) extends RuntimeException

class CanOnlyCallALambdaException extends RuntimeException

object TopDownTypeChecker {
  def getType(expression: Expression) = {
    val checker: TopDownTypeChecker = new TopDownTypeChecker()
    checker.evaluateType(checker.getType(expression))
  }
}

private class TopDownTypeChecker {
  var freshVariableCounter = 0
  val typeVariables = new scala.collection.mutable.HashMap[TypeVariable,Type]()
  val environment = new StackedMap[String, Type]()
  environment.push()

  def checkIsAssignableToNoVariables(target: Type, value: Type) : Unit = (target,value) match
  {
    case (Polymorphic(name,typ),_) => {
      val fresh = getFreshVariable
      typeVariables.put(name,fresh)
      checkIsAssignableToNoVariables(typ,value)
      typeVariables.remove(name)
    }
    case (_,Polymorphic(name,typ)) => {
      val fresh = getFreshVariable
      typeVariables.put(name,fresh)
      checkIsAssignableToNoVariables(target,typ)
      typeVariables.remove(name)
    }
    case (LambdaType(targetInput, targetOutput), LambdaType(valueInput, valueOutput)) => {
      checkIsAssignableTo(targetInput, valueInput)
      checkIsAssignableTo(valueOutput, targetOutput)
    }
    case (IntType,IntType) =>
    case _ => throw new TypesDoNotMatchException(target, value)
  }

  def checkIsAssignableTo(target: Type, value: Type): Unit = (target,value) match {
    case (targetVariable: TypeVariable,_) =>
      evaluateType(targetVariable) match {
        case _:TypeVariable => typeVariables.put(targetVariable, value)
        case typ => checkIsAssignableTo(typ,value)
      }
    case (_,valueVariable: TypeVariable) =>
      evaluateType(valueVariable) match {
        case _:TypeVariable  => typeVariables.put(valueVariable, target)
        case typ => checkIsAssignableTo(target,typ)
      }
    case _ => checkIsAssignableToNoVariables(target,value)
  }

  def checkEquals(first: Type, second: Type) : Unit = {
    checkIsAssignableTo(first,second)
    checkIsAssignableTo(second,first)
  }

  def getType(expression: Expression): Type = expression match {
    case Call(callee, argument) => {
      val calleeType = getType(callee)
      val argumentType = getType(argument)
      val fresh = getFreshVariable
      checkIsAssignableTo(new LambdaType(argumentType,fresh), calleeType)
      fresh
    }
    case If(condition, thenExpression, elseExpression) => {
      val conditionType = getType(condition)
      checkIsAssignableTo(IntType, conditionType)

      val thenType = getType(thenExpression)
      val elseType = getType(elseExpression)
      checkEquals(thenType,elseType)

      thenType
    }
    case Lambda(name, body) => {
      environment.push()
      val fresh = getFreshVariable
      environment.put(name,fresh)
      val bodyType = getType(body)
      val result = LambdaType( environment(name), bodyType)
      environment.pop()
      if (typeVariables.get(fresh).isEmpty)
        new Polymorphic(fresh,result)
      else
        result
    }
    case Variable(name) => {
      environment(name)
    }
    case Let(variable,value,body) => {
      environment.push()
      val fresh = getFreshVariable
      environment.put(variable,fresh)
      val valueType = getType(value)
      checkIsAssignableTo(environment(variable), valueType)
      val result = getType(body)
      environment.pop()
      result
    }
    case IntValue(value) => IntType
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

  def getFreshVariable: TypeVariable = {
    val freshVariable = freshVariableCounter
    freshVariableCounter += 1
    new TypeVariable(freshVariable)
  }

  def evaluateType(innerType: Type): Type = {
    innerType match {
      case variable:TypeVariable => typeVariables.get(variable).fold[Type](variable)(evaluateType)
      case LambdaType(input, output) => new LambdaType(evaluateType(input), evaluateType(output))
      case Polymorphic(name, body) => new Polymorphic(name, evaluateType(body))
      case _ => innerType
    }
  }
}
