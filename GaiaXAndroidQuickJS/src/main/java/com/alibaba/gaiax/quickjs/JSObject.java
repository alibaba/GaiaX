package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * JavaScript object.
 */
@Keep
public class JSObject extends JSValue {

    public static int PROP_FLAG_CONFIGURABLE = 0b001;
    public static int PROP_FLAG_WRITABLE = 0b010;
    public static int PROP_FLAG_ENUMERABLE = 0b100;

    private static final int PROP_FLAG_MASK = 0b111;

    private final Object javaObject;

    JSObject(long pointer, JSContext jsContext, Object javaObject) {
        super(pointer, jsContext);
        this.javaObject = javaObject;
    }

    public Object getJavaObject() {
        return javaObject;
    }

    /**
     * Returns the property as a JSValue.
     *
     * @throws JSEvaluationException if the cannot read property of this JSValue.
     */
    public JSValue getProperty(int index) {
        synchronized (jsContext.jsRuntime) {
            long context = jsContext.checkClosed();
            long property = QuickJS.getValueProperty(context, pointer, index);
            return jsContext.wrapAsJSValue(property);
        }
    }

    /**
     * Returns the property as a JSValue.
     *
     * @throws JSEvaluationException if the cannot read property of this JSValue.
     */
    public JSValue getProperty(String name) {
        synchronized (jsContext.jsRuntime) {
            long context = jsContext.checkClosed();
            long property = QuickJS.getValueProperty(context, pointer, name);
            return jsContext.wrapAsJSValue(property);
        }
    }

    /**
     * Sets JSValue as a property.
     */
    public void setProperty(int index, JSValue jsValue) {
        checkSameJSContext(jsValue);
        synchronized (jsContext.jsRuntime) {
            jsContext.checkClosed();
            if (!QuickJS.setValueProperty(jsContext.pointer, pointer, index, jsValue.pointer)) {
                throw new JSEvaluationException(QuickJS.getException(jsContext.pointer));
            }
        }
    }

    /**
     * Sets JSValue as a property.
     */
    public void setProperty(String name, JSValue jsValue) {
        checkSameJSContext(jsValue);
        synchronized (jsContext.jsRuntime) {
            jsContext.checkClosed();
            if (!QuickJS.setValueProperty(jsContext.pointer, pointer, name, jsValue.pointer)) {
                throw new JSEvaluationException(QuickJS.getException(jsContext.pointer));
            }
        }
    }

    /**
     * Defines a new property directly on an object, or modifies an existing property on this object.
     */
    public void defineProperty(int index, JSValue jsValue, int flags) {
        if ((flags & (~PROP_FLAG_MASK)) != 0) {
            throw new IllegalArgumentException("Invalid flags: " + flags);
        }
        synchronized (jsContext.jsRuntime) {
            jsContext.checkClosed();
            if (!QuickJS.defineValueProperty(jsContext.pointer, pointer, index, jsValue.pointer, flags)) {
                throw new JSEvaluationException(QuickJS.getException(jsContext.pointer));
            }
        }
    }

    /**
     * Defines a new property directly on an object, or modifies an existing property on this object.
     */
    public void defineProperty(String name, JSValue jsValue, int flags) {
        if ((flags & (~PROP_FLAG_MASK)) != 0) {
            throw new IllegalArgumentException("Invalid flags: " + flags);
        }
        synchronized (jsContext.jsRuntime) {
            jsContext.checkClosed();
            if (!QuickJS.defineValueProperty(jsContext.pointer, pointer, name, jsValue.pointer, flags)) {
                throw new JSEvaluationException(QuickJS.getException(jsContext.pointer));
            }
        }
    }

    public void freeValue() {
        QuickJS.freeValue(jsContext.pointer, pointer);
    }

    public boolean isValueFunction() {
        return QuickJS.isValueFunction(jsContext.pointer, pointer);
    }

    public void dupValue() {
        QuickJS.dupValue(jsContext.pointer, pointer);
    }
}
