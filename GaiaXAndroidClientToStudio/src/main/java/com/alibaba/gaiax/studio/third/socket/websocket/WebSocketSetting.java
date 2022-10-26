package com.alibaba.gaiax.studio.third.socket.websocket;

import android.Manifest;
import android.content.Context;

import com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.DefaultResponseDispatcher;
import com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.IResponseDispatcher;
import com.alibaba.gaiax.studio.third.socket.websocket.dispatcher.ResponseDelivery;

import com.alibaba.gaiax.studio.third.socket.java_websocket.drafts.Draft;

import java.net.Proxy;
import java.util.Map;

/**
 * WebSocket 使用配置
 * Created by ZhangKe on 2018/6/26.
 */
public class WebSocketSetting {

    /**
     * WebSocket 连接地址
     */
    private String connectUrl;
    /**
     * 消息处理分发器
     */
    private IResponseDispatcher responseProcessDispatcher;
    /**
     * 设置是否使用子线程处理数据
     */
    private boolean processDataOnBackground;
    /**
     * 设置网络连接变化后是否自动重连。
     */
    private boolean reconnectWithNetworkChanged = true;
    /**
     * 设置心跳间隔时间
     */
    private int connectionLostTimeout = 60;
    /**
     * 代理
     */
    private Proxy mProxy;
    /**
     * WS 协议实现。
     */
    private Draft draft;
    /**
     * 设置连接的请求头
     */
    private Map<String, String> httpHeaders;
    /**
     * 超时时间
     */
    private int connectTimeout = 0;
    /**
     * 重连次数，默认为：10 次
     */
    private int reconnectFrequency = 10;
    /**
     * 消息发射器
     */
    private ResponseDelivery responseDelivery;

    /**
     * 获取 WebSocket 链接地址
     */
    public String getConnectUrl() {
        return this.connectUrl;
    }

    /**
     * 设置 WebSocket 连接地址，必须设置项。
     */
    public void setConnectUrl(String connectUrl) {
        this.connectUrl = connectUrl;
    }

    /**
     * 获取当前已设置的消息分发器
     */
    public IResponseDispatcher getResponseDispatcher() {
        if (responseProcessDispatcher == null) {
            responseProcessDispatcher = new DefaultResponseDispatcher();
        }
        return responseProcessDispatcher;
    }

    /**
     * 设置消息分发器，
     * 不设置则使用 {@link DefaultResponseDispatcher}
     */
    public void setResponseProcessDispatcher(IResponseDispatcher responseDispatcher) {
        this.responseProcessDispatcher = responseDispatcher;
    }

    /**
     * @see #setReconnectWithNetworkChanged(boolean)
     */
    public boolean reconnectWithNetworkChanged() {
        return this.reconnectWithNetworkChanged;
    }

    /**
     * 设置网络连接变化后是否自动重连。</br>
     * 如果设置 true 则需要添加申请 {@link Manifest.permission#ACCESS_NETWORK_STATE} 权限。
     * 需要注意的是，如果希望网络连接发生变化后重新连接，
     * 需要注册监听网络变化的广播，框架中已经实现了这个广播：{@link NetworkChangedReceiver}。
     * 但是需要手动注册，
     * 你可以调用 {@link WebSocketHandler#registerNetworkChangedReceiver(Context)} 方法注册，
     * 也可以在 manifest 中注册，或者自己注册。
     */
    public void setReconnectWithNetworkChanged(boolean reconnectWithNetworkChanged) {
        this.reconnectWithNetworkChanged = reconnectWithNetworkChanged;
    }

    /**
     * 获取心跳间隔时间
     */
    public int getConnectionLostTimeout() {
        return connectionLostTimeout;
    }

    /**
     * 设置心跳间隔时间，单位为秒；
     * 默认为 60 s。
     */
    public void setConnectionLostTimeout(int connectionLostTimeout) {
        this.connectionLostTimeout = connectionLostTimeout;
    }

    /**
     * @see #setProxy(Proxy)
     */
    public Proxy getProxy() {
        return mProxy;
    }

    /**
     * 设置代理
     */
    public void setProxy(Proxy mProxy) {
        this.mProxy = mProxy;
    }

    /**
     * @see #setDraft(Draft)
     */
    public Draft getDraft() {
        return draft;
    }

    /**
     * ws 协议实现，默认为 {@link com.alibaba.gaiax.studio.third.socket.java_websocket.drafts.Draft_6455}，
     * 框架也只提供了这一个实现，一般情况不需要设置。
     * 特殊需求可以自定义继承 {@link Draft} 的类
     */
    public void setDraft(Draft draft) {
        this.draft = draft;
    }

    /**
     * @see #setProcessDataOnBackground(boolean)
     */
    public boolean processDataOnBackground() {
        return processDataOnBackground;
    }

    /**
     * 设置是否使用子线程处理数据。
     * 使用子线程处理完消息后会自动切换到主线程。
     *
     * @param processDataOnBackground true-接收到消息后将使用子线程处理数据，
     *                                false-反之，
     *                                默认为 true。
     */
    public void setProcessDataOnBackground(boolean processDataOnBackground) {
        this.processDataOnBackground = processDataOnBackground;
    }

    /**
     * @see #setReconnectFrequency(int)
     */
    public int getReconnectFrequency() {
        return reconnectFrequency;
    }

    /**
     * 设置连接断开后的重连次数，
     * 默认为 10 次
     */
    public void setReconnectFrequency(int reconnectFrequency) {
        this.reconnectFrequency = reconnectFrequency;
    }

    /**
     * @see #setHttpHeaders(Map)
     */
    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    /**
     * 设置 WebSocket 连接的请求头信息
     */
    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    /**
     * @see #setConnectTimeout(int)
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 设置连接超时时间(单位：毫秒)，
     * 默认为 0（不设置超时时间）
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * @see #setResponseDelivery(ResponseDelivery)
     */
    public ResponseDelivery getResponseDelivery() {
        return responseDelivery;
    }

    /**
     * 设置消息发射器，不设置则默认使用 {@link dispatcher.MainThreadResponseDelivery}.
     * 但你可以设置自己的消息发射器，只需要实现 {@link ResponseDelivery} 接口即可。
     *
     * @param responseDelivery 务必实现 ResponseDelivery 接口
     */
    public void setResponseDelivery(ResponseDelivery responseDelivery) {
        this.responseDelivery = responseDelivery;
    }
}
