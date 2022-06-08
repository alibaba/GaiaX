package com.alibaba.gaiax.analyze

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class GXAnalyzeTest {
    val testData = JSONObject()

    lateinit var instance: GXAnalyze

    @Before
    fun before() {
        // 初始化
        instance = GXAnalyze()
        instance.initComputeExtend(object : GXAnalyze.IComputeExtend {
            override fun computeValueExpression(valuePath: String, source: Any?): Long {
                return if (valuePath == "$$") {
                    GXAnalyze.createValueMap(source)
                } else if (valuePath == "data.array") {
                    GXAnalyze.createValueArray(JSONArray())
                } else if (valuePath == "data.map") {
                    GXAnalyze.createValueMap(JSONObject())
                } else if (valuePath == "data.null") {
                    GXAnalyze.createValueNull()
                } else {
                    GXAnalyze.createValueFloat64(8F)
                }
            }

            override fun computeFunctionExpression(
                functionName: String,
                params: LongArray
            ): Long {
                //获取返回的参数列表结果
                if (functionName == "size" && params.size == 1) {
                    val value = GXAnalyze.wrapAsGXValue(params[0])
                    if (value is GXString) {
                        value.getString()?.let {
                            return GXAnalyze.createValueFloat64(it.length.toFloat())
                        }
                    } else if (value is GXMap) {
                        (value.getValue() as? JSONObject)?.let {
                            return GXAnalyze.createValueFloat64(it.size.toFloat())
                        }
                    } else if (value is GXArray) {
                        (value.getValue() as? JSONArray)?.let {
                            return GXAnalyze.createValueFloat64(it.size.toFloat())
                        }
                    }
                }
                return 0L
            }
        })
        // 初始化数据
        testData.put("test", 11)
    }

    @Test
    fun function_test() {
        Assert.assertEquals(4f, instance.getResult("size('1234')", testData))
        Assert.assertEquals(0f, instance.getResult("size(\$data.map)", testData))
        Assert.assertEquals(0f, instance.getResult("size(\$data.array)", testData))
        Assert.assertEquals(1f, instance.getResult("size(\$\$)", testData))

    }

    @Test
    fun map_or_array() {
        val map = instance.getResult("\$data.map", testData)
        assert(map is JSONObject)
        val array = instance.getResult("\$data.array", testData)
        assert(array is JSONArray)
    }

    @Test
    fun mod_calculate() {
        Assert.assertEquals(0f, instance.getResult("2%2", testData))
        Assert.assertEquals(0f, instance.getResult("3%-1", testData))
        Assert.assertEquals(1f, instance.getResult("3%2", testData))
    }

    @Test
    fun add_calculate() {
        Assert.assertEquals(10f, instance.getResult("1+2+3+4", testData))
        Assert.assertEquals(1f, instance.getResult("0+1", testData))
        Assert.assertEquals("abcd", instance.getResult("'ab'+'cd'", testData))
        Assert.assertEquals(10f, instance.getResult("\$data+2", testData))
        Assert.assertEquals(16f, instance.getResult("\$data+\$data", testData))
        Assert.assertEquals("1231", instance.getResult("'123' + 1", testData))
        Assert.assertEquals("1231", instance.getResult("'1' + 231", testData))
        Assert.assertEquals("1231", instance.getResult("'123' + 1.000", testData))
        Assert.assertEquals("1231.001", instance.getResult("'123' + 1.001", testData))
        Assert.assertEquals("1231.001", instance.getResult("'123' + 1.001000", testData))
        Assert.assertEquals("123.001.001", instance.getResult("'123.00' + 1.001000", testData))
        Assert.assertEquals("1230", instance.getResult("'123' + 0.000", testData))
        Assert.assertEquals("1230.001", instance.getResult("'123' + 0.001", testData))
        Assert.assertEquals("1231", instance.getResult("123.000 + '1'", testData))
        Assert.assertEquals("123.0011", instance.getResult("123.001 + '1'", testData))
        Assert.assertEquals("123.0011.0", instance.getResult("123.001 + '1.0'", testData))
    }

    @Test
    fun subtract_calculate() {
        Assert.assertEquals(3f, instance.getResult("4-1", testData))
        Assert.assertEquals(-1f, instance.getResult("1-2", testData))
        Assert.assertEquals(-1f, instance.getResult("0-1", testData))
        Assert.assertEquals(-2f, instance.getResult("\$data-10", testData))
        Assert.assertEquals(2f, instance.getResult("10-\$data", testData))
        Assert.assertEquals(0f, instance.getResult("8-\$data", testData))
        Assert.assertEquals(-8f, instance.getResult("0-\$data", testData))
        Assert.assertEquals(0f, instance.getResult("\$data-\$data", testData))
    }

    @Test
    fun multiply_calculate() {
        Assert.assertEquals(0f, instance.getResult("0*2", testData))
        Assert.assertEquals(-2f, instance.getResult("-1*2", testData))
        Assert.assertEquals(-2f, instance.getResult("2*-1", testData))
        Assert.assertEquals(-1f, instance.getResult("+1*-1", testData))
        Assert.assertEquals(2f, instance.getResult("2*+1", testData))
        Assert.assertEquals(4.4f, instance.getResult("2.2*2", testData))
        Assert.assertEquals(-4.4f, instance.getResult("-2.2*2", testData))
        Assert.assertEquals(-8f, instance.getResult("\$data*-1", testData))
        Assert.assertEquals(64f, instance.getResult("\$data*\$data", testData))
        Assert.assertEquals(16f, instance.getResult("\$data*2", testData))
        Assert.assertEquals(1f, instance.getResult("1*1", testData))
        Assert.assertEquals(true, instance.getResult("(1+1)>1 ? 1>0 : 2<3", testData))
        Assert.assertEquals(
            2f,
            instance.getResult("(\$data.b>(\$data.a-2)-1) ? ((\$data.b*1)/2)/2 : 1", testData)
        )
        Assert.assertEquals(true, instance.getResult("\$data.size+1>1 ? true : false ", testData))
        Assert.assertEquals(
            2f,
            instance.getResult("(\$data.b>(\$data.a-2)-1) ? ((\$data.b*1)/2)/2 : 1", testData)
        )
        Assert.assertEquals(true, instance.getResult("5%3 == 2", testData))
        Assert.assertEquals(true, instance.getResult("0-2==-2", testData))
        Assert.assertEquals(true, instance.getResult("1/2 == 0.5", testData))
        Assert.assertEquals(null, instance.getResult("\$\$", null))
        Assert.assertEquals(null, instance.getResult("\$data.null", null))
    }

    @Test
    fun divide_calculate() {
        Assert.assertEquals(-0.5f, instance.getResult("-1/2", testData))
        Assert.assertEquals(0.5f, instance.getResult("1/2", testData))
        Assert.assertEquals(1f, instance.getResult("1/1", testData))
        Assert.assertEquals(1f, instance.getResult("-1/-1", testData))
        Assert.assertEquals(-8f, instance.getResult("\$data/-1", testData))
        Assert.assertEquals(1f, instance.getResult("\$data/\$data", testData))
    }

    @Test
    fun multi_calculate() {
        Assert.assertEquals(8f, instance.getResult("1+1*2+3+4/2", testData))
        Assert.assertEquals(-1f, instance.getResult("(1+1-3)", testData))
        Assert.assertEquals(-2f, instance.getResult("(1+1)-2*2", testData))
        Assert.assertEquals(-4f, instance.getResult("(1-3)*2", testData))
        Assert.assertEquals(4f, instance.getResult("1+1*3", testData))
        Assert.assertEquals(4f, instance.getResult("1+2*3/2", testData))
        Assert.assertEquals(4f, instance.getResult("(1+1)*2", testData))
        Assert.assertEquals(2f, instance.getResult("(1+3)/2", testData))
    }

    @Test
    fun single_op_expression() {
        Assert.assertEquals(1f, instance.getResult("+1", testData))
        Assert.assertEquals(-1f, instance.getResult("-1", testData))
        Assert.assertEquals(true, instance.getResult("!false", testData))
        Assert.assertEquals(false, instance.getResult("!true", testData))
    }

    @Test
    fun not_equal() {
        Assert.assertEquals(false, instance.getResult("null != null", testData))
        Assert.assertEquals(true, instance.getResult("1 != null", testData))
        Assert.assertEquals(true, instance.getResult("'string'!='s2tring'", testData))
        Assert.assertEquals(false, instance.getResult("1 != 1.0", testData))
        Assert.assertEquals(true, instance.getResult("1!=1.1", testData))
        Assert.assertEquals(true, instance.getResult("true != 'false'", testData))
        Assert.assertEquals(false, instance.getResult("true != 'true'", testData))
    }

    @Test
    fun equal() {
        Assert.assertEquals(false, instance.getResult("true == false", testData))
        Assert.assertEquals(true, instance.getResult("null == null", testData))
        Assert.assertEquals(true, instance.getResult("\$data == 8", testData))
        Assert.assertEquals(false, instance.getResult("1==2", testData))
        Assert.assertEquals(true, instance.getResult("1==1.0", testData))
        Assert.assertEquals(false, instance.getResult("'string'==1.0", testData))
        Assert.assertEquals(true, instance.getResult("'string'=='string'", testData))
        Assert.assertEquals(true, instance.getResult("1+1 == 2", testData))
        Assert.assertEquals(true, instance.getResult("true == 'true'", testData))
        Assert.assertEquals(false, instance.getResult("3 == 2", testData))
        Assert.assertEquals(false, instance.getResult("'string' == 2", testData))
    }


    @Test
    fun greater_than() {
        Assert.assertEquals(true, instance.getResult("3 > 2", testData))
        Assert.assertEquals(false, instance.getResult("'string' > 2", testData))
        Assert.assertEquals(false, instance.getResult("1+1 > 2", testData))
        Assert.assertEquals(true, instance.getResult("1+1 >= 2", testData))
        Assert.assertEquals(false, instance.getResult("'string' >= 2", testData))
    }

    @Test
    fun less_than() {
        Assert.assertEquals(false, instance.getResult("'string' < 2", testData))
        Assert.assertEquals(false, instance.getResult("3 < 2", testData))
        Assert.assertEquals(true, instance.getResult("2 < 3", testData))
        Assert.assertEquals(false, instance.getResult("'string' <= 2", testData))
        Assert.assertEquals(false, instance.getResult("1+1 < 2", testData))
    }

    @Test
    fun double_or() {
        Assert.assertEquals(true, instance.getResult("true || true", testData))
        Assert.assertEquals(false, instance.getResult("false || false", testData))
        Assert.assertEquals(true, instance.getResult("true || false", testData))
    }

    @Test
    fun double_and() {
        Assert.assertEquals(false, instance.getResult("true && false", testData))
        Assert.assertEquals(true, instance.getResult("true && true", testData))
        Assert.assertEquals(false, instance.getResult("false && false", testData))
    }

    @Test
    fun ternary() {
        Assert.assertEquals(null, instance.getResult("false ? \$data : null", testData))
        Assert.assertEquals(0f, instance.getResult("false ? 1 : 0", testData))
        Assert.assertEquals(1f, instance.getResult("true ? 1 : 0", testData))
        Assert.assertEquals(1f, instance.getResult("true ? 1 : 0", testData))
        Assert.assertEquals(true, instance.getResult("true ?: 1", testData))
        Assert.assertEquals(1f, instance.getResult("false ?: 1", testData))
        Assert.assertEquals(true, instance.getResult("true ?: \$data", testData))
        Assert.assertEquals("123", instance.getResult("true ? '123' : '456'", testData))
        Assert.assertEquals("123", instance.getResult("'true' ? '123' : '456'", testData))
        Assert.assertEquals(null, instance.getResult("true ? null : \$data", testData))
        Assert.assertEquals(1f, instance.getResult("false ? \$\$ : 1", testData))
        Assert.assertEquals(testData, instance.getResult("true ? \$\$ : 1", testData))
        Assert.assertEquals(testData, instance.getResult("\$\$ ?: 0", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_null_add_true() {
        Assert.assertEquals(null, instance.getResult("null + true", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_true_add_1() {
        Assert.assertEquals(null, instance.getResult("true + 1", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_1_add_abc() {
        Assert.assertEquals(null, instance.getResult("1 + abc", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_abc_add_1() {
        Assert.assertEquals(null, instance.getResult("abc + 1", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_abc_add_string_123() {
        Assert.assertEquals(null, instance.getResult("abc + '123'", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_null_add_1() {
        Assert.assertEquals(null, instance.getResult("null + 1", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_null_add_string_abc() {
        Assert.assertEquals(null, instance.getResult("null + 'abc'", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_null_add_self() {
        Assert.assertEquals(null, instance.getResult("null + \$\$", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_null_add_id() {
        Assert.assertEquals(null, instance.getResult("null + id", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_true_add_string_123() {
        Assert.assertEquals(null, instance.getResult("true + '123'", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_1_divide_0() {
        Assert.assertEquals(null, instance.getResult("1/0", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_self_add_1() {
        Assert.assertEquals(null, instance.getResult("\$\$ + 1", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_self_subtract_1() {
        Assert.assertEquals(null, instance.getResult("\$\$ - 1", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_self_multiply_1() {
        Assert.assertEquals(null, instance.getResult("\$\$ * 1", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_self_divide_1() {
        Assert.assertEquals(null, instance.getResult("\$\$ / 1", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_ternary_self_1_0() {
        Assert.assertEquals(null, instance.getResult("\$\$ ? 1 : 0", testData))
    }
}