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
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.adapter.impl.GXJSRenderDelegate
import com.alibaba.gaiax.js.adapter.impl.render.GXExtensionNodeEvent
import com.alibaba.gaiax.studio.GXClientToStudioMultiType

@Keep
class GXJSAdapter : GXJSEngine.IAdapter {

    @SuppressLint("InflateParams")
    override fun init(context: Context) {
        GXRegisterCenter.instance.registerExtensionNodeEvent(GXExtensionNodeEvent())
        GXJSEngine.instance.setSocketProxy(object : GXJSEngine.ISocketProxy {
            override fun sendMessage(data: JSONObject) {
                GXClientToStudioMultiType.instance.sendMessage(data)
            }
        })
        GXClientToStudioMultiType.instance.setSocketReceiverListener(object :
            GXClientToStudioMultiType.GXSocketJSReceiveListener {
            override fun onCallSyncFromStudioWorker(socketId: Int, params: JSONObject) {
                GXJSEngine.instance.getSocketCallBridge()?.callSync(socketId, params)
            }

            override fun onCallAsyncFromStudioWorker(socketId: Int, params: JSONObject) {
                GXJSEngine.instance.getSocketCallBridge()?.callAsync(socketId, params)
            }

            override fun onCallPromiseFromStudioWorker(socketId: Int, params: JSONObject) {
                GXJSEngine.instance.getSocketCallBridge()?.callPromise(socketId, params)
            }

            override fun onCallGetLibraryFromStudioWorker(socketId: Int, methodName: String) {
                GXJSEngine.instance.getSocketCallBridge()?.callGetLibrary(socketId, methodName)
            }

        })
        GXJSEngine.instance.initRenderDelegate(GXJSRenderDelegate())
    }
}