FROM alvrme/alpine-android:android-30-jdk11 AS build
ARG ANDROID_HOME
ARG BUILD_NUMBER
RUN apk update && apk add python3

WORKDIR /app
COPY . .
RUN ls -all && pwd
RUN ANDROID_HOME=${ANDROID_HOME} ./gradlew clean assembleStage -PBUILD_NUMBER=${BUILD_NUMBER}
RUN cp -r app/build/outputs/apk/stage/debug/app-stage-debug.apk  .

FROM andreysenov/firebase-tools
ARG APP_ID
ARG GROUPS
WORKDIR /app
COPY --from=build /app/app-stage-debug.apk /app
COPY firebase.json release-note.txt /app/
RUN GOOGLE_APPLICATION_CREDENTIALS=/app/firebase.json firebase projects:list
RUN GOOGLE_APPLICATION_CREDENTIALS=/app/firebase.json firebase appdistribution:distribute /app/app-stage-debug.apk  \
    --app ${APP_ID}  \
    --release-notes-file "/app/release-note.txt" --groups ${GROUPS} 
