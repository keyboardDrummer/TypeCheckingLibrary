//package rewrite
//
//import ast._
//import scala.collection.mutable
//
//class AbstractRewriter {
//
//  type Machine <: AbstractMachine // with AbstractMachine#AbstractValue <: Expression
//  type Value = AbstractMachine#AbstractValue
//  def machine : Machine
//  val nestedExpressions = new scala.collection.mutable.HashSet[Expression]
//  val argumentStack = new mutable.Stack[Value]()
//
//  def rewrite(expression: Expression) : Either[Expression,Value] = {
//    nestedExpressions.add(expression)
//    val result = rewriteInner(expression)
//    nestedExpressions.remove(expression)
//    result
//  }
//
//  def rewriteInner(expression: Expression) : Either[Expression,Value] = expression match {
//    case operation: Operation => {
//      val rewrites = operation.arguments.map(rewrite)
//      if (rewrites.forall(rewrite => rewrite.isRight))
//        Right[Expression,Value].apply(machine.evaluateOperation(operation, rewrites.map(v => v.right.get)))
//      else
//        Left[Expression,Value].apply(operation.cloneWithArguments(operation.arguments)) //TODO Here I'm not using all the rewrites.
//    }
//
//    case Call(callee,argument) => rewrite(argument) match {
//      case Right(newArgument) =>
//        argumentStack.push(newArgument)
//        val result = rewrite(callee)
//        argumentStack.pop()
//        result
//      case Left(newArgument) => new Call(callee,newArgument)
//    }
//
//    case me@If(condition,thenExpression,elseExpression) => rewrite(condition) match {
//      case constant: IntValue => machine.evaluate(new If(constant,thenExpression,elseExpression))
//      case newCondition => If(newCondition,rewrite(thenExpression),rewrite(elseExpression))
//    }
//
//    case Lambda(variable,body) => {
//      val argument = argumentStack.pop()
//      machine.stack.push(new mutable.HashMap[String,Expression] + (variable -> argument))
//      val result = rewrite(body)
//      argumentStack.push(argument)
//      machine.stack.pop()
//      result
//    }
//
//    case Let(variable,value,body) =>
//    {
//      val map : mutable.Map[String,Expression] = new mutable.HashMap + (variable -> value)
//      machine.stack.push(map)
//      val newValue: Expression = rewrite(value)
//      map.put(variable, newValue)
//      val newBody = rewrite(body)
//      machine.stack.pop()
//      newBody match {
//        case result: Value => result
//        case _ => Let(variable,newValue,newBody)
//      }
//    }
//
//    case variable@Variable(name) =>
//    {
//        machine.stack.find(env => env.contains(name)) match {
//          case Some(env) =>
//            val environmentExpression = env.get(name)
//            environmentExpression.get match {
//            case value: Value => value
//            case expression2: Expression => {
//              if (nestedExpressions.contains(expression2))
//                variable
//              else
//              {
//                val result = rewrite(expression2)
//                env.put(name,result)
//                result
//              }
//            }
//          }
//          case _ => variable
//        }
//    }
//    case x: Value => x
//  }
//}
