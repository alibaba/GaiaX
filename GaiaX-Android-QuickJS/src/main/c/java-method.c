#include <string.h>

#include "java-method.h"
#include "java-common.h"


static int js_value_to_java_value(JSContext *ctx, JNIEnv *env, jobject js_context, jobject type, JSValueConst value, jvalue *result);

static JSClassID java_method_class_id;

typedef JSValue (*JavaMethodCaller)(JSContext *ctx, JNIEnv *env, jobject js_context, jobject return_type, jobject callee, jmethodID method, jvalue *argv);

typedef struct {
    JavaVM *vm;
    jobject js_context;
    jobject callee;
    jmethodID method;
    jobject return_type;
    int arg_count;
    jobject *arg_types;
    JavaMethodCaller caller;
    jboolean is_callback_method;
} JavaMethodData;

static JSValue java_callback_method_call(JSContext *ctx, JavaMethodData *data, int argc, JSValueConst *argv) {
    // Collect js arguments to an array
    JSValue array = JS_NewArray(ctx);
    for (int i = 0; i < argc; i++) {
        JSValue val = argv[i];
        JS_DupValue(ctx, val);
        JS_SetPropertyUint32(ctx, array, (uint32_t) i, val);
    }

    // Set the array as the new argument
    argc = 1;
    JSValueConst new_argv[] = {array};
    argv = new_argv;

    OBTAIN_ENV(data->vm);

    // The first argument is JSContext
    // The second argument is the array
    int arg_count = 2;
    int arg_offset = 1;
    jvalue java_argv[arg_count];
    java_argv[0].l = data->js_context;
    for (int i = 0; i < argc; i++) {
        if (js_value_to_java_value(ctx, env, data->js_context, data->arg_types[arg_offset + i], argv[i], java_argv + arg_offset + i)) {
            goto fail;
        }
    }

    JSValue result = data->caller(ctx, env, data->js_context, data->return_type, data->callee, data->method, java_argv);

    RELEASE_ENV(data->vm);
    JS_FreeValue(ctx, array);
    return result;

    fail:
    RELEASE_ENV(data->vm);
    JS_FreeValue(ctx, array);
    return JS_ThrowInternalError(ctx, "Failed to convert js value to java value");
}

static JSValue java_normal_method_call(JSContext *ctx, JavaMethodData *data, int argc, JSValueConst *argv) {

    if (argc != data->arg_count) {
        // TODO it's not internal, it blames on the caller
        return JS_ThrowInternalError(ctx, "Inconsistent argument count, excepted: %d, actual: %d", data->arg_count, argc);
    }

    OBTAIN_ENV(data->vm);

    // Convert js value arguments to java value arguments
    jvalue java_argv[argc];
    for (int i = 0; i < argc; i++) {
        if (js_value_to_java_value(ctx, env, data->js_context, data->arg_types[i], argv[i], java_argv + i)) {
            goto fail;
        }
    }

    JSValue result = data->caller(ctx, env, data->js_context, data->return_type, data->callee, data->method, java_argv);

    RELEASE_ENV(data->vm);
    return result;

    fail:
    RELEASE_ENV(data->vm);
    return JS_ThrowInternalError(ctx, "Failed to convert js value to java value");
}

static JSValue java_method_call(JSContext *ctx, JSValueConst func_obj, JSValueConst __unused this_val, int argc, JSValueConst *argv, int __unused flags) {
    JavaMethodData *data = JS_GetOpaque(func_obj, java_method_class_id);
    if (data->is_callback_method) {
        return java_callback_method_call(ctx, data, argc, argv);
    } else {
        return java_normal_method_call(ctx, data, argc, argv);
    }
}

static void java_method_finalizer(JSRuntime *rt, JSValue val) {
    JavaMethodData *data = JS_GetOpaque(val, java_method_class_id);

    OBTAIN_ENV(data->vm);

    if (env != NULL) {
        (*env)->DeleteGlobalRef(env, data->callee);
        (*env)->DeleteGlobalRef(env, data->js_context);
        (*env)->DeleteGlobalRef(env, data->return_type);
        for (int i = 0; i < data->arg_count; i++) {
            (*env)->DeleteGlobalRef(env, data->arg_types[i]);
        }
    }

    RELEASE_ENV(data->vm);

    js_free_rt(rt, data->arg_types);
    js_free_rt(rt, data);
}

