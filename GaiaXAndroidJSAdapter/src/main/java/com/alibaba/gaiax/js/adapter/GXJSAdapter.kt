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

@file:Suppress("DEPRECATION")

package com.alibaba.gaiax.js.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.Keep
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.js.GXJSEngineFactory
import com.alibaba.gaiax.js.adapter.impl.render.GXExtensionNodeEvent
import com.alibaba.gaiax.js.adapter.impl.GXJSRenderDelegate

@Keep
class GXJSAdapter : GXJSEngineFactory.GXJSIAdapter {

    @SuppressLint("InflateParams")
    override fun init(context: Context) {
        GXRegisterCenter.instance.registerExtensionNodeEvent(GXExtensionNodeEvent())

        GXJSEngineFactory.instance.initRenderDelegate(GXJSRenderDelegate())
    }
}