#ifndef QUICKJS_ANDROID_JAVA_OBJECT_H
#define QUICKJS_ANDROID_JAVA_OBJECT_H

#include <jni.h>
#include "quickjs.h"

int java_object_init_context(JSContext *ctx);

JSValue QJ_NewJavaObject(JSContext *ctx, JNIEnv *env, jobject object);

jobject QJ_GetJavaObject(JSContext *ctx, JSValueConst val);

#endif //QUICKJS_ANDROID_JAVA_OBJECT_H
