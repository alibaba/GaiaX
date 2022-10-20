package com.alibaba.gaiax.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsoluteLayout
import com.airbnb.lottie.LottieAnimationView

object GXAdapterLottieCreate {

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