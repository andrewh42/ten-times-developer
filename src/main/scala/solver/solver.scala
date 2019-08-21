package solver

import parser._
import z3.scala._, dsl._

class Solver(val statements: Seq[Statement]) {
  protected val persons = PersonsBuilder.build(statements)
    .map(person => person -> IntVar())
    .toMap

  protected val minVal = IntConstant(0)

  protected val maxVal = IntConstant(persons.size - 1)

  def solve(): Option[Seq[Person]] = {
    val ctx = new Z3Context("MODEL" -> true)
    val solver = ctx.mkSolver

    solver.assertCnstr(Distinct(persons.values.toSeq: _*))
    for (value <- persons.values) {
      solver.assertCnstr(value >= minVal && value <= maxVal)
    }

    for (constraint <- contraintsForStatements()) {
      solver.assertCnstr(constraint)
    }

    val status = solver.check
    val model = solver.getModel
    val personValues = persons
      .toSeq
      .map { case (p, v) => (p, model.evalAs[Int](v.ast(ctx))) }

    val init: Option[Seq[(Person, Int)]] = Some(Seq())
    val personAndRanksOpt: Option[Seq[(Person, Int)]] = personValues.foldLeft(init) { (accumOpt, curr) => curr match {
        case (person, Some(currVal)) => accumOpt.map(accum => accum :+ (person, currVal))
        case _ => None
      }
    }

    val rankedPersons = personAndRanksOpt.map(personAndRanks =>
      personAndRanks
        .sortWith((a, b) => a._2 < b._2)
        .map { case (person, rank) => person }
    )

    ctx.delete

    rankedPersons
  }

  protected def contraintsForStatements(): Seq[Tree[BoolSort]] = statements.map { _ match {
    case Best(person) => persons(person) === minVal
    case Better(better, worse) => persons(better) < persons(worse)
  }}
}
