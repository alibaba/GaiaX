package com.youku.gaiax.quickjs;


import androidx.annotation.Keep;

import com.youku.gaiax.quickjs.adapter.TypeAdapter;

import java.lang.reflect.Type;

@Keep
class JNIHelper {

    private static final Type VOID_PRIMITIVE_TYPE = void.class;
    private static final Type CHAR_PRIMITIVE_TYPE = char.class;
    private static final Type BOOLEAN_PRIMITIVE_TYPE = boolean.class;
    private static final Type BYTE_PRIMITIVE_TYPE = byte.class;
    private static final Type SHORT_PRIMITIVE_TYPE = short.class;
    private static final Type INT_PRIMITIVE_TYPE = int.class;
    private static final Type LONG_PRIMITIVE_TYPE = long.class;
    private static final Type FLOAT_PRIMITIVE_TYPE = float.class;
    private static final Type DOUBLE_PRIMITIVE_TYPE = double.class;

    private static Object jsValueToJavaValue(JSContext jsContext, Type type, long value) {
        synchronized (jsContext.jsRuntime) {
            JSValue jsValue = null;
            try {
                jsContext.checkClosed();
                TypeAdapter<Object> adapter = jsContext.quickJS.getAdapter(type);
                jsValue = jsContext.wrapAsJSValue(value);
                return adapter.fromJSValue(jsContext.quickJS, jsContext, jsValue);
            } finally {
                if (jsValue == null) {
                    QuickJS.destroyValue(jsContext.pointer, value);
                }
            }
        }
    }

    private static long javaValueToJSValue(JSContext jsContext, Type type, boolean value) {
        return javaValueToJSValue(jsContext, type, (Boolean) value);
    }

    private static long javaValueToJSValue(JSContext jsContext, Type type, char value) {
        return javaValueToJSValue(jsContext, type, (Character) value);
    }

    private static long javaValueToJSValue(JSContext jsContext, Type type, byte value) {
        return javaValueToJSValue(jsContext, type, (Byte) value);
    }

    private static long javaValueToJSValue(JSContext jsContext, Type type, short value) {
        return javaValueToJSValue(jsContext, type, (Short) value);
    }

    private static long javaValueToJSValue(JSContext jsContext, Type type, int value) {
        return javaValueToJSValue(jsContext, type, (Integer) value);
    }

    private static long javaValueToJSValue(JSContext jsContext, Type type, long value) {
        return javaValueToJSValue(jsContext, type, (Long) value);
    }

    private static long javaValueToJSValue(JSContext jsContext, Type type, float value) {
        return javaValueToJSValue(jsContext, type, (Float) value);
    }

    private static long javaValueToJSValue(JSContext jsContext, Type type, double value) {
        return javaValueToJSValue(jsContext, type, (Double) value);
    }

    private static long javaValueToJSValue(JSContext jsContext, Type type, Object value) {
        synchronized (jsContext.jsRuntime) {
            jsContext.checkClosed();
            TypeAdapter<Object> adapter = jsContext.quickJS.getAdapter(type);
            return adapter.toJSValue(jsContext.quickJS, jsContext, value).pointer;
        }
    }

    private static boolean isPrimitiveType(Type type) {
        return type instanceof Class && ((Class<?>) type).isPrimitive();
    }

    @SuppressWarnings("EqualsReplaceableByObjectsCall")
    private static boolean isSameType(Type t1, Type t2) {
        return (t1 == t2) || (t1 != null && t1.equals(t2));
    }

    private static boolean unbox(Boolean value) {
        return value;
    }

    private static char unbox(Character value) {
        return value;
    }

    private static byte unbox(Byte value) {
        return value;
    }

    private static short unbox(Short value) {
        return value;
    }

    private static int unbox(Integer value) {
        return value;
    }

    private static long unbox(Long value) {
        return value;
    }

    private static float unbox(Float value) {
        return value;
    }

    private static double unbox(Double value) {
        return value;
    }
}
