package com.zhangke.websocket.request;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;

/**
 * 发送 {@link Framedata}
 * <p>
 * Created by ZhangKe on 2019/3/28.
 */
public class FrameDataRequest implements Request<Framedata> {

    private Framedata framedata;

    FrameDataRequest() {
    }

    @Override
    public void setRequestData(Framedata data) {
        this.framedata = data;
    }

    @Override
    public Framedata getRequestData() {
        return this.framedata;
    }

    @Override
    public void send(WebSocketClient client) {
        client.sendFrame(framedata);
    }

    @Override
    public void release() {
        RequestFactory.releaseFrameDataRequest(this);
    }

    @Override
    public String toString() {
        return String.format("[@FrameDataRequest%s,Framedata:%s]",
                hashCode(),
                framedata == null ?
                        "null" :
                        framedata.toString());
    }
}
