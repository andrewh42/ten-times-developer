package app

import io.StdIn
import parser.{ Parser, Statement }
import solver.{ SolverFactory, Z3SolverFactory }

object TenTimes extends App {
  new TenTimes(Z3SolverFactory).run
}

class TenTimes(val solverFactory: SolverFactory) {
  def run(): Unit = {
    val statements = readAndParseStatementsFromStdIn.foldLeft(Seq().asInstanceOf[Seq[Statement]]) { (accum, statementEither) => statementEither match {
      case Right(statement) => accum :+ statement
      case Left(_) => accum
    }}

    solverFactory.createSolver(statements).solve match {
      case Some(solution) => {
        val namesOnly = solution.map(_.name)

        println(s"${namesOnly.head} is the team's 10x developer.")
        println(s"""The developers ranked from best to worst are: ${namesOnly.mkString(", ")}.""")
      }
      case None => println("There's no single 10x developer.")
    }
  }

  protected def readAndParseStatementsFromStdIn(): Iterator[Either[String, Statement]] =
    for (line <- Iterator.continually(StdIn.readLine).takeWhile(_ != null)) yield {
      val statement = Parser.parse(line)
      statement.left.foreach(_ => Console.err.println(s"""Couldn't understand "${line}". Please try again."""))
      statement
    }
}
