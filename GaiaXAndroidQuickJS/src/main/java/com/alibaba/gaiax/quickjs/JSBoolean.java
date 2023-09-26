package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * JavaScript boolean.
 */
@Keep
public class JSBoolean extends JSValue {

    private final boolean value;

    JSBoolean(long pointer, JSContext jsContext, boolean value) {
        super(pointer, jsContext);
        this.value = value;
    }

    /**
     * Returns boolean value.
     */
    public boolean getBoolean() {
        return value;
    }
}
