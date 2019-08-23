package solver

import parser.{ Person, Best, Better, DirectlyAboveOrBelow, Worst, Not => NotStatement, Or => OrStatement, Statement }
import z3.scala._, dsl._

/// Builds Z3 solver constraints for the provided statements.
private class ProblemBuilder(val statements: Seq[Statement]) {
  val persons = PersonsBuilder.build(statements)
    .map(person => person -> IntVar())
    .toMap

  val minVal = IntConstant(0)

  val maxVal = IntConstant(persons.size - 1)

  protected val boundsConstraints: Seq[Tree[BoolSort]] = persons.values.toSeq.map(value => value >= minVal && value <= maxVal)

  def constraints(): Seq[Tree[BoolSort]] = (boundsConstraints :+ Distinct(persons.values.toSeq: _*)) ++ statements.map(constraintsForStatement(_))

  protected def constraintsForStatement(statement: Statement): Tree[BoolSort] = statement match {
    case Best(person) => persons(person) === minVal
    case Better(better, worse) => persons(better) < persons(worse)
    case DirectlyAboveOrBelow(subject, objekt) => (persons(subject) === persons(objekt) - 1) || (persons(subject) === persons(objekt) + 1)
    case Worst(person) => persons(person) === maxVal
    case NotStatement(statement) => Not(constraintsForStatement(statement))
    case OrStatement(statement1, statement2) => Or(constraintsForStatement(statement1), constraintsForStatement(statement2))
  }
}
