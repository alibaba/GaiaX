package com.alibaba.gaiax.js.engine

import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.support.script.ComponentLifecycle
import com.alibaba.gaiax.js.support.script.ComponentScriptStrategy
import com.alibaba.gaiax.js.support.script.GXScriptBuilder

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
        GXScriptBuilder.buildInitScript(ComponentScriptStrategy, bizId, templateId, templateVersion, id, script)
            .let { script ->
                hostContext.executeTask {
                    val argsMap = JSONObject()
                    argsMap["instanceId"] = id
                    argsMap["bizId"] = bizId
                    argsMap["templateId"] = templateId
                    argsMap["templateVersion"] = templateVersion
                    hostContext.evaluateJSWithoutTask(script, bizId, templateId, argsMap)
                }
            }
    }

    override fun onDataInit(data: JSONObject): JSONObject? {
        val script = GXScriptBuilder.buildLifecycleScript(ComponentScriptStrategy, ComponentLifecycle.ON_DATA_INIT, id, data)
        return hostContext.evaluateJSSync(script, bizId, templateId)
    }

    override fun onReady() {
        GXScriptBuilder.buildLifecycleScript(ComponentScriptStrategy, ComponentLifecycle.ON_READY, id).apply { hostContext.evaluateJS(this, bizId, templateId) }
    }

    override fun onReuse() {
        GXScriptBuilder.buildLifecycleScript(ComponentScriptStrategy, ComponentLifecycle.ON_REUSE, id).apply { hostContext.evaluateJS(this, bizId, templateId) }
    }

    override fun onShow() {
        GXScriptBuilder.buildLifecycleScript(ComponentScriptStrategy, ComponentLifecycle.ON_SHOW, id).apply { hostContext.evaluateJS(this, bizId, templateId) }
    }

    override fun onHide() {
        GXScriptBuilder.buildLifecycleScript(ComponentScriptStrategy, ComponentLifecycle.ON_HIDE, id).apply { hostContext.evaluateJS(this, bizId, templateId) }
    }

    override fun onDestroy() {
        GXScriptBuilder.buildLifecycleScript(ComponentScriptStrategy, ComponentLifecycle.ON_DESTROY, id).apply { hostContext.evaluateJS(this, bizId, templateId) }
    }

    /** Java组件销毁时调用*/
    fun destroyComponent() {
        onDestroy()
        GXScriptBuilder.buildLifecycleScript(ComponentScriptStrategy, ComponentLifecycle.ON_DESTROY_COMPONENT, id).apply { hostContext.evaluateJS(this, bizId, templateId) }
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
            .apply { hostContext.evaluateJS(this, bizId, templateId) }
    }

    override fun onNativeEvent(data: JSONObject) {
        GXScriptBuilder.buildPostNativeMessage(data.toJSONString())
            .apply { hostContext.evaluateJS(this, bizId, templateId) }
    }

    override fun onLoadMore(data: JSONObject) {
        GXScriptBuilder.buildLifecycleScript(ComponentScriptStrategy, ComponentLifecycle.ON_LOAD_MORE, id, data)
            .apply { hostContext.evaluateJS(this, bizId, templateId) }
    }


}