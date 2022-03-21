package com.alibaba.gaiax.analyze

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    val testData = JSONObject()

    @Test
    fun testJAnalyze() {
        val instance: GXAnalyze
        testData.put("test", 11)
        val timeC = measureTimeMillis {
            instance = GXAnalyze()
        }
        instance.initComputeExtend(object : GXAnalyze.IComputeExtend {
            override fun computeValueExpression(valuePath: String, source: Any): Long {
                return if(valuePath == "$$"){
                    GXAnalyze.createValueMap(source)
                }else{
                    GXAnalyze.createValueFloat64(8F)
                }
            }

            override fun computeFunctionExpression(
                functionName: String,
                params: LongArray
            ): Long {
                //获取返回的参数列表结果
                var res = GXAnalyze.createValueMap(testData)
                val result = GXContext.wrapAsGXValue(res)
                return res
            }

        })

        //所有测试用例：基于computeValueExpression返回结果为8F

        //报错验证
        Assert.assertEquals(null, instance.getResult("null + true", testData))
        Assert.assertEquals(null, instance.getResult("true + 1", testData))
        Assert.assertEquals(null, instance.getResult("1 + abc", testData))
        Assert.assertEquals(null, instance.getResult("'123' + 1", testData))
        Assert.assertEquals(null, instance.getResult("abc + 1", testData))
        Assert.assertEquals(null, instance.getResult("abc + '123'", testData))
        Assert.assertEquals(null, instance.getResult("null + 1", testData))
        Assert.assertEquals(null, instance.getResult("null + 'abc'", testData))
        Assert.assertEquals(null, instance.getResult("null + \$\$", testData))
        Assert.assertEquals(null, instance.getResult("null + id", testData))
        Assert.assertEquals(null, instance.getResult("true + '123'", testData))
        Assert.assertEquals(null, instance.getResult("1/0", testData))
        Assert.assertEquals(null, instance.getResult("\$\$ + 1", testData))
        Assert.assertEquals(null, instance.getResult("\$\$ - 1", testData))
        Assert.assertEquals(null, instance.getResult("\$\$ * 1", testData))
        Assert.assertEquals(null, instance.getResult("\$\$ / 1", testData))
        Assert.assertEquals(null, instance.getResult("\$\$ ? 1 : 0", testData))
        Assert.assertEquals(testData, instance.getResult("\$\$ ?: 0", testData))
        Assert.assertEquals(testData, instance.getResult("true ? \$\$ : 1", testData))
        Assert.assertEquals(1f, instance.getResult("false ? \$\$ : 1", testData))

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