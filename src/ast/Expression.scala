package ast

import java.lang


object Expression {

  implicit def StringToVariable(string: String) = new Variable(string)

  implicit def IntToConstant(int: Int) = new IntValue(int)

  implicit def BoolToConstant(bool: Boolean) = new IntValue(if (bool) 1 else 0)

  def call(callee: Expression, arguments: Expression*) =
    arguments.foldRight(callee)((argument, result) => new Call(result, argument))

  def function(parameters: String*) = (body: Expression) =>
    parameters.foldRight(body)((variable, result) => new Lambda(variable, result)).asInstanceOf[Lambda]
}

abstract class Operation(val arguments: List[Expression]) extends Expression {
  def apply(values: List[Value]) : Value
  def cloneWithArguments(arguments: List[Expression]) : Operation
}

case class Addition(first: Expression, second: Expression) extends Operation(List.apply(first,second)) {
  override def toString = first.toString + " + " + second
  override def apply(values: List[Value]) = (values(0), values(1)) match {
    case (IntValue(a), IntValue(b)) => a + b
    case _ => throw new RuntimeException("can only add integers")
  }

  def cloneWithArguments(arguments: List[Expression]): Operation = new Addition(arguments(0),arguments(1))
}

case class Equals(first: Expression, second: Expression) extends Operation(List.apply(first,second)) {
  override def toString = s"${addParenthesisIfNecessary(first.toString)} == ${addParenthesisIfNecessary(second.toString)}"
  override def apply(values: List[Value]) = values(0).equals(values(1))

  def cloneWithArguments(arguments: List[Expression]): Operation = new Equals(arguments(0),arguments(1))
}

case class Call(callee: Expression, argument: Expression) extends Expression {
  override def toString = addParenthesisIfNecessary(callee.toString) + "(" + argument + ")"
}

case class Variable(name: String) extends Expression {
  override def toString = name.toString
}

case class Let(variable: String, value: Expression, body: Expression) extends Expression {
  override def toString = s"let $variable = $value in $body"
}

case class Lambda(variable: String, body: Expression) extends Expression {
  override def toString = s"$variable => $body"
}

case class If(condition: Expression, thenExpression: Expression, elseExpression: Expression) extends Expression {
  override def toString = s"if ${addParenthesisIfNecessary(condition.toString)} then $thenExpression else $elseExpression"
}

trait Expression {
  def addParenthesisIfNecessary(input: String) = if (input.contains(' ')) "(" + input + ")" else input

  def +(b: Expression) = new Addition(this, b)
  def -(b: Int) = new Addition(this, -1 * b)

}
