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

package com.alibaba.gaiax.template

/**
 * @suppress
 */
class GXTemplate(
    val id: String,
    val biz: String,
    val version: Int,
    val layer: String,
    val css: String,
    val dataBind: String,
    val js: String
) {

    var type = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GXTemplate

        if (id != other.id) return false
        if (biz != other.biz) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + biz.hashCode()
        result = 31 * result + version
        return result
    }

    override fun toString(): String {
        return "GXTemplate(id='$id', biz='$biz', version=$version)"
    }
}