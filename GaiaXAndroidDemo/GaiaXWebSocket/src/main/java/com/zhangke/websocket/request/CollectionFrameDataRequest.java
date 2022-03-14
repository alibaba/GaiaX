package com.zhangke.websocket.request;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;

import java.util.Collection;

/**
 * 发送 {@link Framedata} 集合
 * <p>
 * Created by ZhangKe on 2019/3/28.
 */
public class CollectionFrameDataRequest implements Request<Collection<Framedata>> {

    private Collection<Framedata> data;

    CollectionFrameDataRequest() {
    }

    @Override
    public void setRequestData(Collection<Framedata> data) {
        this.data = data;
    }

    @Override
    public Collection<Framedata> getRequestData() {
        return this.data;
    }

    @Override
    public void send(WebSocketClient client) {
        client.sendFrame(this.data);
    }

    @Override
    public void release() {
        RequestFactory.releaseCollectionFrameRequest(this);
    }

    @Override
    public String toString() {
        return String.format("[@CollectionFrameDataRequest%s,Collection<Framedata>:%s]",
                hashCode(),
                data == null ?
                        "null" :
                        data.size() + " length");
    }
}
