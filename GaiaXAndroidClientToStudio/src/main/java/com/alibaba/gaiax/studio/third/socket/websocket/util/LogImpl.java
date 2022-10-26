package com.alibaba.gaiax.studio.third.socket.websocket.util;

import android.util.Log;

/**
 * Logable 默认实现类
 * <p>
 * Created by ZhangKe on 2019/4/29.
 */
public class LogImpl implements Logable {

    @Override
    public void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    @Override
    public void v(String tag, String msg, Throwable tr) {
        Log.v(tag, msg, tr);
    }

    @Override
    public void d(String tag, String text) {
        Log.e(tag, text);
    }

    @Override
    public void d(String tag, String text, Throwable tr) {
        Log.e(tag, text, tr);
    }

    @Override
    public void i(String tag, String text) {
        Log.i(tag, text);
    }

    @Override
    public void i(String tag, String text, Throwable tr) {
        Log.i(tag, text, tr);
    }

    @Override
    public void e(String tag, String text) {
        Log.e(tag, text);
    }

    @Override
    public void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }

    @Override
    public void w(String tag, Throwable tr) {
        Log.w(tag, tr);
    }

    @Override
    public void wtf(String tag, String msg) {
        Log.wtf(tag, msg);
    }

    @Override
    public void wtf(String tag, Throwable tr) {
        Log.wtf(tag, tr);
    }

    @Override
    public void wtf(String tag, String msg, Throwable tr) {
        Log.wtf(tag, msg, tr);
    }
}
