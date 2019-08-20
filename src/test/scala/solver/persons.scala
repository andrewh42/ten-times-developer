package solver

import parser._
import z3.scala.dsl._
import org.specs2.mutable._

class PersonsBuilderSpec extends Specification {
  "The persons builder" should {
    "handle one single-person statement" in {
      PersonsBuilder.build(Seq(Best(Person("Fred")))) must beEqualTo(Set(Person("Fred")))
    }
  }
}
