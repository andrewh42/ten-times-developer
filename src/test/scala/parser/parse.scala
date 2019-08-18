package parser

import org.specs2.mutable._

class ParserSpec extends Specification {
  "The Parser" should {
    "parse best statements" in {
      Parser.parse("Jessie is the best developer") must beEqualTo(Right(Best(Person("Jessie"))))
    }
  }
}
