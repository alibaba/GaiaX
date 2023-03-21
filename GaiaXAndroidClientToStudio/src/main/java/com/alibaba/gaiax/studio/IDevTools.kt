package com.alibaba.gaiax.studio

/**
 *  @author: shisan.lms
 *  @date: 2023-02-06
 *  Description:
 */
interface IDevTools {

    fun getPreviewCurrentMode(): String

    fun getJSCurrentMode(): String

    interface devToolsDebuggerListener{
        fun onWebsocketJSModeChanged(modeType :String)
    }
}