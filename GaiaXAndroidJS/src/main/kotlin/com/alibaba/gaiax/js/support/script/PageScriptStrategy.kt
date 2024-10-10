package com.alibaba.gaiax.js.support.script

import com.alibaba.fastjson.JSONObject

object PageScriptStrategy : IScriptStrategy<PageLifecycle> {

    override fun buildInitScript(
        bizId: String,
        templateId: String,
        templateVersion: String,
        instanceId: Long,
        script: String
    ): String {
        val newScript = script.trimIndent()
        val parsePageStr = parsePageStr(newScript)
        val extend =
            "{ bizId: \"${bizId}\", templateId: \"${templateId}\", instanceId: $instanceId, templateVersion: $templateVersion }"
        val prefix = newScript.substring(0, parsePageStr.length - 2).trimIndent()
        val suffix = newScript.substring(parsePageStr.length - 2).trimIndent()
        val innerScript = """
        $prefix, $extend
        $suffix
    """.trimIndent()
        return "(function() { $innerScript })()"
    }


    /**
     * 获取"Component{}"
     */
    private fun parsePageStr(script: String): String {
        val indexOfStart = script.indexOf("//# sourceMappingURL=")
        if (indexOfStart != -1) {
            val PageStr = script.substring(0, indexOfStart).trimIndent()
            return PageStr
        } else {
            return script
        }
    }

    override fun buildLifecycleScript(lifecycle: PageLifecycle, instanceId: Long, data: JSONObject?): String {
        return when (lifecycle) {
            PageLifecycle.ON_LOAD -> buildPageLoadScript(instanceId, data)
            PageLifecycle.ON_SHOW -> buildPageShowScript(instanceId)
            PageLifecycle.ON_READY -> buildPageReadyScript(instanceId)
            PageLifecycle.ON_HIDE -> buildPageHideScript(instanceId)
            PageLifecycle.ON_UNLOAD -> buildPageUnloadScript(instanceId)
            PageLifecycle.ON_PAGE_SCROLL -> buildPageScrollScript(instanceId, data)
            PageLifecycle.ON_REACH_BOTTOM -> buildPageReachBottomScript(instanceId)
        }
    }

    private fun buildPageLoadScript(instanceId: Long, data: JSONObject?): String {
        return """
(function () {
    let instance = IMs.getPage($instanceId); 
    if (instance) { 
        instance.onLoad && instance.onLoad(${data?.toJSONString()}); 
    }    
})()
        """.trimIndent()
    }

    private fun buildPageShowScript(instanceId: Long): String {
        return """
(function () {
    let instance = IMs.getPage($instanceId); 
    if (instance) { 
        instance.onShow && instance.onShow(); 
    }    
})()
        """.trimIndent()
    }

    private fun buildPageReadyScript(instanceId: Long): String {
        return """
(function () {
    let instance = IMs.getPage($instanceId); 
    if (instance) { 
        instance.onReady && instance.onReady(); 
    }    
})()
        """.trimIndent()
    }

    private fun buildPageHideScript(instanceId: Long): String {
        return """
(function () {
    let instance = IMs.getPage($instanceId); 
    if (instance) { 
        instance.onHide && instance.onHide(); 
    }    
})()
        """.trimIndent()
    }

    private fun buildPageUnloadScript(instanceId: Long): String {
        return """
(function () {
    let instance = IMs.getPage($instanceId); 
    if (instance) { 
        instance.onUnload && instance.onUnload(); 
    }    
})()
        """.trimIndent()
    }

    private fun buildPageReachBottomScript(instanceId: Long): String {
        return """
(function () {
    let instance = IMs.getPage($instanceId); 
    if (instance) { 
        instance.onReachBottom && instance.onReachBottom(); 
    }    
})()
        """.trimIndent()
    }

    private fun buildPageScrollScript(instanceId: Long, data: JSONObject?): String {
        return """
(function () {
    let instance = IMs.getPage($instanceId); 
    if (instance) { 
        instance.onPageScroll && instance.onPageScroll(${data?.toJSONString()}); 
    }    
})()
        """.trimIndent()
    }
}