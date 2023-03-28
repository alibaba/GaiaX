package com.youku.gaiax.js.core

import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.core.api.IComponent
import com.youku.gaiax.js.support.GaiaXScriptBuilder
import com.youku.gaiax.js.utils.Aop
import com.youku.gaiax.js.utils.IdGenerator
import com.youku.gaiax.js.utils.MonitorUtils

internal class GaiaXComponent private constructor(val jsContext: GaiaXContext, val bizId: String, val templateId: String, val templateVersion: String, val script: String) : IComponent {

    companion object {
        fun create(jsContext: GaiaXContext, bizId: String, templateId: String, templateVersion: String, script: String): GaiaXComponent {
            val checkedTemplateVersion = if (TextUtils.isEmpty(templateVersion)) "-1" else templateVersion
            val checkedBizId = if (TextUtils.isEmpty(bizId)) "common" else bizId
            return GaiaXComponent(jsContext, checkedBizId, templateId, checkedTemplateVersion, script)
        }
    }

    private val _id: Long by lazy {
        IdGenerator.genLongId()
    }

    val id: Long
        get() = _id

    /**
     * Java组件引用创建时调用
     */
    internal fun initComponent() {

        GaiaXScriptBuilder.buildInitComponentScript(id, bizId, templateId, templateVersion, script).apply {
            jsContext.executeTask {
                Aop.aopTaskTime({
                    val argsMap = JSONObject()
                    argsMap["instanceId"] = id
                    argsMap["bizId"] = bizId
                    argsMap["templateId"] = templateId
                    argsMap["templateVersion"] = templateVersion
                    jsContext.evaluateJSWithoutTask(this,argsMap)
                }, { time ->
                    MonitorUtils.jsTemplate(MonitorUtils.TYPE_LOAD_INDEX_JS, time, templateId, bizId)
                })
            }
        }
    }

    override fun onReady() {
        GaiaXScriptBuilder.buildComponentReadyScript(id).apply { jsContext.evaluateJS(this) }
    }

    override fun onReuse() {
        GaiaXScriptBuilder.buildComponentReuseScript(id).apply { jsContext.evaluateJS(this) }
    }

    override fun onShow() {
        GaiaXScriptBuilder.buildComponentShowScript(id).apply { jsContext.evaluateJS(this) }
    }

    override fun onHide() {
        GaiaXScriptBuilder.buildComponentHideScript(id).apply { jsContext.evaluateJS(this) }
    }

    override fun onDestroy() {
        GaiaXScriptBuilder.buildComponentDestroyScript(id).apply { jsContext.evaluateJS(this) }
    }

    /**
     * Java组件销毁时调用
     */
    fun destroyComponent() {
        GaiaXScriptBuilder.buildComponentDestroyScript(id).apply { jsContext.evaluateJS(this) }
        GaiaXScriptBuilder.buildDestroyComponentScript(id).apply { jsContext.evaluateJS(this) }
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
        GaiaXScriptBuilder.buildPostMessage(targetData.toJSONString()).apply { jsContext.evaluateJS(this) }
    }

    override fun onNativeEvent(data: JSONObject) {
        GaiaXScriptBuilder.buildPostNativeMessage(data.toJSONString()).apply { jsContext.evaluateJS(this) }
    }

    override fun onLoadMore(data: JSONObject) {
        GaiaXScriptBuilder.buildComponentLoadMoreScript(id, data.toJSONString()).apply { jsContext.evaluateJS(this) }
    }

}