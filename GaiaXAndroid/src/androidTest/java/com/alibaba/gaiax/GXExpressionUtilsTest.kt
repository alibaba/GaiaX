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

package com.alibaba.gaiax

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.analyze.GXAnalyze
import com.alibaba.gaiax.template.expression.GXExpressionUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * 表达式集成测试
 */
class GXExpressionUtilsTest {

    @Before
    fun before() {
        GXExpressionUtils.initAnalyze()
    }

    @Test
    fun size_string() {
        val value = GXExpressionUtils.create("size('string')")?.value(null)
        Assert.assertEquals(6.0F, value)
    }

    @Test
    fun size_json_object() {
        val value = GXExpressionUtils.create("size(\$data)")?.value(JSONObject().apply {
            this["data"] = JSONObject().apply {
                this["data1"] = "data1"
                this["data2"] = "data1"
                this["data3"] = "data1"
                this["data4"] = "data1"
                this["data5"] = "data1"
                this["data6"] = "data1"
            }
        })
        Assert.assertEquals(6.0F, value)
    }

    @Test
    fun size_json_array() {
        val value = GXExpressionUtils.create("size(\$data)")?.value(JSONObject().apply {
            this["data"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })
        Assert.assertEquals(6.0F, value)
    }

    @Test
    fun string_http() {
        val value = GXExpressionUtils.create("'https://r1.ykimg.com/053400005DAE75E2859B5E44E509DD51'")?.value(null)
        Assert.assertEquals("https://r1.ykimg.com/053400005DAE75E2859B5E44E509DD51", value)
    }
}