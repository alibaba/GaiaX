package com.alibaba.gaiax.studio.third.socket.websocket.response;

import com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.IResponseDispatcher;
import com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.ResponseDelivery;

import com.alibaba.gaiax.studio.third.socket.java_websocket.framing.Framedata;

/**
 * 接收到 Ping 数据
 * <p>
 * Created by ZhangKe on 2019/3/28.
 */
public class PingResponse implements Response<Framedata> {

    private Framedata framedata;

    PingResponse() {
    }

    @Override
    public Framedata getResponseData() {
        return framedata;
    }

    @Override
    public void setResponseData(Framedata responseData) {
        this.framedata = responseData;
    }

    @Override
    public void onResponse(IResponseDispatcher dispatcher, ResponseDelivery delivery) {
        dispatcher.onPing(framedata, delivery);
    }

    @Override
    public void release() {
        framedata = null;
        ResponseFactory.releasePingResponse(this);
    }

    @Override
    public String toString() {
        return String.format("[@PingResponse%s->Framedata:%s]",
                hashCode(),
                framedata == null ?
                        "null" :
                        framedata.toString());
    }
}
