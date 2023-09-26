package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

/**
 * This exception is raised if QuickJS raises a JavaScript exception.
 */
@Keep
public class JSEvaluationException extends RuntimeException {

    private JSException jsException;

    JSEvaluationException(JSException jsException) {
        super(jsException.toString());
    }

    public JSException getJSException() {
        return jsException;
    }
}
