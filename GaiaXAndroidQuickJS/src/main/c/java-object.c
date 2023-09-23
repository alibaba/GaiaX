#include "java-object.h"
#include "java-common.h"

static JSClassID java_object_class_id;

typedef struct {
    JavaVM *vm;
    jobject object;
} JavaObjectData;

static void java_object_finalizer(JSRuntime *rt, JSValue val) {
    JavaObjectData *data = JS_GetOpaque(val, java_object_class_id);

    OBTAIN_ENV(data->vm);

    if (env != NULL) {
        (*env)->DeleteGlobalRef(env, data->object);
    }

    RELEASE_ENV(data->vm);

    js_free_rt(rt, data);
}

static JSClassDef java_object_class = {
        "JavaObject",
        .finalizer = java_object_finalizer
};

int java_object_init_context(JSContext *ctx) {
    JS_NewClassID(&java_object_class_id);
    if (JS_NewClass(JS_GetRuntime(ctx), java_object_class_id, &java_object_class)) return -1;
    return 0;
}

JSValue QJ_NewJavaObject(JSContext *ctx, JNIEnv *env, jobject object) {
    JSRuntime *rt = JS_GetRuntime(ctx);

    JavaObjectData *data = js_malloc_rt(rt, sizeof(JavaObjectData));
    if (data == NULL) return JS_ThrowOutOfMemory(ctx);

    JSValue value = JS_NewObjectClass(ctx, java_object_class_id);
    if (JS_IsException(value)) {
        js_free_rt(rt, data);
        return value;
    }

    (*env)->GetJavaVM(env, &data->vm);
    data->object = (*env)->NewGlobalRef(env, object);

    JS_SetOpaque(value, data);

    return value;
}

jobject QJ_GetJavaObject(JSContext __unused *ctx, JSValueConst val) {
    JavaObjectData *data = JS_GetOpaque(val, java_object_class_id);
    return data != NULL ? data->object : NULL;
}
