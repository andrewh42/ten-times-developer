package solver

import parser._
import z3.scala._, dsl._

/// Builds Z3 solver constraints for the provided statements.
class ProblemBuilder(val statements: Seq[Statement]) {
  val persons = PersonsBuilder.build(statements)
    .map(person => person -> IntVar())
    .toMap

  val minVal = IntConstant(0)

  val maxVal = IntConstant(persons.size - 1)

  protected val boundsConstraints: Seq[Tree[BoolSort]] = persons.values.toSeq.map(value => value >= minVal && value <= maxVal)

  def constraints(): Seq[Tree[BoolSort]] = (boundsConstraints :+ Distinct(persons.values.toSeq: _*)) ++ statements.flatMap(constraintsForStatement(_))

  protected def constraintsForStatement(statement: Statement): Seq[Tree[BoolSort]] = statement match {
    case Best(person) => Seq(persons(person) === minVal)
    case Better(better, worse) => Seq(persons(better) < persons(worse))
    case Worst(person) => Seq(persons(person) === maxVal)
  }
}
