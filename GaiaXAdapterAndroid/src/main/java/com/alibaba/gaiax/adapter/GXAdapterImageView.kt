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
import android.support.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.basic.GXImageView
import com.bumptech.glide.Glide

@Keep
class GXAdapterImageView(context: Context) : GXImageView(context) {

    override fun bindNetUri(data: JSONObject, uri: String, placeholder: String?) {
        // 占位图仅对网络图生效
        placeholder?.let { resUri ->
            bindRes(resUri)
        }
        // Net
        Glide.with(context).load(uri).into(this)
    }
}