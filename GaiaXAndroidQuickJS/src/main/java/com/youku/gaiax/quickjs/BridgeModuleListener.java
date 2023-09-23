package com.youku.gaiax.quickjs;

public interface BridgeModuleListener {

    /**
     * 用于承接从JS调过来的callSync方法
     *
     * @param contextPointer 上下文
     * @param argsMap        调用GaiaX模块的参数集合；
     *                       contextId - GaiaX上下文ID；
     *                       moduleId - GaiaX注册模块的ID；
     *                       methodId - GaiaX注册模块内方法的ID；
     *                       args - GaiaX注册模块内方法的参数
     * @return 返回JSValue值，用于表示调用业务模块方法是否成功
     */
    long callSync(long contextPointer, String argsMap);

    /**
     * 用于承接从JS调过来的callSync方法
     *
     * @param contextPointer 上下文
     * @param argsMap        调用GaiaX模块的参数集合；
     *                       contextId - GaiaX上下文ID；
     *                       moduleId - GaiaX注册模块的ID；
     *                       methodId - GaiaX注册模块内方法的ID；
     *                       args - GaiaX注册模块内方法的参数
     * @return 返回JSValue值，用于表示调用业务模块方法是否成功
     */
    long callAsync(long contextPointer, long functionPointer, String argsMap);

    /**
     * 用于承接从JS调过来的callSync方法
     *
     * @param contextPointer 上下文
     * @param argsMap        调用GaiaX模块的参数集合；
     *                       contextId - GaiaX上下文ID；
     *                       moduleId - GaiaX注册模块的ID；
     *                       methodId - GaiaX注册模块内方法的ID；
     *                       args - GaiaX注册模块内方法的参数
     * @return 返回JSValue值，用于表示调用业务模块方法是否成功
     */
    long callPromise(long contextPointer, String argsMap);

    void wrapAsJSValueException(Exception e);
}
