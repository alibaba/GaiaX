package com.youku.gaiax.quickjs;


import androidx.annotation.Keep;

@Keep
public class JSArrayBuffer extends JSObject {

    JSArrayBuffer(long pointer, JSContext jsContext) {
        super(pointer, jsContext, null);
    }

    public int getByteLength() {
        return getProperty("byteLength").cast(JSNumber.class).getInt();
    }

    public boolean[] toBooleanArray() {
        return QuickJS.toBooleanArray(jsContext.pointer, pointer);
    }

    public byte[] toByteArray() {
        return QuickJS.toByteArray(jsContext.pointer, pointer);
    }

    /**
     * @throws IllegalStateException if its byteLength isn't a multiple of 2
     */
    public char[] toCharArray() {
        return QuickJS.toCharArray(jsContext.pointer, pointer);
    }

    /**
     * @throws IllegalStateException if its byteLength isn't a multiple of 2
     */
    public short[] toShortArray() {
        return QuickJS.toShortArray(jsContext.pointer, pointer);
    }

    /**
     * @throws IllegalStateException if its byteLength isn't a multiple of 4
     */
    public int[] toIntArray() {
        return QuickJS.toIntArray(jsContext.pointer, pointer);
    }

    /**
     * @throws IllegalStateException if its byteLength isn't a multiple of 8
     */
    public long[] toLongArray() {
        return QuickJS.toLongArray(jsContext.pointer, pointer);
    }

    /**
     * @throws IllegalStateException if its byteLength isn't a multiple of 4
     */
    public float[] toFloatArray() {
        return QuickJS.toFloatArray(jsContext.pointer, pointer);
    }

    /**
     * @throws IllegalStateException if its byteLength isn't a multiple of 8
     */
    public double[] toDoubleArray() {
        return QuickJS.toDoubleArray(jsContext.pointer, pointer);
    }
}
