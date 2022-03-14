package com.zhangke.websocket.request;

import org.java_websocket.client.WebSocketClient;

import java.nio.ByteBuffer;

/**
 * ByteBuffer 类型的请求
 * <p>
 * Created by ZhangKe on 2019/3/22.
 */
public class ByteBufferRequest implements Request<ByteBuffer> {

    private ByteBuffer data;

    ByteBufferRequest() {
    }

    @Override
    public void setRequestData(ByteBuffer data) {
        this.data = data;
    }

    @Override
    public ByteBuffer getRequestData() {
        return this.data;
    }

    @Override
    public void send(WebSocketClient client) {
        client.send(this.data);
    }

    @Override
    public void release() {
        RequestFactory.releaseByteBufferRequest(this);
    }

    @Override
    public String toString() {
        return String.format("[@ByteBufferRequest%s,ByteBuffer:%s]",
                hashCode(),
                data == null ?
                        "null" :
                        data.toString());
    }
}
