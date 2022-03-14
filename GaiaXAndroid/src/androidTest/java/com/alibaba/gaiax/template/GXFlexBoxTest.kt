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

import app.visly.stretch.Dimension
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GXFlexBoxTest {

    @Before
    fun setUp() {
    }

    @Test
    fun size() {
        val flexBox = GXFlexBox.create(JSONObject().apply {
            this[GXTemplateKey.FLEXBOX_SIZE_WIDTH] = "100%"
            this[GXTemplateKey.FLEXBOX_SIZE_HEIGHT] = "100px"
        })
        Assert.assertEquals(true, flexBox.size?.width is Dimension.Percent)
        Assert.assertEquals(1F, flexBox.size?.width?.value)

        Assert.assertEquals(true, flexBox.size?.height is Dimension.Points)
        Assert.assertEquals(100F.dpToPx(), flexBox.size?.height?.value)
    }

    @After
    fun tearDown() {
    }
}