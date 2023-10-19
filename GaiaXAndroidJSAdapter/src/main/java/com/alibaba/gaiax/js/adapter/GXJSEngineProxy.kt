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
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.adapter.modules.GXJSBuildInModule
import com.alibaba.gaiax.js.adapter.modules.GXJSBuildInStorageModule
import com.alibaba.gaiax.js.adapter.modules.GXJSBuildInTipsModule
import com.alibaba.gaiax.js.adapter.modules.GXJSEventModule
import com.alibaba.gaiax.js.adapter.modules.GXJSLogModule
import com.alibaba.gaiax.js.adapter.modules.GXJSNativeEventModule
import com.alibaba.gaiax.js.adapter.modules.GXJSNativeTargetModule
import com.alibaba.gaiax.js.adapter.modules.GXJSNativeUtilModule
import com.alibaba.gaiax.js.impl.debug.ISocketBridgeListener
import com.alibaba.gaiax.js.utils.Log
import com.alibaba.gaiax.js.utils.TimeUtils
import com.alibaba.gaiax.template.GXTemplateInfo
import java.lang.ref.WeakReference

class GXJSEngineProxy {

    companion object {
        private const val TAG = "[GaiaX][JS]"

        val instance by lazy {
            return@lazy GXJSEngineProxy()
        }
    }

    /**
     * 注册Socket消息发送者
     */
    fun setSocketSender(iSocketSender: GXJSEngine.ISocketSender) {
        GXJSEngine.instance.setSocketSender(iSocketSender)
    }

    /**
     * 获取Socket通信桥，用于向Debug引擎中发送消息，经过处理后，最终还会发给Studio
     */
    fun getSocketBridge(): ISocketBridgeListener? {
        return GXJSEngine.instance.getSocketBridge()
    }

    fun init(context: Context) {

        // 初始化JS引擎
        GXJSEngine.instance.init(context)

        // 设置日志监听器
        GXJSEngine.instance.setLogListener(object : GXJSEngine.ILogListener {
            override fun errorLog(data: JSONObject) {
                if (Log.isLog()) {
                    Log.d("errorLog() called with: data = $data")
                }
            }
        })

        // 注册内置模块
        GXJSEngine.instance.registerModule(GXJSBuildInModule::class.java)
        GXJSEngine.instance.registerModule(GXJSBuildInStorageModule::class.java)
        GXJSEngine.instance.registerModule(GXJSBuildInTipsModule::class.java)
        GXJSEngine.instance.registerModule(GXJSEventModule::class.java)
        GXJSEngine.instance.registerModule(GXJSLogModule::class.java)
        GXJSEngine.instance.registerModule(GXJSNativeEventModule::class.java)
        GXJSEngine.instance.registerModule(GXJSNativeTargetModule::class.java)
        GXJSEngine.instance.registerModule(GXJSNativeUtilModule::class.java)

        // 注册GaiaX扩展JS事件
        GXRegisterCenter.instance.registerExtensionNodeEvent(GXExtensionNodeEvent())

    }

    fun startDefaultEngine() {
        if (Log.isLog()) {
            Log.d("startDefaultEngine() called")
        }
        GXJSEngine.instance.startDefaultEngine {
            if (Log.isLog()) {
                Log.d("startDefaultEngine() called completed")
            }
        }
    }

    fun stopDefaultEngine() {
        if (Log.isLog()) {
            Log.d("stopDefaultEngine() called")
        }
        GXJSEngine.instance.stopDefaultEngine()
    }

    fun startDebugEngine() {
        if (Log.isLog()) {
            Log.d("startDebugEngine() called")
        }
        GXJSEngine.instance.startDebugEngine {
            if (Log.isLog()) {
                Log.d("startDebugEngine() called completed")
            }
        }
    }

    fun stopDebugEngine() {
        if (Log.isLog()) {
            Log.d("stopDebugEngine() called")
        }
        GXJSEngine.instance.stopDebugEngine()
    }


