#!/bin/bash

CUR=`pwd`
echo "##########################################################################"
echo "Publish GXAnalyzeAndroid"
cd $CUR/GaiaXAnalyze/GXAnalyzeAndroid
chmod +rx ./gradlew && ./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="dev-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo "##########################################################################"
echo "Publish GaiaXAndroid"
cd $CUR/GaiaXAndroid
chmod +rx ./gradlew && ./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="dev-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo "##########################################################################"
echo "Publish GaiaXAndroidAdapter"
cd $CUR/GaiaXAndroidAdapter
chmod +rx ./gradlew && ./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="dev-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo "##########################################################################"
echo "Publish GaiaXAndroidClientToStudio"
cd $CUR/GaiaXAndroidClientToStudio
chmod +rx ./gradlew && ./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="dev-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo "##########################################################################"
echo "Publish GaiaXAndroidQuickJS"
cd $CUR/GaiaXAndroidQuickJS
chmod +rx ./gradlew && ./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="dev-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo "##########################################################################"
echo "Publish GaiaXAndroidJS"
cd $CUR/GaiaXAndroidJS
chmod +rx ./gradlew && ./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="dev-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo "##########################################################################"
echo "Publish GaiaXAndroidJSAdapter"
cd $CUR/GaiaXAndroidJSAdapter
chmod +rx ./gradlew && ./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="dev-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo "##########################################################################"
echo "Publish GaiaXAndroidDemo"
cd $CUR/GaiaXAndroidDemo
chmod +rx ./gradlew && ./gradlew build
