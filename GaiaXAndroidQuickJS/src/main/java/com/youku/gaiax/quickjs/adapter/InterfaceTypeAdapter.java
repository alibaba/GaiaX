package com.youku.gaiax.quickjs.adapter;

import com.youku.gaiax.quickjs.JSFunction;
import com.youku.gaiax.quickjs.JSObject;
import com.youku.gaiax.quickjs.JSValue;
import com.youku.gaiax.quickjs.Method;
import com.youku.gaiax.quickjs.Types;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InterfaceTypeAdapter extends TypeAdapter<Object> {

    /**
     * Returns all methods in the interface type.
     * Returns {@code null} if the type is not interface,
     * or any method is overloaded, or any type can't be resolved.
     */
    static Map<String, Method> getInterfaceMethods(Type type) {
        Class<?> rawType = Types.getRawType(type);
        if (!rawType.isInterface()) return null;

        Map<String, Method> methods = new HashMap<>();

        for (java.lang.reflect.Method method : rawType.getMethods()) {
            Type returnType = Types.resolve(type, rawType, method.getGenericReturnType());
            // It's not resolved
            if (returnType instanceof TypeVariable) return null;

            String name = method.getName();

            Type[] originParameterTypes = method.getGenericParameterTypes();
            Type[] parameterTypes = new Type[originParameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                parameterTypes[i] = Types.resolve(type, rawType, originParameterTypes[i]);
                // It's not resolved
                if (parameterTypes[i] instanceof TypeVariable) return null;
            }

            Method oldMethod = methods.get(name);
            if (oldMethod != null) {
                if (!Arrays.equals(oldMethod.parameterTypes, parameterTypes)) {
                    // overload is not supported
                    return null;
                }
                if (returnType.equals(oldMethod.returnType)
                        || Types.getRawType(returnType).isAssignableFrom(Types.getRawType(oldMethod.returnType))) {
                    // The new method is overridden
                    continue;
                }
            }

            methods.put(name, new Method(returnType, name, parameterTypes));
        }

        return methods;
    }

    public static final Factory FACTORY = new Factory() {
        @Override
        public TypeAdapter<?> create(Depot depot, Type type) {
            Map<String, Method> methods = getInterfaceMethods(type);
            if (methods == null) return null;
            return new InterfaceTypeAdapter(Types.getRawType(type), methods).nullable();
        }
    };

    private final Class<?> rawType;
    private final Map<String, Method> methods;

    private InterfaceTypeAdapter(Class<?> rawType, Map<String, Method> methods) {
        this.rawType = rawType;
        this.methods = methods;
    }

    @Override
    public JSValue toJSValue(Depot depot, Context context, Object value) {
        if (value instanceof JSValueHolder) {
            return ((JSValueHolder) value).getJSValue(JS_VALUE_HOLDER_TAG);
        }

        JSObject jo = context.createJSObject(value);
        for (Method method : methods.values()) {
            jo.setProperty(method.name, context.createJSFunction(value, method));
        }
        return jo;
    }

    @Override
    public Object fromJSValue(final Depot depot, final Context context, final JSValue value) {
        final JSObject jo = value.cast(JSObject.class);

        Object object = jo.getJavaObject();
        // TODO Check generic
        if (rawType.isInstance(object)) return object;

        return Proxy.newProxyInstance(rawType.getClassLoader(), new Class<?>[]{rawType, JSValueHolder.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
                // If the method is a method from Object then defer to normal invocation.
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }

                // Check JSValueHolder.getJSValue(JSValueHolderTag)
                if (args != null && args.length == 1 && args[0] == JS_VALUE_HOLDER_TAG) {
                    return value;
                }

                String name = method.getName();
                Method simpleMethod = methods.get(name);
                if (simpleMethod == null) throw new NoSuchMethodException("Can't find method: " + name);

                int parameterNumber = args != null ? args.length : 0;
                if (parameterNumber != simpleMethod.parameterTypes.length)
                    throw new IllegalStateException("Parameter number doesn't match: " + name);
                JSValue[] parameters = new JSValue[parameterNumber];
                for (int i = 0; i < parameterNumber; i++) {
                    Type type = simpleMethod.parameterTypes[i];
                    TypeAdapter<Object> adapter = depot.getAdapter(type);
                    parameters[i] = adapter.toJSValue(depot, context, args[i]);
                }

                Type resultType = simpleMethod.returnType;
                TypeAdapter<?> resultAdapter = depot.getAdapter(resultType);

                JSFunction function = jo.getProperty(name).cast(JSFunction.class);

                JSValue result = function.invoke(jo, parameters);

                return resultAdapter.fromJSValue(depot, context, result);
            }
        });
    }

    private interface JSValueHolder {
        JSValue getJSValue(JSValueHolderTag tag);
    }

    private static class JSValueHolderTag {
    }

    private static final JSValueHolderTag JS_VALUE_HOLDER_TAG = new JSValueHolderTag();
}
