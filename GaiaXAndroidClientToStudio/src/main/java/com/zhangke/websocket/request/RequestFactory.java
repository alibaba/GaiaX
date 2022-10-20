package com.zhangke.websocket.request;

import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.PingFrame;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

/**
 * 创建请求的工厂类
 * Created by ZhangKe on 2019/3/28.
 */
public class RequestFactory {

    private static final int POLL_SIZE = 7;

    private static Queue<ByteArrayRequest> BYTE_ARRAY_REQUEST_POOL = new ArrayDeque<>(POLL_SIZE);
    private static Queue<ByteBufferRequest> BYTE_BUFFER_REQUEST_POOL = new ArrayDeque<>(POLL_SIZE);
    private static Queue<StringRequest> STRING_REQUEST_POOL = new ArrayDeque<>(POLL_SIZE);
    private static Queue<PingRequest> PING_REQUEST_POOL = new ArrayDeque<>(POLL_SIZE);
    private static Queue<PongRequest> PONG_REQUEST_POOL = new ArrayDeque<>(POLL_SIZE);
    private static Queue<FrameDataRequest> FRAME_DATA_REQUEST_POOL = new ArrayDeque<>(POLL_SIZE);
    private static Queue<CollectionFrameDataRequest> COLLECTION_FRAME_REQUEST_POOL = new ArrayDeque<>(POLL_SIZE);

    public static Request<byte[]> createByteArrayRequest() {
        Request<byte[]> request = BYTE_ARRAY_REQUEST_POOL.poll();
        if (request == null) {
            request = new ByteArrayRequest();
        }
        return request;
    }

    public static Request<ByteBuffer> createByteBufferRequest() {
        Request<ByteBuffer> request = BYTE_BUFFER_REQUEST_POOL.poll();
        if (request == null) {
            request = new ByteBufferRequest();
        }
        return request;
    }

    public static Request<String> createStringRequest() {
        Request<String> request = STRING_REQUEST_POOL.poll();
        if (request == null) {
            request = new StringRequest();
        }
        return request;
    }

    public static Request createPingRequest() {
        Request request = PING_REQUEST_POOL.poll();
        if (request == null) {
            request = new PingRequest();
        }
        return request;
    }

    public static Request<PingFrame> createPongRequest() {
        Request<PingFrame> request = PONG_REQUEST_POOL.poll();
        if (request == null) {
            request = new PongRequest();
        }
        return request;
    }

    public static Request<Framedata> createFrameDataRequest() {
        Request<Framedata> request = FRAME_DATA_REQUEST_POOL.poll();
        if (request == null) {
            request = new FrameDataRequest();
        }
        return request;
    }

    public static Request<Collection<Framedata>> createCollectionFrameRequest() {
        Request<Collection<Framedata>> request = COLLECTION_FRAME_REQUEST_POOL.poll();
        if (request == null) {
            request = new CollectionFrameDataRequest();
        }
        return request;
    }

    static void releaseByteArrayRequest(ByteArrayRequest request) {
        BYTE_ARRAY_REQUEST_POOL.offer(request);
    }

    static void releaseByteBufferRequest(ByteBufferRequest request) {
        BYTE_BUFFER_REQUEST_POOL.offer(request);
    }

    static void releaseStringRequest(StringRequest request) {
        STRING_REQUEST_POOL.offer(request);
    }

    static void releasePingRequest(PingRequest request) {
        PING_REQUEST_POOL.offer(request);
    }

    static void releasePongRequest(PongRequest request) {
        PONG_REQUEST_POOL.offer(request);
    }

    static void releaseFrameDataRequest(FrameDataRequest request) {
        FRAME_DATA_REQUEST_POOL.offer(request);
    }

    static void releaseCollectionFrameRequest(CollectionFrameDataRequest request) {
        COLLECTION_FRAME_REQUEST_POOL.offer(request);
    }
}
