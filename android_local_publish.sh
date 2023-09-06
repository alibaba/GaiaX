#!/bin/bash

CUR=`pwd`
###########
echo "Publish GaiaX ClientToStudio"
cd $CUR/GaiaXAndroidClientToStudio
./gradlew clean build -Pgroup=com.github.alibaba.GaiaX -Pversion=main-SNAPSHOT -xtest -xlint publishToMavenLocal
cd ~/.m2/repository/com/github/alibaba/GaiaX/GaiaX-ClientToStudio
ls -ll

###########
echo "Publish GaiaX Analyze"
cd $CUR/GaiaXAnalyze/GXAnalyzeAndroid
./gradlew clean build -Pgroup=com.github.alibaba.GaiaX -Pversion=main-SNAPSHOT -xtest -xlint publishToMavenLocal
cd ~/.m2/repository/com/github/alibaba/GaiaX/GaiaX-Analyze
ls -ll

###########
echo "Publish GaiaX"
cd $CUR/GaiaXAndroid
./gradlew clean build -Pgroup=com.github.alibaba.GaiaX -Pversion=main-SNAPSHOT -xtest -xlint publishToMavenLocal
cd ~/.m2/repository/com/github/alibaba/GaiaX/GaiaX
ls -ll

###########
echo "Publish GaiaX Adapter"
cd $CUR/GaiaXAndroidAdapter
./gradlew clean build -Pgroup=com.github.alibaba.GaiaX -Pversion=main-SNAPSHOT -xtest -xlint publishToMavenLocal
cd ~/.m2/repository/com/github/alibaba/GaiaX/GaiaX-Adapter
ls -ll