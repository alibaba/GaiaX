package com.zhangke.websocket;

import com.zhangke.websocket.response.ErrorResponse;

import org.java_websocket.framing.Framedata;

import java.nio.ByteBuffer;

/**
 * 一个简单的 WebSocket 监听器，实现了 {@link SocketListener} 接口，
 * 因为 SocketListener 中的方法比较多，所以在此提供了一个简单版本，
 * 只需要实现其中关注的方法即可。
 * <p>
 * Created by ZhangKe on 2019/4/1.
 */
public abstract class SimpleListener implements SocketListener {

    private final String TAG = "SimpleListener";

    @Override
    public void onConnected() {
        //to override
    }

    @Override
    public void onConnectFailed(Throwable e) {
        //to override
    }

    @Override
    public void onDisconnect() {
        //to override
    }

    @Override
    public <T> void onMessage(String message, T data) {
        //to override
    }

    @Override
    public void onSendDataError(ErrorResponse errorResponse) {
        //to override
    }

    @Override
    public <T> void onMessage(ByteBuffer bytes, T data) {
        //to override
    }

    @Override
    public void onPing(Framedata framedata) {
        //to override
    }

    @Override
    public void onPong(Framedata framedata) {
        //to override
    }
}
