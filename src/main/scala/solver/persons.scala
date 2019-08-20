package solver

import parser._

object PersonsBuilder {
  def build(statements: Seq[Statement]): Set[Person] = statements
    .map(_ match {
      case Best(person) => person
      case _ => ???
    })
    .toSet
}
