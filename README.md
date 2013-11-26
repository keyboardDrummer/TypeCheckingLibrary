Partial Evaluation Scala
======================

The goal of this project was to see if I could write a language which is untyped by itself, but which allows you to write a typechecker in a library and apply that library *at compile-time*.

This requires the following ingredients:

  1 Define a simple expression language to toy with.
  2 Metaprogramming features to allow writing a typechecker in a library and applying that to a program. 
  3 Partial evaluation optimization in the compiler that will run the typechecker library at compile-time.

I've completed step 1 and 3. Step 3 enables partial evaluation in the following way.

Given this program:
program x = if x == 0 then 0 else fibonacci 6
fibonacci i = if i == 0 or i == 1 then 1 else fibonacci (i - 1) + fibonacci (i - 2)

It will be rewritten to:
program x = if x == 0 then 0 else 8

======================

While working on this project I defined two type inferencers which I think are different in an interesting way. The first inferencer is similar to a classical Hindley-Milner type inferencer, with support for polymorphic types. Given an expression it returns the expression's type. Types are expressed using several kinds: polymorphic types, function types, value types.

The second inferencer, which I'll call the type interpreter, considers value types as just values, and types simply as expressions.. It operates by evaluating the program in a way similar to how an interpreter would. However it only uses value types and not actual values, so it cannot evaluate branches. This leads to undeterministic computation. The type interpreter determines that the program is validly typed when it finds that all computation paths leads to the same end type.

A question that I find interesting is whether 'types' produced by the type interpreter can be optimized using partial evaluation to become as simple as the types produced by the classical type inferencer.

Another question that interests me is whether I can define an 'AbstractMachine' that is used to implement both the interpreter, rewriter, and type inferencing for the language. It think it should be possible given that all three algorithms deal with the same semantics of the language.
