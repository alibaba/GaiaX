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

import android.animation.Animator
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieListener
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXIExpression
import com.alibaba.gaiax.template.animation.GXDefaultAnimatorListener
import com.alibaba.gaiax.template.animation.GXLottieAnimation
import com.alibaba.gaiax.template.factory.GXExpressionFactory
import com.alibaba.gaiax.utils.setValueExt

class GXAdapterLottieAnimation : GXLottieAnimation() {

    override fun executeAnimation(
        gxState: GXIExpression?,
        gxAnimationExpression: GXIExpression?,
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: JSONObject
    ) {
        val lottieContainer = (gxNode.view as? ViewGroup) ?: return
        val gxAnimationData = gxAnimationExpression?.value(gxTemplateData) as? JSONObject

        val remoteUri = this.gxRemoteUri?.value(gxTemplateData) as? String
        if (remoteUri != null) {
            remotePlay(
                lottieContainer,
                gxTemplateContext,
                gxNode,
                gxTemplateData,
                gxState,
                gxAnimationData,
                remoteUri,
                loopCount
            )
            return
        }

        val localUri = this.gxLocalUri?.value(gxTemplateData) as? String
        if (localUri != null) {
            localPlay(
                lottieContainer,
                gxTemplateContext,
                gxNode,
                gxTemplateData,
                gxState,
                gxAnimationData,
                localUri,
                loopCount
            )
            return
        }
    }

    private fun localAppendJson(value: String?): String? {
        if (value != null && !value.endsWith(".json")) {
            return "$value.json"
        }
        return value
    }

    private fun localCreateLottieView(context: Context): LottieAnimationView {
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

    private fun localInitLottieLocalResourceDir(value: String, lottieView: LottieAnimationView) {
        val dirIndex = value.indexOf("/")
        if (dirIndex > 0) {
            val dir = value.substring(0, dirIndex)
            if (dir.isNotEmpty()) {
                lottieView.imageAssetsFolder = "$dir/images/"
            }
        }
    }

    private fun localPlay(
        lottieContainer: ViewGroup,
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: JSONObject,
        gxState: GXIExpression?,
        gxAnimationData: JSONObject?,
        localUri: String,
        loopCount: Int
    ) {
        val lottieView: LottieAnimationView? = if (lottieContainer.childCount == 0) {
            localCreateLottieView(lottieContainer.context)
        } else {
            lottieContainer.getChildAt(0) as? LottieAnimationView
        }

        if (lottieView?.isAnimating == true || gxNode.isAnimating) {
            return
        }

        if (lottieView == null) {
            return
        }

        localInitLottieLocalResourceDir(localUri, lottieView)

        lottieView.removeAllAnimatorListeners()
        lottieView.removeAllUpdateListeners()
        lottieView.removeAllLottieOnCompositionLoadedListener()

        lottieView.setAnimation(localAppendJson(localUri))
        lottieView.repeatCount = loopCount
        lottieView.addAnimatorListener(object : GXDefaultAnimatorListener() {

            override fun onAnimationEnd(animation: Animator?) {
                gxNode.isAnimating = false
                lottieView.removeAllAnimatorListeners()
                lottieView.removeAllUpdateListeners()
                lottieView.removeAllLottieOnCompositionLoadedListener()
                lottieView.progress = 1F
                GXExpressionFactory.valuePath(gxState?.expression())?.let {
                    gxTemplateData.setValueExt(it, false)
                }
                gxTemplateContext.templateData?.eventListener?.onAnimationEvent(
                    GXTemplateEngine.GXAnimation().apply {
                        this.state = "END"
                        this.nodeId = gxNode.id
                        this.view = lottieView
                        this.animationParams = gxAnimationData
                    })
            }

            override fun onAnimationStart(animation: Animator?) {
                gxTemplateContext.templateData?.eventListener?.onAnimationEvent(
                    GXTemplateEngine.GXAnimation().apply {
                        this.state = "START"
                        this.nodeId = gxNode.id
                        this.view = lottieView
                        this.animationParams = gxAnimationData
                    })
            }

        })
        lottieView.playAnimation()

        if (lottieContainer.childCount == 0) {
            lottieView.isClickable = false
            lottieContainer.addView(lottieView)
        }
    }

    private fun remoteCreateLottieView(context: Context): LottieAnimationView {
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

    private fun remotePlay(
        lottieContainer: ViewGroup,
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: JSONObject,
        gxState: GXIExpression?,
        gxAnimationData: JSONObject?,
        remoteUri: String,
        loopCount: Int
    ) {
        val lottieView: LottieAnimationView? = if (lottieContainer.childCount == 0) {
            remoteCreateLottieView(lottieContainer.context)
        } else {
            lottieContainer.getChildAt(0) as? LottieAnimationView
        }

        if (lottieView?.isAnimating == true || gxNode.isAnimating) {
            return
        }

        if (lottieView == null) {
            return
        }

        lottieView.removeAllAnimatorListeners()
        lottieView.removeAllUpdateListeners()
        lottieView.removeAllLottieOnCompositionLoadedListener()

        gxNode.isAnimating = true
        val downloadTask = LottieCompositionFactory.fromUrl(lottieView.context, remoteUri)
        downloadTask.addListener(object : LottieListener<LottieComposition> {

            override fun onResult(composition: LottieComposition?) {
                downloadTask.removeListener(this)
                gxNode.isAnimating = composition != null

                composition?.let { it ->
                    lottieView.setComposition(it)
                    lottieView.repeatCount = loopCount
                    lottieView.addAnimatorListener(object : GXDefaultAnimatorListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            gxNode.isAnimating = false

                            lottieView.removeAllAnimatorListeners()
                            lottieView.removeAllUpdateListeners()
                            lottieView.removeAllLottieOnCompositionLoadedListener()

                            lottieView.progress = 1F
                            GXExpressionFactory.valuePath(gxState?.expression())?.let {
                                gxTemplateData.setValueExt(it, false)
                            }
                            gxTemplateContext.templateData?.eventListener?.onAnimationEvent(
                                GXTemplateEngine.GXAnimation().apply {
                                    this.state = "END"
                                    this.nodeId = gxNode.id
                                    this.view = lottieView
                                    this.animationParams = gxAnimationData
                                })
                        }

                        override fun onAnimationStart(animation: Animator?) {
                            gxNode.isAnimating = true
                            gxTemplateContext.templateData?.eventListener?.onAnimationEvent(
                                GXTemplateEngine.GXAnimation().apply {
                                    this.state = "START"
                                    this.nodeId = gxNode.id
                                    this.view = lottieView
                                    this.animationParams = gxAnimationData
                                })
                        }
                    })
                    lottieView.playAnimation()
                }
            }
        })

        if (lottieContainer.childCount == 0) {
            lottieView.isClickable = false
            lottieContainer.addView(lottieView)
        }
    }
}