static JSClassDef java_method_class = {
        "JavaMethod",
        .call = java_method_call,
        .finalizer = java_method_finalizer
};

int java_method_init_context(JSContext *ctx) {
    JS_NewClassID(&java_method_class_id);
    if (JS_NewClass(JS_GetRuntime(ctx), java_method_class_id, &java_method_class)) return -1;
    return 0;
}

static jclass jni_helper_class;
static jmethodID js_value_to_java_value_method;
static jmethodID java_boolean_to_js_value_method;
static jmethodID java_char_to_js_value_method;
static jmethodID java_byte_to_js_value_method;
static jmethodID java_short_to_js_value_method;
static jmethodID java_int_to_js_value_method;
static jmethodID java_long_to_js_value_method;
static jmethodID java_float_to_js_value_method;
static jmethodID java_double_to_js_value_method;
static jmethodID java_object_to_js_value_method;
static jmethodID is_primitive_type_method;
static jmethodID is_same_type_method;
static jmethodID unbox_boolean_method;
static jmethodID unbox_char_method;
static jmethodID unbox_byte_method;
static jmethodID unbox_short_method;
static jmethodID unbox_int_method;
static jmethodID unbox_long_method;
static jmethodID unbox_float_method;
static jmethodID unbox_double_method;
static jobject void_primitive_type;
static jobject char_primitive_type;
static jobject boolean_primitive_type;
static jobject byte_primitive_type;
static jobject short_primitive_type;
static jobject int_primitive_type;
static jobject long_primitive_type;
static jobject float_primitive_type;
static jobject double_primitive_type;

