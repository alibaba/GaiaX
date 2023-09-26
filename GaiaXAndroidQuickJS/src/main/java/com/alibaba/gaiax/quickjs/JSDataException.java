package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * This exception is raised if JSValue can't be convert to Java type.
 */
@Keep
public class JSDataException extends RuntimeException {
    public JSDataException(String message) {
        super(message);
    }
}
