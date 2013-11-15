package typeChecker

abstract class Type
object IntType extends Type
case class LambdaType(input: Type, output: Type) extends Type
case class VariableType(name: String) extends Type