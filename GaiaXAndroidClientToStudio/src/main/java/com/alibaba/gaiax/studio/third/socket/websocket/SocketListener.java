package com.alibaba.gaiax.studio.third.socket.websocket;

import com.alibaba.gaiax.studio.third.socket.java_websocket.framing.Framedata;
import com.alibaba.gaiax.studio.third.socket.websocket.response.ErrorResponse;

import java.nio.ByteBuffer;

/**
 * WebSocket 监听器,
 * for user
 * Created by ZhangKe on 2018/6/8.
 */
public interface SocketListener {

    /**
     * 连接成功
     */
    void onConnected();

    /**
     * 连接失败
     */
    void onConnectFailed(Throwable e);

    /**
     * 连接断开
     */
    void onDisconnect();

    /**
     * 数据发送失败
     *
     * @param errorResponse 失败响应
     */
    void onSendDataError(ErrorResponse errorResponse);

    /**
     * 接收到文本消息
     *
     * @param message 文本消息
     * @param data    用户可将数据转成对应的泛型类型，可能为空，具体看用户在 {@link dispatcher.IResponseDispatcher}
     *                中的实现，默认为空
     * @param <T>     IResponseDispatcher 中转换的泛型类型
     */
    <T> void onMessage(String message, T data);

    /**
     * 接收到二进制消息
     *
     * @param bytes 二进制消息
     * @param data  用户可将数据转成对应的泛型类型，可能为空，具体看用户在 {@link dispatcher.IResponseDispatcher}
     *              中的实现，默认为空
     * @param <T>   IResponseDispatcher 中转换的泛型类型
     */
    <T> void onMessage(ByteBuffer bytes, T data);

    /**
     * 接收到 ping
     */
    void onPing(Framedata framedata);

    /**
     * 接收到 pong
     */
    void onPong(Framedata framedata);
}
