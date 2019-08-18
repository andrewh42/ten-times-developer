package parser

import org.specs2.mutable._

class ParserSpec extends Specification {
  "The Parser" should {
    "parse best statements" in {
      Parser.parse("Jessie is the best developer") must beEqualTo(Right(Best(Person("Jessie"))))
    }

    "parse worst statements" in {
      Parser.parse("Jessie is the worst developer") must beEqualTo(Right(Worst(Person("Jessie"))))
    }

    "parse negative statements" in {
      Parser.parse("Jessie is not the best developer") must beEqualTo(Right(Not(Best(Person("Jessie")))))
      Parser.parse("Jessie is not the worst developer") must beEqualTo(Right(Not(Worst(Person("Jessie")))))
    }
  }
}
