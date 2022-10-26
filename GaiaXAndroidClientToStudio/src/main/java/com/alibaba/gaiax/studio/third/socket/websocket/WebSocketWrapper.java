package com.alibaba.gaiax.studio.third.socket.websocket;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.gaiax.studio.third.socket.websocket.request.Request;
import com.alibaba.gaiax.studio.third.socket.websocket.response.Response;
import com.alibaba.gaiax.studio.third.socket.websocket.util.LogUtil;
import com.alibaba.gaiax.studio.third.socket.websocket.response.ErrorResponse;
import com.alibaba.gaiax.studio.third.socket.websocket.response.ResponseFactory;

import com.alibaba.gaiax.studio.third.socket.java_websocket.WebSocket;
import com.alibaba.gaiax.studio.third.socket.java_websocket.client.WebSocketClient;
import com.alibaba.gaiax.studio.third.socket.java_websocket.drafts.Draft;
import com.alibaba.gaiax.studio.third.socket.java_websocket.drafts.Draft_6455;
import com.alibaba.gaiax.studio.third.socket.java_websocket.exceptions.WebsocketNotConnectedException;
import com.alibaba.gaiax.studio.third.socket.java_websocket.framing.Framedata;
import com.alibaba.gaiax.studio.third.socket.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * 负责 WebSocket 连接的建立，数据发送，监听数据等。
 * <p>
 * Created by ZhangKe on 2018/6/11.
 */
public class WebSocketWrapper {

    private static final String TAG = "[WSWrapper]";

    private WebSocketSetting mSetting;
    private SocketWrapperListener mSocketListener;

    private WebSocketClient mWebSocket;

    /**
     * 0-未连接
     * 1-正在连接
     * 2-已连接
     */
    private int connectStatus = 0;

    /**
     * 需要关闭连接标志，调用 #disconnect 方法后为 true
     */
    private boolean needClose = false;
    /**
     * 是否已销毁
     */
    private boolean destroyed = false;

    WebSocketWrapper(WebSocketSetting setting, SocketWrapperListener socketListener) {
        this.mSetting = setting;
        this.mSocketListener = socketListener;
    }

    void connect() {
        Log.e(TAG, "connect: ");
        if (destroyed) {
            return;
        }
        needClose = false;
        if (connectStatus == 0) {
            connectStatus = 1;
            try {
                if (mWebSocket == null) {
                    if (TextUtils.isEmpty(mSetting.getConnectUrl())) {
                        throw new RuntimeException("WebSocket connect url is empty!");
                    }
                    Draft draft = mSetting.getDraft();
                    if (draft == null) {
                        draft = new Draft_6455();
                    }
                    int connectTimeOut = mSetting.getConnectTimeout();
                    if (connectTimeOut <= 0) {
                        connectTimeOut = 0;
                    }
                    mWebSocket = new MyWebSocketClient(
                            new URI(mSetting.getConnectUrl()),
                            draft,
                            mSetting.getHttpHeaders(),
                            connectTimeOut);
                    LogUtil.i(TAG, "WebSocket start connect...");
                    if (mSetting.getProxy() != null) {
                        mWebSocket.setProxy(mSetting.getProxy());
                    }
                    mWebSocket.connect();
                    mWebSocket.setConnectionLostTimeout(mSetting.getConnectionLostTimeout());
                    if (needClose) {
                        disConnect();
                    }
                    checkDestroy();
                } else {
                    LogUtil.i(TAG, "WebSocket reconnecting...");
                    mWebSocket.reconnect();
                    if (needClose) {
                        disConnect();
                    }
                    checkDestroy();
                }
            } catch (Throwable e) {
                connectStatus = 0;
                LogUtil.e(TAG, "WebSocket connect failed:", e);
                if (mSocketListener != null) {
                    mSocketListener.onConnectFailed(e);
                }
            }
        }
    }

    /**
     * 重新连接
     */
    void reconnect() {
        needClose = false;
        if (connectStatus == 0) {
            connect();
        }
    }

    /**
     * 断开连接
     */
    void disConnect() {
        needClose = true;
        if (connectStatus == 2) {
            LogUtil.i(TAG, "WebSocket disconnecting...");
            if (mWebSocket != null) {
                mWebSocket.close();
            }
            LogUtil.i(TAG, "WebSocket disconnected");
        }
    }

    /**
     * 发送数据
     *
     * @param request 请求数据
     */
    void send(Request request) {
        if (mWebSocket == null) {
            return;
        }
        if (request == null) {
            LogUtil.e(TAG, "send data is null!");
            return;
        }
        if (connectStatus == 2) {
            try {
                request.send(mWebSocket);
                LogUtil.i(TAG, "send success:" + request.toString());
            } catch (WebsocketNotConnectedException e) {
                connectStatus = 0;
                LogUtil.e(TAG, "ws is disconnected, send failed:" + request.toString(), e);
                if (mSocketListener != null) {
                    //not connect
                    mSocketListener.onSendDataError(request,
                            ErrorResponse.ERROR_NO_CONNECT,
                            e);
                    mSocketListener.onDisconnect();
                }
            } catch (Throwable e) {
                connectStatus = 0;
                LogUtil.e(TAG, "Exception,send failed:" + request.toString(), e);
                if (mSocketListener != null) {
                    //unknown error
                    mSocketListener.onSendDataError(request,
                            ErrorResponse.ERROR_UNKNOWN,
                            e);
                }
            } finally {
                request.release();
            }
        } else {
            LogUtil.e(TAG, "WebSocket not connect,send failed:" + request.toString());
            if (mSocketListener != null) {
                //not connect
                mSocketListener.onSendDataError(request,
                        ErrorResponse.ERROR_NO_CONNECT,
                        null);
            }
        }
    }

