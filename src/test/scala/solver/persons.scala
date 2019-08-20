package solver

import PersonsBuilder.build
import parser._
import org.specs2.mutable._

class PersonsBuilderSpec extends Specification {
  "The persons builder" should {
    "handle any one single-person statement" in {
      build(Seq(Best(Person("Fred")))) must beEqualTo(Set(Person("Fred")))
      build(Seq(Worst(Person("Fred")))) must beEqualTo(Set(Person("Fred")))
    }

    "handle any one multi-person statement" in {
      build(Seq(Better(Person("Jane"), Person("Fred")))) must beEqualTo(Set(Person("Fred"), Person("Jane")))
      build(Seq(DirectlyAboveOrBelow(Person("Fred"), Person("Jane")))) must beEqualTo(Set(Person("Fred"), Person("Jane")))
    }

    "handle any one composite statement" in {
      build(Seq(Or(Best(Person("Fred")), Worst(Person("Jane"))))) must beEqualTo(Set(Person("Fred"), Person("Jane")))
      build(Seq(Not(Best(Person("Fred"))))) must beEqualTo(Set(Person("Fred")))
    }

    "handle multiple statements" in {
      build(Seq(Best(Person("Fred")), Worst(Person("Jane")))) must beEqualTo(Set(Person("Fred"), Person("Jane")))
    }
  }
}
