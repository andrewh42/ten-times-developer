package solver

import parser._
import z3.scala.dsl._
import org.specs2.mutable._

class SolverSpec extends Specification {
  "The solver" should {
    "solve a single-person problem" in {
      new Solver(Seq(Best(Person("Fred")))).solve() must beEqualTo(Seq(Person("Fred")))
    }
  }
}
