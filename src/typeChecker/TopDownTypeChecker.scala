package typeChecker

import ast._
import ast.Let
import ast.Lambda
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
    val result: Type = checker.getType(expression)
    checker.evaluateType(result)
  }
}

private class TopDownTypeChecker {
  var freshVariableCounter = 0
  val typeVariables = new scala.collection.mutable.HashMap[TypeVariable,Type]()
  val environment = new StackedMap[String, Type]()
  environment.push()

  def checkIsAssignableToNoVariables(target: Type, value: Type) : Type = (target,value) match
  {
    case (Polymorphic(name,typ),_) => {
      val fresh = getFreshVariable
      checkPolymorphic(fresh, checkIsAssignableToNoVariables(replaceTypeVariable(typ,name,fresh),value))
    }
    case (_,Polymorphic(name,typ)) => {
      val fresh = getFreshVariable
      checkPolymorphic(fresh, checkIsAssignableToNoVariables(target, replaceTypeVariable(typ,name,fresh)))
    }
    case (LambdaType(targetInput, targetOutput), LambdaType(valueInput, valueOutput)) => {
      val input = checkIsAssignableTo(targetInput, valueInput)
      val output = checkIsAssignableTo(valueOutput, targetOutput)
      LambdaType(input,output)
    }
    case (IntType,IntType) => IntType
    case _ => throw new TypesDoNotMatchException(target, value)
  }

  def checkIsAssignableTo(target: Type, value: Type): Type = {
    val evaluatedTarget = evaluateType(target)
    val evaluatedValue = evaluateType(value)
    checkIsAssignableToEvaluated(evaluatedTarget,evaluatedValue)
  }

  def checkIsAssignableToEvaluated(target: Type, value: Type): Type = {
    (target,value) match {
      case (targetVariable: TypeVariable, valueVariable: TypeVariable) if targetVariable == valueVariable => targetVariable
      case (targetVariable: TypeVariable,_) =>
        typeVariables.put(targetVariable, value)
        value
      case (_,valueVariable: TypeVariable) =>
        typeVariables.put(valueVariable, target)
        target
      case _ => checkIsAssignableToNoVariables(target,value)
    }
  }

  def checkEquals(first: Type, second: Type) : Type = {
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
    }
    case Lambda(name, body) => {
      environment.push()
      val fresh = getFreshVariable
      environment.put(name,fresh)
      val bodyType = getType(body)
      val result = LambdaType( environment(name), bodyType)
      environment.pop()
      checkPolymorphic(fresh, result)
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


  def checkPolymorphic(fresh: TypeVariable, result: Type): Type = {
    if (typeVariables.get(fresh).isEmpty)
      new Polymorphic(fresh, result)
    else
      result
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

  def replaceTypeVariable(typ: Type, original: TypeVariable, replacement: TypeVariable) =
  {
    def helper(typ: Type) : Type = typ match
    {
      case _:TypeVariable if typ == original => replacement
      case LambdaType(input,output) => new LambdaType(helper(input),helper(output))
      case Polymorphic(name,body) => if (name == original) typ else new Polymorphic(name,helper(body))
      case _ => typ
    }
    helper(typ)
  }
}
