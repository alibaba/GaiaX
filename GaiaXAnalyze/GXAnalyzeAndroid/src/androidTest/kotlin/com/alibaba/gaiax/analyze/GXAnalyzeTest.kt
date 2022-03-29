package com.alibaba.gaiax.analyze

import androidx.test.ext.junit.runners.AndroidJUnit4
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
            override fun computeValueExpression(valuePath: String, source: Any): Long {
                return if (valuePath == "$$") {
                    GXAnalyze.createValueMap(source)
                } else {
                    GXAnalyze.createValueFloat64(8F)
                }
            }

            override fun computeFunctionExpression(
                functionName: String,
                params: LongArray,
            ): Long {
                //获取返回的参数列表结果
                var res = GXAnalyze.createValueMap(testData)
                val result = GXContext.wrapAsGXValue(res)
                return res
            }
        })

        // 初始化数据
        testData.put("test", 11)
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
    fun error_string_123_add_1() {
        Assert.assertEquals(null, instance.getResult("'123' + 1", testData))
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

    @Test(expected = RuntimeException::class)
    fun error_ternary_self_0() {
        Assert.assertEquals(testData, instance.getResult("\$\$ ?: 0", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_ternary_true_self_1() {
        Assert.assertEquals(testData, instance.getResult("true ? \$\$ : 1", testData))
    }

    @Test(expected = RuntimeException::class)
    fun error_ternary_false_self_1() {
        Assert.assertEquals(1f, instance.getResult("false ? \$\$ : 1", testData))
    }

    @Test
    fun testJAnalyze() {
        //所有测试用例：基于computeValueExpression返回结果为8F

        //正常结果验证
        Assert.assertEquals(true, instance.getResult("(1+1)>1 ? 1>0 : 2<3", testData))
        Assert.assertEquals(2f, instance.getResult("(\$data.b>(\$data.a-2)-1) ? ((\$data.b*1)/2)/2 : 1", testData))

        Assert.assertEquals(0f, instance.getResult("2%2", testData))
        Assert.assertEquals(0f, instance.getResult("3%-1", testData))
        Assert.assertEquals(1f, instance.getResult("3%2", testData))


        Assert.assertEquals(true, instance.getResult("\$data.size+1>1 ? true : false ", testData))
        Assert.assertEquals(2f, instance.getResult("(\$data.b>(\$data.a-2)-1) ? ((\$data.b*1)/2)/2 : 1", testData))
        Assert.assertEquals(8f, instance.getResult("1+1*2+3+4/2", testData))
        Assert.assertEquals(-1f, instance.getResult("(1+1-3)", testData))
        Assert.assertEquals(-2f, instance.getResult("(1+1)-2*2", testData))
        Assert.assertEquals(-4f, instance.getResult("(1-3)*2", testData))
        Assert.assertEquals(0f, instance.getResult("0*2", testData))
        Assert.assertEquals(-2f, instance.getResult("-1*2", testData))
        Assert.assertEquals(-2f, instance.getResult("2*-1", testData))
        Assert.assertEquals(-1f, instance.getResult("+1*-1", testData))
        Assert.assertEquals(2f, instance.getResult("2*+1", testData))
        Assert.assertEquals(4.4f, instance.getResult("2.2*2", testData))
        Assert.assertEquals(-4.4f, instance.getResult("-2.2*2", testData))
        Assert.assertEquals(-0.5f, instance.getResult("-1/2", testData))
        Assert.assertEquals(0.5f, instance.getResult("1/2", testData))
        Assert.assertEquals(1f, instance.getResult("1/1", testData))
        Assert.assertEquals(1f, instance.getResult("-1/-1", testData))
        Assert.assertEquals(64f, instance.getResult("\$data*\$data", testData))
        Assert.assertEquals(-8f, instance.getResult("\$data*-1", testData))
        Assert.assertEquals(-8f, instance.getResult("\$data/-1", testData))
        Assert.assertEquals(1f, instance.getResult("\$data/\$data", testData))
        Assert.assertEquals(4f, instance.getResult("1+1*3", testData))
        Assert.assertEquals(4f, instance.getResult("1+2*3/2", testData))
        Assert.assertEquals(4f, instance.getResult("(1+1)*2", testData))
        Assert.assertEquals(2f, instance.getResult("(1+3)/2", testData))
        Assert.assertEquals(1f, instance.getResult("+1", testData))
        Assert.assertEquals(-1f, instance.getResult("-1", testData))
        Assert.assertEquals(false, instance.getResult("true == false", testData))
        Assert.assertEquals(true, instance.getResult("null == null", testData))
        Assert.assertEquals(true, instance.getResult("\$data == 8", testData))
        Assert.assertEquals(false, instance.getResult("null != null", testData))
        Assert.assertEquals(true, instance.getResult("1 != null", testData))
        Assert.assertEquals(true, instance.getResult("!false", testData))
        Assert.assertEquals(false, instance.getResult("!true", testData))
        Assert.assertEquals(null, instance.getResult("true ? null : \$data", testData))
        Assert.assertEquals(null, instance.getResult("false ? \$data : null", testData))
        Assert.assertEquals(0f, instance.getResult("false ? 1 : 0", testData))
        Assert.assertEquals(1f, instance.getResult("true ? 1 : 0", testData))
        Assert.assertEquals(1f, instance.getResult("true ? 1 : 0", testData))
        Assert.assertEquals(true, instance.getResult("true ?: 1", testData))
        Assert.assertEquals(1f, instance.getResult("false ?: 1", testData))
        Assert.assertEquals(true, instance.getResult("true ?: \$data", testData))
        Assert.assertEquals(false, instance.getResult("true && false", testData))
        Assert.assertEquals(true, instance.getResult("true || false", testData))
        Assert.assertEquals(true, instance.getResult("true && true", testData))
        Assert.assertEquals(false, instance.getResult("false && false", testData))
        Assert.assertEquals(true, instance.getResult("true || true", testData))
        Assert.assertEquals(false, instance.getResult("false || false", testData))
        Assert.assertEquals(false, instance.getResult("1==2", testData))
        Assert.assertEquals(true, instance.getResult("1==1.0", testData))
        Assert.assertEquals(false, instance.getResult("1 != 1.0", testData))
        Assert.assertEquals(true, instance.getResult("1!=1.1", testData))
        Assert.assertEquals(false, instance.getResult("'string'==1.0", testData))
        Assert.assertEquals(true, instance.getResult("'string'=='string'", testData))
        Assert.assertEquals(true, instance.getResult("'string'!='s2tring'", testData))
        Assert.assertEquals(true, instance.getResult("1+1 == 2", testData))
        Assert.assertEquals(true, instance.getResult("1+1 >= 2", testData))
        Assert.assertEquals(false, instance.getResult("1+1 > 2", testData))
        Assert.assertEquals(false, instance.getResult("1+1 < 2", testData))
        Assert.assertEquals(false, instance.getResult("3 == 2", testData))
        Assert.assertEquals(true, instance.getResult("3 > 2", testData))
        Assert.assertEquals(false, instance.getResult("'string' == 2", testData))
        Assert.assertEquals(false, instance.getResult("'string' > 2", testData))
        Assert.assertEquals(false, instance.getResult("'string' >= 2", testData))
        Assert.assertEquals(false, instance.getResult("'string' <= 2", testData))
        Assert.assertEquals(false, instance.getResult("'string' < 2", testData))
        Assert.assertEquals(false, instance.getResult("3 < 2", testData))
        Assert.assertEquals(true, instance.getResult("2 < 3", testData))
        Assert.assertEquals(true, instance.getResult("5%3 == 2", testData))
        Assert.assertEquals(true, instance.getResult("0-2==-2", testData))
        Assert.assertEquals(true, instance.getResult("1/2 == 0.5", testData))
        Assert.assertEquals("123", instance.getResult("true ? '123' : '456'", testData))
        Assert.assertEquals("123", instance.getResult("'true' ? '123' : '456'", testData))
        Assert.assertEquals(true, instance.getResult("true == 'true'", testData))
        Assert.assertEquals(true, instance.getResult("true != 'false'", testData))
        Assert.assertEquals(false, instance.getResult("true != 'true'", testData))
    }

    fun testFunction(par1: Float?, par2: String?, par3: Float?) {
        val p1 = par1
        val p2 = par2
        val p3 = par3
        val p6 = p3
    }

    fun getResFromA(res: String): Float {
        return res.toFloat()
    }
}