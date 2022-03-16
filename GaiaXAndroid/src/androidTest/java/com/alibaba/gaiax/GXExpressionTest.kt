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
import com.alibaba.gaiax.template.expression.GXExpression
import com.alibaba.gaiax.utils.setValueExt
import org.junit.Test

/**
 * 表达式集成测试
 */
class GXExpressionTest {

    @Test
    fun feature_取值表达式_1() {
        // ${data}
        val dataJson = JSONObject()
        dataJson.setValueExt("data", "data")
        assert(GXExpression.create("\${data}")?.value(dataJson) == "data")
    }

    @Test
    fun feature_取值表达式_2() {
        // ${data.array[0]}
        val dataJson = JSONObject()
        val data = JSONObject()
        val array = JSONArray()
        array[0] = "data"
        data.setValueExt("array", array)
        dataJson.setValueExt("data", data)
        assert(GXExpression.create("\${data.array[0]}")?.value(dataJson) == "data")
    }

    @Test
    fun feature_取值表达式_3() {
        // ${data.array[0].data}
        val dataJson = JSONObject()
        val data = JSONObject()
        val array = JSONArray()
        val childData = JSONObject()
        childData["data"] = "data"
        array[0] = childData
        data.setValueExt("array", array)
        dataJson.setValueExt("data", data)
        assert(GXExpression.create("\${data.array[0].data}")?.value(dataJson) == "data")
    }

    @Test
    fun feature_关系() {
        val expression = GXExpression.create("\${data.title} < \${data.subtitle}")
        assert(expression is GXExpression.GXText)
    }

    @Test
    fun feature_三元表达式_形式1_普通取值() {
        assert(GXExpression.create("@{ true ? a : b }")?.value() == "a")
        assert(GXExpression.create("@{ false ? a : b }")?.value() == "b")

        // 1 0 作为数字
        assert(GXExpression.create("@{ 1 ? a : b }")?.value() == "a")
        assert(GXExpression.create("@{ 0 ? a : b }")?.value() == "b")

        assert(GXExpression.create("@{ biezhihua ? a : b }")?.value() == "a")
        assert(GXExpression.create("@{  ? a : b }")?.value() == "b")
    }

    @Test
    fun feature_三元表达式_形式1_普通取值_6() {
        assert(GXExpression.create("@{  ? a : b }")?.value() == "b")
    }

    @Test
    fun feature_三元表达式_形式2_普通取值() {
        assert(GXExpression.create("@{ false ?: b }")?.value() == "b")

        val desireData = GXExpression.create("@{ 1 ?: b }")?.value()
        assert(desireData == 1.0F || desireData == 1)
        assert(GXExpression.create("@{ 0 ?: b }")?.value() == "b")


    }

    @Test
    fun feature_三元表达式_形式2_普通取值_1() {
        assert(GXExpression.create("@{ true ?: b }")?.value() == true)
    }

    @Test
    fun feature_三元表达式_形式2_普通取值_4() {
        assert(GXExpression.create("@{ biezhihua ?: b }")?.value() == "biezhihua")
    }

    @Test
    fun feature_三元表达式_形式2_普通取值_2() {
        assert(GXExpression.create("@{ false ?: b }")?.value() == "b")
    }

    @Test
    fun feature_三元表达式_形式2_普通取值_3() {
        assert(GXExpression.create("@{  ?: b }")?.value() == "b")
    }

    @Test
    fun feature_三元复合取值表达式_形式1() {
        val root = testJson()

        assert(GXExpression.create("@{ \${condition1} ? value1 : value2 }")?.value(root) == "value1")
        assert(GXExpression.create("@{ \${condition2} ? value1 : value2 }")?.value(root) == "value1")
        assert(GXExpression.create("@{ \${condition3} ? value1 : value2 }")?.value(root) == "value1")
        assert(GXExpression.create("@{ \${condition4} ? value1 : value2 }")?.value(root) == "value1")
        assert(GXExpression.create("@{ \${condition5} ? value1 : value2 }")?.value(root) == "value2")
        assert(GXExpression.create("@{ \${condition6} ? value1 : value2 }")?.value(root) == "value2")
        assert(GXExpression.create("@{ \${condition7} ? value1 : value2 }")?.value(root) == "value2")
        assert(GXExpression.create("@{ \${condition8} ? value1 : value2 }")?.value(root) == "value2")
        assert(GXExpression.create("@{ \${condition1} ? \${data1} : value2 }")?.value(root) == true)
        assert(GXExpression.create("@{ \${condition2} ? \${data2} : value2 }")?.value(root) == "true")
        assert(GXExpression.create("@{ \${condition3} ? \${data3} : value2 }")?.value(root) == 1)
        assert(GXExpression.create("@{ \${condition5} ? \${data1} : \${data5} }")?.value(root) == false)
        assert(GXExpression.create("@{ \${condition6} ? \${data2} : \${data6} }")?.value(root) == "false")
        assert(GXExpression.create("@{ \${condition7} ? \${data3} : \${data7} }")?.value(root) == 0)
        assert(GXExpression.create("@{ \${condition8} ? \${data4} : \${data8} }")?.value(root) == "0")

        ////

        assert(GXExpression.create("@{ true ? value1 : value2 }")?.value(root) == "value1")
        assert(GXExpression.create("@{ 1 ? value1 : value2 }")?.value(root) == "value1")
        assert(GXExpression.create("@{ biezhihua ? value1 : value2 }")?.value(root) == "value1")

        assert(GXExpression.create("@{ false ? value1 : value2 }")?.value(root) == "value2")
        assert(GXExpression.create("@{ 0 ? value1 : value2 }")?.value(root) == "value2")
        assert(GXExpression.create("@{  ? value1 : value2 }")?.value(root) == "value2")

        assert(GXExpression.create("@{ true ? \${data1} : value2 }")?.value(root) == true)
        assert(GXExpression.create("@{ 1 ? \${data2} : value2 }")?.value(root) == "true")
        assert(GXExpression.create("@{ biezhihua ? \${data3} : value2 }")?.value(root) == 1)

        assert(GXExpression.create("@{ false ? \${data1} : \${data5} }")?.value(root) == false)
        assert(GXExpression.create("@{ 0 ? \${data2} : \${data6} }")?.value(root) == "false")
        assert(GXExpression.create("@{  ? \${data3} : \${data7} }")?.value(root) == 0)

    }

