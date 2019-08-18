package parser

import fastparse._, SingleLineWhitespace._

case class Person(name: String)

sealed trait Statement
case class Best(person: Person) extends Statement
case class Better(better: Person, worse: Person) extends Statement
case class DirectlyAboveOrBelow(subject: Person, objekt: Person) extends Statement
case class Worst(person: Person) extends Statement
case class Not(statement: Statement) extends Statement

private object Parts {
  def aboveOrBelow[_: P] = P(("above" ~ "or" ~ "below") | ("below" ~ "or" ~ "above"))

  def developer[_: P] = P("developer".?)

  def person[_: P]: P[Person] = P(CharIn("A-Z") ~ CharsWhile(_ != ' ')).!.map(Person)

  def subjectAndVerb[_: P] = P(person ~ "is" ~ "not".?)

  def the[_: P] = P("the".?)
}

object Parser {
  import Parts._

  def better[_: P]: P[Better] = P(subjectAndVerb ~ "a".? ~ "better" ~ developer ~ "than".? ~ person).map {
    case (better, worse) => Better(better, worse)
  }

  def best[_: P]: P[Best] = P(subjectAndVerb ~ the ~ "best" ~ developer).map(Best)

  def directlyAboveOrBelow[_: P]: P[DirectlyAboveOrBelow] = P(subjectAndVerb ~ "directly" ~ aboveOrBelow ~ person ~ "as" ~ "a" ~ developer).map {
    case (subject, objekt) => DirectlyAboveOrBelow(subject, objekt)
  }

  def worst[_: P]: P[Worst] = P(subjectAndVerb ~ the ~ "worst" ~ developer).map(Worst)

  def positiveStatement[_: P]: P[Statement] = P(best | better | directlyAboveOrBelow | worst)

  def negativeStatement[_: P]: P[Not] = P(&(person ~ "is" ~ "not") ~ positiveStatement).map(Not)

  def statement[_: P]: P[Statement] = P(negativeStatement | positiveStatement)

  def parse(s: String): Either[String, Statement] = fastparse.parse(s, statement(_)) match {
    case Parsed.Success(statement, _) => Right(statement)
    case Parsed.Failure(message, _, _) => Left(message)
  }
}
