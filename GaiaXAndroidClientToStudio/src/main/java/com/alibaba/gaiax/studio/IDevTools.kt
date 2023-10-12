package com.alibaba.gaiax.studio

/**
 *  @author: shisan.lms
 *  @date: 2023-02-06
 *  Description:
 *      DevTools接口定义：
 *          1.获取当前预览模式（页面预览与手动推送）与JS调试模式（日志调试与断点模式）
 *          2.DevToolsJS调试模式监听器
 */
interface IDevTools {

    fun getPreviewCurrentMode(): String

    fun getJSCurrentMode(): String

    fun changeDevToolsConnectedStateView()

    interface DevToolsDebuggingTypeListener {
        fun onDevToolsJSModeChanged(modeType: String)
    }
}