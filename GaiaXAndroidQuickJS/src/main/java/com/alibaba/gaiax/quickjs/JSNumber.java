package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * JavaScript number.
 */
@Keep
public abstract class JSNumber extends JSValue {

    JSNumber(long pointer, JSContext jsContext) {
        super(pointer, jsContext);
    }

    /**
     * Returns byte value.
     *
     * @throws JSDataException if it has decimal part, or bigger than {@link Byte#MAX_VALUE},
     *                         or smaller than {@link Byte#MIN_VALUE}
     */
    public abstract byte getByte();

    /**
     * Returns short value.
     *
     * @throws JSDataException if it has decimal part, or bigger than {@link Short#MAX_VALUE},
     *                         or smaller than {@link Short#MIN_VALUE}
     */
    public abstract short getShort();

    /**
     * Return int value.
     *
     * @throws JSDataException if it has decimal part, or bigger than {@link Integer#MAX_VALUE},
     *                         or smaller than {@link Integer#MIN_VALUE}
     */
    public abstract int getInt();

    /**
     * Return long value.
     *
     * @throws JSDataException if it has decimal part, or bigger than {@link Long#MAX_VALUE},
     *                         or smaller than {@link Long#MIN_VALUE}
     */
    public abstract long getLong();

    /**
     * Return float value.
     */
    public abstract float getFloat();

    /**
     * Return double value.
     */
    public abstract double getDouble();
}
