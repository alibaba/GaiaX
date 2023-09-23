package com.youku.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * JavaScript symbol.
 */
@Keep
public final class JSSymbol extends JSValue {

    JSSymbol(long pointer, JSContext jsContext) {
        super(pointer, jsContext);
    }
}
