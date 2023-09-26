package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

@Keep
final class JSFloat64 extends JSNumber {

    private final double value;

    JSFloat64(long pointer, JSContext jsContext, double value) {
        super(pointer, jsContext);
        this.value = value;
    }

    private String wrongNumberMessage(String javaType, double value) {
        return "Can't treat " + value + " as " + javaType;
    }

    @Override
    public byte getByte() {
        double value = this.value;
        byte result = (byte) value;
        if (result != value) {
            throw new JSDataException(wrongNumberMessage("byte", value));
        }
        return result;
    }

    @Override
    public short getShort() {
        double value = this.value;
        short result = (short) value;
        if (result != value) {
            throw new JSDataException(wrongNumberMessage("short", value));
        }
        return result;
    }

    @Override
    public int getInt() {
        double value = this.value;
        int result = (int) value;
        if (result != value) {
            throw new JSDataException(wrongNumberMessage("int", value));
        }
        return result;
    }

    @Override
    public long getLong() {
        double value = this.value;
        long result = (long) value;
        if (result != value) {
            throw new JSDataException(wrongNumberMessage("long", value));
        }
        return result;
    }

    @Override
    public float getFloat() {
        return (float) value;
    }

    @Override
    public double getDouble() {
        return value;
    }
}
