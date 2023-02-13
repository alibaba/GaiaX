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

package com.alibaba.gaiax.template.factory

import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.template.GXIExpression

/**
 * @suppress
 */
object GXExpressionFactory {

    fun isTrue(expVersion: String?, value: Any?): Boolean? {
        return GXRegisterCenter.instance.extensionExpression?.isTrue(expVersion, null, value)
    }

    fun create(expVersion: String?, key: String, value: Any?): GXIExpression? {
        return if (value == null) {
            null
        } else {
            GXRegisterCenter.instance.extensionExpression?.create(expVersion, key, value)
        }
    }

    fun create(expVersion: String?, value: Any?): GXIExpression? {
        return if (value == null) {
            null
        } else {
            GXRegisterCenter.instance.extensionExpression?.create(expVersion, null, value)
        }
    }
}