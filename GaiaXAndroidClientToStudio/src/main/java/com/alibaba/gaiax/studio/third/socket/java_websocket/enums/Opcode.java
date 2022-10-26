package com.alibaba.gaiax.studio.third.socket.java_websocket.enums;

import androidx.annotation.Keep;

/**
 * Enum which contains the different valid opcodes
 */
@Keep
public enum Opcode {
    CONTINUOUS, TEXT, BINARY, PING, PONG, CLOSING
    // more to come
}