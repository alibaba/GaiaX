package com.youku.gaiax.quickjs.adapter;

import com.youku.gaiax.quickjs.JSValue;

import java.lang.reflect.Type;

public class JSValueAdapter {

    public static final TypeAdapter.Factory FACTORY = new TypeAdapter.Factory() {

        @Override
        public TypeAdapter<?> create(TypeAdapter.Depot depot, Type type) {
            if (type == JSValue.class) return JS_VALUE_TYPE_ADAPTER;
            return null;
        }
    };

    private static final TypeAdapter<JSValue> JS_VALUE_TYPE_ADAPTER = new TypeAdapter<JSValue>() {
        @Override
        public JSValue toJSValue(Depot depot, Context context, JSValue value) {
            if (value == null) throw new NullPointerException("value == null");
            return value;
        }

        @Override
        public JSValue fromJSValue(Depot depot, Context context, JSValue value) {
            return value;
        }
    };
}
