#!/bin/bash

CUR=`pwd`
echo ##########################################################################
echo "Publish GXAnalyzeAndroid"
cd $CUR/GaiaXAnalyze/GXAnalyzeAndroid
call ./gradlew build -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroid"
cd $CUR/GaiaXAndroid
call ./gradlew build -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidAdapter"
cd $CUR/GaiaXAndroidAdapter
call ./gradlew build -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidClientToStudio"
cd $CUR/GaiaXAndroidClientToStudio
call ./gradlew build -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidQuickJS"
cd $CUR/GaiaXAndroidQuickJS
call ./gradlew build -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidJS"
cd $CUR/GaiaXAndroidJS
call ./gradlew build -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidJSAdapter"
cd $CUR/GaiaXAndroidJSAdapter
call ./gradlew build -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidDemo"
cd $CUR/GaiaXAndroidDemo
call ./gradlew build