    fun onReady(gxView: View?) {
        if (Log.isLog()) {
            Log.d("onReady() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let {
            it.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.onReady(jsComponentId)
            }
        }
    }

    fun onReuse(gxView: View) {
        if (Log.isLog()) {
            Log.d("onReuse() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let {
            it.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.onReuse(jsComponentId)
            }
        }
    }

    fun onShow(gxView: View?) {
        if (Log.isLog()) {
            Log.d("onShow() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let {
            it.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.onShow(jsComponentId)
            }
        }
    }

    fun onHide(gxView: View?) {
        if (Log.isLog()) {
            Log.d("onHide() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let {
            it.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.onHide(jsComponentId)
            }
        }
    }

    fun onDestroy(gxView: View?) {
        if (Log.isLog()) {
            Log.d("onDestroy() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let {
            it.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.onDestroy(jsComponentId)
            }
        }
    }

    fun onLoadMore(gxView: View, data: JSONObject) {
        if (Log.isLog()) {
            Log.d("onLoadMore() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let {
            it.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.onLoadMore(jsComponentId, data)
            }
        }
    }

    fun registerComponent(gxView: View?) {
        if (Log.isLog()) {
            Log.d("registerComponent() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let { gxTemplateContext ->
            if (gxTemplateContext.jsComponentIds == null) {
                gxTemplateContext.jsComponentIds = mutableListOf()
            }
            registerTemplateTree(gxTemplateContext, gxTemplateContext.templateInfo)

            // 默认使用第一个组件ID作为和Context的映射关系
            gxTemplateContext.jsComponentIds?.forEach { jsComponentId ->
                GXJSRenderProxy.instance.jsComponentMap[jsComponentId] = WeakReference(gxView)
            }
        }
    }

    fun unregisterComponent(gxView: View?) {
        if (Log.isLog()) {
            Log.d("unregisterComponent() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let { gxTemplateContext ->
            gxTemplateContext.jsComponentIds?.forEach { jsComponentId ->
                GXJSRenderProxy.instance.jsComponentMap.remove(jsComponentId)
            }
            gxTemplateContext.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.unregisterComponent(jsComponentId)
            }
            gxTemplateContext.jsComponentIds?.clear()
        }
    }

    private fun registerTemplateTree(
        gxTemplateContext: GXTemplateContext, templateInfo: GXTemplateInfo
    ) {
        val templateBiz = templateInfo.template.biz
        val templateId = templateInfo.template.id
        val templateVersion = templateInfo.template.version.toString()
        val script = templateInfo.js
        val layer = templateInfo.layer

        // 仅当脚本不为空时才注册
        if (script?.isNotEmpty() == true) {
            val jsComponentId = GXJSEngine.instance.registerComponent(
                templateBiz, templateId, templateVersion, script
            )
            gxTemplateContext.jsComponentIds?.add(jsComponentId)
        }

        // 仅有当前模板不是容器模板时，但又是由子模板组成的时候，子模板也可以有JS代码，所以需要向下递归注册
        val children = templateInfo.children
        if (!layer.isContainerType() && !children.isNullOrEmpty()) {
            for (gxTemplateInfo in children) {
                registerTemplateTree(gxTemplateContext, gxTemplateInfo)
            }
        }
    }

    fun onEvent(componentId: Long, type: String, data: JSONObject) {
        GXJSEngine.instance.onEvent(componentId, type, data)
    }

    fun onNativeEvent(componentId: Long, data: JSONObject) {
        GXJSEngine.instance.onNativeEvent(componentId, data)
    }

    /**
     * 分发原生消息给JS
     */
    fun dispatchNativeEvent(data: JSONObject) {
        GXJSRenderProxy.instance.nativeEvents.forEach { componentData ->
            val componentId = componentData.getLongValue("instanceId")
            val result = JSONObject().apply {
                this.putAll(data)
                this.putAll(componentData)
                this["timestamp"] = TimeUtils.elapsedRealtime()
            }
            onNativeEvent(componentId, result)
        }
    }
}