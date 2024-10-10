package com.alibaba.gaiax.js.support.script

import com.alibaba.fastjson.JSONObject


object ComponentScriptStrategy : IScriptStrategy<ComponentLifecycle> {
    override fun buildInitScript(
        bizId: String,
        templateId: String,
        templateVersion: String,
        instanceId: Long,
        script: String
    ): String {
        val newScript = script.trimIndent()
        val componentStr = parseComponentStr(newScript)
        val extend =
            "{ bizId: \"${bizId}\", templateId: \"${templateId}\", instanceId: $instanceId, templateVersion: $templateVersion }"
        val prefix = newScript.substring(0, componentStr.length - 2).trimIndent()
        // 实际是取");"
        val suffix = newScript.substring(componentStr.length - 2).trimIndent()
        val innerScript = """
        $prefix, $extend
        $suffix
    """.trimIndent()
        return "(function() {\n $innerScript \n})()"
    }

    /**
     * 获取"Component{}"
     */
    private fun parseComponentStr(script: String): String {
        val indexOfStart = script.lastIndexOf("//# sourceMappingURL=")
        if (indexOfStart != -1) {
            val componentStr = script.substring(0, indexOfStart).trimIndent()
            return componentStr
        } else {
            return script
        }
    }

    override fun buildLifecycleScript(lifecycle: ComponentLifecycle, instanceId: Long, data: JSONObject?): String {
        return when (lifecycle) {
            ComponentLifecycle.ON_DATA_INIT -> buildComponentDataInitScript(instanceId, data)
            ComponentLifecycle.ON_SHOW -> buildComponentShowScript(instanceId)
            ComponentLifecycle.ON_READY -> buildComponentReadyScript(instanceId)
            ComponentLifecycle.ON_REUSE -> buildComponentReuseScript(instanceId)
            ComponentLifecycle.ON_HIDE -> buildComponentHideScript(instanceId)
            ComponentLifecycle.ON_DESTROY -> buildComponentDestroyScript(instanceId)
            ComponentLifecycle.ON_DESTROY_COMPONENT -> buildDestroyComponentScript(instanceId)
            ComponentLifecycle.ON_LOAD_MORE -> {
                val dataStr = data?.toJSONString() ?: ""
                buildComponentLoadMoreScript(instanceId, dataStr)
            }
        }
    }

    /**
     * //index.js
     * onDataInit: function (options) {
     *     //options.data指向将要绑定的数据，修改后，同步返回
     *     let data = options.data;
     *     data["backgroundColor"] = "#FF0000";
     *     return data;
     * }
     *
     * options:
     * 参数名	类型	默认值	说明
     * data	object	-	模板将要绑定的数据
     */
    private fun buildComponentDataInitScript(componentId: Long, data: JSONObject?): String {
        val options = JSONObject().apply {
            this["data"] = data ?: JSONObject()
        }
        return """
(function () {
    var instance = IMs.getComponent($componentId);
    if (instance && instance.onDataInit) {
         return JSON.stringify(instance.onDataInit(${options.toJSONString()}));
    }
})()
        """.trimIndent()
    }

    private fun buildComponentReadyScript(componentId: Long): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId);
    if (instance) { 
        instance.onShow && instance.onShow(); 
        instance.onReady && instance.onReady(); 
    }
})()
        """.trimIndent()
    }

    private fun buildComponentShowScript(componentId: Long): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId); 
    if (instance) { 
        instance.onShow && instance.onShow(); 
    }    
})()
        """.trimIndent()
    }

    private fun buildComponentHideScript(componentId: Long): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId); 
    if (instance) { 
        instance.onHide && instance.onHide(); 
    }
})()
        """.trimIndent()
    }

    private fun buildComponentDestroyScript(componentId: Long): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId); 
    if (instance) { 
        instance.onDestroy && instance.onDestroy(); 
    }
})()
        """.trimIndent()
    }

    private fun buildDestroyComponentScript(componentId: Long): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId); 
    if (instance) {
        IMs.removeComponent($componentId);
    }
})()
        """.trimIndent()
    }

    private fun buildComponentReuseScript(componentId: Long): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId); 
    if (instance) { 
        instance.onReuse && instance.onReuse(); 
    }
})()
        """.trimIndent()
    }

    private fun buildComponentLoadMoreScript(componentId: Long, msg: String): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId); 
    if (instance) { 
        instance.onLoadMore && instance.onLoadMore($msg); 
    }
})()
        """.trimIndent()
    }
}