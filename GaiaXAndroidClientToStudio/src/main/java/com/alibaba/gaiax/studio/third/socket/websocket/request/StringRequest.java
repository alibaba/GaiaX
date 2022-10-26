package com.alibaba.gaiax.studio.third.socket.websocket.request;

import android.text.TextUtils;

import com.alibaba.gaiax.studio.third.socket.java_websocket.client.WebSocketClient;

/**
 * String 类型的请求
 * <p>
 * Created by ZhangKe on 2019/3/22.
 */
public class StringRequest implements Request<String> {

    private String requestText;

    StringRequest() {
    }

    @Override
    public void setRequestData(String data) {
        this.requestText = data;
    }

    @Override
    public String getRequestData() {
        return requestText;
    }

    @Override
    public void send(WebSocketClient client) {
        client.send(requestText);
    }

    @Override
    public void release() {
        RequestFactory.releaseStringRequest(this);
    }

    @Override
    public String toString() {
        return String.format("@StringRequest%s,requestText:%s",
                hashCode(),
                TextUtils.isEmpty(requestText) ?
                        "null" :
                        requestText);
    }
}
