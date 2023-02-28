package com.alibaba.gaiax

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.utils.GXTestYKExpression
import com.alibaba.gaiax.utils.setValueExt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GXYKExpressionTest {

    @Test
    fun expression_text_enter() {
        val exp = GXTestYKExpression.create("\n")
        assert(exp.desireData() == "\n")
    }

    @Test
    fun expression_bool_true_condition() {
        assert(GXTestYKExpression.isCondition("true"))
        assert(!GXTestYKExpression.isCondition("false"))
        assert(GXTestYKExpression.isCondition(true))
        assert(!GXTestYKExpression.isCondition(false))
        assert(GXTestYKExpression.isCondition(1))
        assert(!GXTestYKExpression.isCondition(0))
        assert(GXTestYKExpression.isCondition(1.0F))
        assert(!GXTestYKExpression.isCondition(0.0F))
        assert(GXTestYKExpression.isCondition("1"))
        assert(!GXTestYKExpression.isCondition("0"))
        assert(GXTestYKExpression.isCondition("1.0"))
        assert(GXTestYKExpression.isCondition("0.0"))
        assert(!GXTestYKExpression.isCondition(""))
        assert(GXTestYKExpression.isCondition("123"))
        assert(GXTestYKExpression.isCondition(Object()))
    }

    @Test
    fun expression_bool_fit_content_condition() {
        assert(GXTestYKExpression.isFitContentCondition("true"))
        assert(GXTestYKExpression.isFitContentCondition("1.0"))
        assert(GXTestYKExpression.isFitContentCondition("1"))

        assert(!GXTestYKExpression.isFitContentCondition("false"))
        assert(!GXTestYKExpression.isFitContentCondition("0.0"))
        assert(!GXTestYKExpression.isFitContentCondition("0"))
    }

    @Test
    fun expression_undefine() {
        val expression = GXTestYKExpression.Undefined
        assert(expression == GXTestYKExpression.Undefined)
    }

    @Test
    fun expression_self() {

        // 检测是否是自己
        val expression = GXTestYKExpression.Self
        assert(expression == GXTestYKExpression.Self)

        // 检测期望的值是否符合预期
        val rawJson = JSONObject()
        assert(expression.desireData(rawJson) == rawJson)
    }

    @Test
    fun expression_constant() {
        // 常量取值
        assert(GXTestYKExpression.GText.create("url").desireData() == "url")

        // "null"串取值
        assert(GXTestYKExpression.GText.create("null").desireData() == null)
    }

    @Test
    fun expression_value() {
        // 检测表达式
        assert(GXTestYKExpression.GValue.create("\${data}").value == "data")

        // 检测取值空值
        assert(GXTestYKExpression.GValue.create("\${data}").desireData(JSONObject()) == null)

        // 检测取值有值
        assert(
            GXTestYKExpression.GValue.create("\${data}")
                .desireData(JSONObject().apply { this["data"] = "gaiax" }) == "gaiax"
        )

    }

    @Test
    fun expression_ternary_1() {

        val data = JSONObject()
        data.put("isFollow", 0)
        data.put("session", 1)

        val create =
            GXTestYKExpression.GTernaryValue3.create("@{\${isFollow} ? (A01-A02)+B01 : @{\${session} ? (A01-A02)+B01+B02 : (A01-A02)+B01}}")
        val desireData = create.desireData(data)
        assert(desireData == "(A01-A02)+B01+B02")
        val value = GXTestYKExpression.GTernaryValue3.create("@{ true ? @{ false ? 1 : 0 } : b }")
        println(value.desireData())
        assert(value.desireData() == 0F || value.desireData() == 0)

        val value2 =
            GXTestYKExpression.GTernaryValue3.create("@{ true ? @{ true ? @{ false ? 1 : 0 } : 0 } : @{ false ? 1 : 0 } }")
        println(value2.desireData())
        assert(value2.desireData() == 0F || value2.desireData() == 0)

        val value3 =
            GXTestYKExpression.GTernaryValue3.create("@{ true ? @{ true ? @{ true ? 1 : 0 } : 0 } : @{ false ? 1 : 0 } }")
        println(value3.desireData())
        assert(value3.desireData() == 1F || value3.desireData() == 1)

        val value4 =
            GXTestYKExpression.GTernaryValue3.create("@{ true ? @{ false ? @{ true ? 1 : 0 } : 0 } : @{ false ? 1 : 0 } }")
        println(value4.desireData())
        assert(value4.desireData() == 0F || value4.desireData() == 0)

        data.put("isPad", 1)
        data.put("isFollow", 1)
        val value5 =
            GXTestYKExpression.GTernaryValue3.create("@{\${isFollow} ? @{\${isPad} ? @{\${isPad} ? 480px : 100%} : 100%} : 200px}")
        println(value5.desireData(data))
        assert(value5.desireData(data) == "480px")
    }

    @Test
    fun expression_ternary_2() {
        assert(GXTestYKExpression.create("@{ true ? a : b }").desireData() == "a")
        assert(GXTestYKExpression.create("@{ false ? a : b }").desireData() == "b")

        // 1 0 作为数字
        assert(GXTestYKExpression.create("@{ 1 ? a : b }").desireData() == "a")
        assert(GXTestYKExpression.create("@{ 0 ? a : b }").desireData() == "b")

        assert(GXTestYKExpression.create("@{ biezhihua ? a : b }").desireData() == "a")
        assert(GXTestYKExpression.create("@{  ? a : b }").desireData() == "b")
    }

    @Test
    fun expression_ternary_3() {
        assert(GXTestYKExpression.create("@{  ? a : b }").desireData() == "b")
    }

    @Test
    fun expression_ternary_4() {
        assert(GXTestYKExpression.create("@{ false ?: b }").desireData() == "b")

        val desireData = GXTestYKExpression.create("@{ 1 ?: b }").desireData()
        assert(desireData == 1.0F || desireData == 1)
        assert(GXTestYKExpression.create("@{ 0 ?: b }").desireData() == "b")


    }

    @Test
    fun expression_ternary_5() {
        assert(GXTestYKExpression.create("@{ true ?: b }").desireData() == true)
    }

    @Test
    fun expression_ternary_6() {
        assert(GXTestYKExpression.create("@{ biezhihua ?: b }").desireData() == "biezhihua")
    }

    @Test
    fun expression_ternary_7() {
        assert(GXTestYKExpression.create("@{ false ?: b }").desireData() == "b")
    }

    @Test
    fun expression_ternary_8() {
        assert(GXTestYKExpression.create("@{  ?: b }").desireData() == "b")
    }

    @Test
    fun expression_ternary_9() {
        val root = testJson()

        assert(
            GXTestYKExpression.create("@{ \${condition1} ? value1 : value2 }")
                .desireData(root) == "value1"
        )
        assert(
            GXTestYKExpression.create("@{ \${condition2} ? value1 : value2 }")
                .desireData(root) == "value1"
        )
        assert(
            GXTestYKExpression.create("@{ \${condition3} ? value1 : value2 }")
                .desireData(root) == "value1"
        )
        assert(
            GXTestYKExpression.create("@{ \${condition4} ? value1 : value2 }")
                .desireData(root) == "value1"
        )
        assert(
            GXTestYKExpression.create("@{ \${condition5} ? value1 : value2 }")
                .desireData(root) == "value2"
        )
        assert(
            GXTestYKExpression.create("@{ \${condition6} ? value1 : value2 }")
                .desireData(root) == "value2"
        )
        assert(
            GXTestYKExpression.create("@{ \${condition7} ? value1 : value2 }")
                .desireData(root) == "value2"
        )
        assert(
            GXTestYKExpression.create("@{ \${condition8} ? value1 : value2 }")
                .desireData(root) == "value2"
        )
        assert(
            GXTestYKExpression.create("@{ \${condition1} ? \${data1} : value2 }")
                .desireData(root) == true
        )
        assert(
            GXTestYKExpression.create("@{ \${condition2} ? \${data2} : value2 }")
                .desireData(root) == "true"
        )
        assert(
            GXTestYKExpression.create("@{ \${condition3} ? \${data3} : value2 }")
                .desireData(root) == 1
        )
        assert(
            GXTestYKExpression.create("@{ \${condition5} ? \${data1} : \${data5} }")
                .desireData(root) == false
        )
        assert(
            GXTestYKExpression.create("@{ \${condition6} ? \${data2} : \${data6} }")
                .desireData(root) == "false"
        )
        assert(
            GXTestYKExpression.create("@{ \${condition7} ? \${data3} : \${data7} }")
                .desireData(root) == 0
        )
        assert(
            GXTestYKExpression.create("@{ \${condition8} ? \${data4} : \${data8} }")
                .desireData(root) == "0"
        )

        ////

        assert(
            GXTestYKExpression.create("@{ true ? value1 : value2 }").desireData(root) == "value1"
        )
        assert(GXTestYKExpression.create("@{ 1 ? value1 : value2 }").desireData(root) == "value1")
        assert(
            GXTestYKExpression.create("@{ biezhihua ? value1 : value2 }")
                .desireData(root) == "value1"
        )

        assert(
            GXTestYKExpression.create("@{ false ? value1 : value2 }").desireData(root) == "value2"
        )
        assert(GXTestYKExpression.create("@{ 0 ? value1 : value2 }").desireData(root) == "value2")
        assert(GXTestYKExpression.create("@{  ? value1 : value2 }").desireData(root) == "value2")

        assert(GXTestYKExpression.create("@{ true ? \${data1} : value2 }").desireData(root) == true)
        assert(GXTestYKExpression.create("@{ 1 ? \${data2} : value2 }").desireData(root) == "true")
        assert(
            GXTestYKExpression.create("@{ biezhihua ? \${data3} : value2 }").desireData(root) == 1
        )

        assert(
            GXTestYKExpression.create("@{ false ? \${data1} : \${data5} }")
                .desireData(root) == false
        )
        assert(
            GXTestYKExpression.create("@{ 0 ? \${data2} : \${data6} }").desireData(root) == "false"
        )
        assert(GXTestYKExpression.create("@{  ? \${data3} : \${data7} }").desireData(root) == 0)

    }

    @Test
    fun expression_ternary_10() {
        assert(
            GXTestYKExpression.create("@{ \${condition1} ?: value2 }")
                .desireData(testJson()) == true
        )
    }

    @Test
    fun expression_ternary_11() {
        val root = testJson()


        assert(
            GXTestYKExpression.create("@{ \${condition2} ?: value2 }").desireData(root) == "true"
        )
        assert(GXTestYKExpression.create("@{ \${condition3} ?: value2 }").desireData(root) == 1)

        assert(
            GXTestYKExpression.create("@{ \${condition5} ?: value2 }").desireData(root) == "value2"
        )
        assert(
            GXTestYKExpression.create("@{ \${condition6} ?: value2 }").desireData(root) == "value2"
        )
        assert(
            GXTestYKExpression.create("@{ \${condition7} ?: value2 }").desireData(root) == "value2"
        )

        assert(
            GXTestYKExpression.create("@{ \${condition5} ?: \${data5} }").desireData(root) == false
        )
        assert(
            GXTestYKExpression.create("@{ \${condition6} ?: \${data6} }")
                .desireData(root) == "false"
        )
        assert(GXTestYKExpression.create("@{ \${condition7} ?: \${data7} }").desireData(root) == 0)
        assert(
            GXTestYKExpression.create("@{ \${condition8} ?: \${data8} }").desireData(root) == "0"
        )

        assert(GXTestYKExpression.create("@{ true ?: value2 }").desireData(root) == true)
        val desireData = GXTestYKExpression.create("@{ 1 ?: value2 }").desireData(root)
        assert(desireData == 1.0F || desireData == 1)
        assert(
            GXTestYKExpression.create("@{ biezhihua ?: value2 }").desireData(root) == "biezhihua"
        )

        assert(GXTestYKExpression.create("@{ false ?: value2 }").desireData(root) == "value2")
        assert(GXTestYKExpression.create("@{ 0 ?: value2 }").desireData(root) == "value2")
        assert(GXTestYKExpression.create("@{  ?: value2 }").desireData(root) == "value2")

        assert(GXTestYKExpression.create("@{ false ?: \${data5} }").desireData(root) == false)
        assert(GXTestYKExpression.create("@{ 0 ?: \${data6} }").desireData(root) == "false")
        val desireData1 = GXTestYKExpression.create("@{  ?: \${data7} }").desireData(root)
        assert(desireData1 == 0 || desireData1 == 0.0F)
    }

    @Test
    fun expression_function_size_string() {
        Assert.assertEquals(
            "biezhihua".length, GXTestYKExpression.GSize.create("size(biezhihua)").desireData()
        )
    }

    @Test
    fun expression_function_size_json_string() {
        Assert.assertEquals(
            "biezhihua".length,
            GXTestYKExpression.GSize.create("size(\${data.title})").desireData(JSONObject().apply {
                val data = JSONObject()
                this["data"] = data
                data["title"] = "biezhihua"
            })
        )
    }

    @Test
    fun expression_function_size_json_jsonarray() {
        Assert.assertEquals(
            3, GXTestYKExpression.GSize.create("size(\${nodes})").desireData(JSONObject().apply {
                val nodes = JSONArray()
                this["nodes"] = nodes
                nodes.add(JSONObject())
                nodes.add(JSONObject())
                nodes.add(JSONObject())
            })
        )
    }

    @Test
    fun expression_function_size_json_jsonobj() {
        Assert.assertEquals(
            2, GXTestYKExpression.GSize.create("size(\${data})").desireData(JSONObject().apply {
                val data = JSONObject()
                this["data"] = data
                data["title"] = ""
                data["name"] = ""
            })
        )
    }

    @Test
    fun expression_function_scroll_position() {
        Assert.assertEquals(
            -1,
            GXTestYKExpression.GScroll.create("scroll(position)").desireData(JSONObject().apply {})
        )

        Assert.assertEquals(
            0,
            GXTestYKExpression.GScroll.create("scroll(position)")
                .desireData(JSONObject().apply { this["gaiax_scroll_position"] = 0 })
        )

        Assert.assertEquals(
            1,
            GXTestYKExpression.GScroll.create("scroll(position)")
                .desireData(JSONObject().apply { this["gaiax_scroll_position"] = 1 })
        )
    }

    @Test
    fun expression_value_1() {
        // ${data}
        val dataJson = JSONObject()
        dataJson.setValueExt("data", "data")
        assert(GXTestYKExpression.create("\${data}").desireData(dataJson) == "data")
    }

    @Test
    fun expression_value_2() {
        // ${data.array[0]}
        val dataJson = JSONObject()
        val data = JSONObject()
        val array = JSONArray()
        array[0] = "data"
        data.setValueExt("array", array)
        dataJson.setValueExt("data", data)
        assert(GXTestYKExpression.create("\${data.array[0]}").desireData(dataJson) == "data")
    }

    @Test
    fun expression_value_3() {
        // ${data.array[0].data}
        val dataJson = JSONObject()
        val data = JSONObject()
        val array = JSONArray()
        val childData = JSONObject()
        childData["data"] = "data"
        array[0] = childData
        data.setValueExt("array", array)
        dataJson.setValueExt("data", data)
        assert(GXTestYKExpression.create("\${data.array[0].data}").desireData(dataJson) == "data")
    }

    @Test
    fun expression_text_joint() {
        val data = testJson()
        Assert.assertEquals(
            GXTestYKExpression.create("测试文案 + \${condition1}").desireData(data), "测试文案true"
        )
        Assert.assertEquals(
            GXTestYKExpression.create("测试文案 + \${condition2}").desireData(data), "测试文案true"
        )
        Assert.assertEquals(
            GXTestYKExpression.create("测试文案 + \${condition3}").desireData(data), "测试文案1"
        )
        Assert.assertEquals(
            GXTestYKExpression.create("测试文案 + \${condition4}").desireData(data), "测试文案1"
        )
        Assert.assertEquals(
            GXTestYKExpression.create("测试文案 + @{ \${condition1} ? value1 : value2 }")
                .desireData(data), "测试文案value1"
        )
        Assert.assertEquals(
            "测试文案value2",
            GXTestYKExpression.create("测试文案 + @{ \${condition5} ? value1 : value2 }")
                .desireData(data)
        )

        val exp =
            GXTestYKExpression.GTextValue.create("linear-gradient(to top right, + \${topColor} + , green)")
        val desireData = exp.desireData(JSONObject().apply { this["topColor"] = "#00ff00" })
        assert(desireData == "linear-gradient(to top right,#00ff00, green)")
        val result = GXTestYKExpression.GTextValue.create("\${title1} + \${title2} + \${title3}")
            .desireData(JSONObject().apply {
                this["title1"] = "bie"
                this["title3"] = "hua"
            })
        Assert.assertEquals("biehua", result)

    }

    @Test
    fun expression_string() {
        Assert.assertEquals("biezhihua", GXTestYKExpression.create("biezhihua").desireData())
    }

    @Test
    fun expression_eval_null() {
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( null == null)").desireData()
        )
    }

    @Test
    fun expression_eval_mod() {
        Assert.assertEquals(1 % 1, GXTestYKExpression.GEval.create("eval(1 % 1)").desireData())
        Assert.assertEquals(2 % 2, GXTestYKExpression.GEval.create("eval(2 % 2)").desireData())

        Assert.assertEquals(
            1.0F % 1.0F, GXTestYKExpression.GEval.create("eval(1.0 % 1.0)").desireData()
        )
        Assert.assertEquals(
            2.0F % 2.0F, GXTestYKExpression.GEval.create("eval(2.0 % 2.0)").desireData()
        )

        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(null % 2.0)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(1 % null)").desireData())
    }

    @Test
    fun expression_eval_equal() {
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(1 == 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(1 == 2)").desireData())

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval('A' == 'A')").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval('A' == 'B')").desireData())

        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(true == true)").desireData()
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(false == false)").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(true == false)").desireData()
        )

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(1 == true)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(1 == false)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(0 == true)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(0 == false)").desireData())

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(true == 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(false == 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(true == 0)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(false == 0)").desireData())

        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(null == null)").desireData()
        )
    }

    @Test
    fun expression_eval_not_equal() {
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(1 != 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(1 != 2)").desireData())

        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval('A' != 'A')").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval('A' != 'B')").desireData())

        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(true != true)").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(false != false)").desireData()
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(true != false)").desireData()
        )

        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(1 != true)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(1 != false)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(0 != true)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(0 != false)").desireData())

        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(true != 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(false != 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(true != 0)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(false != 0)").desireData())

        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(null != null)").desireData()
        )
    }

    @Test
    fun expression_eval_greater_than_or_equal() {
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 >= 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 >= 2)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(2 >= 3)").desireData())

        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(null >= null)").desireData()
        )
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval('A' >= 'B')").desireData())
    }

    @Test
    fun expression_eval_greater_than() {
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 > 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(2 > 2)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(2 > 3)").desireData())

        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(null > null)").desireData()
        )
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval('A' > 'B')").desireData())
    }

    @Test
    fun expression_eval_less_than_or_equal() {
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(2 <= 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 <= 2)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 <= 3)").desireData())

        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(null <= null)").desireData()
        )
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval('A' <= 'B')").desireData())
    }

    @Test
    fun expression_eval_less_than() {
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(2 < 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(2 < 2)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 < 3)").desireData())

        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(null < null)").desireData()
        )
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval('A' < 'B')").desireData())
    }

    @Test
    fun expression_eval_double_or() {
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 1 || 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 1 || 0)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 0 || 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval( 0 || 0)").desireData())

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 1 || true)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 1 || false)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 0 || true)").desireData())
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( 0 || false)").desireData()
        )

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 1 || 'A')").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 0 || 'A')").desireData())

        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( true || true)").desireData()
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( true || false)").desireData()
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( false || true)").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( false || false)").desireData()
        )

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( true || 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( false || 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( true || 0)").desireData())
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( false || 0)").desireData()
        )

        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( true || 'A')").desireData()
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( false || 'A')").desireData()
        )

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 'A' || 'A')").desireData())

        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( 'A' || true)").desireData()
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( 'A' || false)").desireData()
        )

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 'A' || 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 'A' || 0)").desireData())

        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( null || true )").desireData()
        )
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( null || 1 )").desireData())
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( null || 0 )").desireData()
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( null || 'A' )").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( null || null )").desireData()
        )

        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( true || null )").desireData()
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(  1 || null )").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( 0 || null )").desireData()
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( | 'A' || null )").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(  null || null)").desireData()
        )
    }

    @Test
    fun expression_eval_double_and() {
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 1 && 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 1 && 2)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 2 && 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval( 1 && 0)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval( 0 && 1)").desireData())

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 1 && true)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval( 0 && true)").desireData())
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( 1 && false)").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( 0 && false)").desireData()
        )

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 1 && 'A')").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval( 0 && 'A')").desireData())

        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( true && true)").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( false && true)").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( true && false)").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( false && false)").desireData()
        )

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( true && 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval( true && 0)").desireData())
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( false && 1)").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( false && 0)").desireData()
        )

        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( true && 'A')").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( false && 'A')").desireData()
        )

        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval( 'A' && true )").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( 'A' && false )").desireData()
        )

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval( 'A' && 1 )").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval( 'A' && 0 )").desireData())

        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( null && null )").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( null && '' )").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval( '' && null )").desireData()
        )
    }

    @Test
    fun expression_eval_number() {
        assert(GXTestYKExpression.create("@{ eval(2>1) ? 'A' : 'B' }").desireData() == "A")
        assert(GXTestYKExpression.create("@{ eval(2<1) ? 'A' : 'B' }").desireData() == "B")
        assert(GXTestYKExpression.create("@{ eval(2>=1) ? 'A' : 'B' }").desireData() == "A")
        assert(GXTestYKExpression.create("@{ eval(2<=1) ? 'A' : 'B' }").desireData() == "B")
        assert(GXTestYKExpression.create("@{ eval(2!=1) ? 'A' : 'B' }").desireData() == "A")
        assert(GXTestYKExpression.create("@{ eval(2==1) ? 'A' : 'B' }").desireData() == "B")
    }

    @Test
    fun expression_eval_bool() {
        assert(GXTestYKExpression.create("@{ eval(true==true) ? 'A' : 'B' }").desireData() == "A")
        assert(GXTestYKExpression.create("@{ eval(true!=true) ? 'A' : 'B' }").desireData() == "B")
        assert(GXTestYKExpression.create("@{ eval(false!=true) ? 'A' : 'B' }").desireData() == "A")
        assert(GXTestYKExpression.create("@{ eval(false==true) ? 'A' : 'B' }").desireData() == "B")
    }

    @Test
    fun expression_eval_string() {
        assert(GXTestYKExpression.create("@{ eval('A'=='A') ? 'A' : 'B' }").desireData() == "A")
        assert(GXTestYKExpression.create("@{ eval('A'!='A') ? 'A' : 'B' }").desireData() == "B")
    }

    @Test
    fun expression_eval_value() {
        val data = JSONObject()

        // 1 显示LOTTIE动画
        // 2 不显示LOTTIE动画
        data["state"] = 0
        assert(
            GXTestYKExpression.create("@{ eval(\${state} == 1) ? flex : none }")
                .desireData(data) == "none"
        )

        data["state"] = 1
        assert(
            GXTestYKExpression.create("@{ eval(\${state} == 1) ? flex : none }")
                .desireData(data) == "flex"
        )
    }

    @Test
    fun expression_eval_ternary_1() {
        assert(
            GXTestYKExpression.create("@{ eval( 2 > 1) ? @{ eval( 3 > 2) ? 'C' : 'D' } : 'B' }")
                .desireData() == "C"
        )
    }

    @Test
    fun expression_eval_ternary_2() {
        assert(
            GXTestYKExpression.create("@{ eval( 2 > 1) ? @{ eval( 3 <= 2) ? 'C' : 'D' } : 'B' }")
                .desireData() == "D"
        )
    }

    @Test
    fun expression_eval_ternary_3() {
        assert(
            GXTestYKExpression.create("@{ eval( 'A' == 'A' ) ? @{ eval( 'B' != 'C') ? 'E' : 'F' } : 'G' }")
                .desireData() == "E"
        )
    }

    @Test
    fun expression_eval_ternary_4() {
        assert(
            GXTestYKExpression.create("@{ eval( 2 > 1) ? @{ eval( \${A} <= \${B} ) ? 'C' : 'D' } : 'B' }")
                .desireData(JSONObject().apply {
                    this["A"] = 1
                    this["B"] = 2
                }) == "C"
        )
    }

    @Test
    fun expression_eval_base_operate() {
        // operate
        assert(GXTestYKExpression.GEval.create("eval(2>1)").operate == ">")
        assert(GXTestYKExpression.GEval.create("eval(2>=1)").operate == ">=")
        assert(GXTestYKExpression.GEval.create("eval(2==1)").operate == "==")
        assert(GXTestYKExpression.GEval.create("eval(2!=1)").operate == "!=")
        assert(GXTestYKExpression.GEval.create("eval(2<=1)").operate == "<=")
        assert(GXTestYKExpression.GEval.create("eval(2<1)").operate == "<")
    }

    @Test
    fun expression_eval_mod_number() {
        Assert.assertEquals(GXTestYKExpression.GEval.create("eval( 0 % 4)").desireData(), 0)
        Assert.assertEquals(GXTestYKExpression.GEval.create("eval( 1 % 4)").desireData(), 1)
        Assert.assertEquals(GXTestYKExpression.GEval.create("eval( 2 % 4)").desireData(), 2)
        Assert.assertEquals(GXTestYKExpression.GEval.create("eval( 3 % 4)").desireData(), 3)
        Assert.assertEquals(GXTestYKExpression.GEval.create("eval( 4 % 4)").desireData(), 0)

        Assert.assertEquals(GXTestYKExpression.GEval.create("eval( 0.0 % 4.0)").desireData(), 0.0F)
        Assert.assertEquals(GXTestYKExpression.GEval.create("eval( 1.0 % 4.0)").desireData(), 1.0F)
        Assert.assertEquals(GXTestYKExpression.GEval.create("eval( 2.0 % 4.0)").desireData(), 2.0F)
        Assert.assertEquals(GXTestYKExpression.GEval.create("eval( 3.0 % 4.0)").desireData(), 3.0F)
        Assert.assertEquals(GXTestYKExpression.GEval.create("eval( 4.0 % 4.0)").desireData(), 0.0F)
    }

    @Test
    fun expression_eval_base_number() {
        val desireData = GXTestYKExpression.GEval.create("eval(2>1)").leftValue.desireData()
        assert(desireData == 2 || desireData == 2F)
        val desireData1 = GXTestYKExpression.GEval.create("eval(2>1)").rightValue.desireData()
        assert(desireData1 == 1 || desireData1 == 1F)
    }

    @Test
    fun expression_eval_base_bool() {
        assert(GXTestYKExpression.GEval.create("eval(true==true)").leftValue.desireData() == true)
        assert(GXTestYKExpression.GEval.create("eval(true==true)").rightValue.desireData() == true)
    }

    @Test
    fun expression_eval_base_string() {
        assert(GXTestYKExpression.GEval.create("eval('A'=='B')").leftValue.desireData() == "A")
        assert(GXTestYKExpression.GEval.create("eval('A'=='B')").rightValue.desireData() == "B")
    }

    @Test
    fun expression_eval_base_exp() {
        val data = JSONObject()
        data["string"] = "string"
        data["boolean"] = true
        data["int"] = 1

        var value = GXTestYKExpression.GEval.create("eval(\${string}==\${string})")
        assert(value.leftValue.desireData(data) == "string")
        assert(value.rightValue.desireData(data) == "string")

        value = GXTestYKExpression.GEval.create("eval(\${boolean}==\${boolean})")
        assert(value.leftValue.desireData(data) == true)
        assert(value.rightValue.desireData(data) == true)

        value = GXTestYKExpression.GEval.create("eval(\${int}==\${int})")
        assert(value.leftValue.desireData(data) == 1)
        assert(value.rightValue.desireData(data) == 1)
    }

    @Test
    fun expression_eval_base_result() {
        assert(GXTestYKExpression.GEval.create("eval(2>1)").desireData() == true)
        assert(GXTestYKExpression.GEval.create("eval(2>=1)").desireData() == true)
        assert(GXTestYKExpression.GEval.create("eval(2!=1)").desireData() == true)
        assert(GXTestYKExpression.GEval.create("eval(2==1)").desireData() == false)
        assert(GXTestYKExpression.GEval.create("eval(2<=1)").desireData() == false)
        assert(GXTestYKExpression.GEval.create("eval(2<1)").desireData() == false)

        assert(GXTestYKExpression.GEval.create("eval('A'=='A')").desireData() == true)
        assert(GXTestYKExpression.GEval.create("eval('A'!='A')").desireData() == false)
        assert(GXTestYKExpression.GEval.create("eval('A'!='A')").desireData() == false)
        assert(GXTestYKExpression.GEval.create("eval('A'!='B')").desireData() == true)

        assert(GXTestYKExpression.GEval.create("eval(true==true)").desireData() == true)
        assert(GXTestYKExpression.GEval.create("eval(true==false)").desireData() == false)
        assert(GXTestYKExpression.GEval.create("eval(true!=true)").desireData() == false)
        assert(GXTestYKExpression.GEval.create("eval(true!=false)").desireData() == true)

        val data = JSONObject()
        data["string"] = "string"
        data["boolean"] = true
        data["int"] = 1
        assert(
            GXTestYKExpression.GEval.create("eval(\${string}==\${string})").desireData(data) == true
        )
        assert(
            GXTestYKExpression.GEval.create("eval(\${string}!=\${string})")
                .desireData(data) == false
        )

        assert(
            GXTestYKExpression.GEval.create("eval(\${boolean}==\${boolean})")
                .desireData(data) == true
        )
        assert(
            GXTestYKExpression.GEval.create("eval(\${boolean}!=\${boolean})")
                .desireData(data) == false
        )

        assert(GXTestYKExpression.GEval.create("eval(\${int}==\${int})").desireData(data) == true)
        assert(GXTestYKExpression.GEval.create("eval(\${int}!=\${int})").desireData(data) == false)
    }

    @Test
    fun expression_eval_base_result_logic_number() {
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 && 'A')").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 && 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(2 && 0)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 && true)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(2 && false)").desireData())

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(1 && 'A')").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(1 && 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(1 && 0)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(1 && true)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(1 && false)").desireData())

        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(0 && 'A')").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(0 && 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(0 && 0)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(0 && true)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(0 && false)").desireData())


        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 || 'A')").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 || 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 || 0)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 || true)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(2 || false)").desireData())

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(1 || 'A')").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(1 || 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(1 || 0)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(1 || true)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(1 || false)").desireData())

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(0 || 'A')").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(0 || 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(0 || 0)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(0 || true)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(0 || false)").desireData())
    }

    @Test
    fun expression_eval_base_result_logic_string() {
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval('A' && 1)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval('A' && 0)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval('A' && true)").desireData())
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval('A' && false)").desireData()
        )
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval('A' && 'A')").desireData())

        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval('A' || 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval('A' || 0)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval('A' || true)").desireData())
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval('A' || false)").desireData()
        )
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval('A' || 'A')").desireData())
    }

    @Test
    fun expression_eval_base_result_logic_bool() {
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(true && true)").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(true && false)").desireData()
        )
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(true && 0)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(true && 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(true && 'A')").desireData())

        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(false && true)").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(false && false)").desireData()
        )
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(false && 0)").desireData())
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(false && 1)").desireData())
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(false && 'A')").desireData()
        )

        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(true || true)").desireData()
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(true || false)").desireData()
        )
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(true || 0)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(true || 1)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(true || 'A')").desireData())

        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(false || true)").desireData()
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(false || false)").desireData()
        )
        Assert.assertEquals(false, GXTestYKExpression.GEval.create("eval(false || 0)").desireData())
        Assert.assertEquals(true, GXTestYKExpression.GEval.create("eval(false || 1)").desireData())
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(false || 'A')").desireData()
        )
    }

    @Test
    fun expression_eval_base_result_logic_null() {
        Assert.assertEquals(
            false,
            GXTestYKExpression.GEval.create("eval(\${title} && true)").desireData(JSONObject())
        )
        Assert.assertEquals(
            false,
            GXTestYKExpression.GEval.create("eval(\${title} && false)").desireData(JSONObject())
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(\${title} && 1)").desireData(JSONObject())
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(\${title} && 0)").desireData(JSONObject())
        )
        Assert.assertEquals(
            false,
            GXTestYKExpression.GEval.create("eval(\${title} && 'A')").desireData(JSONObject())
        )

        Assert.assertEquals(
            true,
            GXTestYKExpression.GEval.create("eval(\${title} || true)").desireData(JSONObject())
        )
        Assert.assertEquals(
            false,
            GXTestYKExpression.GEval.create("eval(\${title} || false)").desireData(JSONObject())
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(\${title} || 1)").desireData(JSONObject())
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(\${title} || 0)").desireData(JSONObject())
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(\${title} || 'A')").desireData(JSONObject())
        )

        Assert.assertEquals(
            false,
            GXTestYKExpression.GEval.create("eval(true && \${title})").desireData(JSONObject())
        )
        Assert.assertEquals(
            false,
            GXTestYKExpression.GEval.create("eval(false && \${title})").desireData(JSONObject())
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(1 && \${title})").desireData(JSONObject())
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(0 && \${title})").desireData(JSONObject())
        )
        Assert.assertEquals(
            false,
            GXTestYKExpression.GEval.create("eval('A' && \${title})").desireData(JSONObject())
        )

        Assert.assertEquals(
            true,
            GXTestYKExpression.GEval.create("eval(true || \${title})").desireData(JSONObject())
        )
        Assert.assertEquals(
            false,
            GXTestYKExpression.GEval.create("eval(false || \${title})").desireData(JSONObject())
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval(1 || \${title})").desireData(JSONObject())
        )
        Assert.assertEquals(
            false, GXTestYKExpression.GEval.create("eval(0 || \${title})").desireData(JSONObject())
        )
        Assert.assertEquals(
            true, GXTestYKExpression.GEval.create("eval('A' || \${title})").desireData(JSONObject())
        )
    }

    @Test
    fun expression_text() {
        val result = GXTestYKExpression.create("'共' + \${data.totalBooks} + '本'")
            .desireData(JSONObject().apply {
                this["data"] = JSONObject().apply {
                    this["totalBooks"] = 12
                }
            })
        Assert.assertEquals("共12本", result)
    }

    @Test
    fun expression_text2() {
        val result =
            GXTestYKExpression.create("'linear-gradient(to right, ' + \${data.coverBackgroundColor} + ' 0%, ' + \${data.coverBackgroundColorEnd} + ' 100%)'")
                .desireData(JSONObject().apply {
                    this["data"] = JSONObject().apply {
                        this["coverBackgroundColor"] = "#888888"
                        this["coverBackgroundColorEnd"] = "#888888"
                    }
                })
        Assert.assertEquals("linear-gradient(to right, #888888 0%, #888888 100%)", result)
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