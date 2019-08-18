package parser

import Parser.parse
import org.specs2.mutable._

class ParserSpec extends Specification {
  "The Parser" should {
    "parse best statements" in {
      parse("Jessie is the best developer") must beEqualTo(Right(Best(Person("Jessie"))))
    }

    "parse worst statements" in {
      parse("Jessie is the worst developer") must beEqualTo(Right(Worst(Person("Jessie"))))
    }

    "parse better statements" in {
      parse("Sarah is a better developer than Evan") must beEqualTo(Right(Better(Person("Sarah"), Person("Evan"))))
    }

    "parse negative statements" in {
      parse("Jessie is not the best developer") must beEqualTo(Right(Not(Best(Person("Jessie")))))
      parse("Jessie is not the worst developer") must beEqualTo(Right(Not(Worst(Person("Jessie")))))
    }
  }
}
