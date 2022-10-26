package com.alibaba.gaiax.studio.third.socket.java_websocket.enums;

import androidx.annotation.Keep;

/**
 * Enum which represents type of handshake is required for a close
 */
@Keep
public enum CloseHandshakeType {
    NONE, ONEWAY, TWOWAY
}