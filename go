#!/bin/sh -o pipefail

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

run_step () {
  local errorlog=/tmp/$$.errorlog
  eval $* 2>$errorlog
  if [ $? -eq 0 ]; then
    rm $errorlog
  else
    echo x
    echo
    echo "Failed with error:" >&2
    cat $errorlog >&2
    rm $errorlog
    exit 2
  fi
}

installation_step () {
  local title=$1
  local runlog=/tmp/$$.log

  printf "$title" >&2
  shift
  eval $* >$runlog 2>&1
  local result=$?
  if [ $result -eq 0 ]; then
    echo ✓ >&2
    rm $runlog
  else
    echo x
    echo
    echo "Failed with error while running the command:" >&2
    echo "  $*" >&2
    echo "Execution logs:" >&2
    cat $runlog >&2
    rm $runlog
    exit 2
  fi
}

install_sbt () {
  if ! available sbt; then
    if [ ! -d extras/sbt ]; then
      installation_step "Installing SBT " "( chdir_to_extras; curl -sS https://sbt-downloads.cdnedge.bluemix.net/releases/v1.2.8/sbt-1.2.8.tgz | tar xzf - )"
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
        installation_step "  - cloning the ScalaZ3 git repo " git clone -q https://github.com/epfl-lara/ScalaZ3.git
      fi

      cd ScalaZ3
      installation_step "  - building the C++ library and Scala binding " sbt --error +package
    )

    mkdir unmanaged && cp extras/ScalaZ3/target/scala-2.13/scalaz3_2.13-*.jar unmanaged
  fi
}

find_the_10x_developer_inner () {
  cat <<EOD | sbt --error run 2>/dev/null
Jessie is not the best developer
Evan is not the worst developer
John is not the best developer or the worst developer
Sarah is a better developer than Evan
Matt is not directly below or above John as a developer
John is not directly below or above Evan as a developer
EOD
}

find_the_10x_developer () {
  echo "Building and running the 10x developer problem solver..." >&2
  echo >&2
  run_step find_the_10x_developer_inner
}

###
### main script starts here
###

check_for_java
check_for_python
install_sbt
install_Z3
find_the_10x_developer
