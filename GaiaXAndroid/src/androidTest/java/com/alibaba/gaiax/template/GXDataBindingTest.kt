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

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.expression.GXExpression
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test

class GXDataBindingTest {

    @Test
    fun createMergeValueBinding_value() {

        val valueBinding1 = GXDataBinding.create(value = "\${data.title}", null, null, null, null)
        val valueBinding2 = GXDataBinding.create(value = "\${data.subtitle}", null, null, null, null)

        val valueBinding3 = GXDataBinding.createMergeDataBinding(valueBinding1, valueBinding2)

        Assert.assertEquals(true, valueBinding3 !== valueBinding1)
        Assert.assertEquals(true, valueBinding3 !== valueBinding2)

        Assert.assertEquals(true, valueBinding3?.value is GXExpression.GXValue)
        Assert.assertEquals("data.subtitle", (valueBinding3?.value as GXExpression.GXValue).value)
    }

    @Test
    fun createMergeValueBinding_placeholder() {

        val valueBinding1 = GXDataBinding.create(null, placeholder = "'res:uri'", null, null, null)
        val valueBinding2 = GXDataBinding.create(null, placeholder = "'res:url'", null, null, null)

        val valueBinding3 = GXDataBinding.createMergeDataBinding(valueBinding1, valueBinding2)

        Assert.assertEquals(true, valueBinding3 !== valueBinding1)
        Assert.assertEquals(true, valueBinding3 !== valueBinding2)

        Assert.assertEquals(true, valueBinding3?.placeholder is GXExpression.GXString)
        Assert.assertEquals("res:url", (valueBinding3?.placeholder as GXExpression.GXString).value)
    }

    @Test
    fun createMergeValueBinding_extend() {

        val valueBinding1 = GXMockUtils.createDataBindingWithExtend(JSONObject().apply {
            this[GXTemplateKey.FLEXBOX_SIZE_WIDTH] = "'10px'"
            this[GXTemplateKey.FLEXBOX_SIZE_HEIGHT] = "'10px'"
        })

        val valueBinding2 = GXMockUtils.createDataBindingWithExtend(JSONObject().apply {
            this[GXTemplateKey.FLEXBOX_SIZE_WIDTH] = "'100px'"
            this[GXTemplateKey.FLEXBOX_SIZE_HEIGHT] = "'100px'"
        })

        val valueBinding3 = GXDataBinding.createMergeDataBinding(valueBinding1, valueBinding2)

        Assert.assertEquals(true, valueBinding3 !== valueBinding1)
        Assert.assertEquals(true, valueBinding3 !== valueBinding2)

        Assert.assertEquals(true, valueBinding3?.extend?.get(GXTemplateKey.FLEXBOX_SIZE_WIDTH) is GXExpression.GXString)
        Assert.assertEquals("100px", (valueBinding3?.extend?.get(GXTemplateKey.FLEXBOX_SIZE_WIDTH) as GXExpression.GXString).value)

    }

    @Test
    fun createMergeValueBinding_null() {
        val valueBinding3 = GXDataBinding.createMergeDataBinding(null, null)
        Assert.assertEquals(null, valueBinding3)
    }

}