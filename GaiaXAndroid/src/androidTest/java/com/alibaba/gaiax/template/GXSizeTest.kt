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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.GXSize.Companion.ptToPx
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GXSizeTest {

    @Test
    fun create_undefine() {
        Assert.assertEquals(GXSize.Undefined, GXSize.create(""))
        Assert.assertEquals(0F, GXSize.create("").valueFloat)
    }

    @Test
    fun create_auto() {
        Assert.assertEquals(GXSize.Auto, GXSize.create("auto"))
        Assert.assertEquals(0F, GXSize.create("auto").valueFloat)
    }

    @Test
    fun create_px() {
        Assert.assertEquals(14.0F.dpToPx(), GXSize.create("14px").valueFloat)
    }

    @Test
    fun create_pt() {
        Assert.assertEquals(14.0F.ptToPx(), GXSize.create("14pt").valueFloat)
    }

    @Test
    fun create_pe() {
        Assert.assertEquals(1F, GXSize.create("100%").valueFloat)
        Assert.assertEquals(1F, GXSize.create("100%").valueDimension.value)
    }
}

