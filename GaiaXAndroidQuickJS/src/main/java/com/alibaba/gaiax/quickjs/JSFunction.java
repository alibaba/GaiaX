package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * JavaScript function.
 */
@Keep
public final class JSFunction extends JSObject {

    public JSFunction(long pointer, JSContext jsContext) {
        super(pointer, jsContext, null);
    }

    /**
     * Calls the JavaScript function.
     */
    public JSValue invoke(JSValue thisObj, JSValue[] args) {
        // Check whether JSValues are from the same JSRuntime
        if (thisObj != null) checkSameJSContext(thisObj);
        for (JSValue arg : args) checkSameJSContext(arg);

        long[] valueArgs = new long[args.length];
        for (int i = 0; i < args.length; i++) {
            valueArgs[i] = args[i].pointer;
        }

        synchronized (jsContext.jsRuntime) {
            long context = jsContext.checkClosed();
            long ret = QuickJS.invokeValueFunction(context, pointer, thisObj != null ? thisObj.pointer : 0, valueArgs);
            return jsContext.wrapAsJSValue(ret);
        }
    }

}
