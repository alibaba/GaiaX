package com.youku.gaiax.quickjs.adapter;

import com.youku.gaiax.quickjs.JSArray;
import com.youku.gaiax.quickjs.JSValue;
import com.youku.gaiax.quickjs.Types;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

public class ArrayTypeAdapter extends TypeAdapter<Object> {

    public static final Factory FACTORY = new Factory() {
        @Override
        public TypeAdapter<?> create(Depot depot, Type type) {
            Type elementType = Types.arrayComponentType(type);
            if (elementType == null) return null;
            Class<?> elementClass = Types.getRawType(elementType);
            TypeAdapter<Object> elementAdapter = depot.getAdapter(elementType);
            return new ArrayTypeAdapter(elementClass, elementAdapter).nullable();
        }
    };

    private final Class<?> elementClass;
    private final TypeAdapter<Object> elementAdapter;

    private ArrayTypeAdapter(Class<?> elementClass, TypeAdapter<Object> elementAdapter) {
        this.elementClass = elementClass;
        this.elementAdapter = elementAdapter;
    }

    @Override
    public JSValue toJSValue(Depot depot, Context context, Object value) {
        JSArray result = context.createJSArray();
        for (int i = 0, length = Array.getLength(value); i < length; i++) {
            result.setProperty(i, elementAdapter.toJSValue(depot, context, Array.get(value, i)));
        }
        return result;
    }

    @Override
    public Object fromJSValue(Depot depot, Context context, JSValue value) {
        JSArray array = value.cast(JSArray.class);
        int length = array.getLength();
        Object result = Array.newInstance(elementClass, length);
        for (int i = 0; i < length; i++) {
            Array.set(result, i, elementAdapter.fromJSValue(depot, context, array.getProperty(i)));
        }
        return result;
    }
}
