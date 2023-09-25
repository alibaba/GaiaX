@if "%DEBUG%" == "" @echo off

set CUR=%~dp0

echo ##########################################################################
echo "Publish GXAnalyzeAndroid"
cd %CUR%/GaiaXAnalyze/GXAnalyzeAndroid
call .\gradlew.bat clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroid"
cd %CUR%/GaiaXAndroid
call .\gradlew.bat clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidAdapter"
cd %CUR%/GaiaXAndroidAdapter
call .\gradlew.bat clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidClientToStudio"
cd %CUR%/GaiaXAndroidClientToStudio
call .\gradlew.bat clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidQuickJS"
cd %CUR%/GaiaXAndroidQuickJS
call .\gradlew.bat clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidJS"
cd %CUR%/GaiaXAndroidJS
call .\gradlew.bat clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidJSAdapter"
cd %CUR%/GaiaXAndroidJSAdapter
call .\gradlew.bat clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal

echo ##########################################################################
echo "Publish GaiaXAndroidDemo"
cd %CUR%/GaiaXAndroidDemo
call .\gradlew.bat assembleDebug
