package com.zhangke.websocket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zhangke.websocket.request.Request;
import com.zhangke.websocket.response.ErrorResponse;
import com.zhangke.websocket.util.LogUtil;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 使用操作线程发送数据
 * <p>
 * Created by ZhangKe on 2019/3/29.
 */
public class WebSocketEngine {

    private static final String TAG = "WSWebSocketEngine";

    private OptionThread mOptionThread;

    WebSocketEngine() {
        mOptionThread = new OptionThread();
        mOptionThread.start();
    }

    void sendRequest(WebSocketWrapper webSocket,
                     Request request,
                     SocketWrapperListener listener) {
        if (mOptionThread.mHandler == null) {
            listener.onSendDataError(request,
                    ErrorResponse.ERROR_UN_INIT,
                    null);
        } else {
            ReRunnable runnable = ReRunnable.obtain();
            runnable.type = 0;
            runnable.request = request;
            runnable.webSocketWrapper = webSocket;
            mOptionThread.mHandler.post(runnable);
        }
    }

    void connect(WebSocketWrapper webSocket,
                 SocketWrapperListener listener) {
        if (mOptionThread.mHandler == null) {
            listener.onConnectFailed(new Exception("WebSocketEngine not start!"));
        } else {
            ReRunnable runnable = ReRunnable.obtain();
            runnable.type = 1;
            runnable.webSocketWrapper = webSocket;
            mOptionThread.mHandler.post(runnable);
        }
    }

    void disConnect(WebSocketWrapper webSocket,
                    SocketWrapperListener listener) {
        if (mOptionThread.mHandler != null) {
            ReRunnable runnable = ReRunnable.obtain();
            runnable.type = 2;
            runnable.webSocketWrapper = webSocket;
            mOptionThread.mHandler.post(runnable);
        } else {
            LogUtil.e(TAG, "WebSocketEngine not start!");
        }
    }

    void destroyWebSocket(WebSocketWrapper webSocket) {
        if (mOptionThread.mHandler != null) {
            ReRunnable runnable = ReRunnable.obtain();
            runnable.type = 3;
            runnable.webSocketWrapper = webSocket;
            mOptionThread.mHandler.post(runnable);
        } else {
            LogUtil.e(TAG, "WebSocketEngine not start!");
        }
    }

    public void destroy() {
        if (mOptionThread != null) {
            if (mOptionThread.mHandler != null) {
                mOptionThread.mHandler.sendEmptyMessage(OptionHandler.QUIT);
            }
        }
    }

    private class OptionThread extends Thread {

        private OptionHandler mHandler;

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            mHandler = new OptionHandler();
            Looper.loop();
        }
    }

    private static class OptionHandler extends Handler {

        private static final int QUIT = 1;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    private static class ReRunnable implements Runnable {

        private static Queue<ReRunnable> POOL = new ArrayDeque<>(10);

        static ReRunnable obtain() {
            ReRunnable runnable = POOL.poll();
            if (runnable == null) {
                runnable = new ReRunnable();
            }
            return runnable;
        }

        /**
         * 0-发送数据；
         * 1-连接；
         * 2-断开连接；
         * 3-销毁 WebSocketWrapper 对象。
         */
        private int type;
        private WebSocketWrapper webSocketWrapper;
        private Request request;

        @Override
        public void run() {
            try {
                if (webSocketWrapper == null) return;
                if (type == 0 && request == null) return;
                if (type == 0) {
                    webSocketWrapper.send(request);
                } else if (type == 1) {
                    webSocketWrapper.reconnect();
                } else if (type == 2) {
                    webSocketWrapper.disConnect();
                } else if (type == 3) {
                    webSocketWrapper.destroy();
                }
            } finally {
                webSocketWrapper = null;
                request = null;
                release();
            }
        }

        void release() {
            POOL.offer(this);
        }
    }
}
