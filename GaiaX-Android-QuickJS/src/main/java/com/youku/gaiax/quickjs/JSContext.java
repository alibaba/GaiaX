package com.youku.gaiax.quickjs;


import androidx.annotation.Keep;

import com.youku.gaiax.quickjs.adapter.TypeAdapter;

import java.io.Closeable;

/**
 * JSContext is a JavaScript context with its own global objects.
 * JSContexts in the same JSRuntime share the same memory heap.
 *
 * @see JSRuntime
 */
@Keep
public class JSContext implements Closeable, TypeAdapter.Context {

    static final int TYPE_SYMBOL = -8;
    static final int TYPE_STRING = -7;
    static final int TYPE_OBJECT = -1;
    static final int TYPE_INT = 0;
    static final int TYPE_BOOLEAN = 1;
    static final int TYPE_NULL = 2;
    static final int TYPE_UNDEFINED = 3;
    static final int TYPE_EXCEPTION = 6;
    static final int TYPE_FLOAT64 = 7;

    /**
     * Global code.
     */
    public static final int EVAL_TYPE_GLOBAL = 0;

    /**
     * Module code.
     */
    public static final int EVAL_TYPE_MODULE = 1;

    /**
     * Force 'strict' mode.
     */
    public static final int EVAL_FLAG_STRICT = 0b01000;

    /**
     * Force 'strip' mode.
     * <p>
     * Remove the debug information (including the source code
     * of the functions) to save memory.
     */
    public static final int EVAL_FLAG_STRIP = 0b10000;

    private static final int EVAL_FLAG_MASK = 0b11000;

    public long pointer;
    final QuickJS quickJS;
    final JSRuntime jsRuntime;
    private final NativeCleaner<JSValue> cleaner;

    JSContext(long pointer, QuickJS quickJS, JSRuntime jsRuntime) {
        this.pointer = pointer;
        this.quickJS = quickJS;
        this.jsRuntime = jsRuntime;
        this.cleaner = new JSValueCleaner();
    }

    long checkClosed() {
        if (pointer == 0) {
            throw new IllegalStateException("The JSContext is closed");
        }

        // Trigger cleaner
        cleaner.clean();

        return pointer;
    }

    /**
     * Evaluates the script in this JSContext.
     */
    public void evaluate(String script, String fileName) {
        evaluateInternal(script, fileName, EVAL_TYPE_GLOBAL, 0, null);
    }

    /**
     * Evaluates the script in this JSContext.
     *
     * @param type  must be one of {@link #EVAL_TYPE_GLOBAL} and {@link #EVAL_TYPE_MODULE}
     * @param flags must be logic and of {@link #EVAL_FLAG_STRICT} and {@link #EVAL_FLAG_STRIP}
     */
    public void evaluate(String script, String fileName, int type, int flags) {
        evaluateInternal(script, fileName, type, flags, null);
    }

    /**
     * Evaluates the script in this JSContext.
     * Returns the result as the java class.
     */
    public <T> T evaluate(String script, String fileName, Class<T> clazz) {
        return (T) evaluateInternal(script, fileName, EVAL_TYPE_GLOBAL, 0, quickJS.getAdapter(clazz));
    }

    /**
     * Evaluates the script in this JSContext.
     * Returns the result converted by the TypeAdapter.
     */
    public <T> T evaluate(String script, String fileName, TypeAdapter<T> adapter) {
        return evaluateInternal(script, fileName, EVAL_TYPE_GLOBAL, 0, adapter);
    }

    /**
     * Evaluates the script in this JSContext.
     * Returns the result as the java class.
     *
     * @param type  must be one of {@link #EVAL_TYPE_GLOBAL} and {@link #EVAL_TYPE_MODULE}
     * @param flags must be logic and of {@link #EVAL_FLAG_STRICT} and {@link #EVAL_FLAG_STRIP}
     */
    public <T> T evaluate(String script, String fileName, int type, int flags, Class<T> clazz) {
        return (T) evaluateInternal(script, fileName, type, flags, quickJS.getAdapter(clazz));
    }

