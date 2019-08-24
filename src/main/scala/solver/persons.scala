package solver

import parser._

private object PersonsBuilder {
  /** Extracts the persons referred to in the provided statements. */
  def build(statements: Seq[Statement]): Set[Person] = statements
    .flatMap(_ match {
      case Best(person) => Seq(person)
      case Worst(person) => Seq(person)
      case Better(person1, person2) => Seq(person1, person2)
      case DirectlyAboveOrBelow(person1, person2) => Seq(person1, person2)
      case Or(statement1, statement2) => build(Seq(statement1, statement2))
      case Not(statement) => build(Seq(statement))
    })
    .toSet
}
