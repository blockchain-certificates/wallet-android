#!/bin/bash

set +x
cd LearningMachine

# The below block is temporary while we're troubleshooting CI builds
echo "Bolot: CI troubleshooting"
echo "Building Staging and Production releases on $TRAVIS_BRANCH"
export BUILD_NUMBER=42
./gradlew clean
./gradlew assembleStagingRelease  -PdisablePreDex
./gradlew assembleProductionRelease -PdisablePreDex
exit $?


#### BUILD
if [[ "$TRAVIS_PULL_REQUEST" != "false" || ( "$TRAVIS_BRANCH" != "master" && "$TRAVIS_BRANCH" != hotfix/* ) ]]; then
    echo "This is a pull request. No deployment will be done."
    ./gradlew clean testDevDebug -PdisablePreDex
    exit $?
else
	echo "Building Staging and Production releases on $TRAVIS_BRANCH"
	export BUILD_NUMBER=$TRAVIS_BUILD_NUMBER
	./gradlew clean
	./gradlew assembleStagingRelease  -PdisablePreDex
	./gradlew assembleProductionRelease -PdisablePreDex
	exit $?
fi
