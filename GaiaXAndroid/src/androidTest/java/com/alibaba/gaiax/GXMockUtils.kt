package com.alibaba.gaiax

import android.content.Context
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry

object GXMockUtils {
    fun deviceGap(): Float {
        if (Build.BRAND == "Xiaomi" && Build.MODEL == "Mi 10") {
            return 0.5F
        }
        return 0F
    }

    var context: Context = InstrumentationRegistry.getInstrumentation().context

}

