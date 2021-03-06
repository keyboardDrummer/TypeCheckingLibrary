package ast


trait Value extends Expression
{
}

case class Closure(env: scala.collection.Map[String,Expression], lambda: Lambda) extends Value {
  override def toString = env.keys + ": " + lambda.toString
  override def hashCode = lambda.hashCode
  override def equals(that: Any) = that.equals(lambda)
}
case class IntValue(value: Int) extends Value
{
  override def toString = value.toString
}