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
import com.alibaba.gaiax.studio.GXStudioClient

@Keep
class GXJSAdapter : GXJSEngine.IAdapter {

    @SuppressLint("InflateParams")
    override fun init(context: Context) {

        // 注册JS事件
        GXRegisterCenter.instance.registerExtensionNodeEvent(GXExtensionNodeEvent())

        // 注册Socket消息发送者
        GXJSEngine.instance.setSocketSender(object : GXJSEngine.ISocketSender {
            override fun onSendMsg(data: JSONObject) {
                GXStudioClient.instance.sendMsg(data)
            }
        })

        // 注册消息接受者
        GXStudioClient.instance.setSocketReceiver(object : GXStudioClient.ISocketReceiver {
            override fun onReceiveCallSync(socketId: Int, params: JSONObject) {
                GXJSEngine.instance.getSocketCallBridge()?.callSync(socketId, params)
            }

            override fun onReceiveCallAsync(socketId: Int, params: JSONObject) {
                GXJSEngine.instance.getSocketCallBridge()?.callAsync(socketId, params)
            }

            override fun onReceiveCallPromise(socketId: Int, params: JSONObject) {
                GXJSEngine.instance.getSocketCallBridge()?.callPromise(socketId, params)
            }

            override fun onReceiveCallGetLibrary(socketId: Int, methodName: String) {
                GXJSEngine.instance.getSocketCallBridge()?.callGetLibrary(socketId, methodName)
            }

        })

        GXJSEngine.instance.initRenderDelegate(GXJSRenderDelegate())
    }
}