package com.alibaba.gaiax.quickjs;


import androidx.annotation.Keep;

@Keep
public class JNIBridgeModuleHelper {

    private static BridgeModuleListener moduleListener;

    private static long callSync(long context, String argsMap) {
        if (moduleListener != null) {
            return moduleListener.callSync(context, argsMap);
        }
        return -1;
    }

    private static long callAsync(long context, long function, String argsMap) {
        if (moduleListener != null) {
            return moduleListener.callAsync(context, function, argsMap);
        }
        return -1;
    }

    private static long callPromise(long context, String argsMap) {
        if (moduleListener != null) {
            return moduleListener.callPromise(context, argsMap);
        }
        return -1;
    }

    public static void setListener(BridgeModuleListener listener) {
        moduleListener = listener;
    }

    public static void wrapAsJSValueException(Exception e) {
        if (moduleListener != null) {
            moduleListener.wrapAsJSValueException(e);
        }
    }
}
