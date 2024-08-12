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

package com.alibaba.gaiax.js.proxy

import android.content.Context
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.impl.debug.ISocketBridgeListener
import com.alibaba.gaiax.js.proxy.modules.GXJSBuildInModule
import com.alibaba.gaiax.js.proxy.modules.GXJSBuildInStorageModule
import com.alibaba.gaiax.js.proxy.modules.GXJSBuildInTipsModule
import com.alibaba.gaiax.js.proxy.modules.GXJSEventModule
import com.alibaba.gaiax.js.proxy.modules.GXJSLogModule
import com.alibaba.gaiax.js.proxy.modules.GXJSNativeEventModule
import com.alibaba.gaiax.js.proxy.modules.GXJSNativeTargetModule
import com.alibaba.gaiax.js.proxy.modules.GXJSNativeUtilModule
import com.alibaba.gaiax.js.utils.Log
import com.alibaba.gaiax.js.utils.TimeUtils
import com.alibaba.gaiax.render.utils.GXContainerUtils
import com.alibaba.gaiax.template.GXTemplateInfo

/**
 * JS引擎的代理增强类，封装了与GaiaX渲染库组合使用的一些常用方法。
 *
 * 如果外部接入方想要直接使用GXJSEngine，应该重新实现一份 GXJSEngineProxy 的功能。
 */
class GXJSEngineProxy {

    class GXTemplateContextExtArg {
        /**
         * 用于存储JS组件ID
         */
        var jsComponentIds: MutableSet<Long> = mutableSetOf()

        /**
         * 根节点JS组件ID
         */
        var rootJSComponentId: Long? = null
    }

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

    var jsExceptionListener: GXJSEngine.IJsExceptionListener? = null

