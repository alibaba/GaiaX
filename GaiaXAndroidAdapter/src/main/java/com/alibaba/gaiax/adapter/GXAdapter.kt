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

@file:Suppress("DEPRECATION")

package com.alibaba.gaiax.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.AbsoluteLayout
import androidx.annotation.Keep
import com.airbnb.lottie.LottieAnimationView
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.render.view.GXViewKey
import com.alibaba.gaiax.template.animation.GXLottieAnimation

@Keep
class GXAdapter : GXTemplateEngine.GXIAdapter {

    @SuppressLint("InflateParams")
    override fun init(context: Context) {
        GXRegisterCenter.instance
            .registerExtensionViewSupport(
                GXViewKey.VIEW_TYPE_IMAGE,
                GXAdapterImageView::class.java
            )
            .registerExtensionLottieAnimation(object :
                GXRegisterCenter.GXIExtensionLottieAnimation {
                override fun create(): GXLottieAnimation {
                    return GXAdapterLottieAnimation()
                }
            })
            .registerExtensionViewSupport(
                GXViewKey.VIEW_TYPE_LOTTIE
            ) {
                val lottieView: LottieAnimationView = LayoutInflater.from(context)
                    .inflate(R.layout.gaiax_inner_lottie_auto_play, null) as LottieAnimationView
                lottieView.layoutParams = AbsoluteLayout.LayoutParams(
                    AbsoluteLayout.LayoutParams.MATCH_PARENT,
                    AbsoluteLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0
                )
                lottieView
            }
    }
}