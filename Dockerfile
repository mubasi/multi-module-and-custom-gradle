FROM alvrme/alpine-android:android-30-jdk11 AS build
ARG BUCKET \
    ANDROID_HOME \
    DEPLOY_BUILD_DATE \
    BRANCH_NAME=default \ 
    BUILD_NUMBER=0 

RUN apk update
RUN apk add python3\
    curl \
    bash

# Downloading gcloud package
RUN curl https://dl.google.com/dl/cloudsdk/release/google-cloud-sdk.tar.gz > /tmp/google-cloud-sdk.tar.gz

# Installing the package
RUN mkdir -p /usr/local/gcloud \
  && tar -C /usr/local/gcloud -xvf /tmp/google-cloud-sdk.tar.gz \
  && /usr/local/gcloud/google-cloud-sdk/install.sh

# Adding the package path to local
ENV PATH $PATH:/usr/local/gcloud/google-cloud-sdk/bin
WORKDIR /app
COPY . .
RUN ANDROID_HOME=${ANDROID_HOME} ./gradlew clean assembleRelease -PBUILD_NUMBER=${BUILD_NUMBER}
RUN gcloud auth activate-service-account --key-file service-account.json
RUN gsutil -m cp -r app/build/outputs/apk/prod/* gs://$BUCKET/$DEPLOY_BUILD_DATE/${BRANCH_NAME}/${BUILD_NUMBER}/