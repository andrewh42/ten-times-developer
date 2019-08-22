package solver

import parser._
import z3.scala._, dsl._

class Solver(val statements: Seq[Statement]) {
  protected val problemBuilder = new ProblemBuilder(statements)

  def solve(): Option[Seq[Person]] = {
    val ctx = new Z3Context("MODEL" -> true)
    val solver = ctx.mkSolver

    val persons = problemBuilder.persons
    val (minVal, maxVal) = (problemBuilder.minVal, problemBuilder.maxVal)

    val boundsConstraints: Seq[Tree[BoolSort]] = persons.values.toSeq.map(value => value >= minVal && value <= maxVal)

    for (constraint <- (boundsConstraints :+ Distinct(persons.values.toSeq: _*)) ++ problemBuilder.constraints()) {
      solver.assertCnstr(constraint)
    }

    val status = solver.check
    val model = solver.getModel
    val personValues = problemBuilder.persons
      .toSeq
      .map { case (p, v) => (p, model.evalAs[Int](v.ast(ctx))) }

    val personAndRanksOpt = personValues.foldLeft(Some(Seq()).asInstanceOf[Option[Seq[(Person, Int)]]]) { (accumOpt, curr) => curr match {
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
}
