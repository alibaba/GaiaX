package com.alibaba.gaiax.studio.third.socket.java_websocket.enums;

import androidx.annotation.Keep;

/**
 * Enum which represents the state a websocket may be in
 */
@Keep
public enum ReadyState {
    NOT_YET_CONNECTED, OPEN, CLOSING, CLOSED
}