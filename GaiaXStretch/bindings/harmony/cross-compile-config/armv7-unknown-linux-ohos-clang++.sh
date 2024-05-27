#!/bin/sh
exec /Applications/DevEco-Studio.app/Contents/sdk/HarmonyOS-NEXT-DB1/openharmony/native/llvm/bin/clang++ \
  -target arm-linux-ohos \
  --sysroot=/Applications/DevEco-Studio.app/Contents/sdk/HarmonyOS-NEXT-DB1/openharmony/native/sysroot \
  -D__MUSL__ \
  -march=armv7-a \
  -mfloat-abi=softfp \
  -mtune=generic-armv7-a \
  -mthumb \
  "$@"

