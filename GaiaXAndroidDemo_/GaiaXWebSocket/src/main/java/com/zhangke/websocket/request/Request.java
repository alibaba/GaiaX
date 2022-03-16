package com.zhangke.websocket.request;

import org.java_websocket.client.WebSocketClient;

/**
 * 请求接口，
 * 之所以使用 Request 对请求数据进行封装，除了考虑数据发送统一管理之外，
 * 还考虑到后面可能会有其他需求。
 * <p>
 * Created by ZhangKe on 2019/3/22.
 */
public interface Request<T> {

    /**
     * 设置要发送的数据
     */
    void setRequestData(T data);

    /**
     * 获取要发送的数据
     */
    T getRequestData();

    /**
     * 发送数据
     */
    void send(WebSocketClient client);

    /**
     * 释放改资源
     */
    void release();
}
