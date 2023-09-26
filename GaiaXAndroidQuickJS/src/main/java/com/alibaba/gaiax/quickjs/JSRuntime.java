package com.alibaba.gaiax.quickjs;



import androidx.annotation.Keep;

import java.io.Closeable;

// TODO Check all JSContext closed when closing JSRuntime

/**
 * JSRuntime is a JavaScript runtime with a memory heap.
 * It can't evaluate JavaScript script.
 *
 * @see JSContext
 */
@Keep
public class JSRuntime implements Closeable {

    private long pointer;
    private final QuickJS quickJS;

    JSRuntime(long pointer, QuickJS quickJS) {
        this.pointer = pointer;
        this.quickJS = quickJS;
    }

    private void checkClosed() {
        if (pointer == 0) {
            throw new IllegalStateException("The JSRuntime is closed");
        }
    }

    /**
     * Set the malloc limit for this JSRuntime.
     * Only positive number and {@code -1} are accepted.
     * {@code -1} for no limit.
     */
    public synchronized void setMallocLimit(int mallocLimit) {
        checkClosed();

        if (mallocLimit == 0 || mallocLimit < -1) {
            throw new IllegalArgumentException("Only positive number and -1 are accepted as malloc limit");
        }

        QuickJS.setRuntimeMallocLimit(pointer, mallocLimit);
    }

    public synchronized void initStdHandlers() {
        checkClosed();
        QuickJS.initStdHandlers(pointer);
    }

    /**
     * Set the InterruptHandler for this JSRuntime.
     * {@link InterruptHandler#onInterrupt()} is called every 10000 js instructions.
     */
    public synchronized void setInterruptHandler(InterruptHandler interruptHandler) {
        checkClosed();
        QuickJS.setRuntimeInterruptHandler(pointer, interruptHandler);
    }

    public synchronized void setPromiseRejectionHandler(PromiseRejectionHandler promiseRejectionHandler) {
        checkClosed();
        QuickJS.setPromiseRejectionHandler(pointer, promiseRejectionHandler);
    }

    public synchronized void setRuntimeMaxStackSize(int size) {
        checkClosed();
        QuickJS.setRuntimeMaxStackSize(pointer, size);
    }


    /**
     * Creates a JSContext with the memory heap of this JSRuntime.
     */
    public synchronized JSContext createJSContext() {
        checkClosed();
        long context = QuickJS.createContext(pointer);
        if (context == 0) {
            throw new IllegalStateException("Cannot create JSContext instance");
        }
        return new JSContext(context, quickJS, this);
    }

    @Override
    public synchronized void close() {
        if (pointer != 0) {
            long runtimeToClose = pointer;
            pointer = 0;
            QuickJS.destroyRuntime(runtimeToClose);
        }
    }

    @Keep
    public interface InterruptHandler {
        /**
         * Returns {@code true} to interrupt.
         */
        @Keep
        boolean onInterrupt();
    }

    @Keep
    public interface PromiseRejectionHandler {

        @Keep
        void onError(String message);
    }
}