    fun init(context: Context) {

        // 初始化JS引擎
        GXJSEngine.instance.init(context)

        // 设置日志监听器
        GXJSEngine.instance.setLogListener(object : GXJSEngine.ILogListener {
            override fun errorLog(data: JSONObject) {
                if (Log.isLog()) {
                    Log.d("errorLog() called with: data = $data")
                }
                GXJSEngine.instance.getSocketSender()?.let {
                    GXJSLogModule.sendJSLogMsg("error", data.toJSONString())
                }
            }
        })

        // 设置异常监听器
        GXJSEngine.instance.setJSExceptionListener(object : GXJSEngine.IJsExceptionListener {
            override fun exception(data: JSONObject) {
                if (Log.isLog()) {
                    Log.d("exception() called with: data = $data")
                }
                jsExceptionListener?.exception(data)
                GXJSEngine.instance.getSocketSender()?.let {
                    GXJSLogModule.sendJSLogMsg("error", data.toJSONString())
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

        // 注册GaiaX视图可见监听器
        GXRegisterCenter.instance.registerExtensionItemViewLifecycleListener(object :
            GXRegisterCenter.GXIItemViewLifecycleListener {
            override fun onCreate(gxView: View?) {
                if (Log.isLog()) {
                    Log.d("onCreate() called with: $gxView")
                }
                // 注册容器
                instance.registerComponentAndOnReady(gxView)
            }

            override fun onVisible(gxView: View?) {
                if (Log.isLog()) {
                    Log.d("onVisible() called with: $gxView")
                }
                instance.onShow(gxView)
            }

            override fun onInvisible(gxView: View?) {
                if (Log.isLog()) {
                    Log.d("onInvisible() called with: $gxView")
                }
                instance.onHide(gxView)
            }

            override fun onReuse(gxView: View?) {
                if (Log.isLog()) {
                    Log.d("onReuse() called with: $gxView")
                }
                // 执行生命周期变化
                instance.onReuse(gxView)
            }

            override fun onStart(gxView: View?, gxTemplateData: GXTemplateEngine.GXTemplateData) {
            }

            override fun onStarted(gxView: View?) {
            }

            override fun onDestroy(gxView: View?) {
                if (Log.isLog()) {
                    Log.d("onDestroy() called with: $gxView")
                }
                instance.onDestroy(gxView)
                instance.unregisterComponent(gxView)
            }

        })

    }

    fun registerModule(moduleClazz: Class<out GXJSBaseModule>) {
        GXJSEngine.instance.registerModule(moduleClazz)
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

    /**
     * 通知视图的JS组件可用
     */
    fun onReady(gxView: View?) {
        if (Log.isLog()) {
            Log.d("onReady() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let { gxTemplateContext ->
            gxExtJSArg(gxTemplateContext)?.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.onReady(jsComponentId)
            }
        }
    }

    /**
     * 通知视图的JS组件复用
     */
    fun onReuse(gxView: View?) {
        if (Log.isLog()) {
            Log.d("onReuse() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let { gxTemplateContext ->
            gxExtJSArg(gxTemplateContext)?.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.onReuse(jsComponentId)
            }
        }
    }

    /**
     * 通知视图的JS组件显示，如果视图是容器那么也通知其坑位JS组件显示
     */
    fun onShow(gxView: View?) {
        if (Log.isLog()) {
            Log.d("onShow() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let { gxTemplateContext ->

            // 通知JS组件显示
            gxExtJSArg(gxTemplateContext)?.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.onShow(jsComponentId)
            }

            // 遍历容器子视图，通知坑位销毁
            GXContainerUtils.notifyView(gxTemplateContext) { gxView: View ->
                onShow(gxView)
            }
        }
    }

    /**
     * 通知视图的JS组件隐藏，如果视图是容器那么也通知其坑位JS组件隐藏
     */
    fun onHide(gxView: View?) {
        if (Log.isLog()) {
            Log.d("onHide() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let { gxTemplateContext ->

            // 通知JS组件隐藏
            gxExtJSArg(gxTemplateContext)?.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.onHide(jsComponentId)
            }

            // 遍历容器子视图，通知坑位隐藏
            GXContainerUtils.notifyView(gxTemplateContext) { gxView: View ->
                onHide(gxView)
            }
        }
    }

    /**
     * 通知视图的JS组件销毁，如果视图是容器那么也通知其坑位JS组件销毁
     */
    fun onDestroy(gxView: View?) {
        if (Log.isLog()) {
            Log.d("onDestroy() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let { gxTemplateContext ->

            // 通知JS组件销毁
            gxExtJSArg(gxTemplateContext)?.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.onDestroy(jsComponentId)
            }

            // 遍历容器子视图，通知坑位组件销毁
            GXContainerUtils.notifyView(gxTemplateContext) { gxView: View ->
                onDestroy(gxView)
            }
        }
    }

    fun onLoadMore(gxView: View, data: JSONObject) {
        if (Log.isLog()) {
            Log.d("onLoadMore() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let { gxTemplateContext ->
            gxExtJSArg(gxTemplateContext)?.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.onLoadMore(jsComponentId, data)
            }
        }
    }

    /**
     * 通知视图的JS组件注册和可用
     */
    fun registerComponentAndOnReady(gxView: View?) {
        if (Log.isLog()) {
            Log.d("registerComponentAndOnReady() called with: gxView = $gxView")
        }
        gxView?.post {
            registerComponent(gxView)
            onReady(gxView)
        }
    }

    /**
     * 通知视图的JS组件注册
     */
    fun registerComponent(gxView: View?) {
        if (Log.isLog()) {
            Log.d("registerComponent() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let { gxTemplateContext ->

            // 在GaiaX中存储JS组件ID
            if (gxTemplateContext.extArg == null) {
                gxTemplateContext.extArg = GXTemplateContextExtArg()
            }

            // 寻找可注册的JS组件
            registerTemplateTree(gxTemplateContext, gxTemplateContext.templateInfo)

            // 将注册组件ID都和跟视图做全局映射
            gxExtJSArg(gxTemplateContext)?.jsComponentIds?.forEach { jsComponentId ->
                GXJSRenderProxy.instance.jsGlobalComponentMap[jsComponentId] = gxView
            }
        }
    }

    /**
     * 通知视图的JS组件解除注册，如果视图是容器那么也通知其坑位JS组件解除注册
     */
    fun unregisterComponent(gxView: View?) {
        if (Log.isLog()) {
            Log.d("unregisterComponent() called with: gxView = $gxView")
        }
        GXTemplateContext.getContext(gxView)?.let { gxTemplateContext ->

            // 解除JS组件ID和视图的全局映射
            gxExtJSArg(gxTemplateContext)?.jsComponentIds?.forEach { jsComponentId ->
                GXJSRenderProxy.instance.jsGlobalComponentMap.remove(jsComponentId)
            }
            gxExtJSArg(gxTemplateContext)?.jsComponentIds?.forEach { jsComponentId ->
                GXJSEngine.instance.unregisterComponent(jsComponentId)
            }
            gxExtJSArg(gxTemplateContext)?.jsComponentIds?.clear()

            // 遍历容器子视图，通知坑位解除注册
            GXContainerUtils.notifyView(gxTemplateContext) { gxView: View ->
                unregisterComponent(gxView)
            }
        }
    }

    private fun registerTemplateTree(gxTemplateContext: GXTemplateContext, templateInfo: GXTemplateInfo) {
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
            gxExtJSArg(gxTemplateContext)?.let {
                if (it.rootJSComponentId == null) {
                    it.rootJSComponentId = jsComponentId
                }
                it.jsComponentIds.add(jsComponentId)
            }

        } else {
            if (Log.isLog()) {
                Log.d("registerTemplateTree() called with: $templateId script is null")
            }
        }

        val children = templateInfo.children
        // 仅有当前模板不是容器模板时，但又是由子模板组成的时候，子模板也可以有JS代码，所以需要向下递归注册
        if (!layer.isContainerType()) {
            if (!children.isNullOrEmpty()) {
                for (gxTemplateInfo in children) {
                    registerTemplateTree(gxTemplateContext, gxTemplateInfo)
                }
            }
        } else {
            // 如果是容器模板，那么其子模板要独立注册
        }
    }

    private fun gxExtJSArg(gxTemplateContext: GXTemplateContext) = (gxTemplateContext.extArg as? GXTemplateContextExtArg)

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