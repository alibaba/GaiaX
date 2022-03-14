package com.zhangke.websocket.util;

import android.os.Handler;
import android.os.Looper;

/**
 * 线程相关的工具类，
 * 考虑到后面可能要同时兼容 Java 与 Android，
 * 所以此处使用工具类收拢入口。
 * <p>
 * Created by ZhangKe on 2019/3/25.
 */
public class ThreadUtil {

    private static Handler sMainHandler;

    /**
     * 是否为主线程
     */
    public static boolean checkMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * 将 Runnable 运行在主线程
     */
    public static void runOnMainThread(Runnable runnable) {
        checkMainHandlerIsNull();
        sMainHandler.post(runnable);
    }

    private static void checkMainHandlerIsNull() {
        if (sMainHandler == null) {
            sMainHandler = new Handler(Looper.getMainLooper());
        }
    }
}
