#!/bin/sh

# Ensure a JDK is available even if the system Java runtime isn't registered
# (e.g. only a Homebrew JDK is installed, with no symlink under
# /Library/Java/JavaVirtualMachines).
if ! /usr/libexec/java_home >/dev/null 2>&1; then
    if [ -x /opt/homebrew/opt/openjdk@17/bin/java ]; then
        export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
    elif [ -x /usr/local/opt/openjdk@17/bin/java ]; then
        export JAVA_HOME="/usr/local/opt/openjdk@17"
    fi
    if [ -n "$JAVA_HOME" ]; then
        export PATH="$JAVA_HOME/bin:$PATH"
    fi
fi

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
