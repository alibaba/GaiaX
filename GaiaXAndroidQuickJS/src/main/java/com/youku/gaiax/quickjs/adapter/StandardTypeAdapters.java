package com.youku.gaiax.quickjs.adapter;

import com.youku.gaiax.quickjs.JSBoolean;
import com.youku.gaiax.quickjs.JSDataException;
import com.youku.gaiax.quickjs.JSNull;
import com.youku.gaiax.quickjs.JSNumber;
import com.youku.gaiax.quickjs.JSString;
import com.youku.gaiax.quickjs.JSUndefined;
import com.youku.gaiax.quickjs.JSValue;

import java.lang.reflect.Type;

public class StandardTypeAdapters {

    public static final TypeAdapter.Factory FACTORY = new TypeAdapter.Factory() {

        @Override
        public TypeAdapter<?> create(TypeAdapter.Depot depot, Type type) {
            if (type == void.class) return VOID_TYPE_ADAPTER;
            if (type == boolean.class) return BOOLEAN_TYPE_ADAPTER;
            if (type == byte.class) return BYTE_TYPE_ADAPTER;
            if (type == char.class) return CHARACTER_TYPE_ADAPTER;
            if (type == short.class) return SHORT_TYPE_ADAPTER;
            if (type == int.class) return INTEGER_TYPE_ADAPTER;
            if (type == long.class) return LONG_TYPE_ADAPTER;
            if (type == float.class) return FLOAT_TYPE_ADAPTER;
            if (type == double.class) return DOUBLE_TYPE_ADAPTER;
            if (type == Void.class) return VOID_TYPE_ADAPTER;
            if (type == Boolean.class) return BOOLEAN_TYPE_ADAPTER.nullable();
            if (type == Byte.class) return BYTE_TYPE_ADAPTER.nullable();
            if (type == Character.class) return CHARACTER_TYPE_ADAPTER.nullable();
            if (type == Short.class) return SHORT_TYPE_ADAPTER.nullable();
            if (type == Integer.class) return INTEGER_TYPE_ADAPTER.nullable();
            if (type == Long.class) return LONG_TYPE_ADAPTER.nullable();
            if (type == Float.class) return FLOAT_TYPE_ADAPTER.nullable();
            if (type == Double.class) return DOUBLE_TYPE_ADAPTER.nullable();
            if (type == String.class) return STRING_TYPE_ADAPTER.nullable();
            return null;
        }
    };

    private static final TypeAdapter<Void> VOID_TYPE_ADAPTER = new TypeAdapter<Void>() {
        @Override
        public JSValue toJSValue(Depot depot, Context context, Void value) {
            return context.createJSNull();
        }

        @Override
        public Void fromJSValue(Depot depot, Context context, JSValue value) {
            if (value instanceof JSNull || value instanceof JSUndefined) return null;
            throw new JSDataException("excepted: JSNull or JSUndefined, actual: " + value.getClass().getSimpleName());
        }
    };

    private static final TypeAdapter<Boolean> BOOLEAN_TYPE_ADAPTER = new TypeAdapter<Boolean>() {
        @Override
        public JSValue toJSValue(Depot depot, Context context, Boolean value) {
            return context.createJSBoolean(value);
        }

        @Override
        public Boolean fromJSValue(Depot depot, Context context, JSValue value) {
            return value.cast(JSBoolean.class).getBoolean();
        }
    };

    private static final TypeAdapter<Byte> BYTE_TYPE_ADAPTER = new TypeAdapter<Byte>() {
        @Override
        public JSValue toJSValue(Depot depot, Context context, Byte value) {
            return context.createJSNumber(value);
        }

        @Override
        public Byte fromJSValue(Depot depot, Context context, JSValue value) {
            return value.cast(JSNumber.class).getByte();
        }
    };

    private static final TypeAdapter<Character> CHARACTER_TYPE_ADAPTER = new TypeAdapter<Character>() {
        @Override
        public JSValue toJSValue(Depot depot, Context context, Character value) {
            return context.createJSString(value.toString());
        }

        @Override
        public Character fromJSValue(Depot depot, Context context, JSValue value) {
            String str = value.cast(JSString.class).getString();
            if (str.length() != 1) {
                throw new JSDataException("Can't treat \"" + str + "\" as char");
            }
            return str.charAt(0);
        }
    };

    private static final TypeAdapter<Short> SHORT_TYPE_ADAPTER = new TypeAdapter<Short>() {
        @Override
        public JSValue toJSValue(Depot depot, Context context, Short value) {
            return context.createJSNumber(value);
        }

        @Override
        public Short fromJSValue(Depot depot, Context context, JSValue value) {
            return value.cast(JSNumber.class).getShort();
        }
    };

    private static final TypeAdapter<Integer> INTEGER_TYPE_ADAPTER = new TypeAdapter<Integer>() {
        @Override
        public JSValue toJSValue(Depot depot, Context context, Integer value) {
            return context.createJSNumber(value);
        }

        @Override
        public Integer fromJSValue(Depot depot, Context context, JSValue value) {
            return value.cast(JSNumber.class).getInt();
        }
    };

    private static final TypeAdapter<Long> LONG_TYPE_ADAPTER = new TypeAdapter<Long>() {
        @Override
        public JSValue toJSValue(Depot depot, Context context, Long value) {
            return context.createJSNumber(value);
        }

        @Override
        public Long fromJSValue(Depot depot, Context context, JSValue value) {
            return value.cast(JSNumber.class).getLong();
        }
    };

    private static final TypeAdapter<Float> FLOAT_TYPE_ADAPTER = new TypeAdapter<Float>() {
        @Override
        public JSValue toJSValue(Depot depot, Context context, Float value) {
            return context.createJSNumber(value);
        }

        @Override
        public Float fromJSValue(Depot depot, Context context, JSValue value) {
            return value.cast(JSNumber.class).getFloat();
        }
    };

    private static final TypeAdapter<Double> DOUBLE_TYPE_ADAPTER = new TypeAdapter<Double>() {
        @Override
        public JSValue toJSValue(Depot depot, Context context, Double value) {
            return context.createJSNumber(value);
        }

        @Override
        public Double fromJSValue(Depot depot, Context context, JSValue value) {
            return value.cast(JSNumber.class).getDouble();
        }
    };

    private static final TypeAdapter<String> STRING_TYPE_ADAPTER = new TypeAdapter<String>() {
        @Override
        public JSValue toJSValue(Depot depot, Context context, String value) {
            return context.createJSString(value);
        }

        @Override
        public String fromJSValue(Depot depot, Context context, JSValue value) {
            return value.cast(JSString.class).getString();
        }
    };
}