    /**
     * Evaluates the script in this JSContext.
     * Returns the result converted by the TypeAdapter.
     *
     * @param type  must be one of {@link #EVAL_TYPE_GLOBAL} and {@link #EVAL_TYPE_MODULE}
     * @param flags must be logic and of {@link #EVAL_FLAG_STRICT} and {@link #EVAL_FLAG_STRIP}
     */
    public <T> T evaluate(String script, String fileName, int type, int flags, TypeAdapter<T> adapter) {
        return evaluateInternal(script, fileName, type, flags, adapter);
    }

    private <T> T evaluateInternal(String script, String fileName, int type, int flags, TypeAdapter<T> adapter) {
        if (type != EVAL_TYPE_GLOBAL && type != EVAL_TYPE_MODULE) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        if ((flags & (~EVAL_FLAG_MASK)) != 0) {
            throw new IllegalArgumentException("Invalid flags: " + flags);
        }

        synchronized (jsRuntime) {
            checkClosed();

            long value = QuickJS.evaluate(pointer, script, fileName, type | flags);

            if (adapter != null) {
                JSValue jsValue = wrapAsJSValue(value);
                return adapter.fromJSValue(quickJS, this, jsValue);
            } else {
                // Only check exception
                try {
                    if (QuickJS.getValueTag(value) == TYPE_EXCEPTION) {
                        throw new JSEvaluationException(QuickJS.getException(pointer));
                    }
                } finally {
                    QuickJS.destroyValue(pointer, value);
                }
                return null;
            }
        }
    }

    /**
     * Execute next pending job. Returns {@code false} if it has no pending job.
     */
    public boolean executePendingJob() {
        synchronized (jsRuntime) {
            checkClosed();

            int code = QuickJS.executePendingJob(pointer);
            if (code < 0) {
                throw new JSEvaluationException(QuickJS.getException(pointer));
            } else {
                return code != 0;
            }
        }
    }

