package com.alibaba.gaiax.js.engine

import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.support.GXScriptBuilder

internal class GXHostComponent private constructor(
    private val hostContext: GXHostContext,
    val componentId: Long,
    val bizId: String,
    val templateId: String,
    private val templateVersion: String,
    val script: String
) : IComponent {

    companion object {
        fun create(
            hostContext: GXHostContext,
            componentId: Long,
            bizId: String,
            templateId: String,
            templateVersion: String,
            script: String
        ): GXHostComponent {
            val checkedTemplateVersion = if (TextUtils.isEmpty(templateVersion)) "-1" else templateVersion
            val checkedBizId = if (TextUtils.isEmpty(bizId)) "common" else bizId
            return GXHostComponent(hostContext, componentId, checkedBizId, templateId, checkedTemplateVersion, script)
        }
    }

    val id: Long
        get() = componentId

    /**
     * Java组件引用创建时调用
     */
    internal fun initComponent() {
        GXScriptBuilder.buildInitComponentScript(id, bizId, templateId, templateVersion, script)
            .let { script ->
                hostContext.executeTask {
                    val argsMap = JSONObject()
                    argsMap["instanceId"] = id
                    argsMap["bizId"] = bizId
                    argsMap["templateId"] = templateId
                    argsMap["templateVersion"] = templateVersion
                    hostContext.evaluateJSWithoutTask(script, argsMap)
                }
            }
    }

    override fun onReady() {
        GXScriptBuilder.buildComponentReadyScript(id).apply { hostContext.evaluateJS(this) }
    }

    override fun onReuse() {
        GXScriptBuilder.buildComponentReuseScript(id).apply { hostContext.evaluateJS(this) }
    }

    override fun onShow() {
        GXScriptBuilder.buildComponentShowScript(id).apply { hostContext.evaluateJS(this) }
    }

    override fun onHide() {
        GXScriptBuilder.buildComponentHideScript(id).apply { hostContext.evaluateJS(this) }
    }

    override fun onDestroy() {
        GXScriptBuilder.buildComponentDestroyScript(id).apply { hostContext.evaluateJS(this) }
    }

    /** Java组件销毁时调用*/
    fun destroyComponent() {
        GXScriptBuilder.buildComponentDestroyScript(id).apply { hostContext.evaluateJS(this) }
        GXScriptBuilder.buildDestroyComponentScript(id).apply { hostContext.evaluateJS(this) }
    }

    override fun onEvent(type: String, data: JSONObject) {
        val targetData = JSONObject().apply {
            this.putAll(data)
            this["bizId"] = bizId
            this["templateId"] = templateId
            this["templateVersion"] = templateVersion
            this["instanceId"] = id
            this["type"] = type
        }
        GXScriptBuilder.buildPostMessage(targetData.toJSONString())
            .apply { hostContext.evaluateJS(this) }
    }

    override fun onNativeEvent(data: JSONObject) {
        GXScriptBuilder.buildPostNativeMessage(data.toJSONString())
            .apply { hostContext.evaluateJS(this) }
    }

    override fun onLoadMore(data: JSONObject) {
        GXScriptBuilder.buildComponentLoadMoreScript(id, data.toJSONString())
            .apply { hostContext.evaluateJS(this) }
    }

    override fun onDataInit(data: JSONObject): JSONObject? {
        GXScriptBuilder.buildComponentDataInitScript(id, data.toJSONString())
            .apply {
                return hostContext.evaluateJS(this, String::class.java)?.let {
                    JSONObject.parseObject(it)
                }
            }
    }
}