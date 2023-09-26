#ifndef QUICKJS_ANDROID_JAVA_HELPER_H
#define QUICKJS_ANDROID_JAVA_HELPER_H

#include <quickjs.h>
#include <jni.h>
#include <quickjs-libc.h>
#include <string.h>
#include <malloc.h>

#define CLASS_NAME_ILLEGAL_STATE_EXCEPTION "java/lang/IllegalStateException"
#define CLASS_NAME_JS_DATA_EXCEPTION "com/alibaba/gaiax/quickjs/JSDataException"

#define MSG_OOM "Out of memory"
#define MSG_NULL_JS_RUNTIME "Null JSRuntime"
#define MSG_NULL_JS_CONTEXT "Null JSContext"
#define MSG_NULL_JS_VALUE "Null JSValue"
#define MSG_NULL_VALUE "Null value"

#define COPY_JS_VALUE(JS_CONTEXT, JS_VALUE, RESULT)                                    \
    do {                                                                               \
        void *__copy__ = js_malloc_rt(JS_GetRuntime(JS_CONTEXT), sizeof(JSValue));     \
        if (__copy__ != NULL) {                                                        \
            memcpy(__copy__, &(JS_VALUE), sizeof(JSValue));                            \
            (RESULT) = __copy__;                                                       \
        } else {                                                                       \
            JS_FreeValue((JS_CONTEXT), (JS_VALUE));                                    \
        }                                                                              \
    } while (0)



///
#define THROW_EXCEPTION(ENV, EXCEPTION_NAME, ...)                               \
    do {                                                                        \
        throw_exception((ENV), (EXCEPTION_NAME), __VA_ARGS__);                  \
        return;                                                                 \
    } while (0)

///
#define THROW_EXCEPTION_RET(ENV, EXCEPTION_NAME, ...)                           \
    do {                                                                        \
        throw_exception((ENV), (EXCEPTION_NAME), __VA_ARGS__);                  \
        return 0;                                                               \
    } while (0)

///
#define THROW_ILLEGAL_STATE_EXCEPTION(ENV, ...)                                 \
    THROW_EXCEPTION(ENV, CLASS_NAME_ILLEGAL_STATE_EXCEPTION, __VA_ARGS__)

///
#define THROW_ILLEGAL_STATE_EXCEPTION_RET(ENV, ...)                             \
    THROW_EXCEPTION_RET(ENV, CLASS_NAME_ILLEGAL_STATE_EXCEPTION, __VA_ARGS__)

///
#define THROW_JS_DATA_EXCEPTION(ENV, ...)                                       \
    THROW_EXCEPTION(ENV, CLASS_NAME_JS_DATA_EXCEPTION, __VA_ARGS__)

///
#define THROW_JS_DATA_EXCEPTION_RET(ENV, ...)                                   \
    THROW_EXCEPTION_RET(ENV, CLASS_NAME_JS_DATA_EXCEPTION, __VA_ARGS__)

///
#define CHECK_NULL(ENV, POINTER, MESSAGE)                                       \
    do {                                                                        \
        if ((POINTER) == NULL) {                                                \
            THROW_ILLEGAL_STATE_EXCEPTION((ENV), (MESSAGE));                    \
        }                                                                       \
    } while (0)

///
#define CHECK_NULL_RET(ENV, POINTER, MESSAGE)                                   \
    do {                                                                        \
        if ((POINTER) == NULL) {                                                \
            THROW_ILLEGAL_STATE_EXCEPTION_RET((ENV), (MESSAGE));                \
        }                                                                       \
    } while (0)

///
#define CHECK_FALSE_RET(ENV, STATEMENT, MESSAGE)                                \
    do {                                                                        \
        if (!(STATEMENT)) {                                                     \
            THROW_ILLEGAL_STATE_EXCEPTION_RET((ENV), (MESSAGE));                \
        }                                                                       \
    } while (0)

///
jint throw_exception(JNIEnv *env, const char *exception_name, const char *message, ...);

///
#define OBTAIN_ENV(VM)                                                                                  \
    JNIEnv *env = NULL;                                                                                 \
    int __require_detach__ = 0;                                                                         \
    do {                                                                                                \
        (*(VM))->GetEnv((VM), (void **) &env, JNI_VERSION_1_6);                                         \
        if (env == NULL) __require_detach__ = (*(VM))->AttachCurrentThread((VM), &env, NULL) == JNI_OK; \
    } while (0)

///
#define RELEASE_ENV(VM)                                                        \
    do {                                                                       \
        if (__require_detach__) (*(VM))->DetachCurrentThread((VM));            \
    } while (0)


/// TODO append the java exception to the js exception
#define CHECK_JAVA_EXCEPTION_NO(ENV)                                 \
    do {                                                             \
        if ((*(ENV))->ExceptionCheck(ENV)) {                         \
            (*(ENV))->ExceptionDescribe(ENV);                        \
            (*(ENV))->ExceptionClear(ENV);                           \
            return -1;                                               \
        }                                                            \
    } while (0)

/// TODO append the java exception to the js exception
#define CHECK_JAVA_EXCEPTION_NULL(ENV)                               \
    do {                                                             \
        if ((*(ENV))->ExceptionCheck(ENV)) {                         \
            (*(ENV))->ExceptionDescribe(ENV);                        \
            (*(ENV))->ExceptionClear(ENV);                           \
            return NULL;                                             \
        }                                                            \
    } while (0)


/// TODO append the java exception to the js exception
#define CHECK_JAVA_EXCEPTION_JS_EXCEPTION(CTX, ENV)                            \
    do {                                                                       \
        if ((*(ENV))->ExceptionCheck(ENV)) {                                   \
            (*(ENV))->ExceptionDescribe(ENV);                                  \
            (*(ENV))->ExceptionClear(ENV);                                     \
            return JS_ThrowInternalError((CTX), "Catch java exception");       \
        }                                                                      \
    } while (0)


// FIX: https://www.jianshu.com/p/56e8491e1bc4
// https://stackoverflow.com/questions/60722231/jni-detected-error-in-application-input-is-not-valid-modified-utf-8-illegal-st
// JNI DETECTED ERROR IN APPLICATION: input is not valid Modified UTF-8: illegal start byte 0xf0
jstring charToJString(JNIEnv *env, const char *pat);

#endif //QUICKJS_ANDROID_JAVA_HELPER_H