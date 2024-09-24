package com.alibaba.gaiax.js.proxy.modules

import android.util.Base64
import androidx.annotation.Keep
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.annotation.GXSyncMethod
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset

@Keep
class GXJSNativeUtilModule : GXJSBaseModule() {

    @GXSyncMethod
    fun base64Decode(content: String): String {
        val result = String(Base64.decode(content.toByteArray(), Base64.DEFAULT), Charset.forName("utf-8"))
        return result
    }

    @GXSyncMethod
    fun base64Encode(content: String): String {
        val result = Base64.encodeToString(content.toByteArray(), Base64.DEFAULT)
        return result
    }

    @GXSyncMethod
    fun urlDecode(content: String): String {
        val result = URLDecoder.decode(content, "utf-8")
        return result
    }

    @GXSyncMethod
    fun urlEncode(content: String): String {
        val result = URLEncoder.encode(content, "utf-8")
        return result
    }

    override val name: String
        get() = "NativeUtil"

}