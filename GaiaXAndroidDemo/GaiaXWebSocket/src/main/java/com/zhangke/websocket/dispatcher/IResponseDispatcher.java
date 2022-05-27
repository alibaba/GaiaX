package com.zhangke.websocket.dispatcher;


import com.zhangke.websocket.response.ErrorResponse;

import org.java_websocket.framing.Framedata;

import java.nio.ByteBuffer;

/**
 * 消息分发器，用于处理及分发接收到的消息的接口，
 * 如果需要自定义事件的分发，实现这个类并设置到{@link WebSocketSetting} 中即可。
 * Created by ZhangKe on 2018/6/26.
 */
public interface IResponseDispatcher {

    /**
     * 连接成功
     */
    void onConnected(ResponseDelivery delivery);

    /**
     * 连接失败
     *
     * @param cause 失败原因
     */
    void onConnectFailed(Throwable cause, ResponseDelivery delivery);

    /**
     * 连接断开
     */
    void onDisconnect(ResponseDelivery delivery);

    /**
     * 接收到文本消息
     *
     * @param message  接收到的消息
     * @param delivery 消息发射器
     */
    void onMessage(String message, ResponseDelivery delivery);

    /**
     * 接收到二进制消息
     *
     * @param byteBuffer 接收到的消息
     * @param delivery   消息发射器
     */
    void onMessage(ByteBuffer byteBuffer, ResponseDelivery delivery);

    /**
     * 接收到 ping
     *
     * @param framedata 数据帧
     */
    void onPing(Framedata framedata, ResponseDelivery delivery);

    /**
     * 接收到 pong
     *
     * @param framedata 数据帧
     */
    void onPong(Framedata framedata, ResponseDelivery delivery);

    /**
     * 消息发送失败或接受到错误消息等等
     */
    void onSendDataError(ErrorResponse error, ResponseDelivery delivery);

}
