package solver

import parser._
import z3.scala._, dsl._

class Solver(val statements: Seq[Statement]) {
  def solve(): Seq[Person] = {
    val persons = PersonsBuilder.build(statements)
      .map(person => person -> IntVar())
      .toMap

    val minVal = IntConstant(0)
    val maxVal = IntConstant(persons.size - 1)

    val ctx = new Z3Context("MODEL" -> true)
    val solver = ctx.mkSolver

    solver.assertCnstr(Distinct(persons.values.toSeq: _*))
    for (value <- persons.values) {
      solver.assertCnstr(value >= minVal && value <= maxVal)
    }

    println("solver checked after basic constraints", solver.check)

    for (statement <- statements) {
      val constraint: Tree[BoolSort] = statement match {
        case Best(person) => persons(person) === minVal
        case Better(better, worse) => persons(better) < persons(worse)
      }

      solver.assertCnstr(constraint)
    }

    val status = solver.check
    println("solver checked", status)

    val model = solver.getModel

//    for ((person, intvar) <- persons) {
//      println(person, model.eval(intvar.ast(ctx)))
//    }

    val evals = persons
      .toSeq
      .map { case (p, v) => (p, model.eval(v.ast(ctx)).get) }
    println("evals", evals);

    val rankedPersons = ???
//    val rankedPersons = persons
//      .toSeq
//      .map { case (p, v) => (p, model.eval(v.ast(ctx))) }
//      .sortWith((a, b) => a._2.get < b._2.get)
//      .map { case (person, intvar) => person }

    ctx.delete

    rankedPersons
  }
}
