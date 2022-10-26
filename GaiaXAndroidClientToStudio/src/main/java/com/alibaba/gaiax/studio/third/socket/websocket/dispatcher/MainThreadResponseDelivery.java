package com.alibaba.gaiax.studio.third.socket.websocket.dispatcher;

import static com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.MainThreadResponseDelivery.RUNNABLE_TYPE.BYTE_BUFFER_MSG;
import static com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.MainThreadResponseDelivery.RUNNABLE_TYPE.CONNECTED;
import static com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.MainThreadResponseDelivery.RUNNABLE_TYPE.CONNECT_FAILED;
import static com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.MainThreadResponseDelivery.RUNNABLE_TYPE.DISCONNECT;
import static com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.MainThreadResponseDelivery.RUNNABLE_TYPE.NON;
import static com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.MainThreadResponseDelivery.RUNNABLE_TYPE.PING;
import static com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.MainThreadResponseDelivery.RUNNABLE_TYPE.PONG;
import static com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.MainThreadResponseDelivery.RUNNABLE_TYPE.SEND_ERROR;
import static com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.MainThreadResponseDelivery.RUNNABLE_TYPE.STRING_MSG;

import android.text.TextUtils;

import com.alibaba.gaiax.studio.third.socket.websocket.SocketListener;
import com.alibaba.gaiax.studio.third.socket.websocket.util.ThreadUtil;
import com.alibaba.gaiax.studio.third.socket.websocket.response.ErrorResponse;

import com.alibaba.gaiax.studio.third.socket.java_websocket.framing.Framedata;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * 用户注册的消息发射器,
 * 内部维护一个 {@link SocketListener} 的 List，
 * 调用每一个方法都会通知 List 中所有的 Listener，
 * 这么做主要为了统一控制消息的回调线程以及简化代码。
 * <p>
 * Created by ZhangKe on 2019/3/25.
 */
public class MainThreadResponseDelivery implements ResponseDelivery {

    /**
     * Listener 操作锁
     */
    private static final Object LISTENER_BLOCK = new Object();

    private static Queue<CallbackRunnable> RUNNABLE_POOL;

    private final List<SocketListener> mSocketListenerList = new ArrayList<>();

    public MainThreadResponseDelivery() {
    }

    @Override
    public void addListener(SocketListener listener) {
        if (listener == null) {
            return;
        }
        if (!mSocketListenerList.contains(listener)) {
            synchronized (LISTENER_BLOCK) {
                mSocketListenerList.add(listener);
            }
        }
    }

    @Override
    public void removeListener(SocketListener listener) {
        if (listener == null || isEmpty()) {
            return;
        }
        if (mSocketListenerList.contains(listener)) {
            synchronized (LISTENER_BLOCK) {
                mSocketListenerList.remove(listener);
            }
        }
    }

    @Override
    public void onConnected() {
        if (isEmpty()) {
            return;
        }
        if (ThreadUtil.checkMainThread()) {
            synchronized (LISTENER_BLOCK) {
                for (SocketListener listener : mSocketListenerList) {
                    listener.onConnected();
                }
            }
        } else {
            CallbackRunnable callbackRunnable = getRunnable();
            callbackRunnable.type = CONNECTED;
            callbackRunnable.mSocketListenerList = mSocketListenerList;
            ThreadUtil.runOnMainThread(callbackRunnable);
        }
    }

    @Override
    public void onConnectFailed(Throwable cause) {
        if (isEmpty()) {
            return;
        }
        if (ThreadUtil.checkMainThread()) {
            synchronized (LISTENER_BLOCK) {
                for (SocketListener listener : mSocketListenerList) {
                    listener.onConnectFailed(cause);
                }
            }
        } else {
            CallbackRunnable callbackRunnable = getRunnable();
            callbackRunnable.type = CONNECT_FAILED;
            callbackRunnable.connectErrorCause = cause;
            callbackRunnable.mSocketListenerList = mSocketListenerList;
            ThreadUtil.runOnMainThread(callbackRunnable);
        }
    }

