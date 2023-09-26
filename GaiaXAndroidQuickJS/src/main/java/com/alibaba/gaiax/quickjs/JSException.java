package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * The information of JavaScript exception.
 */
@Keep
public class JSException {

    private final boolean isError;
    private final String exception;
    private final String stack;

    private JSException(boolean isError, String exception, String stack) {
        this.isError = isError;
        this.exception = exception;
        this.stack = stack;
    }

    public boolean isError() {
        return isError;
    }

    /**
     * The exception message.
     */
    public String getException() {
        return exception;
    }

    /**
     * The stack trace.
     */
    public String getStack() {
        return stack;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!isError) {
            sb.append("Throw: ");
        }
        sb.append(exception).append("\n");
        if (stack != null) {
            sb.append(stack);
        }
        return sb.toString();
    }
}
