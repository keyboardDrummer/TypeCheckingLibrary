package ast


trait Type

object IntType extends Type
{
  override def toString = "Int"
}
class ForeignType(typ : Class[_]) extends Type

class CheckTypeOperation(program: Expression) extends ast.Operation(List.apply(program)) {
  def apply(values: List[Value]): Value = {
    new TypeCheckerFromAbstract().evaluate(values(0))
    0
  }

  def cloneWithArguments(arguments: List[Expression]): Operation = new CheckTypeOperation(arguments(0))
}

class TypeCheckerFromAbstract extends AbstractMachine {
  type AbstractValue = Type
  type Closure = TypeClosure

  def createClosure(env: EnvType, lambda: Lambda): Closure = new TypeClosure(env, lambda)

  def valueToAbstractValue(value: Value): TypeCheckerFromAbstract#AbstractValue = value match {
    case IntValue(_) => IntType
    case _ => throw new RuntimeException()
  }

  case class TypeClosure(env: EnvType, lambda: Lambda) extends Type with ClosureLike

  def evaluateOperation(operation: Operation, arguments: List[AbstractValue]): AbstractValue = {

    def evaluateBinaryInteger: Type = {
      if (arguments(0) != IntType)
        throw new TypeMismatchException(IntType, arguments(0))
      if (arguments(1) != IntType)
        throw new TypeMismatchException(IntType, arguments(1))
      IntType
    }
    operation match {
      case Addition(_, _) => {
        evaluateBinaryInteger
      }
      case Equals(_, _) => {
        evaluateBinaryInteger
      }
    }
  }

  def evaluateIf(iff: If): TypeCheckerFromAbstract#AbstractValue =
  {
    evaluate(iff.condition) match {
      case IntType => unifyTypes(evaluate(iff.thenExpression), evaluate(iff.elseExpression))
      case value => throw new IfPredicateMustBeAConstant(value)
    }
  }

  def unifyTypes(first: Type, second: Type) : Type = {
    (first,second) match {
      case (IntType,IntType) => IntType
      case (TypeClosure(env1, lambda1),TypeClosure(env2,lambda2)) => {
        throw new RuntimeException()
      }
      case _ => throw new TypeMismatchException(first,second)
    }
  }
}


class TypeMismatchException(expected: Type, actual: Type) extends RuntimeException {
}
