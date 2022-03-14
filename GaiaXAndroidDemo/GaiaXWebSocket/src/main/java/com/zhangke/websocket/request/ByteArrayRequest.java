package com.zhangke.websocket.request;

import org.java_websocket.client.WebSocketClient;

/**
 * byte[] 类型的请求
 * <p>
 * Created by ZhangKe on 2019/3/22.
 */
public class ByteArrayRequest implements Request<byte[]> {

    ByteArrayRequest() {
    }

    private byte[] data;

    @Override
    public void setRequestData(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] getRequestData() {
        return this.data;
    }

    @Override
    public void send(WebSocketClient client) {
        client.send(this.data);
    }

    @Override
    public void release() {
        RequestFactory.releaseByteArrayRequest(this);
    }

    @Override
    public String toString() {
        return String.format("[@ByteArrayRequest%s,%s]",
                hashCode(),
                data == null ?
                        "data:null" :
                        "data.length:" + data.length);
    }
}
