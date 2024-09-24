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

/**
 * @suppress
 */
object GXPropUtils {

    private var isTrace: Boolean? = null

    /**
     * adb shell setprop debug.com.alibaba.gaiax.trace 1
     */
    fun isTrace(): Boolean {
        if (isTrace == null) {
            isTrace = "1" == SystemProp["debug.com.alibaba.gaiax.trace", "0"]
        }
        return  isTrace ?: false
    }

    private var isShowNodeLog: Boolean? = null

    fun isShowNodeLog(): Boolean {
        if (isShowNodeLog == null) {
            isShowNodeLog = "1" == SystemProp["debug.com.alibaba.gaiax.log.show_node_log", "0"]
        }
        return isShowNodeLog ?: false
    }
}