    @Override
    public void onDisconnect() {
        if (isEmpty()) {
            return;
        }
        if (ThreadUtil.checkMainThread()) {
            synchronized (LISTENER_BLOCK) {
                for (SocketListener listener : mSocketListenerList) {
                    listener.onDisconnect();
                }
            }
        } else {
            CallbackRunnable callbackRunnable = getRunnable();
            callbackRunnable.type = DISCONNECT;
            callbackRunnable.mSocketListenerList = mSocketListenerList;
            ThreadUtil.runOnMainThread(callbackRunnable);
        }
    }

    @Override
    public void onSendDataError(ErrorResponse errorResponse) {
        if (isEmpty() || errorResponse == null) {
            return;
        }
        if (ThreadUtil.checkMainThread()) {
            synchronized (LISTENER_BLOCK) {
                for (SocketListener listener : mSocketListenerList) {
                    listener.onSendDataError(errorResponse);
                }
            }
        } else {
            CallbackRunnable callbackRunnable = getRunnable();
            callbackRunnable.type = SEND_ERROR;
            callbackRunnable.errorResponse = errorResponse;
            callbackRunnable.mSocketListenerList = mSocketListenerList;
            ThreadUtil.runOnMainThread(callbackRunnable);
        }
    }

    @Override
    public <T> void onMessage(String message, T data) {
        if (isEmpty() || message == null) {
            return;
        }
        if (ThreadUtil.checkMainThread()) {
            synchronized (LISTENER_BLOCK) {
                for (SocketListener listener : mSocketListenerList) {
                    listener.onMessage(message, data);
                }
            }
        } else {
            CallbackRunnable callbackRunnable = getRunnable();
            callbackRunnable.type = STRING_MSG;
            callbackRunnable.textResponse = message;
            callbackRunnable.formattedData = data;
            callbackRunnable.mSocketListenerList = mSocketListenerList;
            ThreadUtil.runOnMainThread(callbackRunnable);
        }
    }

    @Override
    public <T> void onMessage(ByteBuffer bytes, T data) {
        if (isEmpty() || bytes == null) {
            return;
        }
        if (ThreadUtil.checkMainThread()) {
            synchronized (LISTENER_BLOCK) {
                for (SocketListener listener : mSocketListenerList) {
                    listener.onMessage(bytes, data);
                }
            }
        } else {
            CallbackRunnable callbackRunnable = getRunnable();
            callbackRunnable.type = BYTE_BUFFER_MSG;
            callbackRunnable.byteResponse = bytes;
            callbackRunnable.formattedData = data;
            callbackRunnable.mSocketListenerList = mSocketListenerList;
            ThreadUtil.runOnMainThread(callbackRunnable);
        }
    }

    @Override
    public void onPing(Framedata framedata) {
        if (isEmpty()) {
            return;
        }
        if (ThreadUtil.checkMainThread()) {
            synchronized (LISTENER_BLOCK) {
                for (SocketListener listener : mSocketListenerList) {
                    listener.onPing(framedata);
                }
            }
        } else {
            CallbackRunnable callbackRunnable = getRunnable();
            callbackRunnable.type = PING;
            callbackRunnable.framedataResponse = framedata;
            callbackRunnable.mSocketListenerList = mSocketListenerList;
            ThreadUtil.runOnMainThread(callbackRunnable);
        }
    }

    @Override
    public void onPong(Framedata framedata) {
        if (isEmpty()) {
            return;
        }
        if (ThreadUtil.checkMainThread()) {
            synchronized (LISTENER_BLOCK) {
                for (SocketListener listener : mSocketListenerList) {
                    listener.onPong(framedata);
                }
            }
        } else {
            CallbackRunnable callbackRunnable = getRunnable();
            callbackRunnable.type = PONG;
            callbackRunnable.framedataResponse = framedata;
            callbackRunnable.mSocketListenerList = mSocketListenerList;
            ThreadUtil.runOnMainThread(callbackRunnable);
        }
    }

