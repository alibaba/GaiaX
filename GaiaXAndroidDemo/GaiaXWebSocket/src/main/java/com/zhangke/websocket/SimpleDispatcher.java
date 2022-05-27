package com.zhangke.websocket;

import com.zhangke.websocket.dispatcher.IResponseDispatcher;
import com.zhangke.websocket.dispatcher.ResponseDelivery;

import org.java_websocket.framing.Framedata;

import java.nio.ByteBuffer;

/**
 * 一个简单的 WebSocket 消息分发器，实现了 {@link IResponseDispatcher} 接口，
 * 因为 IResponseDispatcher 中的方法比较多，所以在此提供了一个简单版本，
 * 只需要实现其中主要的几个方法即可。
 * <p>
 * Created by ZhangKe on 2019/4/2.
 */
public abstract class SimpleDispatcher implements IResponseDispatcher {

    @Override
    public void onConnected(ResponseDelivery delivery) {
        delivery.onConnected();
    }

    @Override
    public void onConnectFailed(Throwable cause, ResponseDelivery delivery) {
        delivery.onConnectFailed(cause);
    }

    @Override
    public void onDisconnect(ResponseDelivery delivery) {
        delivery.onDisconnect();
    }

    @Override
    public void onMessage(ByteBuffer byteBuffer, ResponseDelivery delivery) {
        delivery.onMessage(byteBuffer, null);
    }

    @Override
    public void onPing(Framedata framedata, ResponseDelivery delivery) {
        delivery.onPing(framedata);
    }

    @Override
    public void onPong(Framedata framedata, ResponseDelivery delivery) {
        delivery.onPong(framedata);
    }

}
