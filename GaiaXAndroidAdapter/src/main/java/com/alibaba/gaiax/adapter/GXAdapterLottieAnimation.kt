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

internal class GXAdapterLottieAnimation : GXLottieAnimation() {

    override fun playAnimation(
        state: GXIExpression?,
        gxAnimationExpression: GXIExpression?,
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: JSONObject
    ) {
        val gxAnimationData = gxAnimationExpression?.value(gxTemplateData) as? JSONObject

        val remoteUri = this.gxRemoteUri
        if (remoteUri != null) {
            remotePlay(
                gxTemplateContext,
                gxNode,
                gxTemplateData,
                state,
                gxAnimationData,
                remoteUri,
                loopCount
            )
            return
        }

        val localUri = this.gxLocalUri
        if (localUri != null) {
            localPlay(
                gxTemplateContext,
                gxNode,
                gxTemplateData,
                state,
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
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: JSONObject,
        gxState: GXIExpression?,
        gxAnimationData: JSONObject?,
        localUri: String,
        loopCount: Int
    ) {
        val lottieView: LottieAnimationView? = gxNode.lottieView as? LottieAnimationView
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
        lottieView.isClickable = false
        lottieView.playAnimation()
    }

    private fun remotePlay(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: JSONObject,
        gxState: GXIExpression?,
        gxAnimationData: JSONObject?,
        remoteUri: String,
        loopCount: Int
    ) {
        val lottieView: LottieAnimationView? = gxNode.lottieView as? LottieAnimationView

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
                    lottieView.isClickable = false
                    lottieView.playAnimation()
                }
            }
        })
    }
}