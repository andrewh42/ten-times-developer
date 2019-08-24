package solver

import parser._
import org.specs2.mutable._

class Z3SolverSpec extends Specification {
  sequential

  "The Z3 solver" should {
    "solve a single-person problem" in {
      new Z3Solver(Seq(Best(Person("Fred")))).solve must beEqualTo(Some(Seq(Person("Fred"))))
    }

    "solve a two-person problem" in {
      new Z3Solver(Seq(Better(Person("Sally"), Person("Fred")))).solve must beEqualTo(Some(Seq(Person("Sally"), Person("Fred"))))
    }

    "solve not directly above or below" in {
      new Z3Solver(Seq(Not(DirectlyAboveOrBelow(Person("Sally"), Person("Fred"))), Better(Person("Sally"), Person("Fiona")))).solve must
        beEqualTo(Some(Seq(Person("Sally"), Person("Fiona"), Person("Fred"))))
    }
  }
}