    @Test
    fun feature_三元复合取值表达式_形式2_1() {
        assert(GXExpression.create("@{ \${condition1} ?: value2 }")?.value(testJson()) == true)
    }

    @Test
    fun feature_三元复合取值表达式_形式2() {
        val root = testJson()


        assert(GXExpression.create("@{ \${condition2} ?: value2 }")?.value(root) == "true")
        assert(GXExpression.create("@{ \${condition3} ?: value2 }")?.value(root) == 1)

        assert(GXExpression.create("@{ \${condition5} ?: value2 }")?.value(root) == "value2")
        assert(GXExpression.create("@{ \${condition6} ?: value2 }")?.value(root) == "value2")
        assert(GXExpression.create("@{ \${condition7} ?: value2 }")?.value(root) == "value2")

        assert(GXExpression.create("@{ \${condition5} ?: \${data5} }")?.value(root) == false)
        assert(GXExpression.create("@{ \${condition6} ?: \${data6} }")?.value(root) == "false")
        assert(GXExpression.create("@{ \${condition7} ?: \${data7} }")?.value(root) == 0)
        assert(GXExpression.create("@{ \${condition8} ?: \${data8} }")?.value(root) == "0")

        ////

        assert(GXExpression.create("@{ true ?: value2 }")?.value(root) == true)
        val desireData = GXExpression.create("@{ 1 ?: value2 }")?.value(root)
        assert(desireData == 1.0F || desireData == 1)
        assert(GXExpression.create("@{ biezhihua ?: value2 }")?.value(root) == "biezhihua")

        assert(GXExpression.create("@{ false ?: value2 }")?.value(root) == "value2")
        assert(GXExpression.create("@{ 0 ?: value2 }")?.value(root) == "value2")
        assert(GXExpression.create("@{  ?: value2 }")?.value(root) == "value2")

        assert(GXExpression.create("@{ false ?: \${data5} }")?.value(root) == false)
        assert(GXExpression.create("@{ 0 ?: \${data6} }")?.value(root) == "false")
        val desireData1 = GXExpression.create("@{  ?: \${data7} }")?.value(root)
        assert(desireData1 == 0 || desireData1 == 0.0F)

    }

    @Test
    fun feature_表达式_文案() {
        val data = testJson()
        assert(GXExpression.create("测试文案 + \${condition1}")?.value(data) == "测试文案true")
        assert(GXExpression.create("测试文案 + \${condition2}")?.value(data) == "测试文案true")
        assert(GXExpression.create("测试文案 + \${condition3}")?.value(data) == "测试文案1")
        assert(GXExpression.create("测试文案 + \${condition4}")?.value(data) == "测试文案1")
        assert(GXExpression.create("测试文案 + @{ \${condition1} ? value1 : value2 }")?.value(data) == "测试文案value1")
        assert(GXExpression.create("测试文案 + @{ \${condition5} ? value1 : value2 }")?.value(data) == "测试文案value2")
    }

    @Test
    fun feature_字符串值() {
        assert(GXExpression.create("test")?.value() == "test")
    }

    private fun testJson(): JSONObject {
        val root = JSONObject()
        val data = JSONObject()
        val array = JSONArray()
        val childData = JSONObject()

        root["data"] = data

        root["condition1"] = true
        root["condition2"] = "true"
        root["condition3"] = 1
        root["condition4"] = "1"

        root["condition5"] = false
        root["condition6"] = "false"
        root["condition7"] = 0
        root["condition8"] = "0"

        root["data1"] = true
        root["data2"] = "true"
        root["data3"] = 1
        root["data4"] = "1"

        root["data5"] = false
        root["data6"] = "false"
        root["data7"] = 0
        root["data8"] = "0"

        data["array"] = array

        array[0] = childData

        childData["text"] = "text"
        return root
    }

}