package com.alibaba.gaiax.studio.third.socket.websocket;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.gaiax.studio.third.socket.websocket.request.Request;
import com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.MainThreadResponseDelivery;
import com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.ResponseDelivery;
import com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.ResponseProcessEngine;
import com.alibaba.gaiax.studio.third.socket.websocket.request.RequestFactory;
import com.alibaba.gaiax.studio.third.socket.websocket.response.ErrorResponse;
import com.alibaba.gaiax.studio.third.socket.websocket.response.Response;
import com.alibaba.gaiax.studio.third.socket.websocket.response.ResponseFactory;
import com.alibaba.gaiax.studio.third.socket.websocket.util.LogUtil;

import com.alibaba.gaiax.studio.third.socket.java_websocket.framing.Framedata;
import com.alibaba.gaiax.studio.third.socket.java_websocket.framing.PingFrame;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * WebSocket 管理类
 * <p>
 * Created by ZhangKe on 2019/3/21.
 */
public class WebSocketManager {

    private static final String TAG = "[WSManager]";

    private WebSocketSetting mSetting;

    private WebSocketWrapper mWebSocket;

    /**
     * 注册的监听器集合
     */
    private ResponseDelivery mDelivery;
    private ReconnectManager mReconnectManager;

    private SocketWrapperListener mSocketWrapperListener;
    /**
     * 当前是否已销毁
     */
    private boolean destroyed = false;
    /**
     * 用户调用了 disconnect 方法后为 true
     */
    private boolean disconnect = false;

    private WebSocketEngine mWebSocketEngine;
    private ResponseProcessEngine mResponseProcessEngine;

    WebSocketManager(WebSocketSetting setting,
                     WebSocketEngine webSocketEngine,
                     ResponseProcessEngine responseProcessEngine) {
        this.mSetting = setting;
        this.mWebSocketEngine = webSocketEngine;
        this.mResponseProcessEngine = responseProcessEngine;

        mDelivery = mSetting.getResponseDelivery();
        if (mDelivery == null) {
            mDelivery = new MainThreadResponseDelivery();
        }
        mSocketWrapperListener = getSocketWrapperListener();
        if (mWebSocket == null) {
            mWebSocket = new WebSocketWrapper(this.mSetting, mSocketWrapperListener);
        }
        Log.e(TAG, "WebSocketManager: " + mWebSocket.getConnectState());
        start();
    }

    /**
     * 启动，调用此方法开始连接
     */
    public WebSocketManager start() {
        if (mWebSocket == null) {
            mWebSocket = new WebSocketWrapper(this.mSetting, mSocketWrapperListener);
        }
        if (mWebSocket.getConnectState() == 0) {
            Log.e(TAG, "start: reconnect!!!!!");
            reconnect();
        }
        return this;
    }

    /**
     * WebSocket 是否已连接
     */
    public boolean isConnect() {
        return mWebSocket != null && mWebSocket.getConnectState() == 2;
    }

    /**
     * 设置重连管理类。
     * 用户可根据需求设置自己的重连管理类，只需要实现接口即可
     */
    public void setReconnectManager(ReconnectManager reconnectManager) {
        this.mReconnectManager = reconnectManager;
    }

    /**
     * 通过 {@link ReconnectManager} 开始重接
     */
    public WebSocketManager reconnect() {
        disconnect = false;
        if (mReconnectManager == null) {
            mReconnectManager = getDefaultReconnectManager();
        }
        if (!mReconnectManager.reconnecting()) {
            mReconnectManager.startReconnect();
        }
        return this;
    }

    /**
     * 使用新的 Setting 重新创建连接，同时会销毁之前的连接
     */
    public WebSocketManager reconnect(WebSocketSetting setting) {
        disconnect = false;
        if (destroyed) {
            LogUtil.e(TAG, "This WebSocketManager is destroyed!");
            return this;
        }
        this.mSetting = setting;
        if (mWebSocket != null) {
            mWebSocket.destroy();
            mWebSocket = null;
        }
        start();
        return this;
    }

    /**
     * 断开连接，断开后可使用 {@link this#reconnect()} 方法重新建立连接
     */
    public WebSocketManager disConnect() {
        disconnect = true;
        if (destroyed) {
            LogUtil.e(TAG, "This WebSocketManager is destroyed!");
            return this;
        }
        if (mWebSocket.getConnectState() != 0) {
            mWebSocketEngine.disConnect(mWebSocket, mSocketWrapperListener);
        }
        return this;
    }

    /**
     * 发送文本数据
     */
    public void send(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        Request<String> request = RequestFactory.createStringRequest();
        request.setRequestData(text);
        sendRequest(request);
    }

    /**
     * 发送 byte[] 数据
     */
    public void send(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        Request<byte[]> request = RequestFactory.createByteArrayRequest();
        request.setRequestData(bytes);
        sendRequest(request);
    }

