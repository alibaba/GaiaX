#!/bin/bash

################################################################################################
################################################################################################
################################################################################################
################################################################################################
USER_HOME=/Users/biezhihua
HUAWEI_SDK=$USER_HOME/Software/Huawei

################################################################################################
################################################################################################
################################################################################################
################################################################################################
# 编译模块
NODE=$HUAWEI_SDK/deveco4.0/nodejs16.19.1/bin/node
HVIGOR=$USER_HOME/.hvigor/project_caches/b49b9dba22b58f3507b8566a7acaae0e/workspace/node_modules/@ohos/hvigor/bin/hvigor.js

echo "====== 编译GaiaX ======"
$NODE $HVIGOR clean --mode module -p module=GaiaX@default -p product=default assembleHsp --parallel --incremental --daemon
echo "====== 编译App ======"
$NODE $HVIGOR clean --mode module -p module=app@default -p product=default assembleHap --parallel --incremental --daemon

################################################################################################
################################################################################################
################################################################################################
################################################################################################
# 更新模块
HDC=$HUAWEI_SDK/sdk/hmscore/toolchains/hdc
# WRITE_PATH=data/locl/gaiax
WRITE_PATH=/sdcard/gaiax
echo $HDC
$HDC shell mkdir $WRITE_PATH

echo "====== 更新GaiaX ======"
$HDC file send "GaiaX/build/default/outputs/default/GaiaX-default-signed.hsp" "$WRITE_PATH/GaiaX-default-signed.hsp"
$HDC shell bm install -p "$WRITE_PATH/GaiaX-default-signed.hsp"

echo "====== 更新App ======"
$HDC file send "app/build/default/outputs/default/app-default-signed.hap" "$WRITE_PATH/app-default-signed.hap"
$HDC shell bm install -p "$WRITE_PATH/app-default-signed.hap"

$HDC shell rm -rf $WRITE_PATH/GaiaX-default-signed.hsp
$HDC shell rm -rf $WRITE_PATH/app-default-signed.hap

echo "====== 启动AppAbility ======"
HDC=$HUAWEI_SDK/sdk/hmscore/3.1.0/toolchains/hdc
$HDC shell aa start -a AppAbility -b com.alibaba.gaiax.demo

