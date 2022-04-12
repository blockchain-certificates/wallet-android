#!/bin/bash

cd LearningMachine
./gradlew clean testDevDebug -PdisablePreDex -PdevBuild --console=plain
