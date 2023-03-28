package com.youku.gaiax.js.utils

import com.youku.gaiax.js.GaiaXJSManager


/**
 * https://yuque.antfin-inc.com/ronghui.zrh/bpnuxl/mnbq8q
 */
object MonitorUtils {

    // 创建Context和Runtime耗时
    const val TYPE_JS_CONTEXT_INIT = "JS_CONTEXT_INIT"

    // 加载Module耗时
    const val TYPE_LOAD_MODULE = "LOAD_MODULE"

    // native生成的js和bootstrap.js加载执行耗时
    const val TYPE_JS_LIBRARY_INIT = "JS_LIBRARY_INIT"

    // 加载index.js耗时
    const val TYPE_LOAD_INDEX_JS = "LOAD_INDEX_JS"

    // JS到Native注册的callSync，callAsync，callPromise的耗时
    const val TYPE_JS_TO_CONTEXT = "JS_TO_CONTEXT"

    // callSync，callAsync，callPromise到具体的API返回的耗时
    const val TYPE_CONTEXT_TO_RETURN = "CONTEXT_TO_RETURN"

    // API返回处到callSync，callAsync，callPromise的耗时
    const val TYPE_RETURN_TO_CONTEXT = "RETURN_TO_CONTEXT"

    private fun monitor(scene: String, biz: String = "", id: String = "", type: String = "", state: String = "", value: Long = -1L, jsModuleName: String = "", jsApiName: String = "", jsApiType: String = "") {
        if (Log.isLog()) {
            Log.d("monitor() called with: scene = $scene, biz = $biz, id = $id, type = $type, state = $state, value = $value, moduleName = $jsModuleName, apiName = $jsApiName, apiType = $jsApiType")
        }
        //todo 此处为GaiaXJSManager初始化位置
        GaiaXJSManager.instance.errorListener?.monitor(scene, biz, id, type, state, value, jsModuleName, jsApiName, jsApiType)
    }

    fun jsInitScene(type: String, value: Long) {
        monitor(scene = "GAIAX_JS_INIT", type = type, value = value)
    }

    fun jsTemplate(type: String, value: Long, templateId: String, bizId: String) {
        monitor(scene = "GAIAX_JS_TEMPLATE", type = type, value = value, id = templateId, biz = bizId)
    }

    fun jsApi(type: String, jsModuleName: String, jsApiName: String, jsApiType: String, value: Long) {
        monitor(scene = "GAIAX_JS_API", type = type, value = value, jsModuleName = jsModuleName, jsApiName = jsApiName, jsApiType = jsApiType)
    }
}