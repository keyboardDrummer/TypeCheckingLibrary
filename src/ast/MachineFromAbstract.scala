package ast

class MachineFromAbstract extends AbstractMachine {
  def valueToAbstractValue(value: Value): Value = value

  def evaluateOperation(operation: Operation, arguments: List[Value]): Value = operation.apply(arguments)

  type AbstractValue = Value
  type Closure = ValueClosure

  override def evaluateIf(iff: If) = {
    val If(condition,thenExpression,elseExpression) = iff
    val conditionValue = evaluate(condition)
    conditionValue match {
      case value: IntValue => evaluate(if (value.value > 0) thenExpression else elseExpression)
      case _ => throw new IfPredicateMustBeAConstant(conditionValue)
    }
  }

  class ValueClosure(val env: EnvType, val lambda: Lambda) extends Value with ClosureLike {
  }

  def createClosure(env: EnvType, lambda: Lambda): Closure = new ValueClosure(env,lambda)
}

