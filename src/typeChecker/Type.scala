package typeChecker

abstract class Type
object IntType extends Type
{
  override def toString() = "Int"
}
case class LambdaType(input: Type, output: Type) extends Type
{
  override def toString() = s"$input -> $output"
}
case class TypeVariable(name: Int) extends Type
{
  override def toString() = "$" + name
}
case class Polymorphic(name: TypeVariable, typ: Type) extends Type
{
  override def toString() = s"$name => $typ"
}