#!/bin/bash

set +x

#### BUILD
if [[ "$TRAVIS_PULL_REQUEST" != "false" ]]; then
    echo "PR build, running tests"
    ./gradlew clean test -PdisablePreDex
    exit $?
elif [[ ( "$TRAVIS_BRANCH" == "master" ) ]]; then
    echo "This is a master merge. Building Release"
    export BUILD_NUMBER=$TRAVIS_BUILD_NUMBER
    ./gradlew clean assembleRelease -PdisablePreDex
    exit $?
elif [[ ("$TRAVIS_BRANCH" == release/*) ]]; then
    echo "Building QA, Staging, and Production releases on $TRAVIS_BRANCH"
    export BUILD_NUMBER=$TRAVIS_BUILD_NUMBER
    ./gradlew clean assembleStagingRelease assembleProductionRelease -PdisablePreDex
    exit $?
else
    echo "Testing on a branch other than master or release. Running tests"
    ./gradlew clean testDebug -PdisablePreDex
    exit $?
fi
