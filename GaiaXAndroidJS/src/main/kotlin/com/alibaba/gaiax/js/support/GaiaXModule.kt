package com.alibaba.gaiax.js.support

import com.alibaba.fastjson.JSONArray
import com.alibaba.gaiax.js.api.IGaiaXModule
import com.alibaba.gaiax.js.api.annotation.GaiaXAsyncMethod
import com.alibaba.gaiax.js.api.annotation.GaiaXPromiseMethod
import com.alibaba.gaiax.js.api.annotation.GaiaXSyncMethod
import com.alibaba.gaiax.js.utils.IdGenerator
import com.alibaba.gaiax.js.utils.Log

internal class GaiaXModule(nativeModule: IGaiaXModule) {

    val id: Long
        get() = module.id

    val name: String
        get() = module.name

    private var isInit: Boolean = false

    private val moduleInfo = GaiaXModuleInfo(nativeModule.name, nativeModule.id, nativeModule.javaClass.simpleName)

    internal val module = nativeModule

    val syncMethods: MutableMap<Long, GaiaXMethodInfo> = mutableMapOf()

    private val asyncMethods: MutableMap<Long, GaiaXMethodInfo> = mutableMapOf()

    private val promiseMethods: MutableMap<Long, GaiaXMethodInfo> = mutableMapOf()

    internal fun initMethods() {
        if (!isInit) {
            isInit = true
            initSyncMethods()
            initAsyncMethods()
            initPromiseMethods()
        }
    }

    private fun initPromiseMethods() {
        val classForMethods = module.javaClass
        val targetMethods = classForMethods.declaredMethods
        for (targetMethod in targetMethods) {
            val annotation = targetMethod.getAnnotation(GaiaXPromiseMethod::class.java)
            if (annotation != null) {
                val id = IdGenerator.genLongId()
                val methodInfo = GaiaXMethodInfo.GaiaXPromiseMethodInfo(id, targetMethod)
                promiseMethods[id] = methodInfo
            }
        }
    }

    private fun initAsyncMethods() {
        val classForMethods = module.javaClass
        val targetMethods = classForMethods.declaredMethods
        for (targetMethod in targetMethods) {
            val annotation = targetMethod.getAnnotation(GaiaXAsyncMethod::class.java)
            if (annotation != null) {
                val id = IdGenerator.genLongId()
                val methodInfo = GaiaXMethodInfo.GaiaXAsyncMethodInfo(id, targetMethod)
                asyncMethods[id] = methodInfo
            }
        }
    }

    private fun initSyncMethods() {
        val classForMethods = module.javaClass
        val targetMethods = classForMethods.declaredMethods
        for (targetMethod in targetMethods) {
            val annotation = targetMethod.getAnnotation(GaiaXSyncMethod::class.java)
            if (annotation != null) {
                val id = IdGenerator.genLongId()
                val methodInfo = GaiaXMethodInfo.GaiaXSyncMethodInfo(id, targetMethod)
                syncMethods[id] = methodInfo
            }
        }
    }

    fun invokeMethodSync(methodId: Long, args: JSONArray): Any? {
        try {
            return syncMethods[methodId]?.invoke(module, args)
        } catch (e: Exception) {
            if (Log.isLog()) {
                Log.d("invokeMethodSync() called with: exception message = ${e.message}")
            }
            e.printStackTrace()
        }
        return null
    }

    fun invokeMethodAsync(methodId: Long, args: JSONArray) {
        try {
            asyncMethods[methodId]?.invoke(module, args)
        } catch (e: Exception) {
            if (Log.isLog()) {
                Log.d("invokeMethodAsync() called with: exception message = ${e.message}")
            }
            e.printStackTrace()
        }
    }

    fun invokePromiseMethod(methodId: Long, args: JSONArray) {
        try {
            promiseMethods[methodId]?.invoke(module, args)
        } catch (e: Exception) {
            if (Log.isLog()) {
                Log.d("invokePromiseMethod() called with: exception message = ${e.message}")
            }
            e.printStackTrace()
        }
    }

    fun buildModuleScript(): StringBuilder {
        initMethods()
        return buildScript()
    }

    private fun buildScript(): StringBuilder {

        val syncScript = initSyncMethodsScript()
        val asyncScript = initAsyncMethodsScript()
        val promiseScript = initPromiseMethodsScript()

        val content = StringBuilder()
        content.append(syncScript)
        content.append(asyncScript)
        content.append(promiseScript)

        return content
    }

    private fun initPromiseMethodsScript(): String {
        val script = StringBuilder()
        promiseMethods.forEach {
            script.append(GaiaXScriptBuilder.buildPromiseMethodDeclareScript(moduleInfo.name, it.value.name, moduleInfo.id, it.value.id))
        }
        return script.toString()
    }

    private fun initAsyncMethodsScript(): String {
        val script = StringBuilder()
        asyncMethods.forEach {
            script.append(GaiaXScriptBuilder.buildAsyncMethodDeclareScript(moduleInfo.name, it.value.name, moduleInfo.id, it.value.id))
        }
        return script.toString()
    }

    private fun initSyncMethodsScript(): String {
        val script = StringBuilder()
        syncMethods.forEach {
            script.append(GaiaXScriptBuilder.buildSyncMethodDeclareScript(moduleInfo.name, it.value.name, moduleInfo.id, it.value.id))
        }
        return script.toString()
    }
}


