package solver

import parser.{ Person, Statement }
import z3.scala._, dsl._

trait SolverFactory {
  def createSolver(statements: Seq[Statement]): Solver
}

trait Solver {
  def solve(): Option[Seq[Person]]
}

object Z3SolverFactory extends SolverFactory {
  def createSolver(statements: Seq[Statement]): Solver = new Z3Solver(statements)
}

class Z3Solver(statements: Seq[Statement]) extends Solver {
  private val problemBuilder = new Z3ProblemBuilder(statements)

  /** @return Persons ordered from best to worst. */
  def solve(): Option[Seq[Person]] =
    solveWithZ3(problemBuilder.constraints).map { personAndRanks =>
      personAndRanks
        .sortWith((a, b) => a._2 < b._2)
        .map { case (person, rank) => person }
    }

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
