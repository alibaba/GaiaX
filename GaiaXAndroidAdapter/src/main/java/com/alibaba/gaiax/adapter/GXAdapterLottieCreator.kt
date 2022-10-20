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
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsoluteLayout
import com.airbnb.lottie.LottieAnimationView

object GXAdapterLottieCreator {

    fun localCreateLottieView(context: Context): View {
        val lottieView: LottieAnimationView = LayoutInflater.from(context)
            .inflate(R.layout.gaiax_inner_lottie_auto_play, null) as LottieAnimationView
        lottieView.layoutParams = AbsoluteLayout.LayoutParams(
            AbsoluteLayout.LayoutParams.MATCH_PARENT,
            AbsoluteLayout.LayoutParams.MATCH_PARENT,
            0,
            0
        )
        return lottieView
    }
}