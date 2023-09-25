# GXAnalyzeAndroid

## 发布

发布debug
```bat
.\gradlew.bat clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal
```
```shell
./gradlew clean assembleDebug -Ptype="debug" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal
```

发布release
```bat
.\gradlew.bat clean assembleRelease -Ptype="release" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal
```
```shell
./gradlew clean assembleRelease -Ptype="release" -Pgroup="com.github.alibaba.GaiaX" -Pversion="main-SNAPSHOT" -xtest -xlint publishToMavenLocal
```