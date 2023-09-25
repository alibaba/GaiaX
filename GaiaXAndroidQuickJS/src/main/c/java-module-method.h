#ifndef QUICKJS_ANDROID_JAVA_CALL_METHOD_H
#define QUICKJS_ANDROID_JAVA_CALL_METHOD_H

#include <jni.h>
#include <quickjs.h>
#include "java-common.h"

static JavaVM *bridge_vm = NULL;

static jclass jni_gaiax_helper_class = NULL;

static jmethodID jni_gaiax_sync_method = NULL;
static jmethodID jni_gaiax_async_method = NULL;
static jmethodID jni_gaiax_promise_method = NULL;

int java_gaiax_method_init(JNIEnv *env);

void java_gaiax_init_module_bridge(JNIEnv *env, JSContext *ctx, const char *module_name);

#endif
