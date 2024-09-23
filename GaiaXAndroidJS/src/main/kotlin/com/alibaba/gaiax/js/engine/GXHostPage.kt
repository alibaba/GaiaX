package com.alibaba.gaiax.js.engine

import android.content.Context
import android.text.TextUtils
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.api.IGXCallback
import com.alibaba.gaiax.js.api.IGXPage
import com.alibaba.gaiax.js.api.IGXPromise
import com.alibaba.gaiax.js.support.script.GXScriptBuilder
import com.alibaba.gaiax.js.support.script.PageLifecycle
import com.alibaba.gaiax.js.support.script.PageScriptStrategy

internal class GXHostPage private constructor(
    val hostContext: GXHostContext,
    val bizId: String,
    val pageInstanceId: Long,
    val templateId: String,
    val templateVersion: String,
    val script: String,
    val nativePage: IGXPage
) : IGXPage {

    companion object {
        fun create(
            hostContext: GXHostContext,
            bizId: String,
            pageInstanceId: Long,
            templateId: String,
            templateVersion: String,
            script: String,
            nativePage: IGXPage
        ): GXHostPage {
            val checkedTemplateVersion =
                if (TextUtils.isEmpty(templateVersion)) "-1" else templateVersion
            val checkedBizId = if (TextUtils.isEmpty(bizId)) "common" else bizId
            return GXHostPage(
                hostContext,
                checkedBizId,
                pageInstanceId,
                templateId,
                checkedTemplateVersion,
                script,
                nativePage
            )
        }
    }

    val id: Long
        get() = pageInstanceId

    override fun initPage() {
        GXScriptBuilder.buildInitScript(PageScriptStrategy, bizId, templateId, templateVersion, id, script)
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

    override fun onLoad(data: JSONObject) {
        GXScriptBuilder.buildLifecycleScript(PageScriptStrategy, PageLifecycle.ON_LOAD, id, data)
            .apply { hostContext.evaluateJS(this) }
    }

    override fun onShow() {
        GXScriptBuilder.buildLifecycleScript(PageScriptStrategy, PageLifecycle.ON_SHOW, id)
            .apply { hostContext.evaluateJS(this) }
    }

    override fun onReady() {
        GXScriptBuilder.buildLifecycleScript(PageScriptStrategy, PageLifecycle.ON_READY, id)
            .apply { hostContext.evaluateJS(this) }
    }

    override fun onHide() {
        GXScriptBuilder.buildLifecycleScript(PageScriptStrategy, PageLifecycle.ON_HIDE, id)
            .apply { hostContext.evaluateJS(this) }
    }

    override fun onUnload() {
        GXScriptBuilder.buildLifecycleScript(PageScriptStrategy, PageLifecycle.ON_UNLOAD, id)
            .apply { hostContext.evaluateJS(this) }
    }

    override fun onPageScroll(data: JSONObject) {
        GXScriptBuilder.buildLifecycleScript(PageScriptStrategy, PageLifecycle.ON_PAGE_SCROLL, id, data)
            .apply { hostContext.evaluateJS(this) }
    }

    override fun onReachBottom() {
        GXScriptBuilder.buildLifecycleScript(PageScriptStrategy, PageLifecycle.ON_REACH_BOTTOM, id)
            .apply { hostContext.evaluateJS(this) }
    }

    override fun createComponentByData(data: JSONObject, promise: IGXPromise) {
        nativePage.createComponentByData(data, promise)
    }

    override fun addComponentByData(data: JSONObject, promise: IGXPromise) {
        nativePage.addComponentByData(data, promise)
    }

    override fun updateComponentByData(data: JSONObject, promise: IGXPromise) {
        nativePage.updateComponentByData(data, promise)
    }

    override fun removeComponentByData(data: JSONObject, promise: IGXPromise) {
        nativePage.removeComponentByData(data, promise)
    }

    override fun batchCreateComponentByData(dataList: JSONArray, promise: IGXPromise) {
        nativePage.batchCreateComponentByData(dataList, promise)
    }

    override fun batchAddComponentByData(dataList: JSONArray, promise: IGXPromise) {
        nativePage.batchAddComponentByData(dataList, promise)
    }

    override fun batchUpdateComponentByData(dataList: JSONArray, promise: IGXPromise) {
        nativePage.batchUpdateComponentByData(dataList, promise)
    }

    override fun batchRemoveComponentByData(dataList: JSONArray, promise: IGXPromise) {
        nativePage.batchRemoveComponentByData(dataList, promise)
    }

    override fun batchReplaceComponentByData(data: JSONArray, promise: IGXPromise) {
        nativePage.batchReplaceComponentByData(data, promise)
    }

    override fun showFloatingView(data: JSONObject, callback: IGXCallback) {
        nativePage.showFloatingView(data, callback)
    }

    override fun hideFloatingView(data: JSONObject, callback: IGXCallback) {
        nativePage.hideFloatingView(data, callback)
    }

    override fun setupActionBar(data: JSONObject, callback: IGXCallback) {
        nativePage.setupActionBar(data, callback)
    }

    override fun setBackgroundColor(data: JSONObject, callback: IGXCallback) {
        nativePage.setBackgroundColor(data, callback)
    }

    override fun setStatusBarBlack(data: JSONObject, callback: IGXCallback) {
        nativePage.setStatusBarBlack(data, callback)
    }

    override fun presentModal(data: JSONObject, callback: IGXCallback) {
        nativePage.presentModal(data, callback)
    }

    override fun dismissModal(data: JSONObject, callback: IGXCallback) {
        nativePage.dismissModal(data, callback)
    }

    override fun closePage(data: JSONObject, callback: IGXCallback) {
        nativePage.closePage(data, callback)
    }

    override fun getContext(): Context {
        return nativePage.getContext()
    }

    override fun getPageStateManager(): IGXPage.IPageStateManager {
        return nativePage.getPageStateManager()
    }

    override fun onNativeEvent(data: JSONObject) {
        GXScriptBuilder.buildPostNativeMessage(data.toJSONString())
                .apply { hostContext.evaluateJS(this) }
    }
}