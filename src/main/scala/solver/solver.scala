package solver

import parser.{ Person, Statement }
import z3.scala._, dsl._

class Solver(val statements: Seq[Statement]) {
  protected val problemBuilder = new ProblemBuilder(statements)

  /** @return Persons ordered from best to worst. */
  def solve(): Option[Seq[Person]] =
    solveWithZ3(problemBuilder.constraints()).map { personAndRanks =>
      personAndRanks
        .sortWith((a, b) => a._2 < b._2)
        .map { case (person, rank) => person }
    }

  /** @return Person-rank pairs (0 = ranked first). */
  protected def solveWithZ3(constraints: Seq[Tree[BoolSort]]): Option[Seq[(Person, Int)]] = {
    val ctx = new Z3Context("MODEL" -> true)
    val solver = ctx.mkSolver

    constraints.foreach(solver.assertCnstr)

    val status = solver.check
    val model = solver.getModel
    val personValues = problemBuilder.persons
      .toSeq
      .map { case (p, v) => (p, model.evalAs[Int](v.ast(ctx))) }

    ctx.delete

    personValues.foldLeft(Some(Seq()).asInstanceOf[Option[Seq[(Person, Int)]]]) { (accum, curr) => curr match {
      case (person, Some(currVal)) => accum.map(_ :+ (person, currVal))
      case _ => None
    }}
  }
}
