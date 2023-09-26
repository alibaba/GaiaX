package com.alibaba.gaiax.quickjs;

public interface PromiseExecutor {
    void execute(JSFunction resolve, JSFunction reject);
}
