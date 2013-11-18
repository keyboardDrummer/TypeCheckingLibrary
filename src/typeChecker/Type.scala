package typeChecker

abstract class Type
object IntType extends Type
{
  override def toString() = "Int"
}
case class LambdaType(variable: String, input: Type, output: Type) extends Type
{
  override def toString() = s"$variable: $input => $output"
}
case class VariableType(name: String) extends Type
{
  override def toString() = "$" + name
}