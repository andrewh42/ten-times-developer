package solver

import parser._
import z3.scala._, dsl._

class Solver(val statements: Seq[Statement]) {
  def solve(): Seq[Person] = {
    val persons = PersonsBuilder.build(statements)
      .map(person => person -> IntVar())
      .toMap

    val ctx = new Z3Context("MODEL" -> true)
    val solver = ctx.mkSolver

    solver.assertCnstr(Distinct(persons.values.toSeq: _*))
    for (statement <- statements) {
      val constraint = statement match {
        case Best(person) => Eq(persons(person), IntConstant(persons.size - 1))
      }

      solver.assertCnstr(constraint)
    }

    val status = solver.check
    println("solver checked", status)

    val model = solver.getModel

    for ((person, intvar) <- persons) {
      println(person, model.eval(intvar.ast(ctx)))
    }

    val rankedPersons = persons.toSeq
      .sortWith((a, b) => true)
      .map { case (person, intvar) => person }

    ctx.delete

    rankedPersons
  }
}