    @Override
    public void clear() {
        if (!mSocketListenerList.isEmpty()) {
            synchronized (LISTENER_BLOCK) {
                mSocketListenerList.clear();
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return mSocketListenerList.isEmpty();
    }

    private CallbackRunnable getRunnable() {
        if (RUNNABLE_POOL == null) {
            RUNNABLE_POOL = new ArrayDeque<>(5);
        }
        CallbackRunnable runnable = RUNNABLE_POOL.poll();
        if (runnable == null) {
            runnable = new CallbackRunnable();
        }
        return runnable;
    }

    enum RUNNABLE_TYPE {
        NON,//未设置
        CONNECTED,//连接成功
        CONNECT_FAILED,//连接失败
        DISCONNECT,//连接断开
        SEND_ERROR,//数据发送失败
        STRING_MSG,//接收到 String 数据
        BYTE_BUFFER_MSG,//接收到 ByteBuffer 数据
        PING,//接收到 Ping
        PONG//接收到 Pong
    }

    /**
     * 避免频繁创建 Runnable 对象造成的内存浪费，
     * 故此处使用可重用的 Runnable
     */
    private static class CallbackRunnable<T> implements Runnable {

        List<SocketListener> mSocketListenerList;

        ErrorResponse errorResponse;
        Throwable connectErrorCause;
        String textResponse;
        ByteBuffer byteResponse;
        Framedata framedataResponse;
        T formattedData;

        RUNNABLE_TYPE type = NON;

        @Override
        public void run() {
            try {
                if (type == NON ||
                        mSocketListenerList == null ||
                        mSocketListenerList.isEmpty()) {
                    return;
                }
                //check null
                if (type == CONNECT_FAILED && connectErrorCause == null) return;
                if (type == SEND_ERROR && errorResponse == null) return;
                if (type == STRING_MSG && TextUtils.isEmpty(textResponse)) return;
                if (type == BYTE_BUFFER_MSG && byteResponse == null) return;
                if (type == PING && framedataResponse == null) return;
                if (type == PONG && framedataResponse == null) return;
                synchronized (LISTENER_BLOCK) {
                    switch (type) {
                        case CONNECTED:
                            for (SocketListener listener : mSocketListenerList) {
                                listener.onConnected();
                            }
                            break;
                        case CONNECT_FAILED:
                            for (SocketListener listener : mSocketListenerList) {
                                listener.onConnectFailed(connectErrorCause);
                            }
                            break;
                        case DISCONNECT:
                            for (SocketListener listener : mSocketListenerList) {
                                listener.onDisconnect();
                            }
                            break;
                        case SEND_ERROR:
                            for (SocketListener listener : mSocketListenerList) {
                                listener.onSendDataError(errorResponse);
                            }
                            break;
                        case STRING_MSG:
                            for (SocketListener listener : mSocketListenerList) {
                                listener.onMessage(textResponse, formattedData);
                            }
                            break;
                        case BYTE_BUFFER_MSG:
                            for (SocketListener listener : mSocketListenerList) {
                                listener.onMessage(byteResponse, formattedData);
                            }
                            break;
                        case PING:
                            for (SocketListener listener : mSocketListenerList) {
                                listener.onPing(framedataResponse);
                            }
                            break;
                        case PONG:
                            for (SocketListener listener : mSocketListenerList) {
                                listener.onPong(framedataResponse);
                            }
                            break;
                    }
                    mSocketListenerList = null;
                    errorResponse = null;
                    connectErrorCause = null;
                    textResponse = null;
                    byteResponse = null;
                    framedataResponse = null;
                    formattedData = null;
                }
            } finally {
                RUNNABLE_POOL.offer(this);
            }
        }
    }
}
