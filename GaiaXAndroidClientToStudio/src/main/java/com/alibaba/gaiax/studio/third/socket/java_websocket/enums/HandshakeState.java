package com.alibaba.gaiax.studio.third.socket.java_websocket.enums;

import androidx.annotation.Keep;

/**
 * Enum which represents the states a handshake may be in
 */
@Keep
public enum HandshakeState {
    /**
     * Handshake matched this Draft successfully
     */
    MATCHED,
    /**
     * Handshake is does not match this Draft
     */
    NOT_MATCHED
}