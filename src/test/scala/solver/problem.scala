package solver

import parser.{ Person, Best, Better, DirectlyAboveOrBelow, Worst, Not => NotStatement, Or => OrStatement }
import z3.scala.dsl._
import org.specs2.mutable._

class ProblemBuilderSpec extends Specification {
  "The problem builder" should {
    "create a single person to rank variable map" in {
      new ProblemBuilder(Seq(Best(Person("Fred")))).persons must beEqualTo(Map(Person("Fred") -> IntVar()))
    }

    "create a multi-person to rank variable map" in {
      new ProblemBuilder(Seq(Best(Person("Fred")), Worst(Person("Muriel")))).persons must beEqualTo(Map(
        Person("Fred") -> IntVar(),
        Person("Muriel") -> IntVar(),
      ))
    }

    "construct constraints for " >> {
      "a best statement" in {
        var builder = new ProblemBuilder(Seq(Best(Person("Fred"))))
        val fred = builder.persons(Person("Fred"))
        var expected: Seq[Tree[BoolSort]] = Seq(
          fred >= IntConstant(0) && fred <= IntConstant(0),
          Distinct(Seq(fred): _*),
          fred === builder.minVal,
        )

        builder.constraints must beEqualTo(expected)
      }

      "a better statement" in {
        var builder = new ProblemBuilder(Seq(Better(Person("Jane"), Person("Fred"))))
        val fred = builder.persons(Person("Fred"))
        val jane = builder.persons(Person("Jane"))
        var expected: Seq[Tree[BoolSort]] = Seq(
          fred >= IntConstant(0) && fred <= IntConstant(1),
          jane >= IntConstant(0) && jane <= IntConstant(1),
          Distinct(Seq(fred, jane): _*),
          jane < fred,
        )

        builder.constraints must beEqualTo(expected)
      }

      "a directly above or below statement" in {
        var builder = new ProblemBuilder(Seq(DirectlyAboveOrBelow(Person("Fred"), Person("Sarah"))))
        val fred = builder.persons(Person("Fred"))
        val sarah = builder.persons(Person("Sarah"))
        var expected: Seq[Tree[BoolSort]] = Seq(
          fred >= IntConstant(0) && fred <= IntConstant(1),
          sarah >= IntConstant(0) && sarah <= IntConstant(1),
          Distinct(Seq(fred, sarah): _*),
          (fred !== sarah - 1) && (fred !== sarah + 1),
        )

        builder.constraints must beEqualTo(expected)
      }

      "a not statement" in {
        var builder = new ProblemBuilder(Seq(NotStatement(DirectlyAboveOrBelow(Person("Fred"), Person("Sarah")))))
        val fred = builder.persons(Person("Fred"))
        val sarah = builder.persons(Person("Sarah"))
        var expected: Seq[Tree[BoolSort]] = Seq(
          fred >= IntConstant(0) && fred <= IntConstant(1),
          sarah >= IntConstant(0) && sarah <= IntConstant(1),
          Distinct(Seq(fred, sarah): _*),
          Not((fred !== sarah - 1) && (fred !== sarah + 1)),
        )

        builder.constraints must beEqualTo(expected)
      }

      "an or statement" in {
        var builder = new ProblemBuilder(Seq(OrStatement(Best(Person("Fred")), Worst(Person("Sarah")))))
        val fred = builder.persons(Person("Fred"))
        val sarah = builder.persons(Person("Sarah"))
        var expected: Seq[Tree[BoolSort]] = Seq(
          fred >= IntConstant(0) && fred <= IntConstant(1),
          sarah >= IntConstant(0) && sarah <= IntConstant(1),
          Distinct(Seq(fred, sarah): _*),
          fred === IntConstant(0) || sarah === IntConstant(1),
        )

        builder.constraints must beEqualTo(expected)
      }

      "a worst statement" in {
        var builder = new ProblemBuilder(Seq(Worst(Person("Fred"))))
        val fred = builder.persons(Person("Fred"))
        var expected: Seq[Tree[BoolSort]] = Seq(
          fred >= IntConstant(0) && fred <= IntConstant(0),
          Distinct(Seq(fred): _*),
          fred === builder.maxVal,
        )

        builder.constraints must beEqualTo(expected)
      }
    }
  }
}

