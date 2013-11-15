//package usages
//
//import ast._
//
//class ScopedType(env: Map[String,Type], body: Type) extends TypeBase{
//  def valueType: Type = body
//  def typeScope = env
//}
//
//abstract class Type extends TypeBase {
//  def valueType = this
//  def typeScope = Map.empty[String,Type]
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
//  def valueType : Type
//  def typeScope : TypeScope
//
//  type TypeScope = Map[String,Type]
//}
//
//
//object IntType extends Type
//object AnyType extends Type
//case class LambdaType(input: Type, output: Type) extends Type {
//
//}
//
//class TypesDoNotMatchException(expected: Type, actual: Type) extends RuntimeException
//
//class CanOnlyCallALambdaException extends RuntimeException
//
//object IntType extends Type
//
//class TypeCheckingLibrary {
//  val anyLambdaType: Type = new LambdaType(AnyType, AnyType)
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
//      if (!conditionType.equals(IntType))
//        throw new TypesDoNotMatchException(conditionType, IntType)
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
