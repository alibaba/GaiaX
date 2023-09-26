package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

@Keep
public final class JSInt extends JSNumber {

    private final int value;

    JSInt(long pointer, JSContext jsContext, int value) {
        super(pointer, jsContext);
        this.value = value;
    }

    private int getIntInRange(String javaType, int min, int max) {
        int value = this.value;
        if (min <= value && value <= max) {
            return value;
        } else {
            throw new JSDataException("Can't treat " + value + " as " + javaType);
        }
    }

    @Override
    public byte getByte() {
        return (byte) getIntInRange("byte", Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    @Override
    public short getShort() {
        return (short) getIntInRange("short", Short.MIN_VALUE, Short.MAX_VALUE);
    }

    @Override
    public int getInt() {
        return value;
    }

    @Override
    public long getLong() {
        return value;
    }

    @Override
    public float getFloat() {
        return value;
    }

    @Override
    public double getDouble() {
        return value;
    }
}
