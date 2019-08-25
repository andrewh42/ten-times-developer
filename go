#!/bin/sh -e

available () {
  return $(hash $1 2>/dev/null)
}

check_for_java () {
  if ! available javac; then
    echo "$0: javac not found: please install Java JDK 11 and try again" >&2
    exit 1
  fi
}

check_for_python () {
  if ! available python; then
    echo "$0: python not found: please install and try again" >&2
    exit 1
  fi
}

chdir_to_extras () {
  test -d extras || mkdir extras
  cd extras
}

install_sbt () {
  if ! available sbt; then
    if [ ! -d extras/sbt ]; then
      printf "Installing SBT " >&2
      ( chdir_to_extras; curl -s https://sbt-downloads.cdnedge.bluemix.net/releases/v1.2.8/sbt-1.2.8.tgz | tar xzf - )
      echo "✓" >&2
    fi

    export PATH="$(pwd)/extras/sbt/bin:$PATH"
  fi
}

install_Z3 () {
  if [ ! -d unmanaged ]; then
    echo "Installing the Z3 prover:" >&2

    (
      chdir_to_extras

      if [ ! -d ScalaZ3 ]; then
        printf "  - cloning the ScalaZ3 git repo " >&2
        git clone -q https://github.com/epfl-lara/ScalaZ3.git
        echo "✓" >&2
      fi

      printf "  - building the C++ library and Scala binding " >&2
      cd ScalaZ3
      sbt --error +package >/dev/null 2>&1
      echo "✓" >&2
    )

    mkdir unmanaged && cp extras/ScalaZ3/target/scala-2.13/scalaz3_2.13-*.jar unmanaged
  fi
}

find_the_10x_developer () {
  echo "Building and running the 10x developer problem solver..." >&2
  echo "" >&2

  cat <<EOD | sbt --error run 2>/dev/null
Jessie is not the best developer
Evan is not the worst developer
John is not the best developer or the worst developer
Sarah is a better developer than Evan
Matt is not directly below or above John as a developer
John is not directly below or above Evan as a developer
EOD
}

###
### main script starts here
###

check_for_java
check_for_python
install_sbt
install_Z3
find_the_10x_developer
