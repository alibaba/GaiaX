#include <cutils.h>
#include "java-method.h"
#include "java-common.h"
#include "java-module-method.h"
#include <android/log.h>
#include "Log.h"

/// https://bellard.org/quickjs/quickjs.html

int java_gaiax_method_init(JNIEnv *env) {

    // 查找帮助类
    jni_gaiax_helper_class = (*env)->FindClass(env, "com/alibaba/gaiax/quickjs/JNIBridgeModuleHelper");

    // 因为jni_call_helper_class是静态的，需要包一层JNI全局引用
    jni_gaiax_helper_class = (*env)->NewGlobalRef(env, jni_gaiax_helper_class);

    // 判定合法性
    if (jni_gaiax_helper_class == NULL) {
        return -1;
    }

        // START - GET_STATIC_MODULE_METHOD
        // 定义宏 - 用于从jni_call_helper_class中获取静态方法ID（methodId）
#define GET_STATIC_MODULE_METHOD(RESULT, NAME, SIGN)                                        \
    do {                                                                                  \
        (RESULT) = (*env)->GetStaticMethodID(env, jni_gaiax_helper_class, (NAME), (SIGN)); \
        if ((RESULT) == NULL) return -1;                                                  \
    } while (0)

    GET_STATIC_MODULE_METHOD(jni_gaiax_sync_method, "callSync", "(JLjava/lang/String;)J");
    GET_STATIC_MODULE_METHOD(jni_gaiax_async_method, "callAsync", "(JJLjava/lang/String;)J");
    GET_STATIC_MODULE_METHOD(jni_gaiax_promise_method, "callPromise", "(JLjava/lang/String;)J");

    // END - GET_STATIC_CALL_METHOD
#undef GET_STATIC_CALL_METHOD

    return 0;
}

static JSValue js_bridge_module_sync(JSContext *ctx, JSValueConst this_val, int argc, JSValueConst *argv) {

    const char *arg_str = JS_ToCString(ctx, argv[0]);
    if (arg_str == NULL) {
        return JS_UNDEFINED;
    }

    if (bridge_vm != NULL) {
        OBTAIN_ENV(bridge_vm);

        jstring args_str = charToJString(env, arg_str);

        JSValue *value = (JSValue *) (*env)->CallStaticLongMethod(env, jni_gaiax_helper_class, jni_gaiax_sync_method, (jlong) ctx, args_str);

        (*env)->DeleteLocalRef(env, args_str);

        // 增加引用计数
        JS_DupValue(ctx, *value);

        RELEASE_ENV(bridge_vm);

        if (value != NULL) {
            return *value;
        }
    }
    return JS_UNDEFINED;
}

static JSValue js_bridge_module_async(JSContext *ctx, JSValueConst this_val, int argc, JSValueConst *argv) {

    const char *arg_str = JS_ToCString(ctx, argv[0]);
    if (arg_str == NULL) {
        return JS_UNDEFINED;
    }

    // 获取ASYNC回调函数
    JSValue *callback = NULL;
    for (int i = 0; i < argc; i++) {
        if (JS_IsFunction(ctx, argv[i])) {
            callback = &argv[i];
            break;
        }
    }

    if (callback != NULL) {
        if (bridge_vm != NULL) {
            OBTAIN_ENV(bridge_vm);

            jstring args_str = charToJString(env, arg_str);

            JS_DupValue(ctx, *callback);

            JSValue *result = NULL;
            COPY_JS_VALUE(ctx, *callback, result);

            (*env)->CallStaticLongMethod(env, jni_gaiax_helper_class, jni_gaiax_async_method, (jlong) ctx, (jlong) result, args_str);

            (*env)->DeleteLocalRef(env, args_str);

            RELEASE_ENV(bridge_vm);
        }
    }

    return JS_UNDEFINED;
}

static JSValue js_bridge_module_promise(JSContext *ctx, JSValueConst this_val, int argc, JSValueConst *argv) {

    const char *arg_str = JS_ToCString(ctx, argv[0]);
    if (arg_str == NULL) {
        return JS_UNDEFINED;
    }

    if (bridge_vm != NULL) {
        OBTAIN_ENV(bridge_vm);

        jstring args_str = charToJString(env, arg_str);

        JSValue *value = (JSValue *) (*env)->CallStaticLongMethod(env, jni_gaiax_helper_class, jni_gaiax_promise_method, (jlong) ctx, args_str);

        (*env)->DeleteLocalRef(env, args_str);

        JS_DupValue(ctx, *value);

        RELEASE_ENV(bridge_vm);

        if (value != NULL) {
            return *value;
        }
    }

    return JS_UNDEFINED;
}

static const JSCFunctionListEntry js_bridge_module_funcs[] = {
        JS_CFUNC_DEF("callSync", 1, js_bridge_module_sync),
        JS_CFUNC_DEF("callAsync", 2, js_bridge_module_async),
        JS_CFUNC_DEF("callPromise", 1, js_bridge_module_promise)
};

static int js_bridge_module_init(JSContext *ctx, JSModuleDef *module) {
    return JS_SetModuleExportList(ctx, module, js_bridge_module_funcs, countof(js_bridge_module_funcs));
}

void java_gaiax_init_module_bridge(JNIEnv *env, JSContext *ctx, const char *module_name) {

    // 模块定义
    JSModuleDef *module = JS_NewCModule(ctx, module_name, js_bridge_module_init);
    CHECK_NULL(env, module, MSG_OOM);

    // 初始化JVM
    (*env)->GetJavaVM(env, &(bridge_vm));

    // 给模块添加方法
    JS_AddModuleExportList(ctx, module, js_bridge_module_funcs, countof(js_bridge_module_funcs));
}