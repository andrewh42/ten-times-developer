Setup
-----
Ensure you have the following installed on macOS:
* Java JDK 11
* [Command Line Tools for Xcode](https://developer.apple.com/download/more/) (`xcode-select --install` may do the trick)
* python (should already be present)

Then run `./go` from the same directory as this README file.

Run
---
The first run builds the [Z3 theorem prover](https://github.com/Z3Prover/z3) which takes 10-15 minutes on circa 2018 Apple hardware. But it's worth it for the satisfaction
of not using brute force to solve the problem. :-)

Here's what the first run looks like:

    $ ./go
    Installing SBT ✓
    Installing the Z3 prover:
      - cloning the ScalaZ3 git repo ✓
      - building the C++ library and Scala binding ✓
    Building and running the 10x developer problem solver...
    
    Sarah is the team's 10x developer.
    The developers ranked from best to worst are: Sarah, John, Jessie, Evan, Matt.

In the event of a build error, the `go` script should display the logs collected during the
failed step.
