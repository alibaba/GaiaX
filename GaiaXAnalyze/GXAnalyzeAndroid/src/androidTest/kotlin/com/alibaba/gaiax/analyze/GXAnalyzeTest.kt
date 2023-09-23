package com.alibaba.gaiax.analyze

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class GXAnalyzeTest {


    private var instance: GXAnalyze = GXAnalyzeWrapper.analyze

    //        @Test
    fun bad_case_1() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("\$data", null))
        Assert.assertEquals(null, instance.getResult("\$\$", null))

        Assert.assertEquals(true, instance.getResult("size(\$title)+1>1 ? true : false ", testData))
        Assert.assertEquals(
            2f,
            instance.getResult("(\$data.b > (\$data.a-2)-1) ? ((\$data.b*1)/2)/2 : 1", testData)
        )
        Assert.assertEquals(
            2f,
            instance.getResult("(\$data.b>(\$data.a-2)-1) ? ((\$data.b*1)/2)/2 : 1", testData)
        )

        // java => null+'gaiax' = nullgaiax
        Assert.assertEquals("test", instance.getResult("\$data.stringEmpty + 'test'", testData))

        Assert.assertEquals(10f, instance.getResult("\$data+2", testData))

        // 应该抛异常
        Assert.assertEquals(10f, instance.getResult("\$data+2", testData))

        Assert.assertEquals(0L, instance.getResult("\$data ? 1 : 0", testData))
    }

    @Test
    fun empty_test() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(null, instance.getResult("\$data.stringEmpty", testData))
            Assert.assertEquals("", instance.getResult("''", testData))
            Assert.assertEquals(null, instance.getResult("", testData))
            Assert.assertEquals(null, instance.getResult("null", testData))
            Assert.assertEquals(0f, instance.getResult("size(null)", JSONObject()))
        }
    }

    @Test
    fun num_test() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(100000000001F, instance.getResult("100000000000+1.0", testData))
            Assert.assertEquals(
                9223372036854775802L,
                instance.getResult("9223372036854775801 + 1", testData)
            )
            Assert.assertEquals(10001L, instance.getResult("10000+1", testData))
            Assert.assertEquals(100001L, instance.getResult("100000+1", testData))
            Assert.assertEquals(1000001L, instance.getResult("1000000+1", testData))
            Assert.assertEquals(10000001L, instance.getResult("10000000+1", testData))
            Assert.assertEquals(100000001L, instance.getResult("100000000+1", testData))
            Assert.assertEquals(1000000001L, instance.getResult("1000000000+1", testData))
            Assert.assertEquals(10000000001L, instance.getResult("10000000000+1", testData))
            Assert.assertEquals(100000000001L, instance.getResult("100000000000+1", testData))
            Assert.assertEquals(1000000000001L, instance.getResult("1000000000000+1", testData))
            Assert.assertEquals(10000000000001L, instance.getResult("10000000000000+1", testData))
            Assert.assertEquals(100000000000001L, instance.getResult("100000000000000+1", testData))
            Assert.assertEquals(
                10000000000000001L,
                instance.getResult("10000000000000000+1", testData)
            )
            Assert.assertEquals(
                100000000000000001L,
                instance.getResult("100000000000000000+1", testData)
            )
            Assert.assertEquals(
                1000000000000000001L,
                instance.getResult("1000000000000000000+1", testData)
            )

            Assert.assertEquals(100000000000F, instance.getResult("100000000001-1.0", testData))
            Assert.assertEquals(
                9223372036854775802L,
                instance.getResult("9223372036854775803 - 1", testData)
            )
            Assert.assertEquals(10000L, instance.getResult("10001-1", testData))
            Assert.assertEquals(100001L, instance.getResult("100002-1", testData))
            Assert.assertEquals(1000001L, instance.getResult("1000002-1", testData))
            Assert.assertEquals(10000001L, instance.getResult("10000002-1", testData))
            Assert.assertEquals(100000001L, instance.getResult("100000002-1", testData))
            Assert.assertEquals(1000000001L, instance.getResult("1000000002-1", testData))
            Assert.assertEquals(10000000001L, instance.getResult("10000000002-1", testData))
            Assert.assertEquals(100000000001L, instance.getResult("100000000002-1", testData))
            Assert.assertEquals(1000000000001L, instance.getResult("1000000000002-1", testData))
            Assert.assertEquals(10000000000001L, instance.getResult("10000000000002-1", testData))
            Assert.assertEquals(100000000000001L, instance.getResult("100000000000002-1", testData))
            Assert.assertEquals(
                10000000000000001L,
                instance.getResult("10000000000000002-1", testData)
            )
            Assert.assertEquals(
                100000000000000001L,
                instance.getResult("100000000000000002-1", testData)
            )
            Assert.assertEquals(
                1000000000000000001L,
                instance.getResult("1000000000000000002-1", testData)
            )
            Assert.assertEquals(200000000000F, instance.getResult("100000000000 * 2.0", testData))
            Assert.assertEquals(20000L, instance.getResult("10000 * 2", testData))
            Assert.assertEquals(200000L, instance.getResult("100000 * 2", testData))
            Assert.assertEquals(2000000L, instance.getResult("1000000 * 2", testData))
            Assert.assertEquals(20000000L, instance.getResult("10000000 * 2", testData))
            Assert.assertEquals(200000000L, instance.getResult("100000000 * 2", testData))
            Assert.assertEquals(2000000000L, instance.getResult("1000000000 * 2", testData))
            Assert.assertEquals(20000000000L, instance.getResult("10000000000 * 2", testData))
            Assert.assertEquals(200000000000L, instance.getResult("100000000000 * 2", testData))
            Assert.assertEquals(2000000000000L, instance.getResult("1000000000000 * 2", testData))
            Assert.assertEquals(20000000000000L, instance.getResult("10000000000000 * 2", testData))
            Assert.assertEquals(
                200000000000000L,
                instance.getResult("100000000000000 * 2", testData)
            )
            Assert.assertEquals(
                20000000000000000L,
                instance.getResult("10000000000000000 * 2", testData)
            )
            Assert.assertEquals(
                200000000000000000L,
                instance.getResult("100000000000000000 * 2", testData)
            )
            Assert.assertEquals(
                2000000000000000000L,
                instance.getResult("1000000000000000000 * 2", testData)
            )
            Assert.assertEquals(62.5F, instance.getResult("125.0 / 2", testData))
            Assert.assertEquals(5000L, instance.getResult("10000 / 2", testData))
            Assert.assertEquals(50000L, instance.getResult("100000 / 2", testData))
            Assert.assertEquals(500000L, instance.getResult("1000000 / 2", testData))
            Assert.assertEquals(5000000L, instance.getResult("10000000 / 2", testData))
            Assert.assertEquals(50000000L, instance.getResult("100000000 / 2", testData))
            Assert.assertEquals(500000000L, instance.getResult("1000000000 / 2", testData))
            Assert.assertEquals(5000000000L, instance.getResult("10000000000 / 2", testData))
            Assert.assertEquals(50000000000L, instance.getResult("100000000000 / 2", testData))
            Assert.assertEquals(500000000000L, instance.getResult("1000000000000 / 2", testData))
            Assert.assertEquals(5000000000000L, instance.getResult("10000000000000 / 2", testData))
            Assert.assertEquals(
                50000000000000L,
                instance.getResult("100000000000000 / 2", testData)
            )
            Assert.assertEquals(
                5000000000000000L,
                instance.getResult("10000000000000000 / 2", testData)
            )
            Assert.assertEquals(
                50000000000000000L,
                instance.getResult("100000000000000000 / 2", testData)
            )
            Assert.assertEquals(
                500000000000000000L,
                instance.getResult("1000000000000000000 / 2", testData)
            )
            Assert.assertEquals(500000.5F, instance.getResult("1000001.0 / 2", testData))
            Assert.assertEquals(
                500000000000.5F,
                instance.getResult("1000000000001.0 / 2", testData)
            )
            Assert.assertEquals(
                500000000000000000.5F,
                instance.getResult("1000000000000000001.0 / 2", testData)
            )
            Assert.assertEquals(30L, instance.getResult("80 % 50", testData))
            Assert.assertEquals(300L, instance.getResult("800 % 500", testData))
            Assert.assertEquals(3000L, instance.getResult("8000 % 5000", testData))
            Assert.assertEquals(30000L, instance.getResult("80000 % 50000", testData))
            Assert.assertEquals(300000L, instance.getResult("800000 % 500000", testData))
            Assert.assertEquals(3000000L, instance.getResult("8000000 % 5000000", testData))
            Assert.assertEquals(30000000L, instance.getResult("80000000 % 50000000", testData))
            Assert.assertEquals(300000000L, instance.getResult("800000000 % 500000000", testData))
            Assert.assertEquals(
                3000000000L,
                instance.getResult("8000000000 % 5000000000", testData)
            )
            Assert.assertEquals(
                30000000000L,
                instance.getResult("80000000000 % 50000000000", testData)
            )
            Assert.assertEquals(
                300000000000L,
                instance.getResult("800000000000 % 500000000000", testData)
            )
            Assert.assertEquals(
                3000000000000L,
                instance.getResult("8000000000000 % 5000000000000", testData)
            )
            Assert.assertEquals(
                30000000000000L,
                instance.getResult("80000000000000 % 50000000000000", testData)
            )
            Assert.assertEquals(
                300000000000000L,
                instance.getResult("800000000000000 % 500000000000000", testData)
            )
            Assert.assertEquals(
                3000000000000000L,
                instance.getResult("8000000000000000 % 5000000000000000", testData)
            )
            Assert.assertEquals(10000L, instance.getResult("10000", testData))
            Assert.assertEquals(10f, instance.getResult("10.000", testData))
            Assert.assertEquals(100f, instance.getResult("100.00", testData))
            Assert.assertEquals(1000f, instance.getResult("1000.0", testData))
            Assert.assertEquals(1001f, instance.getResult("1001.0", testData))
            Assert.assertEquals(1111L, instance.getResult("1111", testData))
            Assert.assertEquals(11110L, instance.getResult("11110", testData))
            Assert.assertEquals(20000L, instance.getResult("10000+10000", testData))
            Assert.assertEquals(
                26L,
                instance.getResult("1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1+1", testData)
            )
        }
    }

    @Test
    fun function_test() {

        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(null, instance.getResult("env('testWrong')", null))
            Assert.assertEquals(
                2L,
                instance.getResult("env('testWrong') > env('testWrong') ? 1 : 2", null)
            )
            Assert.assertEquals(false, instance.getResult("env('isiOS')", null))
            Assert.assertEquals(true, instance.getResult("env('isAndroid')", null))
            Assert.assertEquals(4f, instance.getResult("size('1234')", testData))
            Assert.assertEquals(0f, instance.getResult("size(\$data.map)", testData))
            Assert.assertEquals(0f, instance.getResult("size(\$data.array)", testData))
            Assert.assertEquals(3f, instance.getResult("size(\$\$)", testData))
            Assert.assertEquals(5f, instance.getResult("size('1234')+1", testData))
        }
    }

    @Test
    fun map_or_array() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(true, instance.getResult("\$data.map", testData) is JSONObject)
            Assert.assertEquals(true, instance.getResult("\$data.array", testData) is JSONArray)
        }
    }

    //
    @Test
    fun mod_calculate() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(0L, instance.getResult("2%2", testData))
            Assert.assertEquals(0L, instance.getResult("3%-1", testData))
            Assert.assertEquals(1L, instance.getResult("3%2", testData))
        }
    }

    @Test
    fun add_calculate() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(10f, instance.getResult("\$eight+2", testData))
            Assert.assertEquals(16f, instance.getResult("\$eight+\$eight", testData))
            Assert.assertEquals(10L, instance.getResult("1+2+3+4", testData))
            Assert.assertEquals(1L, instance.getResult("0+1", testData))
            Assert.assertEquals("abcd", instance.getResult("'ab'+'cd'", testData))
            Assert.assertEquals("1231", instance.getResult("'123' + 1", testData))
            Assert.assertEquals("1231", instance.getResult("'1' + 231", testData))
            Assert.assertEquals("1231.000", instance.getResult("'123' + 1.000", testData))
            Assert.assertEquals("1231.001", instance.getResult("'123' + 1.001", testData))
            Assert.assertEquals("1231.001000", instance.getResult("'123' + 1.001000", testData))
            Assert.assertEquals(
                "123.001.001000",
                instance.getResult("'123.00' + 1.001000", testData)
            )
            Assert.assertEquals("1230.000", instance.getResult("'123' + 0.000", testData))
            Assert.assertEquals("1230.001", instance.getResult("'123' + 0.001", testData))
            Assert.assertEquals("123.0001", instance.getResult("123.000 + '1'", testData))
            Assert.assertEquals("123.0011", instance.getResult("123.001 + '1'", testData))
            Assert.assertEquals("123.0011.0", instance.getResult("123.001 + '1.0'", testData))
        }
    }

    @Test
    fun subtract_calculate() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(3L, instance.getResult("4-1", testData))
            Assert.assertEquals(-1L, instance.getResult("1-2", testData))
            Assert.assertEquals(-1L, instance.getResult("0-1", testData))
            Assert.assertEquals(-2f, instance.getResult("\$eight-10", testData))
            Assert.assertEquals(2f, instance.getResult("10-\$eight", testData))
            Assert.assertEquals(0f, instance.getResult("8-\$eight", testData))
            Assert.assertEquals(-8f, instance.getResult("0-\$eight", testData))
            Assert.assertEquals(0f, instance.getResult("\$eight-\$eight", testData))
        }
    }

    @Test
    fun multiply_calculate() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(0L, instance.getResult("0*2", testData))
            Assert.assertEquals(-2L, instance.getResult("-1*2", testData))
            Assert.assertEquals(-2L, instance.getResult("2*-1", testData))
            Assert.assertEquals(-1L, instance.getResult("+1*-1", testData))
            Assert.assertEquals(2L, instance.getResult("2*+1", testData))
            Assert.assertEquals(4.4f, instance.getResult("2.2*2", testData))
            Assert.assertEquals(-4.4f, instance.getResult("-2.2*2", testData))
            Assert.assertEquals(-8f, instance.getResult("\$eight*-1", testData))
            Assert.assertEquals(64f, instance.getResult("\$eight*\$eight", testData))
            Assert.assertEquals(16f, instance.getResult("\$eight*2", testData))
            Assert.assertEquals(1L, instance.getResult("1*1", testData))
            Assert.assertEquals(true, instance.getResult("(1+1)>1 ? 1>0 : 2<3", testData))
            Assert.assertEquals(true, instance.getResult("5%3 == 2", testData))
            Assert.assertEquals(true, instance.getResult("0-2==-2", testData))
            Assert.assertEquals(true, instance.getResult("1.0/2 == 0.5", testData))

            Assert.assertEquals(110.2F, instance.getResult("10.2 + 100", testData))
            Assert.assertEquals("110.2", instance.getResult("10.2 + 100 + ''", testData))
            Assert.assertEquals("10.21", instance.getResult("10.21 + ''", testData))
            Assert.assertEquals("10.212", instance.getResult("10.21 + '2'", testData))
            Assert.assertEquals("9.2", instance.getResult("10.2-1 + ''", testData))
            Assert.assertEquals("20.22", instance.getResult("10.22+10 + ''", testData))
            Assert.assertEquals("20.2123456", instance.getResult("10.2123456+10.0 + ''", testData))
            Assert.assertEquals(20.2123456F, instance.getResult("10.2123456+10.0", testData))
            Assert.assertEquals("20.2", instance.getResult("10.2+10 + ''", testData))
            Assert.assertEquals("20.4", instance.getResult("10.2*2 + ''", testData))
            Assert.assertEquals("5.1", instance.getResult("10.2/2 + ''", testData))
            Assert.assertEquals("3.1", instance.getResult("10.9%3.9 + ''", testData))
            Assert.assertEquals("0.5", instance.getResult("2.5%2 + ''", testData))
            Assert.assertEquals("21.22", instance.getResult("10.22+10+1.0+1.1-1.1 + ''", testData))
            Assert.assertEquals(22.32F, instance.getResult("10.22+10+1.0+1.1", testData))
            Assert.assertEquals(10.1F, instance.getResult("20.2-10.1", testData))
            Assert.assertEquals(5.5F, instance.getResult("15.5%10", testData))
            Assert.assertEquals(6.25F, instance.getResult("2.50*2.50", testData))
            Assert.assertEquals(5.0625F, instance.getResult("2.2500*2.2500", testData))
            Assert.assertEquals("5.96959188", instance.getResult("4.96959188+1 + ''", testData))
            Assert.assertEquals(4.938262F, instance.getResult("2.222220*2.222220", testData))
            Assert.assertEquals("4.969592", instance.getResult("2.2220*2.236540 + '' ", testData))
            Assert.assertEquals("22.32", instance.getResult("10.22+10+1.0+1.1 + ''", testData))
            Assert.assertEquals("1.0000000", instance.getResult("1.0000000 + '' ", testData))
            Assert.assertEquals("2.0000000", instance.getResult("1.0000000 + 1.0000000 + '' ", testData))
            Assert.assertEquals("1.0000000", instance.getResult("1.0000000 * 1.0000000 + '' ", testData))
            //加法
            Assert.assertEquals(20.3F, instance.getResult("10.1+ 10.2", testData))
            Assert.assertEquals(20.03F, instance.getResult("10.01+ 10.02", testData))
            Assert.assertEquals(20.003F, instance.getResult("10.001+ 10.002", testData))
            Assert.assertEquals(20.0003F, instance.getResult("10.0001+ 10.0002", testData))
            Assert.assertEquals(20.00003F, instance.getResult("10.00001+ 10.00002", testData))
            Assert.assertEquals(20.000003F, instance.getResult("10.000001+ 10.000002", testData))
            Assert.assertEquals(20.0000003F, instance.getResult("10.0000001+ 10.0000002", testData))
            Assert.assertEquals(20.00000003F, instance.getResult("10.00000001+ 10.00000002", testData))
            Assert.assertEquals(20.000000003F, instance.getResult("10.000000001+ 10.000000002", testData))
            Assert.assertEquals(20.0000000003F, instance.getResult("10.0000000001+ 10.0000000002", testData))
            Assert.assertEquals(20.00000000003F, instance.getResult("10.00000000001+ 10.00000000002", testData))
            Assert.assertEquals(20.000000000003F, instance.getResult("10.000000000001+ 10.000000000002", testData))
            Assert.assertEquals("20.3", instance.getResult("10.1+ 10.2 + ''", testData))
            Assert.assertEquals("20.03", instance.getResult("10.01+ 10.02  + ''", testData))
            Assert.assertEquals("20.003", instance.getResult("10.001+ 10.002  + ''", testData))
            Assert.assertEquals("20.0003", instance.getResult("10.0001+ 10.0002  + ''", testData))
            Assert.assertEquals("20.00003", instance.getResult("10.00001+ 10.00002  + ''", testData))
            Assert.assertEquals("20.000003", instance.getResult("10.000001+ 10.000002  + ''", testData))
            Assert.assertEquals("20.0000003", instance.getResult("10.0000001+ 10.0000002  + ''", testData))
            Assert.assertEquals("20.00000003", instance.getResult("10.00000001+ 10.00000002  + ''", testData))
            Assert.assertEquals("20.000000003", instance.getResult("10.000000001+ 10.000000002  + ''", testData))
            Assert.assertEquals("20.0000000003", instance.getResult("10.0000000001+ 10.0000000002  + ''", testData))
            Assert.assertEquals("20.00000000003", instance.getResult("10.00000000001+ 10.00000000002  + ''", testData))
            Assert.assertEquals("20.000000000003", instance.getResult("10.000000000001+ 10.000000000002  + ''", testData))
            //减法
            Assert.assertEquals(20.3F, instance.getResult("30.5- 10.2", testData))
            Assert.assertEquals(20.03F, instance.getResult("30.05- 10.02", testData))
            Assert.assertEquals(20.003F, instance.getResult("30.005- 10.002", testData))
            Assert.assertEquals(20.0003F, instance.getResult("30.0005- 10.0002", testData))
            Assert.assertEquals(20.00003F, instance.getResult("30.00005- 10.00002", testData))
            Assert.assertEquals(20.000003F, instance.getResult("30.000005- 10.000002", testData))
            Assert.assertEquals(20.0000003F, instance.getResult("30.0000005- 10.0000002", testData))
            Assert.assertEquals(20.00000003F, instance.getResult("30.00000005- 10.00000002", testData))
            Assert.assertEquals(20.000000003F, instance.getResult("30.000000005- 10.000000002", testData))
            Assert.assertEquals(20.0000000003F, instance.getResult("30.0000000005- 10.0000000002", testData))
            Assert.assertEquals(20.00000000003F, instance.getResult("30.00000000005- 10.00000000002", testData))
            Assert.assertEquals(20.000000000003F, instance.getResult("30.000000000005- 10.000000000002", testData))
            Assert.assertEquals("20.3", instance.getResult("30.5- 10.2 + ''", testData))
            Assert.assertEquals("20.03", instance.getResult("30.05- 10.02  + ''", testData))
            Assert.assertEquals("20.003", instance.getResult("30.005- 10.002  + ''", testData))
            Assert.assertEquals("20.0003", instance.getResult("30.0005- 10.0002  + ''", testData))
            Assert.assertEquals("20.00003", instance.getResult("30.00005- 10.00002  + ''", testData))
            Assert.assertEquals("20.000003", instance.getResult("30.000005- 10.000002  + ''", testData))
            Assert.assertEquals("20.0000003", instance.getResult("30.0000005- 10.0000002  + ''", testData))
            Assert.assertEquals("20.00000003", instance.getResult("30.00000005- 10.00000002  + ''", testData))
            Assert.assertEquals("20.000000003", instance.getResult("30.000000005- 10.000000002  + ''", testData))
            Assert.assertEquals("20.0000000003", instance.getResult("30.0000000005- 10.0000000002  + ''", testData))
            Assert.assertEquals("20.00000000003", instance.getResult("30.00000000005- 10.00000000002  + ''", testData))
            Assert.assertEquals("20.000000000003", instance.getResult("30.000000000005- 10.000000000002  + ''", testData))
            //乘法
            Assert.assertEquals(40.3F, instance.getResult("40.3* 1.0", testData))
            Assert.assertEquals(40.03F, instance.getResult("40.03* 1.00", testData))
            Assert.assertEquals(40.003F, instance.getResult("40.003* 1.000", testData))
            Assert.assertEquals(40.0003F, instance.getResult("40.0003* 1.0000", testData))
            Assert.assertEquals(40.00003F, instance.getResult("40.00003* 1.00000", testData))
            Assert.assertEquals(40.000003F, instance.getResult("40.000003* 1.000000", testData))
            Assert.assertEquals(40.0000003F, instance.getResult("40.0000003* 1.0000000", testData))
            Assert.assertEquals(40.00000003F, instance.getResult("40.00000003* 1.00000000", testData))
            Assert.assertEquals(40.000000003F, instance.getResult("40.000000003* 1.000000000", testData))
            Assert.assertEquals(40.0000000003F, instance.getResult("40.0000000003* 1.0000000000", testData))
            Assert.assertEquals(40.00000000003F, instance.getResult("40.00000000003* 1.00000000000", testData))
            Assert.assertEquals(40.000000000003F, instance.getResult("40.000000000003* 1.000000000000", testData))
            Assert.assertEquals("40.3", instance.getResult("40.3* 1.0 + ''", testData))
            Assert.assertEquals("40.03", instance.getResult("40.03* 1.00  + ''", testData))
            Assert.assertEquals("40.003", instance.getResult("40.003* 1.000  + ''", testData))
            Assert.assertEquals("40.0003", instance.getResult("40.0003* 1.0000  + ''", testData))
            Assert.assertEquals("40.00003", instance.getResult("40.00003* 1.00000  + ''", testData))
            Assert.assertEquals("40.000003", instance.getResult("40.000003* 1.000000  + ''", testData))
            Assert.assertEquals("40.0000003", instance.getResult("40.0000003* 1.0000000  + ''", testData))
            Assert.assertEquals("40.00000003", instance.getResult("40.00000003* 1.00000000  + ''", testData))
            Assert.assertEquals("40.000000003", instance.getResult("40.000000003* 1.000000000  + ''", testData))
            Assert.assertEquals("40.0000000003", instance.getResult("40.0000000003* 1.0000000000  + ''", testData))
            Assert.assertEquals("40.00000000003", instance.getResult("40.00000000003* 1.00000000000  + ''", testData))
            Assert.assertEquals("40.000000000003", instance.getResult("40.000000000003* 1.000000000000  + ''", testData))

            Assert.assertEquals(20.3F, instance.getResult("20.3/ 1.0", testData))
            Assert.assertEquals(20.03F, instance.getResult("20.03/ 1.00", testData))
            Assert.assertEquals(20.003F, instance.getResult("20.003/ 1.000", testData))
            Assert.assertEquals(20.0003F, instance.getResult("20.0003/ 1.0000", testData))
            Assert.assertEquals(20.00003F, instance.getResult("20.00003/ 1.00000", testData))
            Assert.assertEquals(20.000003F, instance.getResult("20.000003/ 1.000000", testData))
            Assert.assertEquals(20.0000003F, instance.getResult("20.0000003/ 1.0000000", testData))
            Assert.assertEquals(20.00000003F, instance.getResult("20.00000003/ 1.00000000", testData))
            Assert.assertEquals(20.000000003F, instance.getResult("20.000000003/ 1.000000000", testData))
            Assert.assertEquals(20.0000000003F, instance.getResult("20.0000000003/ 1.0000000000", testData))
            Assert.assertEquals(20.00000000003F, instance.getResult("20.00000000003/ 1.00000000000", testData))
            Assert.assertEquals(20.000000000003F, instance.getResult("20.000000000003/ 1.000000000000", testData))
            Assert.assertEquals("20.3", instance.getResult("20.3/ 1.0 + ''", testData))
            Assert.assertEquals("20.03", instance.getResult("20.03/ 1.00  + ''", testData))
            Assert.assertEquals("20.003", instance.getResult("20.003/ 1.000  + ''", testData))
            Assert.assertEquals("20.0003", instance.getResult("20.0003/ 1.0000  + ''", testData))
            Assert.assertEquals("20.00003", instance.getResult("20.00003/ 1.00000  + ''", testData))
            Assert.assertEquals("20.000003", instance.getResult("20.000003/ 1.000000  + ''", testData))
            Assert.assertEquals("20.0000003", instance.getResult("20.0000003/ 1.0000000  + ''", testData))
            Assert.assertEquals("20.00000003", instance.getResult("20.00000003/ 1.00000000  + ''", testData))
            Assert.assertEquals("20.000000003", instance.getResult("20.000000003/ 1.000000000  + ''", testData))
            Assert.assertEquals("20.0000000003", instance.getResult("20.0000000003/ 1.0000000000  + ''", testData))
            Assert.assertEquals("20.00000000003", instance.getResult("20.00000000003/ 1.00000000000  + ''", testData))
            Assert.assertEquals("20.000000000003", instance.getResult("20.000000000003/ 1.000000000000  + ''", testData))
            //除法运算 保留小数测试
            Assert.assertEquals(5.084746F, instance.getResult("12.000000/ 2.36", testData))
            Assert.assertEquals("5.084746", instance.getResult("12.000000/ 2.36 + ''", testData))
            Assert.assertEquals(5.0847458F, instance.getResult("12.0000000/ 2.36", testData))
            Assert.assertEquals("5.0847458", instance.getResult("12.0000000/ 2.36 + ''", testData))
            Assert.assertEquals(5.08475F, instance.getResult("12.00000/ 2.36", testData))
            Assert.assertEquals("5.08475", instance.getResult("12.00000/ 2.36 + ''", testData))
            Assert.assertEquals("5.084746", instance.getResult("12.000000/ 2.36  + ''", testData))

            //取模运算
            Assert.assertEquals(2.3F, instance.getResult("62.3% 60.0", testData))
            Assert.assertEquals(2.03F, instance.getResult("62.03% 60.00", testData))
            Assert.assertEquals(2.003F, instance.getResult("62.003% 60.000", testData))
            Assert.assertEquals(2.0003F, instance.getResult("62.0003% 60.0000", testData))
            Assert.assertEquals(2.00003F, instance.getResult("62.00003% 60.00000", testData))
            Assert.assertEquals(2.000003F, instance.getResult("62.000003% 60.000000", testData))
            Assert.assertEquals(2.0000003F, instance.getResult("62.0000003% 60.0000000", testData))
            Assert.assertEquals(2.00000003F, instance.getResult("62.00000003% 60.00000000", testData))
            Assert.assertEquals(2.000000003F, instance.getResult("62.000000003% 60.000000000", testData))
            Assert.assertEquals(2.0000000003F, instance.getResult("62.0000000003% 60.0000000000", testData))
            Assert.assertEquals(2.00000000003F, instance.getResult("62.00000000003% 60.00000000000", testData))
            Assert.assertEquals(2.000000000003F, instance.getResult("62.000000000003% 60.000000000000", testData))
            Assert.assertEquals("2.3", instance.getResult("62.3% 60.0 + ''", testData))
            Assert.assertEquals("2.03", instance.getResult("62.03% 60.00  + ''", testData))
            Assert.assertEquals("2.003", instance.getResult("62.003% 60.000  + ''", testData))
            Assert.assertEquals("2.0003", instance.getResult("62.0003% 60.0000  + ''", testData))
            Assert.assertEquals("2.00003", instance.getResult("62.00003% 60.00000  + ''", testData))
            Assert.assertEquals("2.000003", instance.getResult("62.000003% 60.000000  + ''", testData))
            Assert.assertEquals("2.0000003", instance.getResult("62.0000003% 60.0000000  + ''", testData))
            Assert.assertEquals("2.00000003", instance.getResult("62.00000003% 60.00000000  + ''", testData))
            Assert.assertEquals("2.000000003", instance.getResult("62.000000003% 60.000000000  + ''", testData))
            Assert.assertEquals("2.0000000003", instance.getResult("62.0000000003% 60.0000000000  + ''", testData))
            Assert.assertEquals("2.00000000003", instance.getResult("62.00000000003% 60.00000000000  + ''", testData))
            Assert.assertEquals("2.000000000003", instance.getResult("62.000000000003% 60.000000000000  + ''", testData))

            Assert.assertEquals("22.32", instance.getResult("10.22+10+1.0+1.1 + ''", testData))
            Assert.assertEquals("22.32", instance.getResult("10.22+10+1.0+1.1 + ''", testData))

        }

    }

    @Test
    fun divide_calculate() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(-0.5f, instance.getResult("-1.0/2.0", testData))
            Assert.assertEquals(0L, instance.getResult("-1/2", testData))
            Assert.assertEquals(0.5f, instance.getResult("1.0/2", testData))
            Assert.assertEquals(0L, instance.getResult("1/2", testData))
            Assert.assertEquals(1L, instance.getResult("1/1", testData))
            Assert.assertEquals(1L, instance.getResult("-1/-1", testData))
            Assert.assertEquals(-8f, instance.getResult("\$eight/-1", testData))
            Assert.assertEquals(1f, instance.getResult("\$eight/\$eight", testData))
        }
    }

    @Test
    fun multi_calculate() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(8L, instance.getResult("1+1*2+3+4/2", testData))
            Assert.assertEquals(-1L, instance.getResult("(1+1-3)", testData))
            Assert.assertEquals(-2L, instance.getResult("(1+1)-2*2", testData))
            Assert.assertEquals(-4L, instance.getResult("(1-3)*2", testData))
            Assert.assertEquals(4L, instance.getResult("1+1*3", testData))
            Assert.assertEquals(4L, instance.getResult("1+2*3/2", testData))
            Assert.assertEquals(4L, instance.getResult("(1+1)*2", testData))
            Assert.assertEquals(2L, instance.getResult("(1+3)/2", testData))
        }
    }

    @Test
    fun single_op_expression() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(1L, instance.getResult("+1", testData))
            Assert.assertEquals(-1L, instance.getResult("-1", testData))
            Assert.assertEquals(true, instance.getResult("!false", testData))
            Assert.assertEquals(false, instance.getResult("!true", testData))
        }
    }

    @Test
    fun not_equal() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(false, instance.getResult("null != null", testData))
            Assert.assertEquals(true, instance.getResult("1 != null", testData))
            Assert.assertEquals(true, instance.getResult("'string'!='s2tring'", testData))
            Assert.assertEquals(false, instance.getResult("1 != 1.0", testData))
            Assert.assertEquals(true, instance.getResult("1!=1.1", testData))
            Assert.assertEquals(true, instance.getResult("true != 'false'", testData))
            Assert.assertEquals(false, instance.getResult("true != 'true'", testData))
        }
    }

    @Test
    fun equal() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(false, instance.getResult("true == false", testData))
            Assert.assertEquals(true, instance.getResult("null == null", testData))
            Assert.assertEquals(true, instance.getResult("\$eight == 8", testData))
            Assert.assertEquals(false, instance.getResult("1==2", testData))
            Assert.assertEquals(true, instance.getResult("1==1.0", testData))
            Assert.assertEquals(false, instance.getResult("'string'==1.0", testData))
            Assert.assertEquals(true, instance.getResult("'string'=='string'", testData))
            Assert.assertEquals(true, instance.getResult("1+1 == 2", testData))
            Assert.assertEquals(true, instance.getResult("true == 'true'", testData))
            Assert.assertEquals(false, instance.getResult("3 == 2", testData))
            Assert.assertEquals(false, instance.getResult("'string' == 2", testData))
        }
    }


    @Test
    fun greater_than() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(true, instance.getResult("3 > 2", testData))
            Assert.assertEquals(false, instance.getResult("'string' > 2", testData))
            Assert.assertEquals(false, instance.getResult("1+1 > 2", testData))
            Assert.assertEquals(true, instance.getResult("1+1 >= 2", testData))
            Assert.assertEquals(false, instance.getResult("'string' >= 2", testData))
        }
    }

    @Test
    fun less_than() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(false, instance.getResult("'string' < 2", testData))
            Assert.assertEquals(false, instance.getResult("3 < 2", testData))
            Assert.assertEquals(true, instance.getResult("2 < 3", testData))
            Assert.assertEquals(false, instance.getResult("'string' <= 2", testData))
            Assert.assertEquals(false, instance.getResult("1+1 < 2", testData))
        }
    }

    @Test
    fun double_or() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(true, instance.getResult("true || true", testData))
            Assert.assertEquals(false, instance.getResult("false || false", testData))
            Assert.assertEquals(true, instance.getResult("true || false", testData))
        }
    }

    @Test
    fun double_and() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(false, instance.getResult("true && false", testData))
            Assert.assertEquals(true, instance.getResult("true && true", testData))
            Assert.assertEquals(false, instance.getResult("false && false", testData))
        }
    }

    @Test
    fun ternary() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }
        repeat(10) {
            Assert.assertEquals(
                "flex",
                instance.getResult("\$data.map ? 'flex' : 'none'", testData)
            )
            Assert.assertEquals("none", instance.getResult("\$data.map ? 'flex' : 'none'", null))
            Assert.assertEquals(null, instance.getResult("false ? \$data : null", testData))
            Assert.assertEquals(0L, instance.getResult("false ? 1 : 0", testData))
            Assert.assertEquals(1L, instance.getResult("true ? 1 : 0", testData))
            Assert.assertEquals(1L, instance.getResult("true ? 1 : 0", testData))
            Assert.assertEquals(true, instance.getResult("true ?: 1", testData))
            Assert.assertEquals(1L, instance.getResult("false ?: 1", testData))
            Assert.assertEquals(true, instance.getResult("true ?: \$data", testData))
            Assert.assertEquals("123", instance.getResult("true ? '123' : '456'", testData))
            Assert.assertEquals("123", instance.getResult("'true' ? '123' : '456'", testData))
            Assert.assertEquals(null, instance.getResult("true ? null : \$data", testData))
            Assert.assertEquals(1L, instance.getResult("false ? \$\$ : 1", testData))
            Assert.assertEquals(testData, instance.getResult("true ? \$\$ : 1", testData))
            Assert.assertEquals(testData, instance.getResult("\$\$ ?: 0", testData))
            Assert.assertEquals(2L, instance.getResult("\$data.true?1:2", testData))
            Assert.assertEquals(2L, instance.getResult("\$data.false?1:2", testData))
        }
    }

    @Test
    fun error_null_add_true() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("null + true", testData))
    }

    @Test
    fun error_true_add_1() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("true + 1", testData))
    }

    @Test
    fun error_1_add_abc() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("1 + abc", testData))
    }

    @Test
    fun error_abc_add_1() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("abc + 1", testData))
    }

    @Test
    fun error_abc_add_string_123() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("abc + '123'", testData))
    }

    @Test
    fun error_null_add_1() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("null + 1", testData))
    }

    @Test
    fun error_null_add_string_abc() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("null + 'abc'", testData))
    }

    @Test
    fun error_null_add_self() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("null + \$\$", testData))
    }

    @Test
    fun error_null_add_id() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("null + id", testData))
    }

    @Test
    fun error_true_add_string_123() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("true + '123'", testData))
    }

    @Test
    fun error_1_divide_0() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("1/0", testData))
    }

    @Test
    fun error_self_add_1() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("\$\$ + 1", testData))
    }

    @Test
    fun error_self_subtract_1() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("\$\$ - 1", testData))
    }

    @Test
    fun error_self_multiply_1() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("\$\$ * 1", testData))
    }

    @Test
    fun error_self_divide_1() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(null, instance.getResult("\$\$ / 1", testData))
    }

    @Test
    fun ternary_self_1_0() {
        val testData = JSONObject().apply {
            this["eight"] = 8F
            this["title"] = "gaiax"
            this["data"] = JSONObject().apply {
                this["map"] = JSONObject()
                this["array"] = JSONArray()
            }
        }

        Assert.assertEquals(1L, instance.getResult("\$\$ ? 1 : 0", testData))
    }
}