#!/bin/bash

set +x

#### BUILD
if [[ "$TRAVIS_PULL_REQUEST" != "false" ]]; then
    echo "This is a pull request. No deployment will be done."
    ./gradlew clean testDevDebug -PdisablePreDex
    exit $?
elif [[ ( "$TRAVIS_BRANCH" != "master" && "$TRAVIS_BRANCH" != hotfix/* ) ]]; then
    echo "Testing on a branch other than master or hotfix."
    export BUILD_NUMBER=$TRAVIS_BUILD_NUMBER
    ./gradlew clean testDevDebug -PdisablePreDex
    exit $?
else
  echo "Building Staging and Production releases on $TRAVIS_BRANCH"
  export BUILD_NUMBER=$TRAVIS_BUILD_NUMBER
  ./gradlew clean assembleStagingRelease assembleProductionRelease -PdisablePreDex
    exit $?
else
  echo "Testing on a branch other than master or release. Running tests"
  ./gradlew clean testDebug -PdisablePreDex
  exit $?
fi
