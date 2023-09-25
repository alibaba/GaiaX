package com.youku.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * JavaScript undefined.
 */
@Keep
public final class JSUndefined extends JSValue {

    JSUndefined(long pointer, JSContext jsContext) {
        super(pointer, jsContext);
    }
}
