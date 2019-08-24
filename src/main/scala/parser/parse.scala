package parser

import fastparse._, SingleLineWhitespace._

case class Person(name: String)

sealed trait Statement
case class Best(person: Person) extends Statement
case class Better(better: Person, worse: Person) extends Statement
case class DirectlyAboveOrBelow(subject: Person, objekt: Person) extends Statement
case class Worst(person: Person) extends Statement

case class Not(statement: Statement) extends Statement
case class Or(statement1: Statement, statement2: Statement) extends Statement

private object StatementsAndParts {
  def aboveOrBelow[_: P] = P(("above" ~ "or" ~ "below") | ("below" ~ "or" ~ "above"))

  def best[_: P] = P(the ~ "best" ~ developer)

  def developer[_: P] = P("as".? ~ "a".? ~ "developer".?)

  def orPart[_: P] = P(best.!.map(Unit => "best") | worst.!.map(Unit => "worst"))

  def person[_: P]: P[Person] = P(CharIn("A-Z") ~ CharsWhile(_ != ' ')).!.map(Person)

  def subjectAndVerb[_: P] = P(person ~ "is" ~ "not".?)

  def the[_: P] = P("the".?)

  def worst[_: P] = P(the ~ "worst" ~ developer)

  def betterStatement[_: P]: P[Better] = P(subjectAndVerb ~ "a".? ~ "better" ~ developer ~ "than".? ~ person).map {
    case (better, worse) => Better(better, worse)
  }

  def bestStatement[_: P]: P[Best] = P(subjectAndVerb ~ best).map(Best)

  def directlyAboveOrBelowStatement[_: P]: P[DirectlyAboveOrBelow] = P(subjectAndVerb ~ "directly" ~ aboveOrBelow ~ person ~ developer).map {
    case (subject, objekt) => DirectlyAboveOrBelow(subject, objekt)
  }

  def worstStatement[_: P]: P[Worst] = P(subjectAndVerb ~ worst).map(Worst)

  def relativeComparisonStatement[_: P]: P[Or] = P(subjectAndVerb ~ the ~ orPart ~ "or" ~ orPart)
    .filter { case (subject, bw1, bw2) => bw1 != bw2 }
    .map {
      case (subject, _, "best") => Or(Worst(subject), Best(subject))
      case (subject, _, _) => Or(Best(subject), Worst(subject))
    }

  def positiveStatement[_: P]: P[Statement] = P(
    relativeComparisonStatement |
    bestStatement |
    betterStatement |
    directlyAboveOrBelowStatement |
    worstStatement
  )

  def negativeStatement[_: P]: P[Not] = P(&(person ~ "is" ~ "not") ~ positiveStatement).map(Not)

  def statement[_: P]: P[Statement] = P(negativeStatement | positiveStatement)
}

object Parser {
  import StatementsAndParts._

  def parse(s: String): Either[String, Statement] = fastparse.parse(s, statement(_)) match {
    case Parsed.Success(statement, _) => Right(statement)
    case failure @ Parsed.Failure(message, _, _) => Left(failure.trace().msg)
  }
}
