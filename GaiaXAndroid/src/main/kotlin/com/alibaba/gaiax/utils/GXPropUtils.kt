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

    private var isLog: Boolean? = null

    /**
     * adb shell setprop debug.com.alibaba.gaiax.log 1
     */
    fun isLog(): Boolean {
        if (isLog == null) {
            isLog = "1" == GXSystemProp["debug.com.alibaba.gaiax.log", "0"]
        }
        return isLog ?: false
    }


}