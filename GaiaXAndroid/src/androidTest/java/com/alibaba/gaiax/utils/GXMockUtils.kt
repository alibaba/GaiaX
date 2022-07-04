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

package com.alibaba.gaiax.utils

import android.content.Context
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.GXViewKey
import com.alibaba.gaiax.template.GXLayer
import com.alibaba.gaiax.template.GXTemplateKey

object GXMockUtils {

    fun isSpecialDevice(): Boolean {
        return Build.BRAND == "Xiaomi" && Build.MODEL == "Mi 10"
    }

    fun deviceGap(): Float {
        if (Build.BRAND == "Xiaomi" && Build.MODEL == "Mi 10") {
            return 0.5F
        }
        return 0F
    }

    var layerIdCount: Int = 0

    var context: Context = InstrumentationRegistry.getInstrumentation().context

    fun createViewTypeLayer(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_VIEW
        })
    }

    fun createRootTypeLayer(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_GAIA_TEMPLATE
        })
    }

    fun createTextTypeLayer(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_TEXT
        })
    }

    fun createImageTypeLayer(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_IMAGE
        })
    }

    fun createGaiaTemplateTypeLayer(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_GAIA_TEMPLATE
        })
    }

    fun createNestChildTemplateTypeLayer(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_GAIA_TEMPLATE
            this[GXTemplateKey.GAIAX_LAYER_SUB_TYPE] = GXViewKey.VIEW_TYPE_CUSTOM
        })
    }

    fun createGridTypeLayer(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_GAIA_TEMPLATE
            this[GXTemplateKey.GAIAX_LAYER_SUB_TYPE] = GXViewKey.VIEW_TYPE_CONTAINER_GRID
        })
    }

    fun createScrollTypeLayer(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_GAIA_TEMPLATE
            this[GXTemplateKey.GAIAX_LAYER_SUB_TYPE] = GXViewKey.VIEW_TYPE_CONTAINER_SCROLL
        })
    }

    fun createIconFontTypeLayer(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_ICON_FONT
        })
    }

    fun createLottieTypeLayer(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_LOTTIE
        })
    }

    fun createRichTextTypeLayer(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_RICH_TEXT
        })
    }

    fun createCustomType(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_CUSTOM
            this[GXTemplateKey.GAIAX_LAYER_CUSTOM_VIEW_CLASS] = "com.alibaba.view.CustomView"
        })
    }

    fun createSliderTypeLayer(): GXLayer {
        return GXLayer.create(JSONObject().apply {
            this[GXTemplateKey.GAIAX_LAYER_ID] = "layerId" + layerIdCount++
            this[GXTemplateKey.GAIAX_LAYER_TYPE] = GXViewKey.VIEW_TYPE_GAIA_TEMPLATE
            this[GXTemplateKey.GAIAX_LAYER_SUB_TYPE] = GXViewKey.VIEW_TYPE_CONTAINER_SLIDER
        })
    }
}

