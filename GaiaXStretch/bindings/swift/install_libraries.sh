#!/bin/sh

STRETCHKIT=../StretchKit

cd $(dirname $0)/StretchCore
cargo lipo --release --targets aarch64-apple-ios,x86_64-apple-ios,armv7-apple-ios
rm -rf $STRETCHKIT/Libraries $STRETCHKIT/Headers
mkdir $STRETCHKIT/Libraries $STRETCHKIT/Headers
cp target/universal/release/libstretch.a $STRETCHKIT/Libraries/libstretch.a
cbindgen . -o $STRETCHKIT/Headers/libstretch.h -l c
