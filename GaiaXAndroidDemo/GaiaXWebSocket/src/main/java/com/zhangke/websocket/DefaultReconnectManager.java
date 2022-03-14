package com.zhangke.websocket;

import com.zhangke.websocket.util.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * 负责 WebSocket 重连
 * <p>
 * Created by ZhangKe on 2018/6/24.
 */
public class DefaultReconnectManager implements ReconnectManager {

    private static final String TAG = "WSDefaultRM";

    /**
     * 重连锁
     */
    private final Object BLOCK = new Object();

    private WebSocketManager mWebSocketManager;
    private OnConnectListener mOnDisconnectListener;

    /**
     * 是否正在重连
     */
    private volatile boolean reconnecting;
    /**
     * 被销毁
     */
    private volatile boolean destroyed;
    /**
     * 是否需要停止重连
     */
    private volatile boolean needStopReconnect = false;
    /**
     * 是否已连接
     */
    private volatile boolean connected = false;

    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    public DefaultReconnectManager(WebSocketManager webSocketManager,
                                   OnConnectListener onDisconnectListener) {
        this.mWebSocketManager = webSocketManager;
        this.mOnDisconnectListener = onDisconnectListener;
        reconnecting = false;
        destroyed = false;
    }

    @Override
    public boolean reconnecting() {
        return reconnecting;
    }

    @Override
    public void startReconnect() {
        if (reconnecting) {
            LogUtil.i(TAG, "Reconnecting, do not call again.");
            return;
        }
        if (destroyed) {
            LogUtil.e(TAG, "ReconnectManager is destroyed!!!");
            return;
        }
        needStopReconnect = false;
        reconnecting = true;
        try {
            singleThreadPool.execute(getReconnectRunnable());
        } catch (RejectedExecutionException e) {
            LogUtil.e(TAG, "线程队列已满，无法执行此次任务。", e);
            reconnecting = false;
        }
    }

    private int reconnectCount = 1;
    private int finishCount = 1;

    private Runnable getReconnectRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (destroyed || needStopReconnect) {
                    reconnecting = false;
                    return;
                }
                LogUtil.d(TAG, "开始重连:" + reconnectCount);
                reconnectCount++;
                reconnecting = true;
                connected = false;
                try {
                    int count = mWebSocketManager.getSetting().getReconnectFrequency();
                    for (int i = 0; i < count; i++) {
                        LogUtil.i(TAG, String.format("第%s次重连", i + 1));
                        mWebSocketManager.reconnectOnce();
                        synchronized (BLOCK) {
                            try {
                                BLOCK.wait(mWebSocketManager.getSetting().getConnectTimeout());
                                if (connected) {
                                    LogUtil.i(TAG, "reconnectOnce success!");
                                    mOnDisconnectListener.onConnected();
                                    return;
                                }
                                if (needStopReconnect) {
                                    break;
                                }
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                    //重连失败
                    LogUtil.i(TAG, "reconnectOnce failed!");
                    mOnDisconnectListener.onDisconnect();
                } finally {
                    LogUtil.d(TAG, "重连结束:" + finishCount);
                    finishCount++;
                    reconnecting = false;
                    LogUtil.i(TAG, "reconnecting = false");
                }
            }
        };
    }

    @Override
    public void stopReconnect() {
        needStopReconnect = true;
        if (singleThreadPool != null) {
            singleThreadPool.shutdownNow();
        }
    }

    @Override
    public void onConnected() {
        connected = true;
        synchronized (BLOCK) {
            LogUtil.i(TAG, "onConnected()->BLOCK.notifyAll()");
            BLOCK.notifyAll();
        }
    }

    @Override
    public void onConnectError(Throwable th) {
        connected = false;
        synchronized (BLOCK) {
            LogUtil.i(TAG, "onConnectError(Throwable)->BLOCK.notifyAll()");
            BLOCK.notifyAll();
        }
    }

    /**
     * 销毁资源，并停止重连
     */
    @Override
    public void destroy() {
        destroyed = true;
        stopReconnect();
        mWebSocketManager = null;
    }
}
