package com.youku.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * Used internally in QuickJS.
 */
@Keep
class JSInternal extends JSValue {

    JSInternal(long pointer, JSContext jsContext) {
        super(pointer, jsContext);
    }
}
