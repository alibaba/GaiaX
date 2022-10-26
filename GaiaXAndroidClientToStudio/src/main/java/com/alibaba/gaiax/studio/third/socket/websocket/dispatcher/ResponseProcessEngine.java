package com.alibaba.gaiax.studio.third.socket.websocket.dispatcher;

import com.alibaba.gaiax.studio.third.socket.websocket.response.ErrorResponse;
import com.alibaba.gaiax.studio.third.socket.websocket.response.Response;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 响应消息处理类
 * <p>
 * Created by ZhangKe on 2019/3/25.
 */
public class ResponseProcessEngine {

    private EngineThread mThread;

    public ResponseProcessEngine() {
        mThread = new EngineThread();
        mThread.start();
    }

    public void onMessageReceive(Response message,
                                 IResponseDispatcher dispatcher,
                                 ResponseDelivery delivery) {
        if (message == null || dispatcher == null || delivery == null) {
            return;
        }
        EngineEntity entity = EngineEntity.obtain();
        entity.dispatcher = dispatcher;
        entity.delivery = delivery;
        entity.isError = false;
        entity.response = message;
        entity.errorResponse = null;
        mThread.add(entity);
    }

    public void onSendDataError(ErrorResponse errorResponse,
                                IResponseDispatcher dispatcher,
                                ResponseDelivery delivery) {
        if (errorResponse == null || dispatcher == null || delivery == null) {
            return;
        }
        EngineEntity entity = EngineEntity.obtain();
        entity.dispatcher = dispatcher;
        entity.delivery = delivery;
        entity.isError = true;
        entity.errorResponse = errorResponse;
        entity.response = null;
        mThread.add(entity);
    }

    static class EngineEntity {

        private static Queue<EngineEntity> ENTITY_POOL = new ArrayDeque<>(10);

        boolean isError;
        Response response;
        ErrorResponse errorResponse;
        IResponseDispatcher dispatcher;
        ResponseDelivery delivery;

        static EngineEntity obtain() {
            EngineEntity engineEntity = ENTITY_POOL.poll();
            if (engineEntity == null) {
                engineEntity = new EngineEntity();
            }
            return engineEntity;
        }

        static void release(EngineEntity entity) {
            ENTITY_POOL.offer(entity);
        }
    }
}
