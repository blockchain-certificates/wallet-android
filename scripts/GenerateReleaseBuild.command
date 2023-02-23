#!/bin/sh

cd "./LearningMachine/"

echo "version code (match versionCode in build.gradle):"
read versioncode

echo "key password:"
read keypassword

echo "store password:"
read storepassword

export BUILD_NUMBER=$versioncode
export SIGNING_KEY_ALIAS="key0"
export SIGNING_KEY_PASSWORD=$keypassword
export SIGNING_STORE_PASSWORD=$storepassword

./gradlew assembleRelease

open ./app/build/outputs/apk/production/release/

#zipalign -v -p 4 my-app-unsigned.apk my-app-unsigned-aligned.apk

#apksigner sign --ks my-release-key.jks --out my-app-release.apk my-app-unsigned-aligned.apk

#jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore ../keys/blockcerts.jks  ./app/production/release/productionRelease-1.1.0.apk alias_name.
