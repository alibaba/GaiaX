package com.zhangke.websocket.request;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.PingFrame;
import org.java_websocket.framing.PongFrame;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 发送 Pong
 * <p>
 * Created by ZhangKe on 2019/3/28.
 */
public class PongRequest implements Request<PingFrame> {

    private static Queue<PongFrame> PONG_POOL = new ArrayDeque<>(7);

    private PingFrame pingFrame;

    PongRequest() {
    }

    @Override
    public void setRequestData(PingFrame data) {
        this.pingFrame = data;
    }

    @Override
    public PingFrame getRequestData() {
        return pingFrame;
    }

    @Override
    public void send(WebSocketClient client) {
        PongFrame pongFrame = getPongFrame();
        if (pingFrame != null) {
            pongFrame.setPayload(pingFrame.getPayloadData());
            pingFrame = null;
        } else {
            pongFrame.setPayload(null);
        }
        client.sendFrame(pongFrame);
        offerPongFrame(pongFrame);
    }

    @Override
    public void release() {
        RequestFactory.releasePongRequest(this);
    }

    @Override
    public String toString() {
        return String.format("[@PongRequest%s,PingFrame:%s]",
                hashCode(),
                pingFrame == null ?
                        "null" :
                        pingFrame.toString());
    }

    private PongFrame getPongFrame() {
        PongFrame pongFrame = PONG_POOL.poll();
        if (pongFrame == null) {
            pongFrame = new PongFrame();
        }
        return pongFrame;
    }

    private void offerPongFrame(PongFrame pongFrame) {
        pingFrame = null;
        PONG_POOL.offer(pongFrame);
    }
}
