#!/bin/bash

ROOT=`pwd`

ROOT_DATA=$ROOT/data
ROOT_TEMPLATES=$ROOT/templates

cd ..

GAIAX_ROOT=`pwd`

echo $ROOT
echo $ROOT_DATA
echo $ROOT_TEMPLATES

echo $GAIAX_ROOT

GAIAX_ANDROID_TEMPLATES=$GAIAX_ROOT/GaiaXAndroid/src/androidTest/assets/integration

echo $GAIAX_ANDROID_TEMPLATES

rm -rf $ROOT_TEMPLATES
cp -r $GAIAX_ANDROID_TEMPLATES $ROOT_TEMPLATES

