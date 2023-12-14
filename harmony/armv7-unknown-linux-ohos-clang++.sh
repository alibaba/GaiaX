#!/bin/sh
exec /Users/biezhihua/Software/OpenHarmony/Sdk/10/native/llvm/bin/clang++ \
  -target arm-linux-ohos \
  --sysroot=/Users/biezhihua/Software/OpenHarmony/Sdk/10/native/sysroot \
  -D__MUSL__ \
  -march=armv7-a \
  -mfloat-abi=softfp \
  -mtune=generic-armv7-a \
  -mthumb \
  "$@"

