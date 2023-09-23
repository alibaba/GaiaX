#ifndef QUICKJS_ANDROID_JAVA_METHOD_H
#define QUICKJS_ANDROID_JAVA_METHOD_H

#include <jni.h>
#include <quickjs.h>

int java_method_init(JNIEnv *env);

int java_method_init_context(JSContext *ctx);

JSValue QJ_NewJavaMethod(JSContext *ctx, JNIEnv *env, jobject js_context, jboolean is_static, jobject callee, jmethodID method, jobject return_type, int arg_count, jobject *arg_types, jboolean is_callback_method);

#endif //QUICKJS_ANDROID_JAVA_METHOD_H