int java_method_init(JNIEnv *env) {
    jni_helper_class = (*env)->FindClass(env, "com/youku/gaiax/quickjs/JNIHelper");
    jni_helper_class = (*env)->NewGlobalRef(env, jni_helper_class);
    if (jni_helper_class == NULL) return -1;

#define GET_STATIC_METHOD(RESULT, NAME, SIGN)                                             \
    do {                                                                                  \
        (RESULT) = (*env)->GetStaticMethodID(env, jni_helper_class, (NAME), (SIGN));      \
        if ((RESULT) == NULL) return -1;                                                  \
    } while (0)

    GET_STATIC_METHOD(js_value_to_java_value_method, "jsValueToJavaValue", "(Lcom/youku/gaiax/quickjs/JSContext;Ljava/lang/reflect/Type;J)Ljava/lang/Object;");
    GET_STATIC_METHOD(java_boolean_to_js_value_method, "javaValueToJSValue", "(Lcom/youku/gaiax/quickjs/JSContext;Ljava/lang/reflect/Type;Z)J");
    GET_STATIC_METHOD(java_char_to_js_value_method, "javaValueToJSValue", "(Lcom/youku/gaiax/quickjs/JSContext;Ljava/lang/reflect/Type;C)J");
    GET_STATIC_METHOD(java_byte_to_js_value_method, "javaValueToJSValue", "(Lcom/youku/gaiax/quickjs/JSContext;Ljava/lang/reflect/Type;B)J");
    GET_STATIC_METHOD(java_short_to_js_value_method, "javaValueToJSValue", "(Lcom/youku/gaiax/quickjs/JSContext;Ljava/lang/reflect/Type;S)J");
    GET_STATIC_METHOD(java_int_to_js_value_method, "javaValueToJSValue", "(Lcom/youku/gaiax/quickjs/JSContext;Ljava/lang/reflect/Type;I)J");
    GET_STATIC_METHOD(java_long_to_js_value_method, "javaValueToJSValue", "(Lcom/youku/gaiax/quickjs/JSContext;Ljava/lang/reflect/Type;J)J");
    GET_STATIC_METHOD(java_float_to_js_value_method, "javaValueToJSValue", "(Lcom/youku/gaiax/quickjs/JSContext;Ljava/lang/reflect/Type;F)J");
    GET_STATIC_METHOD(java_double_to_js_value_method, "javaValueToJSValue", "(Lcom/youku/gaiax/quickjs/JSContext;Ljava/lang/reflect/Type;D)J");
    GET_STATIC_METHOD(java_object_to_js_value_method, "javaValueToJSValue", "(Lcom/youku/gaiax/quickjs/JSContext;Ljava/lang/reflect/Type;Ljava/lang/Object;)J");
    GET_STATIC_METHOD(is_primitive_type_method, "isPrimitiveType", "(Ljava/lang/reflect/Type;)Z");
    GET_STATIC_METHOD(is_same_type_method, "isSameType", "(Ljava/lang/reflect/Type;Ljava/lang/reflect/Type;)Z");
    GET_STATIC_METHOD(unbox_boolean_method, "unbox", "(Ljava/lang/Boolean;)Z");
    GET_STATIC_METHOD(unbox_char_method, "unbox", "(Ljava/lang/Character;)C");
    GET_STATIC_METHOD(unbox_byte_method, "unbox", "(Ljava/lang/Byte;)B");
    GET_STATIC_METHOD(unbox_short_method, "unbox", "(Ljava/lang/Short;)S");
    GET_STATIC_METHOD(unbox_int_method, "unbox", "(Ljava/lang/Integer;)I");
    GET_STATIC_METHOD(unbox_long_method, "unbox", "(Ljava/lang/Long;)J");
    GET_STATIC_METHOD(unbox_float_method, "unbox", "(Ljava/lang/Float;)F");
    GET_STATIC_METHOD(unbox_double_method, "unbox", "(Ljava/lang/Double;)D");

#undef GET_STATIC_METHOD

#define GET_PRIMITIVE_TYPE(RESULT, NAME)                                                                         \
    do {                                                                                                         \
        jfieldID field_id = (*env)->GetStaticFieldID(env, jni_helper_class, (NAME), "Ljava/lang/reflect/Type;"); \
        if (field_id == NULL) return -1;                                                                         \
        (RESULT) = (*env)->GetStaticObjectField(env, jni_helper_class, field_id);                                \
        (RESULT) = (*env)->NewGlobalRef(env, (RESULT));                                                          \
        if ((RESULT) == NULL) return -1;                                                                         \
    } while (0)

    GET_PRIMITIVE_TYPE(void_primitive_type, "VOID_PRIMITIVE_TYPE");
    GET_PRIMITIVE_TYPE(char_primitive_type, "CHAR_PRIMITIVE_TYPE");
    GET_PRIMITIVE_TYPE(boolean_primitive_type, "BOOLEAN_PRIMITIVE_TYPE");
    GET_PRIMITIVE_TYPE(byte_primitive_type, "BYTE_PRIMITIVE_TYPE");
    GET_PRIMITIVE_TYPE(short_primitive_type, "SHORT_PRIMITIVE_TYPE");
    GET_PRIMITIVE_TYPE(int_primitive_type, "INT_PRIMITIVE_TYPE");
    GET_PRIMITIVE_TYPE(long_primitive_type, "LONG_PRIMITIVE_TYPE");
    GET_PRIMITIVE_TYPE(float_primitive_type, "FLOAT_PRIMITIVE_TYPE");
    GET_PRIMITIVE_TYPE(double_primitive_type, "DOUBLE_PRIMITIVE_TYPE");

#undef GET_PRIMITIVE_TYPE

    return 0;
}

static int unbox_primitive_type(JNIEnv *env, jobject type, jvalue *value) {
    jboolean is_primitive_type = (*env)->CallStaticBooleanMethod(env, jni_helper_class, is_primitive_type_method, type);
    CHECK_JAVA_EXCEPTION_NO(env);
    if (!is_primitive_type) return 0;

#define UNBOX_PRIMITIVE_TYPE(TYPE, GETTER, CALLER, TARGET)                                                                \
    do {                                                                                                                  \
        jboolean is_the_type = (*env)->CallStaticBooleanMethod(env, jni_helper_class, is_same_type_method, type, (TYPE)); \
        CHECK_JAVA_EXCEPTION_NO(env);                                                                                     \
        if (is_the_type) {                                                                                                \
            (TARGET) = (*env)->CALLER(env, jni_helper_class, GETTER, value->l);                                           \
            CHECK_JAVA_EXCEPTION_NO(env);                                                                                 \
            return 0;                                                                                                     \
        }                                                                                                                 \
    } while (0)

    UNBOX_PRIMITIVE_TYPE(boolean_primitive_type, unbox_boolean_method, CallStaticBooleanMethod, value->z);
    UNBOX_PRIMITIVE_TYPE(char_primitive_type, unbox_char_method, CallStaticCharMethod, value->c);
    UNBOX_PRIMITIVE_TYPE(byte_primitive_type, unbox_byte_method, CallStaticByteMethod, value->b);
    UNBOX_PRIMITIVE_TYPE(short_primitive_type, unbox_short_method, CallStaticShortMethod, value->s);
    UNBOX_PRIMITIVE_TYPE(int_primitive_type, unbox_int_method, CallStaticIntMethod, value->i);
    UNBOX_PRIMITIVE_TYPE(long_primitive_type, unbox_long_method, CallStaticLongMethod, value->j);
    UNBOX_PRIMITIVE_TYPE(float_primitive_type, unbox_float_method, CallStaticFloatMethod, value->f);
    UNBOX_PRIMITIVE_TYPE(double_primitive_type, unbox_double_method, CallStaticDoubleMethod, value->d);

#undef UNBOX_PRIMITIVE_TYPE

    // TODO Unknown primitive type
    return -1;
}

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

