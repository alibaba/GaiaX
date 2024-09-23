package com.alibaba.gaiax.js.support.script

import com.alibaba.fastjson.JSONObject

interface IScriptStrategy<T : ILifecycle> {
    fun buildInitScript(
        bizId: String,
        templateId: String,
        templateVersion: String,
        instanceId: Long,
        script: String
    ): String

    fun buildLifecycleScript(lifecycle: T, instanceId: Long, data: JSONObject?): String
}