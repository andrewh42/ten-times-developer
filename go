#!/bin/sh

# install Z3
if [ ! -d unmanaged ]; then
  git clone https://github.com/epfl-lara/ScalaZ3.git
  ( cd Scala23; sbt +package )
  mkdir unmanaged
  cp ScalaZ3/target/scala-2.13/scalaz3_2.13-4.7.1.jar unmanaged
fi

sbt run