    /**
     * 发送 ByteBuffer 数据
     */
    public void send(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return;
        }
        Request<ByteBuffer> request = RequestFactory.createByteBufferRequest();
        request.setRequestData(byteBuffer);
        sendRequest(request);
    }

    /**
     * 发送 Ping
     */
    public void sendPing() {
        sendRequest(RequestFactory.createPingRequest());
    }

    /**
     * 发送 Pong
     */
    public void sendPong() {
        sendRequest(RequestFactory.createPongRequest());
    }

    /**
     * 发送 Pong
     */
    public void sendPong(PingFrame pingFrame) {
        if (pingFrame == null) {
            return;
        }
        Request<PingFrame> request = RequestFactory.createPongRequest();
        request.setRequestData(pingFrame);
        sendRequest(request);
    }

    /**
     * 发送 {@link Framedata}
     */
    public void sendFrame(Framedata framedata) {
        if (framedata == null) {
            return;
        }
        Request<Framedata> request = RequestFactory.createFrameDataRequest();
        request.setRequestData(framedata);
        sendRequest(request);
    }

    /**
     * 发送 {@link Framedata} 集合
     */
    public void sendFrame(Collection<Framedata> frameData) {
        if (frameData == null) {
            return;
        }
        Request<Collection<Framedata>> request = RequestFactory.createCollectionFrameRequest();
        request.setRequestData(frameData);
        sendRequest(request);
    }

    /**
     * 添加一个监听器，使用完成之后需要调用
     * {@link #removeListener(SocketListener)} 方法移除监听器
     */
    public WebSocketManager addListener(SocketListener listener) {
        mDelivery.addListener(listener);
        return this;
    }

    /**
     * 移除一个监听器
     */
    public WebSocketManager removeListener(SocketListener listener) {
        mDelivery.removeListener(listener);
        return this;
    }

    /**
     * 获取配置类，
     * 部分参数支持动态设定。
     */
    public WebSocketSetting getSetting() {
        return mSetting;
    }

    /**
     * 彻底销毁该连接，销毁后改连接完全失效，
     * 请勿使用其他方法。
     */
    public void destroy() {
        destroyed = true;
        if (mWebSocket != null) {
            mWebSocketEngine.destroyWebSocket(mWebSocket);
            mWebSocketEngine = null;
            mWebSocket = null;
        }
        if (mDelivery != null) {
            if (!mDelivery.isEmpty()) {
                mDelivery.clear();
            }
            mDelivery = null;
        }
        if (mReconnectManager != null) {
            if (mReconnectManager.reconnecting()) {
                mReconnectManager.stopReconnect();
            }
            mReconnectManager = null;
        }
        Log.e(TAG, "destroy: 完成");
    }

    /**
     * 重新连接一次,
     * for {@link ReconnectManager}
     */
    void reconnectOnce() {
        if (destroyed) {
            LogUtil.e(TAG, "This WebSocketManager is destroyed!");
            return;
        }
        if (mWebSocket.getConnectState() == 0) {
            mWebSocketEngine.connect(mWebSocket, mSocketWrapperListener);
        } else {
            if (mReconnectManager != null) {
                mReconnectManager.onConnected();
            }
            LogUtil.e(TAG, "WebSocket 已连接，请勿重试。");
        }
    }

    /**
     * 发送数据
     */
    private void sendRequest(Request request) {
        if (destroyed) {
            LogUtil.e(TAG, "This WebSocketManager is destroyed!");
            return;
        }
        mWebSocketEngine.sendRequest(mWebSocket, request, mSocketWrapperListener);
    }

    /**
     * 获取默认的重连器
     */
    private ReconnectManager getDefaultReconnectManager() {
        return new DefaultReconnectManager(this, new ReconnectManager.OnConnectListener() {
            @Override
            public void onConnected() {
                LogUtil.i(TAG, "重连成功");
            }

            @Override
            public void onDisconnect() {
                LogUtil.i(TAG, "重连失败");
                mSetting.getResponseDispatcher()
                        .onDisconnect(mDelivery);
            }
        });
    }

    /**
     * 获取监听器
     */
    private SocketWrapperListener getSocketWrapperListener() {
        return new SocketWrapperListener() {
            @Override
            public void onConnected() {
                if (mReconnectManager != null) {
                    mReconnectManager.onConnected();
                }
                mSetting.getResponseDispatcher()
                        .onConnected(mDelivery);
            }

            @Override
            public void onConnectFailed(Throwable e) {
                //if reconnecting,interrupt this event for ReconnectManager.
                if (mReconnectManager != null &&
                        mReconnectManager.reconnecting()) {
                    mReconnectManager.onConnectError(e);
                }
                mSetting.getResponseDispatcher()
                        .onConnectFailed(e, mDelivery);
            }

            @Override
            public void onDisconnect() {
                mSetting.getResponseDispatcher()
                        .onDisconnect(mDelivery);
                if (mReconnectManager != null &&
                        mReconnectManager.reconnecting()) {
                    if (disconnect) {
                        mSetting.getResponseDispatcher()
                                .onDisconnect(mDelivery);
                    } else {
                        mReconnectManager.onConnectError(null);
                    }
                } else {
                    if (!disconnect) {
                        if (mReconnectManager == null) {
                            mReconnectManager = getDefaultReconnectManager();
                        }
                        mReconnectManager.onConnectError(null);
                        mReconnectManager.startReconnect();
                    }
                }
            }

            @Override
            public void onSendDataError(Request request, int type, Throwable tr) {
                ErrorResponse errorResponse = ResponseFactory.createErrorResponse();
                errorResponse.init(request, type, tr);
                if (mSetting.processDataOnBackground()) {
                    mResponseProcessEngine
                            .onSendDataError(errorResponse,
                                    mSetting.getResponseDispatcher(),
                                    mDelivery);
                } else {
                    mSetting.getResponseDispatcher().onSendDataError(errorResponse, mDelivery);
                }
                if (!disconnect && type == ErrorResponse.ERROR_NO_CONNECT) {
                    LogUtil.e(TAG, "数据发送失败，网络未连接，开始重连。。。");
                    reconnect();
                }
                //todo 使用完注意释放资源 request.release();
            }

            @Override
            public void onMessage(Response message) {
                if (mSetting.processDataOnBackground()) {
                    mResponseProcessEngine
                            .onMessageReceive(message,
                                    mSetting.getResponseDispatcher(),
                                    mDelivery);
                } else {
                    message.onResponse(mSetting.getResponseDispatcher(), mDelivery);
                }
            }
        };
    }
}
