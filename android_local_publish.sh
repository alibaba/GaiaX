#!/bin/bash

CUR=`pwd`
###########
echo "Publish GaiaX ClientToStudio"
cd $CUR/GaiaXAndroidClientToStudio
./gradlew build -Pgroup=com.github.alibaba.GaiaX -Pversion=main-SNAPSHOT -xtest -xlint publishToMavenLocal
cd ~/.m2/repository/com/github/alibaba/GaiaX/GaiaX-ClientToStudio/main-SNAPSHOT
ls -ll

###########
echo "Publish GaiaX Analyze"
cd $CUR/GaiaXAnalyze/GXAnalyzeAndroid
./gradlew build -Pgroup=com.github.alibaba.GaiaX -Pversion=main-SNAPSHOT -xtest -xlint publishToMavenLocal
cd ~/.m2/repository/com/github/alibaba/GaiaX/GaiaX-Analyze/main-SNAPSHOT
ls -ll

###########
echo "Publish GaiaX"
cd $CUR/GaiaXAndroid
./gradlew build -Pgroup=com.github.alibaba.GaiaX -Pversion=main-SNAPSHOT -xtest -xlint publishToMavenLocal
cd ~/.m2/repository/com/github/alibaba/GaiaX/GaiaX/main-SNAPSHOT
ls -ll

###########
echo "Publish GaiaX Adapter"
cd $CUR/GaiaXAndroidAdapter
./gradlew build -Pgroup=com.github.alibaba.GaiaX -Pversion=main-SNAPSHOT -xtest -xlint publishToMavenLocal
cd ~/.m2/repository/com/github/alibaba/GaiaX/GaiaX-Adapter/main-SNAPSHOT
ls -ll