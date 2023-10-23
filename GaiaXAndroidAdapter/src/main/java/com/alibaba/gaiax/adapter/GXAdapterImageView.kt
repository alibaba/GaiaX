/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.gaiax.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.basic.GXImageView
import com.alibaba.gaiax.utils.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

@Keep
class GXAdapterImageView(context: Context) : GXImageView(context) {

    private var lastUrl: String? = null

    private val requestListener = object : RequestListener<Drawable> {
        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            gxTemplateContext?.let {
                it.bindDataCount++
            }
            return false
        }

        override fun onLoadFailed(
            e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean
        ): Boolean {
            return false
        }
    }

    override fun bindNetUri(data: JSONObject, uri: String, placeholder: String?) {

        // 如果要加载的URI和缓存一致，那么跳过这次逻辑处理
        if (lastUrl == uri) {
            if (Log.isLog()) {
                Log.e("bindNetUri() called with: skip $uri")
            }
            return
        } else {
            if (Log.isLog()) {
                Log.e("bindNetUri() called with: data = $data, uri = $uri, placeholder = $placeholder")
            }
        }

        // 占位图仅对网络图生效
        var res = 0
        placeholder?.let { resUri ->
            res = getRes(resUri)
        }
        Glide.with(context).load(uri)
            .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(FitCenter()))
            .placeholder(res).listener(requestListener).into(this)

        this.lastUrl = uri
    }
}