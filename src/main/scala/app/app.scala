package app

import io.BufferedSource
import parser.{ Parser, Statement }
import solver.{ SolverFactory, Z3SolverFactory }

object TenTimes extends App {
  new TenTimes(Z3SolverFactory, io.Source.stdin).run()
}

class TenTimes(val solverFactory: SolverFactory, val in: BufferedSource) {
  def run(): Unit = {
    val statementEithers = for (line <- in.getLines) yield {
      val statement = Parser.parse(line)
      statement.left.foreach(_ => Console.err.println(s"""Couldn't understand "${line}". Please try again."""))
      statement
    }

    val statements = statementEithers.foldLeft(Seq().asInstanceOf[Seq[Statement]]) { (accum, statementEither) => statementEither match {
      case Right(statement) => accum :+ statement
      case Left(_) => accum
    }}

    solverFactory.createSolver(statements).solve() match {
      case Some(solution) => {
        val namesOnly = solution.map(_.name)

        println(s"${namesOnly.head} is the team's 10x developer.")
        println(s"""The developers ranked from best to worst are: ${namesOnly.mkString(", ")}.""")
      }
      case None => println("THere's no single 10x developer.")
    }
  }
}
