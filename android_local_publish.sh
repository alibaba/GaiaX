#!/bin/bash

CUR=`pwd`
echo ##########################################################################
echo "Publish GXAnalyzeAndroid"
cd $CUR/GaiaXAnalyze/GXAnalyzeAndroid
./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroid"
cd $CUR/GaiaXAndroid
./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidAdapter"
cd $CUR/GaiaXAndroidAdapter
./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidClientToStudio"
cd $CUR/GaiaXAndroidClientToStudio
./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidQuickJS"
cd $CUR/GaiaXAndroidQuickJS
./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidJS"
cd $CUR/GaiaXAndroidJS
./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidJSAdapter"
cd $CUR/GaiaXAndroidJSAdapter
./gradlew assembleDebug clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidDemo"
cd $CUR/GaiaXAndroidDemo
./gradlew build