static int js_value_to_java_value(
        JSContext *ctx,
        JNIEnv *env,
        jobject js_context,
        jobject type,
        JSValueConst value,
        jvalue *result
) {
    JSValue *copy = NULL;
    // Duplication is required
    JS_DupValue(ctx, value);
    COPY_JS_VALUE(ctx, value, copy);
    if (copy == NULL) return -1;

    result->l = (*env)->CallStaticObjectMethod(env, jni_helper_class, js_value_to_java_value_method, js_context, type, (jlong) copy);
    CHECK_JAVA_EXCEPTION_NO(env);

    return unbox_primitive_type(env, type, result);
}

static JSValue call_void_java_method(
        JSContext *ctx,
        JNIEnv *env,
        jobject __unused js_context,
        jobject __unused return_type,
        jobject callee,
        jmethodID method,
        jvalue *argv
) {
    (*env)->CallVoidMethodA(env, callee, method, argv);
    CHECK_JAVA_EXCEPTION_JS_EXCEPTION(ctx, env);
    return JS_UNDEFINED;
}

static JSValue call_void_java_static_method(
        JSContext *ctx,
        JNIEnv *env,
        jobject __unused js_context,
        jobject __unused return_type,
        jobject callee,
        jmethodID method,
        jvalue *argv
) {
    (*env)->CallStaticVoidMethodA(env, callee, method, argv);
    CHECK_JAVA_EXCEPTION_JS_EXCEPTION(ctx, env);
    return JS_UNDEFINED;
}

#define FUNCTION_CALL_JAVA_METHOD(FUNCTION_NAME, JAVA_TYPE, JAVA_CALLER, JAVA_CONVERTER)                                                             \
static JSValue FUNCTION_NAME(JSContext *ctx, JNIEnv *env, jobject js_context, jobject return_type, jobject callee, jmethodID method, jvalue *argv) { \
    JAVA_TYPE java_result = (*env)->JAVA_CALLER(env, callee, method, argv);                                                                          \
    CHECK_JAVA_EXCEPTION_JS_EXCEPTION(ctx, env);                                                                                                     \
    JSValue *result = (JSValue *) (*env)->CallStaticLongMethod(env, jni_helper_class, JAVA_CONVERTER, js_context, return_type, java_result);         \
    CHECK_JAVA_EXCEPTION_JS_EXCEPTION(ctx, env);                                                                                                     \
    return JS_DupValue(ctx, *result);                                                                                                                \
}

FUNCTION_CALL_JAVA_METHOD(call_boolean_java_method, jboolean, CallBooleanMethodA, java_boolean_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_boolean_java_static_method, jboolean, CallStaticBooleanMethodA, java_boolean_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_char_java_method, jchar, CallCharMethodA, java_char_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_char_java_static_method, jchar, CallStaticCharMethodA, java_char_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_byte_java_method, jbyte, CallByteMethodA, java_byte_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_byte_java_static_method, jbyte, CallStaticByteMethodA, java_byte_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_short_java_method, jshort, CallShortMethodA, java_short_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_short_java_static_method, jshort, CallStaticShortMethodA, java_short_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_int_java_method, jint, CallIntMethodA, java_int_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_int_java_static_method, jint, CallStaticIntMethodA, java_int_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_long_java_method, jlong, CallLongMethodA, java_long_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_long_java_static_method, jlong, CallStaticLongMethodA, java_long_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_float_java_method, jfloat, CallFloatMethodA, java_float_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_float_java_static_method, jfloat, CallStaticFloatMethodA, java_float_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_double_java_method, jdouble, CallDoubleMethodA, java_double_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_double_java_static_method, jdouble, CallStaticDoubleMethodA, java_double_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_object_java_method, jobject, CallObjectMethodA, java_object_to_js_value_method)

