package com.youku.gaiax.quickjs.adapter;

import com.youku.gaiax.quickjs.JSFunction;
import com.youku.gaiax.quickjs.JSValue;

import java.lang.reflect.Type;

public class JSFunctionAdapter {

    public static final TypeAdapter.Factory FACTORY = new TypeAdapter.Factory() {

        @Override
        public TypeAdapter<?> create(TypeAdapter.Depot depot, Type type) {
            if (type == JSFunction.class) return JS_FUNCTION_TYPE_ADAPTER;
            return null;
        }
    };

    private static final TypeAdapter<JSFunction> JS_FUNCTION_TYPE_ADAPTER = new TypeAdapter<JSFunction>() {
        @Override
        public JSFunction toJSValue(Depot depot, Context context, JSFunction value) {
            if (value == null) throw new NullPointerException("value == null");
            return value;
        }

        @Override
        public JSFunction fromJSValue(Depot depot, Context context, JSValue value) {
            return value.cast(JSFunction.class);
        }
    };
}
