#!/bin/sh
mvn -Pskip-test-in-release release:clean
mvn -Pskip-test-in-release release:prepare
mvn -Pskip-test-in-release release:perform

