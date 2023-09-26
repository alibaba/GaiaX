package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * JavaScript array.
 */
@Keep
public final class JSArray extends JSObject {

    JSArray(long pointer, JSContext jsContext) {
        super(pointer, jsContext, null);
    }

    /**
     * Returns the number of elements in an array.
     */
    public int getLength() {
        return getProperty("length").cast(JSNumber.class).getInt();
    }
}
