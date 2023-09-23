#include <stdio.h>

#include "java-common.h"

#define MAX_MSG_SIZE 1024

jint throw_exception(JNIEnv *env, const char *exception_name, const char *message, ...) {
    char formatted_message[MAX_MSG_SIZE];
    va_list va_args;
    va_start(va_args, message);
    vsnprintf(formatted_message, MAX_MSG_SIZE, message, va_args);
    va_end(va_args);

    jclass exception_class = (*env)->FindClass(env, exception_name);
    if (exception_class == NULL) {
        return -1;
    }

    return (*env)->ThrowNew(env, exception_class, formatted_message);
}

// FIX: https://www.jianshu.com/p/56e8491e1bc4
// https://stackoverflow.com/questions/60722231/jni-detected-error-in-application-input-is-not-valid-modified-utf-8-illegal-st
// JNI DETECTED ERROR IN APPLICATION: input is not valid Modified UTF-8: illegal start byte 0xf0
jstring charToJString(JNIEnv *env, const char *pat) {
    //定义java String类 strClass
    jclass strClass = (*env)->FindClass(env, "java/lang/String");
    //获取java String类方法String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = (*env)->GetMethodID(env, strClass, "<init>", "([BLjava/lang/String;)V");
    //建立byte数组
    jbyteArray bytes = (*env)->NewByteArray(env, (jsize) strlen(pat));
    //将char* 转换为byte数组
    (*env)->SetByteArrayRegion(env, bytes, 0, (jsize) strlen(pat), (jbyte *) pat);
    //设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = (*env)->NewStringUTF(env, "UTF-8");
    //将byte数组转换为java String,并输出
    return (jstring) (*env)->NewObject(env, strClass, ctorID, bytes, encoding);
}