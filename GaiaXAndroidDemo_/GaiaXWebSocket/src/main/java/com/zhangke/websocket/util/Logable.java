package com.zhangke.websocket.util;

/**
 * 打印日志接口
 * <p>
 * Created by ZhangKe on 2019/4/29.
 */
public interface Logable {

    void v(String tag, String msg);

    void v(String tag, String msg, Throwable tr);

    void d(String tag, String text);

    void d(String tag, String text, Throwable tr);

    void i(String tag, String text);

    void i(String tag, String text, Throwable tr);

    void e(String tag, String text);

    void e(String tag, String msg, Throwable tr);

    void w(String tag, Throwable tr);

    void wtf(String tag, String msg);

    void wtf(String tag, Throwable tr);

    void wtf(String tag, String msg, Throwable tr);
}
