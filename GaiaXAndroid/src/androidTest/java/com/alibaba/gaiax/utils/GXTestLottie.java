package com.alibaba.gaiax.utils;

import android.content.Context;
import android.util.AttributeSet;

import com.airbnb.lottie.LottieAnimationView;

public class GXTestLottie extends LottieAnimationView {
    public GXTestLottie(Context context) {
        super(context);
    }

    public GXTestLottie(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GXTestLottie(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isShown() {
        return true;
    }
}
