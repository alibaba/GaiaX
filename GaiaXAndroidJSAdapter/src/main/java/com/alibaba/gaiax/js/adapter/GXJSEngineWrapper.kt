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

package com.alibaba.gaiax.js.adapter

import android.content.Context
import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.adapter.modules.GXJSBuildInModule
import com.alibaba.gaiax.js.adapter.modules.GXJSBuildInTipsModule
import com.alibaba.gaiax.js.adapter.modules.GXJSNativeEventModule
import com.alibaba.gaiax.js.adapter.modules.GXJSNativeTargetModule
import com.alibaba.gaiax.studio.GXStudioClient

class GXJSEngineWrapper {

    companion object {
        private const val TAG = "[GaiaX][JS]"
    }

    fun init(context: Context) {

        // 初始化JS引擎
        GXJSEngine.instance.init(context)

        // 设置日志监听器
        GXJSEngine.instance.setLogListener(object : GXJSEngine.ILogListener {
            override fun errorLog(data: JSONObject) {
                Log.d(TAG, "errorLog() called with: data = $data")
            }
        })

        // 注册内置模块
        GXJSEngine.instance.registerModule(GXJSBuildInTipsModule::class.java)
        GXJSEngine.instance.registerModule(GXJSNativeTargetModule::class.java)
        GXJSEngine.instance.registerModule(GXJSBuildInModule::class.java)
        GXJSEngine.instance.registerModule(GXJSNativeEventModule::class.java)

        // 注册GaiaX扩展JS事件
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
    }
}