#!/bin/bash

set +x

if [[ "$TRAVIS_PULL_REQUEST" != "false" ]]; then
  echo "Nothing to deliver, this is a pull request."
  exit $?
elif [[ "$TRAVIS_BRANCH" != "master" && "$TRAVIS_BRANCH" != release/* ]]; then
  echo "Nothing to deliver, not master or release."
  exit $?
else

    APP_NAME_PRODUCTION=app-production-release-1.0-
    APP_NAME_STAGING=app-staging-release-1.0-
    OUTPUTDIR=$PWD/app/build/outputs/apk
    BUILD_NUMBER=$TRAVIS_BUILD_NUMBER
    RELEASE_NOTES="($TRAVIS_BRANCH): $BUILD_NUMBER"

    echo -e "Release notes:\n$RELEASE_NOTES"

    echo "Uploading build $BUILD_NUMBER."

    echo "Contents of $OUTPUTDIR:"
    ls -l $OUTPUTDIR

    # Upload Stage
    FULL_APP_NAME_STAGING="$APP_NAME_STAGING$BUILD_NUMBER.apk"
    FULL_APP_PATH_STAGING="$OUTPUTDIR/$FULL_APP_NAME_STAGING"
    if [ -e "$FULL_APP_PATH_STAGING" ]
    then
      echo "Uploading $FULL_APP_NAME_STAGING to Hockeyapp."
      curl https://rink.hockeyapp.net/api/2/apps/upload \
        -H "X-HockeyAppToken: $HOCKEYAPP_API_TOKEN" \
        -F "ipa=@$FULL_APP_PATH_STAGING" \
        -F "notes=$RELEASE_NOTES" \
        -F "notes_type=0" \
        -F "status=1"
    else
      echo "Staging apk was not found, not uploading"
    fi

    # Upload Prod
    FULL_APP_NAME_PRODUCTION="$APP_NAME_PRODUCTION$BUILD_NUMBER.apk"
    FULL_APP_PATH_PRODUCTION="$OUTPUTDIR/$FULL_APP_NAME_PRODUCTION"
    if [ -e "$FULL_APP_PATH_PRODUCTION" ]
    then
      echo "Uploading $FULL_APP_NAME_PRODUCTION to Hockeyapp."
      curl https://rink.hockeyapp.net/api/2/apps/upload \
        -H "X-HockeyAppToken: $HOCKEYAPP_API_TOKEN" \
        -F "ipa=@$FULL_APP_PATH_PRODUCTION" \
        -F "notes=$RELEASE_NOTES" \
        -F "notes_type=0" \
        -F "status=1"
    else
      echo "Production apk was not found, not uploading"
    fi

fi
