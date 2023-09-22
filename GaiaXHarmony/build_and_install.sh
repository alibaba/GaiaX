#!/bin/bash

# 编译模块

NODE=/Users/biezhihua/Software/Huawei/deveco4.0/nodejs16.19.1/bin/node
HVIGOR=/Users/biezhihua/.hvigor/project_caches/b49b9dba22b58f3507b8566a7acaae0e/workspace/node_modules/@ohos/hvigor/bin/hvigor.js
HDC=/Users/biezhihua/Software/Huawei/sdk/hmscore/3.1.0/toolchains/hdc

echo "====== 编译GaiaX ======"
$NODE $HVIGOR clean --mode module -p module=GaiaX@default -p product=default assembleHsp --parallel --incremental --daemon
echo "====== 编译App ======"
$NODE $HVIGOR clean --mode module -p module=app@default -p product=default assembleHap --parallel --incremental --daemon
#./hvigorw --mode module -p module=GaiaX@default -p product=default assembleHsp --parallel --incremental --daemon
#./hvigorw --mode module -p module=app@default -p product=default assembleHap --parallel --incremental --daemon
#./hvigorw clean --mode module -p module=GaiaX@default -p product=default assembleHsp --parallel --incremental --daemon
#./hvigorw clean --mode module -p module=app@default -p product=default assembleHap --parallel --incremental --daemon

# 更新模块
echo $HDC
$HDC shell mkdir data/locl/tmp/gaiax/

echo "====== 更新GaiaX ======"
$HDC file send "GaiaX/build/default/outputs/default/GaiaX-default-signed.hsp" "data/local/tmp/GaiaX-default-signed.hsp"
$HDC shell bm install -p "data/local/tmp/GaiaX-default-signed.hsp"

echo "====== 更新App ======"
$HDC file send "app/build/default/outputs/default/app-default-signed.hap" "data/local/tmp/app-default-signed.hap"
$HDC shell bm install -p "data/local/tmp/app-default-signed.hap"

$HDC shell rm -rf data/local/tmp/GaiaX-default-signed.hsp
$HDC shell rm -rf data/local/tmp/app-default-signed.hap

echo "====== 启动AppAbility ======"
$HDC shell aa start -a AppAbility -b com.alibaba.gaiax.demo

