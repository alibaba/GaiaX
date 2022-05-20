package app.visly.stretch

import androidx.annotation.Keep


/**
 * @suppress
 */
@Keep
class Stretch {

    companion object {

        @Volatile
        private var didInit = false

        @Volatile
        private var initing = false

        @Volatile
        var ptr: Long = 0

        fun init() {
            if (didInit || initing) {
                return
            }
            if (!didInit) {
                synchronized(Stretch::class.java) {
                    if (didInit || initing) {
                        return
                    }
                    initing = true
                    System.loadLibrary("stretch")
                    ptr = nInit()
                    didInit = true
                    initing = false
                }
            }
        }

        @JvmStatic
        private external fun nInit(): Long
    }
}