    /**
     * 获取连接状态
     * 0-未连接
     * 1-正在连接
     * 2-已连接
     */
    int getConnectState() {
        return connectStatus;
    }

    /**
     * 彻底销毁资源
     */
    void destroy() {
        destroyed = true;
        disConnect();
        if (connectStatus == 0) {
            mWebSocket = null;
        }
        releaseResource();
    }

    private void checkDestroy() {
        if (destroyed) {
            try {
                if (mWebSocket != null && !mWebSocket.isClosed()) {
                    mWebSocket.close();
                }
                releaseResource();
                connectStatus = 0;
            } catch (Throwable e) {
                LogUtil.e(TAG, "checkDestroy(WebSocketClient)", e);
            }
        }
    }

    private void releaseResource() {
        if (mSocketListener != null) {
            mSocketListener = null;
        }
    }

    private void onWSCallbackOpen(ServerHandshake handshakeData) {
        if (destroyed) {
            checkDestroy();
            return;
        }
        connectStatus = 2;
        LogUtil.i(TAG, "WebSocket connect success");
        if (needClose) {
            disConnect();
        } else {
            if (mSocketListener != null) {
                mSocketListener.onConnected();
            }
        }
    }

    private void onWSCallbackMessage(String message) {
        if (destroyed) {
            checkDestroy();
            return;
        }
        connectStatus = 2;
        if (mSocketListener != null) {
            Response<String> response = ResponseFactory.createTextResponse();
            response.setResponseData(message);
            LogUtil.i(TAG, "WebSocket received message:" + response.toString());
            mSocketListener.onMessage(response);
        }
    }

    private void onWSCallbackMessage(ByteBuffer bytes) {
        if (destroyed) {
            checkDestroy();
            return;
        }
        connectStatus = 2;
        if (mSocketListener != null) {
            Response<ByteBuffer> response = ResponseFactory.createByteBufferResponse();
            response.setResponseData(bytes);
            LogUtil.i(TAG, "WebSocket received message:" + response.toString());
            mSocketListener.onMessage(response);
        }
    }

    private void onWSCallbackWebsocketPing(Framedata f) {
        if (destroyed) {
            checkDestroy();
            return;
        }
        connectStatus = 2;
        if (mSocketListener != null) {
            Response<Framedata> response = ResponseFactory.createPingResponse();
            response.setResponseData(f);
            LogUtil.i(TAG, "WebSocket received ping:" + response.toString());
            mSocketListener.onMessage(response);
        }
    }

    private void onWSCallbackWebsocketPong(Framedata f) {
        if (destroyed) {
            checkDestroy();
            return;
        }
        connectStatus = 2;
        if (mSocketListener != null) {
            Response<Framedata> response = ResponseFactory.createPongResponse();
            response.setResponseData(f);
            LogUtil.i(TAG, "WebSocket received pong:" + response.toString());
            mSocketListener.onMessage(response);
        }
    }

    private void onWSCallbackClose(int code, String reason, boolean remote) {
        connectStatus = 0;
        LogUtil.d(TAG, String.format("WebSocket closed!code=%s,reason:%s,remote:%s",
                code,
                reason,
                remote));
        if (mSocketListener != null) {
            mSocketListener.onDisconnect();
        }
        checkDestroy();
    }

    private void onWSCallbackError(Exception ex) {
        if (destroyed) {
            checkDestroy();
            return;
        }
        LogUtil.e(TAG, "WebSocketClient#onError(Exception)", ex);
    }

    private class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        public MyWebSocketClient(URI serverUri, Draft protocolDraft) {
            super(serverUri, protocolDraft);
        }

        public MyWebSocketClient(URI serverUri, Map<String, String> httpHeaders) {
            super(serverUri, httpHeaders);
        }

        public MyWebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders) {
            super(serverUri, protocolDraft, httpHeaders);
        }

        public MyWebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
            super(serverUri, protocolDraft, httpHeaders, connectTimeout);
        }

        @Override
        public void onOpen(ServerHandshake handshakeData) {
            onWSCallbackOpen(handshakeData);
        }

        @Override
        public void onMessage(String message) {
            onWSCallbackMessage(message);
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            onWSCallbackMessage(bytes);
        }

        @Override
        public void onWebsocketPing(WebSocket conn, Framedata f) {
            super.onWebsocketPing(conn, f);
            onWSCallbackWebsocketPing(f);
        }

        @Override
        public void onWebsocketPong(WebSocket conn, Framedata f) {
            super.onWebsocketPong(conn, f);
            onWSCallbackWebsocketPong(f);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            onWSCallbackClose(code, reason, remote);
        }

        @Override
        public void onError(Exception ex) {
            onWSCallbackError(ex);
        }
    }
}
