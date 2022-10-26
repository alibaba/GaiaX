package com.alibaba.gaiax.studio.third.socket.java_websocket.exceptions;

import androidx.annotation.Keep;

import java.io.UnsupportedEncodingException;

/**
 * The Character Encoding is not supported.
 *
 * @since 1.4.0
 */
@Keep
public class InvalidEncodingException extends RuntimeException {

    /**
     * attribute for the encoding exception
     */
    private final UnsupportedEncodingException encodingException;

    /**
     * constructor for InvalidEncodingException
     *
     * @param encodingException the cause for this exception
     */
    public InvalidEncodingException(UnsupportedEncodingException encodingException) {
        if (encodingException == null)
            throw new IllegalArgumentException();
        this.encodingException = encodingException;
    }

    /**
     * Get the exception which includes more information on the unsupported encoding
     *
     * @return an UnsupportedEncodingException
     */
    public UnsupportedEncodingException getEncodingException() {
        return encodingException;
    }
}
