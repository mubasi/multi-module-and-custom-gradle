#!/usr/bin/env bash

set -e
set -o pipefail

if [ $# -eq 0 ]
  then
    echo "Usage: build.sh [version]"
    exit 1
fi

docker build --no-cache -t asia.gcr.io/$NAMESPACE/$SERVICE:$1 --build-arg ANDROID_HOME=$2 --build-arg APP_ID=$3 --build-arg GROUPS=$4 --build-arg BUILD_NUMBER=$4 .
# docker push asia.gcr.io/$NAMESPACE/$SERVICE:$1
docker rmi asia.gcr.io/$NAMESPACE/$SERVICE:$1
