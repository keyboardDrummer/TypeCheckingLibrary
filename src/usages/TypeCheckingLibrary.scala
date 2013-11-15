//package usages
//
//import ast._
//import typeChecker.VariableType
//
//class ScopedType(env: Map[String,typeChecker.Type], body: typeChecker.Type) extends TypeBase{
//  def valueType: typeChecker.Type = body
//  def typeScope = env
//}
//
//abstract class Type extends TypeBase {
//  def valueType = this
//  def typeScope = Map.empty[String,typeChecker.Type]
//
////  def unify(first: TypeBase, second: TypeBase) : TypeBase = {
////    first match {
////      case VariableType(name) => new ScopedType(unifyScopes(Map.apply(name -> second.valueType),second.typeScope),second.valueType)
////    }
////  }
//
////  def unifyScopes(first: TypeScope, second: TypeScope) : TypeScope = {
////
////  }
//}
//
//abstract class TypeBase {
//  def valueType : typeChecker.Type
//  def typeScope : TypeScope
//
//  type TypeScope = Map[String,typeChecker.Type]
//}
//
//
//object IntType extends typeChecker.Type
//object AnyType extends typeChecker.Type
//case class LambdaType(input: typeChecker.Type, output: typeChecker.Type) extends typeChecker.Type {
//
//}
//
//class TypesDoNotMatchException(expected: typeChecker.Type, actual: typeChecker.Type) extends RuntimeException
//
//class CanOnlyCallALambdaException extends RuntimeException
//
//object IntType extends typeChecker.Type
//
//class TypeCheckingLibrary {
//  val anyLambdaType: typeChecker.Type = new typeChecker.LambdaType(AnyType, AnyType)
//
//  def getType(expression: Expression): ScopedType = expression match {
//    case Call(callee, argument) => (getType(callee), getType(argument)) match {
//      case (LambdaType(inputType, outputType), argumentType) =>
//        //Type.unify(inputType, argumentType)
//        outputType
//      case (calleeType, _) => throw new TypesDoNotMatchException(anyLambdaType, calleeType)
//    }
//    case If(condition, thenExpression, elseExpression) => {
//      val conditionType = getType(condition)
//      if (!conditionType.equals(typeChecker.IntType))
//        throw new TypesDoNotMatchException(conditionType, typeChecker.IntType)
//
//      val thenType = getType(thenExpression)
//      val elseType = getType(elseExpression)
//      if (!thenType.equals(elseType))
//        throw new TypesDoNotMatchException(thenType, elseType)
//
//      thenType
//    }
//    case Variable(name) => new VariableType(name)
//    case Let(variable,value,body) => {
//      new LetType(variable,getType(value),getType(body))
//    }
//    case IntValue(value) => new IntType()
//  }
//}
