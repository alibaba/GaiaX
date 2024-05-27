#!/bin/sh
exec /Applications/DevEco-Studio.app/Contents/sdk/HarmonyOS-NEXT-DB1/openharmony/native/llvm/bin/clang++ \
  -target aarch64-linux-ohos \
  --sysroot=/Applications/DevEco-Studio.app/Contents/sdk/HarmonyOS-NEXT-DB1/openharmony/native/sysroot \
  -D__MUSL__ \
  "$@"
