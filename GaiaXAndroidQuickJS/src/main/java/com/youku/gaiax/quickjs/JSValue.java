package com.youku.gaiax.quickjs;

// TODO Make JSValue closeable?


import androidx.annotation.Keep;

/**
 * JSValue is a Javascript value.
 * It could be a number, a object, null, undefined or something else.
 */
@Keep
public abstract class JSValue {

    public final long pointer;
    public final JSContext jsContext;

    JSValue(long pointer, JSContext jsContext) {
        this.pointer = pointer;
        this.jsContext = jsContext;
    }

    /**
     * Cast this JSValue to a special type.
     *
     * @throws JSDataException if it's not the type
     */
    @SuppressWarnings("unchecked")
    public final <T extends JSValue> T cast(Class<T> clazz) {
        if (clazz.isInstance(this)) {
            return (T) this;
        } else {
            throw new JSDataException("expected: " + clazz.getSimpleName() + ", actual: " + getClass().getSimpleName());
        }
    }

    /**
     * @throws IllegalStateException if two JSValues are not from the same JSContext
     */
    final void checkSameJSContext(JSValue jsValue) {
        if (jsValue.jsContext != jsContext) {
            throw new IllegalStateException("Two JSValues are not from the same JSContext");
        }
    }
}
