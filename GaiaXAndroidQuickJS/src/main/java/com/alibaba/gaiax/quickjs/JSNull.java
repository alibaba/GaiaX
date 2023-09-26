package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * JavaScript null.
 */
@Keep
public final class JSNull extends JSValue {

    JSNull(long pointer, JSContext jsContext) {
        super(pointer, jsContext);
    }
}
