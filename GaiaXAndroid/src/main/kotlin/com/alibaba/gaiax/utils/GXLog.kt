/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.gaiax.utils

import android.util.Log

/**
 * @suppress
 */
object GXLog {

    fun e(tag: String?, msg: String?) {
        longE(tag ?: "[GaiaX]", msg ?: "")
    }

    fun e(msg: String?) {
        longE("[GaiaX]", msg ?: "")
    }

    private fun longE(tag: String, msg: String) {
        val maxLogSize = 1000
        for (i in 0..msg.length / maxLogSize) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = if (end > msg.length) msg.length else end
            Log.e(tag, msg.substring(start, end))
        }
    }

    fun isLog(): Boolean {
        return GXPropUtils.isLog()
    }
}