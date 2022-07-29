#!/usr/bin/env bash

set -e
set -o pipefail

if [ $# -eq 0 ]
  then
    echo "Usage: build.sh [version]"
    exit 1
fi

docker build --no-cache -t asia.gcr.io/$NAMESPACE/$SERVICE:$1 --build-arg BUCKET=$2 --build-arg BRANCH_NAME=$3 --build-arg BUILD_NUMBER=$4 --build-arg ANDROID_HOME=$5 --build-arg DEPLOY_BUILD_DATE=$6 .
# docker push asia.gcr.io/$NAMESPACE/$SERVICE:$1
docker rmi asia.gcr.io/$NAMESPACE/$SERVICE:$1
