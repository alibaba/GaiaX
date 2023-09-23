package com.youku.gaiax.quickjs;


import androidx.annotation.Keep;

import com.youku.gaiax.quickjs.adapter.ArrayTypeAdapter;
import com.youku.gaiax.quickjs.adapter.InterfaceTypeAdapter;
import com.youku.gaiax.quickjs.adapter.JSFunctionAdapter;
import com.youku.gaiax.quickjs.adapter.JSValueAdapter;
import com.youku.gaiax.quickjs.adapter.StandardTypeAdapters;
import com.youku.gaiax.quickjs.adapter.TypeAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * QuickJS is a resources container to create {@link JSRuntime}s.
 * <p>
 * Documents: https://bellard.org/quickjs/quickjs.html
 */
@Keep
public class QuickJS implements TypeAdapter.Depot {

    private static final List<TypeAdapter.Factory> BUILT_IN_FACTORIES = new ArrayList<>(4);

    static {
        BUILT_IN_FACTORIES.add(StandardTypeAdapters.FACTORY);
        BUILT_IN_FACTORIES.add(JSValueAdapter.FACTORY);
        BUILT_IN_FACTORIES.add(ArrayTypeAdapter.FACTORY);
        BUILT_IN_FACTORIES.add(InterfaceTypeAdapter.FACTORY);
        BUILT_IN_FACTORIES.add(JSFunctionAdapter.FACTORY);
    }

    private final List<TypeAdapter.Factory> factories;
    private final Map<Type, TypeAdapter<?>> adapterCache;

    private QuickJS(QuickJS.Builder builder) {
        List<TypeAdapter.Factory> factories = new ArrayList<>(builder.factories.size() + BUILT_IN_FACTORIES.size());
        factories.addAll(builder.factories);
        factories.addAll(BUILT_IN_FACTORIES);
        this.factories = Collections.unmodifiableList(factories);
        this.adapterCache = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> getAdapter(Type type) {
        // Canonicalize type
        Type newType = Types.removeSubtypeWildcard(Types.canonicalize(type));

        TypeAdapter<?> adapter = adapterCache.get(newType);
        if (adapter != null) {
            return (TypeAdapter<T>) adapter;
        }

        for (int i = 0, size = factories.size(); i < size; i++) {
            adapter = factories.get(i).create(this, newType);
            if (adapter != null) {
                adapterCache.put(newType, adapter);
                return (TypeAdapter<T>) adapter;
            }
        }

        throw new IllegalArgumentException("Can't find TypeAdapter for " + type);
    }

    /**
     * Creates a JSRuntime with resources in this QuickJS.
     */
    public JSRuntime createJSRuntime() {
        long runtime = QuickJS.createRuntime();
        if (runtime == 0) {
            throw new IllegalStateException("Cannot create JSRuntime instance");
        }
        return new JSRuntime(runtime, this);
    }

    public static class Builder {

        private final List<TypeAdapter.Factory> factories = new ArrayList<>();

        public <T> Builder registerTypeAdapter(final Type type, final TypeAdapter<T> adapter) {
            return registerTypeAdapterFactory(new TypeAdapter.Factory() {
                @Override
                public TypeAdapter<?> create(TypeAdapter.Depot depot, Type targetType) {
                    if (Types.equals(type, targetType)) {
                        return adapter;
                    }
                    return null;
                }
            });
        }

        public Builder registerTypeAdapterFactory(TypeAdapter.Factory factory) {
            factories.add(factory);
            return this;
        }

        public QuickJS build() {
            return new QuickJS(this);
        }
    }

    static {
        System.loadLibrary("quickjs");
        System.loadLibrary("gaiaxjs");
    }

    static native long createRuntime();

    static native void setRuntimeMallocLimit(long runtime, int mallocLimit);

    static native void setRuntimeMaxStackSize(long pointer, int size);

    static native void setRuntimeInterruptHandler(long runtime, JSRuntime.InterruptHandler interruptHandler);

    static native void setPromiseRejectionHandler(long runtime, JSRuntime.PromiseRejectionHandler promiseRejectionHandler);

    static native void destroyRuntime(long runtime);

    static native long createContext(long runtime);

    static native void initModuleStd(long context);

    static native void initModuleOs(long context);

    static native void initModuleBridge(long context, String moduleName);

    static native void initStdHandlers(long runtime);

    static native void destroyContext(long context);

    static native long createValueUndefined(long context);

    static native long createValueNull(long context);

    static native long createValueBoolean(long context, boolean value);

    static native long createValueInt(long context, int value);

    static native long createValueFloat64(long context, double value);

    static native long createValueString(long context, String value);

    static native long createValueObject(long context);

    static native long createValueJson(long context, String value);

    static native long createValueArray(long context);

    static native long createValueArrayBufferZ(long context, boolean[] array, int start, int length);

    static native long createValueArrayBufferB(long context, byte[] array, int start, int length);

    static native long createValueArrayBufferC(long context, char[] array, int start, int length);

    static native long createValueArrayBufferS(long context, short[] array, int start, int length);

    static native long createValueArrayBufferI(long context, int[] array, int start, int length);

    static native long createValueArrayBufferJ(long context, long[] array, int start, int length);

    static native long createValueArrayBufferF(long context, float[] array, int start, int length);

    static native long createValueArrayBufferD(long context, double[] array, int start, int length);

    static native long createValueFunction(long context, JSContext jsContext, Object instance, String methodName, String methodSign, Type returnType, Type[] argTypes, boolean isCallbackMethod);

    static native long createValueFunctionS(long context, JSContext jsContext, String className, String methodName, String methodSign, Type returnType, Type[] argTypes);

    static native long createValueJavaObject(long context, Object object);

    static native long[] createValuePromise(long context);

    static native int getValueTag(long value);

    static native boolean isValueArray(long context, long value);

    static native boolean isValueArrayBuffer(long context, long value);

    public static native boolean isValueFunction(long context, long value);

    static native long getValueProperty(long context, long value, int index);

    static native long getValueProperty(long context, long value, String name);

    static native boolean setValueProperty(long context, long value, int index, long property);

    static native boolean setValueProperty(long context, long value, String name, long property);

    static native boolean[] toBooleanArray(long context, long value);

    static native byte[] toByteArray(long context, long value);

    static native char[] toCharArray(long context, long value);

    static native short[] toShortArray(long context, long value);

    static native int[] toIntArray(long context, long value);

    static native long[] toLongArray(long context, long value);

    static native float[] toFloatArray(long context, long value);

    static native double[] toDoubleArray(long context, long value);

    static native boolean getValueBoolean(long value);

    static native int getValueInt(long value);

    static native double getValueFloat64(long value);

    static native String getValueString(long context, long value);

    static native String getValueJsonString(long context, long value);

    static native Object getValueJavaObject(long context, long value);

    static native boolean defineValueProperty(long context, long value, int index, long property, int flags);

    static native boolean defineValueProperty(long context, long value, String name, long property, int flags);

    static native long invokeValueFunction(long context, long function, long thisObj, long[] args);

    static native void destroyValue(long context, long value);

    static native void freeValue(long context, long value);

    public static native void dupValue(long context, long value);

    static native JSException getException(long context);

    static native long getGlobalObject(long context);

    static native long evaluate(long context, String sourceCode, String fileName, int flags);

    static native int executePendingJob(long context);

}