    /**
     * Returns the global object.
     */
    public JSObject getGlobalObject() {
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.getGlobalObject(pointer);
            return wrapAsJSValue(val).cast(JSObject.class);
        }
    }

    /**
     * Creates a JavaScript undefined.
     */
    @Override
    public JSUndefined createJSUndefined() {
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueUndefined(pointer);
            return wrapAsJSValue(val).cast(JSUndefined.class);
        }
    }

    /**
     * Creates a JavaScript null.
     */
    @Override
    public JSNull createJSNull() {
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueNull(pointer);
            return wrapAsJSValue(val).cast(JSNull.class);
        }
    }

    /**
     * Creates a JavaScript boolean.
     */
    @Override
    public JSBoolean createJSBoolean(boolean value) {
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueBoolean(pointer, value);
            return wrapAsJSValue(val).cast(JSBoolean.class);
        }
    }

    /**
     * Creates a JavaScript number.
     */
    @Override
    public JSNumber createJSNumber(int value) {
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueInt(pointer, value);
            return wrapAsJSValue(val).cast(JSNumber.class);
        }
    }

    /**
     * Creates a JavaScript number.
     */
    @Override
    public JSNumber createJSNumber(double value) {
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueFloat64(pointer, value);
            return wrapAsJSValue(val).cast(JSNumber.class);
        }
    }

    /**
     * Creates a JavaScript number.
     */
    @Override
    public JSNumber createJSNumber(long value) {
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueFloat64(pointer, value);
            return wrapAsJSValue(val).cast(JSNumber.class);
        }
    }

    /**
     * Creates a JavaScript string.
     */
    @Override
    public JSString createJSString(String value) {
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueString(pointer, value);
            return wrapAsJSValue(val).cast(JSString.class);
        }
    }

    /**
     * Creates a JavaScript object.
     */
    @Override
    public JSObject createJSObject() {
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueObject(pointer);
            return wrapAsJSValue(val).cast(JSObject.class);
        }
    }

    /**
     * Creates a JavaScript object.
     */
    @Override
    public JSObject createJSJsonObject(String json) {
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueJson(pointer, json);
            return wrapAsJSValue(val).cast(JSObject.class);
        }
    }

    /**
     * Creates a JavaScript object holding a java object.
     */
    @Override
    public JSObject createJSObject(Object object) {
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueJavaObject(pointer, object);
            return wrapAsJSValue(val).cast(JSObject.class);
        }
    }

    /**
     * Creates a JavaScript array.
     */
    @Override
    public JSArray createJSArray() {
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueArray(pointer);
            return wrapAsJSValue(val).cast(JSArray.class);
        }
    }

    private void checkArrayBounds(int arrayLength, int start, int length) {
        if (start < 0 || length <= 0 || start + length > arrayLength) {
            throw new IndexOutOfBoundsException(
                    "start = " + start + ", length = " + length + ", but array.length = " + arrayLength
            );
        }
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java boolean array.
     * The size of Java boolean is one byte.
     */
    public JSArrayBuffer createJSArrayBuffer(boolean[] array) {
        return createJSArrayBuffer(array, 0, array.length);
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java boolean array.
     * The size of Java boolean is one byte.
     */
    public JSArrayBuffer createJSArrayBuffer(boolean[] array, int start, int length) {
        checkArrayBounds(array.length, start, length);
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueArrayBufferZ(pointer, array, start, length);
            return wrapAsJSValue(val).cast(JSArrayBuffer.class);
        }
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java byte array.
     * The size of Java byte is one byte.
     */
    public JSArrayBuffer createJSArrayBuffer(byte[] array) {
        return createJSArrayBuffer(array, 0, array.length);
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java byte array.
     * The size of Java byte is one byte.
     */
    public JSArrayBuffer createJSArrayBuffer(byte[] array, int start, int length) {
        checkArrayBounds(array.length, start, length);
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueArrayBufferB(pointer, array, start, length);
            return wrapAsJSValue(val).cast(JSArrayBuffer.class);
        }
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java char array.
     * The size of Java char is two bytes.
     */
    public JSArrayBuffer createJSArrayBuffer(char[] array) {
        return createJSArrayBuffer(array, 0, array.length);
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java char array.
     * The size of Java char is two bytes.
     */
    public JSArrayBuffer createJSArrayBuffer(char[] array, int start, int length) {
        checkArrayBounds(array.length, start, length);
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueArrayBufferC(pointer, array, start, length);
            return wrapAsJSValue(val).cast(JSArrayBuffer.class);
        }
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java short array.
     * The size of Java short is two bytes.
     */
    public JSArrayBuffer createJSArrayBuffer(short[] array) {
        return createJSArrayBuffer(array, 0, array.length);
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java short array.
     * The size of Java short is two bytes.
     */
    public JSArrayBuffer createJSArrayBuffer(short[] array, int start, int length) {
        checkArrayBounds(array.length, start, length);
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueArrayBufferS(pointer, array, start, length);
            return wrapAsJSValue(val).cast(JSArrayBuffer.class);
        }
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java int array.
     * The size of Java int is four bytes.
     */
    public JSArrayBuffer createJSArrayBuffer(int[] array) {
        return createJSArrayBuffer(array, 0, array.length);
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java int array.
     * The size of Java int is four bytes.
     */
    public JSArrayBuffer createJSArrayBuffer(int[] array, int start, int length) {
        checkArrayBounds(array.length, start, length);
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueArrayBufferI(pointer, array, start, length);
            return wrapAsJSValue(val).cast(JSArrayBuffer.class);
        }
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java long array.
     * The size of Java long is eight bytes.
     */
    public JSArrayBuffer createJSArrayBuffer(long[] array) {
        return createJSArrayBuffer(array, 0, array.length);
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java long array.
     * The size of Java long is eight bytes.
     */
    public JSArrayBuffer createJSArrayBuffer(long[] array, int start, int length) {
        checkArrayBounds(array.length, start, length);
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueArrayBufferJ(pointer, array, start, length);
            return wrapAsJSValue(val).cast(JSArrayBuffer.class);
        }
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java float array.
     * The size of Java float is four bytes.
     */
    public JSArrayBuffer createJSArrayBuffer(float[] array) {
        return createJSArrayBuffer(array, 0, array.length);
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java float array.
     * The size of Java float is four bytes.
     */
    public JSArrayBuffer createJSArrayBuffer(float[] array, int start, int length) {
        checkArrayBounds(array.length, start, length);
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueArrayBufferF(pointer, array, start, length);
            return wrapAsJSValue(val).cast(JSArrayBuffer.class);
        }
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java double array.
     * The size of Java double is eight bytes.
     */
    public JSArrayBuffer createJSArrayBuffer(double[] array) {
        return createJSArrayBuffer(array, 0, array.length);
    }

    /**
     * Creates a JavaScript ArrayBuffer from a Java double array.
     * The size of Java double is eight bytes.
     */
    public JSArrayBuffer createJSArrayBuffer(double[] array, int start, int length) {
        checkArrayBounds(array.length, start, length);
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueArrayBufferD(pointer, array, start, length);
            return wrapAsJSValue(val).cast(JSArrayBuffer.class);
        }
    }

    /**
     * Create a JavaScript function from a java non-static method.
     */
    @Override
    public JSFunction createJSFunction(Object instance, Method method) {
        if (instance == null) throw new NullPointerException("instance == null");
        if (method == null) throw new NullPointerException("method == null");
        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueFunction(pointer, this, instance, method.name, method.getSignature(), method.returnType, method.parameterTypes, false);
            return wrapAsJSValue(val).cast(JSFunction.class);
        }
    }

    /**
     * Create a JavaScript function from a function callback.
     */
    public JSFunction createJSFunction(JSFunctionCallback callback) {
        if (callback == null) throw new NullPointerException("callback == null");
        synchronized (jsRuntime) {
            checkClosed();
            String methodName = "invoke";
            String methodSign = "(Lcom/youku/gaiax/quickjs/JSContext;[Lcom/youku/gaiax/quickjs/JSValue;)Lcom/youku/gaiax/quickjs/JSValue;";
            long val = QuickJS.createValueFunction(pointer, this, callback, methodName, methodSign, JSValue.class, new Class[]{JSContext.class, JSValue[].class}, true);
            return wrapAsJSValue(val).cast(JSFunction.class);
        }
    }

    /**
     * Create a JavaScript function from a java static method.
     */
    @Override
    public JSFunction createJSFunctionS(Class clazz, Method method) {
        if (clazz == null) throw new NullPointerException("clazz == null");
        if (method == null) throw new NullPointerException("method == null");

        String name = clazz.getName();
        StringBuilder sb = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            sb.append(c == '.' ? '/' : c);
        }
        String className = sb.toString();

        synchronized (jsRuntime) {
            checkClosed();
            long val = QuickJS.createValueFunctionS(pointer, this, className, method.name, method.getSignature(), method.returnType, method.parameterTypes);
            return wrapAsJSValue(val).cast(JSFunction.class);
        }
    }

    public JSObject createJSPromise(PromiseExecutor executor) {
        JSValue promise, resolve, reject;

        synchronized (jsRuntime) {
            checkClosed();
            long[] values = QuickJS.createValuePromise(pointer);
            if (values == null) throw new NullPointerException("result == null");

            // Check js exception
            for (long value : values) {
                int type = QuickJS.getValueTag(value);
                if (type == TYPE_EXCEPTION) {
                    for (long v : values) {
                        QuickJS.destroyValue(pointer, v);
                    }
                    throw new JSEvaluationException(QuickJS.getException(pointer));
                }
            }

            promise = wrapAsJSValue(values[0]);
            resolve = wrapAsJSValue(values[1]);
            reject = wrapAsJSValue(values[2]);
        }

        executor.execute(resolve.cast(JSFunction.class), reject.cast(JSFunction.class));

        return promise.cast(JSObject.class);
    }

    // TODO No need to save c pointers of JSNull, JSUndefined, JSBoolean, JSNumber and JSString.
    //  Just save their types and values.

    /**
     * Wraps a JSValue c pointer as a Java JSValue.
     *
     * @throws JSEvaluationException if it's JS_EXCEPTION
     */
    public JSValue wrapAsJSValue(long value) {
        try {
            if (value == 0) {
                throw new IllegalStateException("Can't wrap null pointer as JSValue");
            }

            JSValue jsValue;

            int type = QuickJS.getValueTag(value);
            switch (type) {
                case TYPE_SYMBOL:
                    jsValue = new JSSymbol(value, this);
                    break;
                case TYPE_STRING:
                    jsValue = new JSString(value, this, QuickJS.getValueString(pointer, value));
                    break;
                case TYPE_OBJECT:
                    if (QuickJS.isValueFunction(pointer, value)) {
                        jsValue = new JSFunction(value, this);
                    } else if (QuickJS.isValueArray(pointer, value)) {
                        jsValue = new JSArray(value, this);
                    } else if (QuickJS.isValueArrayBuffer(pointer, value)) {
                        jsValue = new JSArrayBuffer(value, this);
                    } else {
                        jsValue = new JSObject(value, this, QuickJS.getValueJavaObject(pointer, value));
                    }
                    break;
                case TYPE_INT:
                    jsValue = new JSInt(value, this, QuickJS.getValueInt(value));
                    break;
                case TYPE_BOOLEAN:
                    jsValue = new JSBoolean(value, this, QuickJS.getValueBoolean(value));
                    break;
                case TYPE_NULL:
                    jsValue = new JSNull(value, this);
                    break;
                case TYPE_UNDEFINED:
                    jsValue = new JSUndefined(value, this);
                    break;
                case TYPE_EXCEPTION:
                    QuickJS.destroyValue(pointer, value);
                    JSException exception = QuickJS.getException(pointer);
                    throw new JSEvaluationException(exception);
                case TYPE_FLOAT64:
                    jsValue = new JSFloat64(value, this, QuickJS.getValueFloat64(value));
                    break;
                default:
                    jsValue = new JSInternal(value, this);
                    break;
            }

            // Register it to cleaner
            cleaner.register(jsValue, value);

            return jsValue;
        } catch (Exception e) {
            JNIBridgeModuleHelper.wrapAsJSValueException(e);
        }
        return new JSUndefined(value, this);
    }

    int getNotRemovedJSValueCount() {
        synchronized (jsRuntime) {
            return cleaner.size();
        }
    }

    public void initModuleStd() {
        synchronized (jsRuntime) {
            checkClosed();
            QuickJS.initModuleStd(pointer);
        }
    }

    public void initModuleOs() {
        synchronized (jsRuntime) {
            checkClosed();
            QuickJS.initModuleOs(pointer);
        }
    }

    public void initModuleBridge(String module) {
        synchronized (jsRuntime) {
            checkClosed();
            QuickJS.initModuleBridge(pointer, module);
        }
    }

    public void registerBridgeModuleListener(BridgeModuleListener listener) {
        synchronized (jsRuntime) {
            checkClosed();
            JNIBridgeModuleHelper.setListener(listener);
        }
    }

    @Override
    public void close() {
        synchronized (jsRuntime) {
            if (pointer != 0) {
                // Destroy all JSValue
                cleaner.forceClean();
                // Destroy self
                long contextToClose = pointer;
                pointer = 0;
                QuickJS.destroyContext(contextToClose);
            }
        }
    }

    private class JSValueCleaner extends NativeCleaner<JSValue> {

        @Override
        public void onRemove(long pointer) {
            QuickJS.destroyValue(JSContext.this.pointer, pointer);
        }
    }
}