FUNCTION_CALL_JAVA_METHOD(call_object_java_static_method, jobject, CallStaticObjectMethodA, java_object_to_js_value_method)

#undef FUNCTION_CALL_JAVA_METHOD

static JavaMethodCaller select_java_method_caller(JNIEnv *env, jboolean is_static, jobject type) {

    jboolean is_primitive_type = (*env)->CallStaticBooleanMethod(env, jni_helper_class, is_primitive_type_method, type);
    CHECK_JAVA_EXCEPTION_NULL(env);

    if (!is_primitive_type)
        return is_static ? call_object_java_static_method : call_object_java_method;

#define CHECK_PRIMITIVE_TYPE(TYPE, METHOD, STATIC_METHOD)                                                                \
    do {                                                                                                                 \
        jboolean is_the_type = (*env)->CallStaticBooleanMethod(env, jni_helper_class,is_same_type_method, type, (TYPE)); \
        CHECK_JAVA_EXCEPTION_NULL(env);                                                                                  \
        if (is_the_type) return is_static ? (STATIC_METHOD) : (METHOD);                                                  \
    } while (0)

    CHECK_PRIMITIVE_TYPE(void_primitive_type, call_void_java_method, call_void_java_static_method);
    CHECK_PRIMITIVE_TYPE(boolean_primitive_type, call_boolean_java_method, call_boolean_java_static_method);
    CHECK_PRIMITIVE_TYPE(char_primitive_type, call_char_java_method, call_char_java_static_method);
    CHECK_PRIMITIVE_TYPE(byte_primitive_type, call_byte_java_method, call_byte_java_static_method);
    CHECK_PRIMITIVE_TYPE(short_primitive_type, call_short_java_method, call_short_java_static_method);
    CHECK_PRIMITIVE_TYPE(int_primitive_type, call_int_java_method, call_int_java_static_method);
    CHECK_PRIMITIVE_TYPE(long_primitive_type, call_long_java_method, call_long_java_static_method);
    CHECK_PRIMITIVE_TYPE(float_primitive_type, call_float_java_method, call_float_java_static_method);
    CHECK_PRIMITIVE_TYPE(double_primitive_type, call_double_java_method, call_double_java_static_method);

#undef CHECK_PRIMITIVE_TYPE

    return NULL;
}

JSValue QJ_NewJavaMethod(
        JSContext *ctx,
        JNIEnv *env,
        jobject js_context,
        jboolean is_static,
        jobject callee,
        jmethodID method,
        jobject return_type,
        int arg_count,
        jobject *arg_types,
        jboolean is_callback_method
) {
    JavaMethodCaller caller = select_java_method_caller(env, is_static, return_type);
    if (caller == NULL) return JS_EXCEPTION;

    JSRuntime *rt = JS_GetRuntime(ctx);
    JavaMethodData *data = NULL;
    jobject *arg_types_copy = NULL;

    data = js_malloc_rt(rt, sizeof(JavaMethodData));
    if (data == NULL) goto oom;
    if (arg_count > 0) {
        arg_types_copy = js_malloc_rt(rt, sizeof(jobject) * arg_count);
        if (arg_types_copy == NULL) goto oom;
    }

    JSValue value = JS_NewObjectClass(ctx, java_method_class_id);
    if (JS_IsException(value)) {
        js_free_rt(rt, data);
        js_free_rt(rt, arg_types_copy);
        return value;
    }

    for (int i = 0; i < arg_count; i++) {
        arg_types_copy[i] = (*env)->NewGlobalRef(env, arg_types[i]);
    }

    (*env)->GetJavaVM(env, &data->vm);
    data->js_context = (*env)->NewGlobalRef(env, js_context);
    data->callee = (*env)->NewGlobalRef(env, callee);
    data->method = method;
    data->return_type = (*env)->NewGlobalRef(env, return_type);
    data->arg_count = arg_count;
    data->arg_types = arg_types_copy;
    data->caller = caller;
    data->is_callback_method = is_callback_method;

    JS_SetOpaque(value, data);

    return value;

    oom:
    js_free_rt(rt, data);
    js_free_rt(rt, arg_types_copy);
    return JS_ThrowOutOfMemory(ctx);
}
