package app

import parser._
import solver._
import io.BufferedSource
import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }
import org.specs2.mutable._

class MockSolverFactory(val expectedStatements: Seq[Statement], val cannedResult: Seq[Person]) extends SolverFactory {
  class MockSolver(val statements: Seq[Statement]) extends Solver {
    def solve(): Option[Seq[Person]] = if (statements == expectedStatements) Some(cannedResult) else None
  }

  def createSolver(statements: Seq[Statement]): Solver = new MockSolver(statements)
}

class AppSpec extends Specification {
  "The app" should {
    "read the problem from standard input and write the solution to standard output" in {
      val mockSolverFactory = new MockSolverFactory(Seq(Best(Person("Jane")), Worst(Person("Harry"))), Seq(Person("Jane"), Person("Harry")))
      val input = new ByteArrayInputStream("Jane is best\nHarry is worst".getBytes)
      val output = new ByteArrayOutputStream
      Console.withIn(input) {
        Console.withOut(output) {
          new TenTimes(mockSolverFactory).run
        }
      }

      output.toString must beEqualTo("Jane is the team's 10x developer.\nThe developers ranked from best to worst are: Jane, Harry.\n")
    }

    "display a message if there's no 10x developer" in {
      val mockSolverFactory = new MockSolverFactory(Seq(Best(Person("Jane"))), Seq(Person("Jane"), Person("Harry")))
      val input = new ByteArrayInputStream("".getBytes)
      val output = new ByteArrayOutputStream
      Console.withIn(input) {
        Console.withOut(output) {
          new TenTimes(mockSolverFactory).run
        }
      }

      output.toString must beEqualTo("There's no single 10x developer.\n")
    }

    "display an error message for an unparseable line and continue" in {
      val mockSolverFactory = new MockSolverFactory(Seq(Best(Person("Jane")), Worst(Person("Harry"))), Seq(Person("Jane"), Person("Harry")))
      val input = new ByteArrayInputStream("Jane is best\nFoo bar baz\nHarry is worst".getBytes)
      val output = new ByteArrayOutputStream
      val error = new ByteArrayOutputStream
      Console.withIn(input) {
        Console.withOut(output) {
          Console.withErr(error) {
            new TenTimes(mockSolverFactory).run
          }
        }
      }

      error.toString must beEqualTo("Couldn't understand \"Foo bar baz\". Please try again.\n")
      output.toString must beEqualTo("Jane is the team's 10x developer.\nThe developers ranked from best to worst are: Jane, Harry.\n")
    }
  }
}
