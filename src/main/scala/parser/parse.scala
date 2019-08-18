package parser

import fastparse._, SingleLineWhitespace._

case class Person(name: String)

case class Best(person: Person)

object Parser {
  def developer[_: P] = P("developer".?)

  def the[_: P] = P("the".?)

  def person[_: P]: P[Person] = P(CharIn("A-Z") ~ CharsWhile(_ != ' ')).!.map(Person)

  def best[_: P]: P[Best] = P(person ~ "is" ~ the ~ "best" ~ developer).map(Best)

  def parse(s: String): Either[String, Best] = fastparse.parse(s, best(_)) match {
    case Parsed.Success(statement, _) => Right(statement)
    case Parsed.Failure(message, _, _) => Left(message)
  }
}
