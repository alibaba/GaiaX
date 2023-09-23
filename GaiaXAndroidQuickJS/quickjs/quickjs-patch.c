#include "quickjs/quickjs.c"

#include "quickjs-patch.h"

int JS_IsArrayBuffer(JSContext *ctx, JSValueConst val) {
    if (JS_VALUE_GET_TAG(val) != JS_TAG_OBJECT) {
        return FALSE;
    }

    JSObject *p = JS_VALUE_GET_OBJ(val);
    return p->class_id == JS_CLASS_ARRAY_BUFFER || p->class_id == JS_CLASS_SHARED_ARRAY_BUFFER;
}
