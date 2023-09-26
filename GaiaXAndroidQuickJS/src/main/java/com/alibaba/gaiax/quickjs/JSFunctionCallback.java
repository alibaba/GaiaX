package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

@Keep
public interface JSFunctionCallback {
    @Keep
    JSValue invoke(JSContext context, JSValue[] args);
}
