package com.alibaba.gaiax.demo.utils

import android.content.Context
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

object AssetsUtils {

    fun parseAssets(context: Context, path: String): JSONObject {
        val content =
            context.resources.assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
        return JSON.parseObject(content)
    }

    fun parseAssetsToString(context: Context, path: String): String {
        return context.resources.assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

}