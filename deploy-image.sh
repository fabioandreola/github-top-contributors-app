#!/bin/bash

failIfError() {
    RESULT=$?
    if [ $RESULT != 0 ]; then
      echo Deploy image failed with error $RESULT
      exit 1
    fi
}

echo "Building project"
./gradlew clean build
failIfError

echo "Building docker image"
docker build . -t fabioandreola/github-app:latest
failIfError

echo "Tagging image"
docker tag fabioandreola/github-app fabioandreola/personal:github-top-contributors
failIfError

echo "Pushing to repository"
docker push fabioandreola/personal
failIfError
