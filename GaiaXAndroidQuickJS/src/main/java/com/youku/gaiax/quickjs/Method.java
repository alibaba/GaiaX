package com.youku.gaiax.quickjs;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

/**
 * Represents a java method or a java static method.
 */
public final class Method {

    public static Method create(Type type, java.lang.reflect.Method rawMethod) {
        Class<?> rawType = Types.getRawType(type);
        Type returnType = Types.resolve(type, rawType, rawMethod.getGenericReturnType());
        // It's not resolved
        if (returnType instanceof TypeVariable) return null;

        String name = rawMethod.getName();

        Type[] originParameterTypes = rawMethod.getGenericParameterTypes();
        Type[] parameterTypes = new Type[originParameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = Types.resolve(type, rawType, originParameterTypes[i]);
            // It's not resolved
            if (parameterTypes[i] instanceof TypeVariable) return null;
        }

        return new Method(returnType, name, parameterTypes);
    }

    public final Type returnType;
    public final String name;
    public final Type[] parameterTypes;

    public Method(Type returnType, String name, Type[] parameterTypes) {
        this.returnType = canonicalize(returnType);
        this.name = name;
        this.parameterTypes = new Type[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            this.parameterTypes[i] = canonicalize(parameterTypes[i]);
        }
    }

    private static Type canonicalize(Type type) {
        return Types.removeSubtypeWildcard(Types.canonicalize(type));
    }

    private static String getTypeSignature(Type type) {
        // Array
        if (type instanceof Types.GenericArrayTypeImpl) {
            return "[" + getTypeSignature(((Types.GenericArrayTypeImpl) type).getGenericComponentType());
        }

        // Primitive
        if (type instanceof Class && ((Class<?>) type).isPrimitive()) {
            if (type == void.class) return "V";
            if (type == boolean.class) return "Z";
            if (type == byte.class) return "B";
            if (type == char.class) return "C";
            if (type == short.class) return "S";
            if (type == int.class) return "I";
            if (type == long.class) return "J";
            if (type == float.class) return "F";
            if (type == double.class) return "D";
        }

        // Class
        Class<?> clazz = Types.getRawType(type);
        String name = clazz.getName();
        StringBuilder sb = new StringBuilder(name.length() + 2);
        sb.append("L");
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            sb.append(c == '.' ? '/' : c);
        }
        sb.append(";");
        return sb.toString();
    }

    // For jni
    String getSignature() {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        for (Type parameterType : parameterTypes) {
            sb.append(getTypeSignature(parameterType));
        }
        sb.append(")");
        sb.append(getTypeSignature(returnType));

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType);
        sb.append(" ");
        sb.append(name);
        sb.append("(");
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i != 0) sb.append(", ");
            sb.append(parameterTypes[i]);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + returnType.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + Arrays.hashCode(parameterTypes);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Method)) return false;
        Method other = (Method) obj;
        return returnType.equals(other.returnType)
                && name.equals(other.name)
                && Arrays.equals(parameterTypes, other.parameterTypes);
    }
}
