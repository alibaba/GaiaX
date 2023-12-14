#!/bin/sh

cp ./config ~/.cargo/config

cargo +nightly build  -Z build-std --target x86_64-unknown-linux-ohos
cargo +nightly build  -Z build-std --target armv7-unknown-linux-ohos
cargo +nightly build  -Z build-std --target aarch64-unknown-linux-ohos