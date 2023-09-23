package com.youku.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * JavaScript string.
 */
@Keep
public final class JSString extends JSValue {

    private final String value;

    JSString(long pointer, JSContext jsContext, String value) {
        super(pointer, jsContext);
        this.value = value;
    }

    public String getString() {
        return value;
    }
}
