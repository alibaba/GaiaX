package com.zhangke.websocket.util;

import com.zhangke.websocket.WebSocketHandler;

/**
 * 日志工具类
 * <p>
 * Created by ZhangKe on 2019/3/21.
 */
public class LogUtil {

    public static void v(String tag, String msg) {
        WebSocketHandler.getLogable().v(tag, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        WebSocketHandler.getLogable().v(tag, msg, tr);
    }

    public static void d(String tag, String text) {
        WebSocketHandler.getLogable().d(tag, text);
    }

    public static void d(String tag, String text, Throwable tr) {
        WebSocketHandler.getLogable().d(tag, text, tr);
    }

    public static void i(String tag, String text) {
        WebSocketHandler.getLogable().i(tag, text);
    }

    public static void i(String tag, String text, Throwable tr) {
        WebSocketHandler.getLogable().i(tag, text, tr);
    }

    public static void e(String tag, String text) {
        WebSocketHandler.getLogable().e(tag, text);
    }

    public static void e(String tag, String msg, Throwable tr) {
        WebSocketHandler.getLogable().e(tag, msg, tr);
    }

    public static void w(String tag, Throwable tr) {
        WebSocketHandler.getLogable().w(tag, tr);
    }

    public static void wtf(String tag, String msg) {
        WebSocketHandler.getLogable().wtf(tag, msg);
    }

    public static void wtf(String tag, Throwable tr) {
        WebSocketHandler.getLogable().wtf(tag, tr);
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        WebSocketHandler.getLogable().wtf(tag, msg, tr);
    }
}
