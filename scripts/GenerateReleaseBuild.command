#!/bin/sh

# set the directory to the dir in which this script resides
newPath=`echo $0 | awk '{split($0, a, ";"); split(a[1], b, "/"); for(x = 2; x < length(b); x++){printf("/%s", b[x]);} print "";}'`
cd "$newPath/../LearningMachine/"


echo "key password:"
read keypassword

export SIGNING_KEY_ALIAS="learningmachine"
export SIGNING_KEY_PASSWORD=$keypassword
export SIGNING_STORE_PASSWORD=$keypassword

./gradlew assembleRelease

open ./app/build/outputs/apk/production/release/

#zipalign -v -p 4 my-app-unsigned.apk my-app-unsigned-aligned.apk

#apksigner sign --ks my-release-key.jks --out my-app-release.apk my-app-unsigned-aligned.apk

#jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore ../keys/learning-machine.jks  ./app/production/release/productionRelease-1.1.0.apk alias_name.
