#!/bin/sh
exec /Users/biezhihua/Software/Huawei/sdk/openharmony/9/native/llvm/bin/clang \
  -target aarch64-linux-ohos \
  --sysroot=/Users/biezhihua/Software/Huawei/sdk/openharmony/9/native/sysroot \
  -D__MUSL__ \
  "$@"
