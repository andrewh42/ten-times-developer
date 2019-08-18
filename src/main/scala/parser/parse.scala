package parser

import fastparse._, SingleLineWhitespace._

case class Person(name: String)

sealed trait Statement
case class Best(person: Person) extends Statement
case class Not(statement: Statement) extends Statement

object Parser {
  def developer[_: P] = P("developer".?)

  def the[_: P] = P("the".?)

  def not[_: P] = P("not")

  def person[_: P]: P[Person] = P(CharIn("A-Z") ~ CharsWhile(_ != ' ')).!.map(Person)

  def best[_: P]: P[Best] = P(person ~ "is" ~ not.? ~ the ~ "best" ~ developer).map(Best)

  def positiveStatement[_: P]: P[Statement] = P(best)

  def negativeStatement[_: P]: P[Not] = P(&(person ~ "is" ~ "not") ~ positiveStatement).map(Not)

  def statement[_: P]: P[Statement] = P(negativeStatement | positiveStatement)

  def parse(s: String): Either[String, Statement] = fastparse.parse(s, statement(_)) match {
    case Parsed.Success(statement, _) => Right(statement)
    case Parsed.Failure(message, _, _) => Left(message)
  }
}
