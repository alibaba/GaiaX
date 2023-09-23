package com.youku.gaiax.quickjs.adapter;


import com.youku.gaiax.quickjs.JSArray;
import com.youku.gaiax.quickjs.JSBoolean;
import com.youku.gaiax.quickjs.JSDataException;
import com.youku.gaiax.quickjs.JSFunction;
import com.youku.gaiax.quickjs.JSNull;
import com.youku.gaiax.quickjs.JSNumber;
import com.youku.gaiax.quickjs.JSObject;
import com.youku.gaiax.quickjs.JSString;
import com.youku.gaiax.quickjs.JSUndefined;
import com.youku.gaiax.quickjs.JSValue;
import com.youku.gaiax.quickjs.Method;

import java.lang.reflect.Type;

public abstract class TypeAdapter<T> {
    /**
     * Converts the java value to {@code JSValue}.
     * Throws {@link JSDataException} if the value can't be handled.
     */
    public abstract JSValue toJSValue(Depot depot, Context context, T value);

    /**
     * Converts the {@code JSValue} to java value.
     */
    public abstract T fromJSValue(Depot depot, Context context, JSValue value);

    /**
     * Returns a TypeAdapter equal to this TypeAdapter,
     * but with support for null java object and null/undefined javascript value.
     */
    public final TypeAdapter<T> nullable() {
        return new NullableTypeAdapter<>(this);
    }

    private static class NullableTypeAdapter<T> extends TypeAdapter<T> {

        private final TypeAdapter<T> delegate;

        NullableTypeAdapter(TypeAdapter<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public JSValue toJSValue(Depot depot, Context context, T value) {
            if (value == null) return context.createJSNull();
            return delegate.toJSValue(depot, context, value);
        }

        @Override
        public T fromJSValue(Depot depot, Context context, JSValue value) {
            if (value instanceof JSNull || value instanceof JSUndefined) return null;
            return delegate.fromJSValue(depot, context, value);
        }
    }

    public interface Factory {
        TypeAdapter<?> create(Depot depot, Type type);
    }

    public interface Depot {
        /**
         * Returns a TypeAdapter for the type.
         *
         * @throws IllegalArgumentException if no TypeAdapter matched
         */
        <T> TypeAdapter<T> getAdapter(Type type);
    }

    public interface Context {

        /**
         * Creates a JavaScript undefined.
         */
        JSUndefined createJSUndefined();

        /**
         * Creates a JavaScript null.
         */
        JSNull createJSNull();

        /**
         * Creates a JavaScript boolean.
         */
        JSBoolean createJSBoolean(boolean value);

        /**
         * Creates a JavaScript number.
         */
        JSNumber createJSNumber(int value);

        /**
         * Creates a JavaScript number.
         */
        JSNumber createJSNumber(double value);

        /**
         * Creates a JavaScript number.
         */
        JSNumber createJSNumber(long value);

        /**
         * Creates a JavaScript string.
         */
        JSString createJSString(String value);

        /**
         * Creates a JavaScript object.
         */
        JSObject createJSObject();

        /**
         * Creates a JavaScript js object.
         */
        JSObject createJSJsonObject(String json);

        /**
         * Creates a JavaScript object holding a java object.
         */
        JSObject createJSObject(Object object);

        /**
         * Creates a JavaScript array.
         */
        JSArray createJSArray();

        /**
         * Create a JavaScript function from a java non-static method.
         */
        JSFunction createJSFunction(Object instance, Method method);

        /**
         * Create a JavaScript function from a java static method.
         */
        JSFunction createJSFunctionS(Class clazz, Method method);
    }
}
