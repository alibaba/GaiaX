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

import android.graphics.Color
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GXColorTest {

    @Test
    fun parseColor_RGB() {
        Assert.assertEquals(Color.rgb(255, 0, 0), GXColor.create("rgb(255,0,0)")?.valueCanNull())
        Assert.assertEquals(null, GXColor.create("rbg(255,0,0)")?.valueCanNull())
        Assert.assertEquals(null, GXColor.create("")?.valueCanNull())
    }

    @Test
    fun parseColor_RGBA() {
        Assert.assertEquals(
            Color.argb((255 * 0.3).toInt(), 255, 0, 0),
            GXColor.create("rgba(255,0,0,0.3)")?.valueCanNull()
        )
        Assert.assertEquals(null, GXColor.create("")?.valueCanNull())
    }

    @Test
    fun parseColor_HEX() {
        Assert.assertEquals(Color.parseColor("#00FF00"), GXColor.create("#00FF00")?.valueCanNull())
        Assert.assertEquals(null, GXColor.create("FF00FF")?.valueCanNull())
        Assert.assertEquals(null, GXColor.create("")?.valueCanNull())
        Assert.assertEquals(
            Color.parseColor("#4C000000"),
            GXColor.create("#0000004C")?.valueCanNull()
        )
    }

    @Test
    fun parseColor_SIMPLE() {
        Assert.assertEquals(Color.RED, GXColor.create("RED")?.valueCanNull())
        Assert.assertEquals(Color.RED, GXColor.create("red")?.valueCanNull())
    }

}