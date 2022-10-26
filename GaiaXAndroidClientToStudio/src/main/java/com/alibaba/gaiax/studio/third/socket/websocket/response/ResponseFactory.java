package com.alibaba.gaiax.studio.third.socket.websocket.response;

import com.alibaba.gaiax.studio.third.socket.java_websocket.framing.Framedata;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 创建 {@link Response} 的工厂类
 * <p>
 * Created by ZhangKe on 2019/3/28.
 */
public class ResponseFactory {

    private static final int POOL_SIZE = 7;
    private static Queue<ErrorResponse> ERROR_RESPONSE_POOL = new ArrayDeque<>(POOL_SIZE);
    private static Queue<TextResponse> TEXT_RESPONSE_POOL = new ArrayDeque<>(POOL_SIZE);
    private static Queue<ByteBufferResponse> BYTE_BUFFER_RESPONSE_POOL = new ArrayDeque<>(POOL_SIZE);
    private static Queue<PingResponse> PING_RESPONSE_POOL = new ArrayDeque<>(POOL_SIZE);
    private static Queue<PongResponse> PONG_RESPONSE_POOL = new ArrayDeque<>(POOL_SIZE);

    public static ErrorResponse createErrorResponse() {
        ErrorResponse response = ERROR_RESPONSE_POOL.poll();
        if (response == null) {
            response = new ErrorResponse();
        }
        return response;
    }

    public static Response<String> createTextResponse() {
        Response<String> response = TEXT_RESPONSE_POOL.poll();
        if (response == null) {
            response = new TextResponse();
        }
        return response;
    }

    public static Response<ByteBuffer> createByteBufferResponse() {
        Response<ByteBuffer> response = BYTE_BUFFER_RESPONSE_POOL.poll();
        if (response == null) {
            response = new ByteBufferResponse();
        }
        return response;
    }

    public static Response<Framedata> createPingResponse() {
        Response<Framedata> response = PING_RESPONSE_POOL.poll();
        if (response == null) {
            response = new PingResponse();
        }
        return response;
    }

    public static Response<Framedata> createPongResponse() {
        Response<Framedata> response = PONG_RESPONSE_POOL.poll();
        if (response == null) {
            response = new PongResponse();
        }
        return response;
    }

    static void releaseErrorResponse(ErrorResponse response) {
        ERROR_RESPONSE_POOL.offer(response);
    }

    static void releaseTextResponse(TextResponse response) {
        TEXT_RESPONSE_POOL.offer(response);
    }

    static void releaseByteBufferResponse(ByteBufferResponse response) {
        BYTE_BUFFER_RESPONSE_POOL.offer(response);
    }

    static void releasePingResponse(PingResponse response) {
        PING_RESPONSE_POOL.offer(response);
    }

    static void releasePongResponse(PongResponse response) {
        PONG_RESPONSE_POOL.offer(response);
    }

}
