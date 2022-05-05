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

package com.alibaba.gaiax

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.test.platform.app.InstrumentationRegistry
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.basic.GXImageView
import com.alibaba.gaiax.render.view.basic.GXShadowLayout
import com.alibaba.gaiax.render.view.basic.GXText
import com.alibaba.gaiax.render.view.basic.GXView
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.render.view.container.GXGridView
import com.alibaba.gaiax.render.view.setFontSize
import com.alibaba.gaiax.template.GXColor
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.math.roundToInt


/**
 * 双端共同的测试用例
 */
class GXTemplateEngineTest {

    companion object {
        var MOCK_SCREEN_WIDTH = 1080F.dpToPx()
    }

    private val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

    @Before
    fun before() {
        GXTemplateEngine.instance.init(GXMockUtils.context)
    }

    @Test
    fun template_register_map_relation() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "mockBiz",
            "template_register_map_relation"
        )

        GXRegisterCenter.instance.registerBizMapRelation("mockBiz", "integration")

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_normal() {

        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "integration", "template_normal")
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(true, rootView.child(0) is GXImageView)
        Assert.assertEquals(true, rootView.child(1) is GXView)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx() - 100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())

        Assert.assertEquals(2, rootView.child(1).childCount())

        Assert.assertEquals(true, rootView.child(1).child(0) is GXText)
        Assert.assertEquals(true, rootView.child(1).child(1) is GXText)

        Assert.assertEquals(20F.dpToPx(), rootView.child(1).child(0).height())
        Assert.assertEquals(20F.dpToPx(), rootView.child(1).child(1).height())
    }

    @Test
    fun template_aspect_ratio_height_to_width() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_aspect_ratio_height_to_width"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_aspect_ratio_width_to_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_aspect_ratio_width_to_height"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_max_size_width() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_max_size_width"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_max_size_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_max_size_height"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_min_size_width() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_min_size_width"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_min_size_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_min_size_height"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_normal_binary() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_normal_binary"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_text_fitcontent_case_1_lines_1_width_100pt_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_fitcontent_case_1_lines_1_width_100pt_height_100px"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_case_1_lines_1_width_null_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_fitcontent_case_1_lines_1_width_null_height_100px"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_case_1_lines_1_width_null_height_null() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_fitcontent_case_1_lines_1_width_null_height_null"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_lines_null_width_100pt_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_fitcontent_case_1_lines_null_width_100pt_height_100px"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_case_1_lines_null_width_null_height_100px_repeat_bind_data() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_fitcontent_case_1_lines_null_width_null_height_100px_repeat_bind_data"
        )

        var templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["text"] = "0"
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "0"
        textView.setFontSize(14F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        ////////////////////////////////////////////////////////////

        val textView1 = GXText(GXMockUtils.context)
        textView1.text = "300"
        textView1.setFontSize(14F.dpToPx())
        textView1.measure(0, 0)

        templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["text"] = "300"
        })
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(textView1.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_case_2_lines_0_width_100pt_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_fitcontent_case_2_lines_0_width_100pt_height_100px"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text =
            "HelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorld"
        textView.setFontSize(20F.dpToPx())
        val widthSpec =
            View.MeasureSpec.makeMeasureSpec(MOCK_SCREEN_WIDTH.toInt(), View.MeasureSpec.AT_MOST)
        textView.measure(widthSpec, 0)

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_case_2_lines_0_width_100px_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_fitcontent_case_2_lines_0_width_100px_height_100px"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        val widthSpec =
            View.MeasureSpec.makeMeasureSpec(100F.dpToPx().toInt(), View.MeasureSpec.AT_MOST)
        textView.measure(widthSpec, 0)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
    }

    @Test(expected = IllegalArgumentException::class)
    fun template_text_fitcontent_case_2_lines_0_width_null_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_fitcontent_case_2_lines_0_width_null_height_100px"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)
    }

    @Test
    fun template_text_fitcontent_case_2_lines_5_width_100pt_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_fitcontent_case_2_lines_5_width_100pt_height_100px"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
    }

    @Test
    fun template_text_text_process() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_text_process"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["text"] = "HelloWorld"
        })
        templateData.dataListener = object : GXTemplateEngine.GXIDataListener {

            override fun onTextProcess(gxTextData: GXTemplateEngine.GXTextData): CharSequence? {
                Assert.assertEquals("HelloWorld", gxTextData.text)
                return "GaiaX"
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals("GaiaX", (rootView.child(0) as GXText).text)
    }

    @Test
    fun template_text_fitcontent_case_1_lines_null_width_null_height_20px_lead_to_error_update_flexbox_size_minsize_maxsize() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_fitcontent_case_1_lines_null_width_null_height_20px_lead_to_error_update_flexbox_size_minsize_maxsize"
        )

        var templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["text"] = "0"
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "0"
        textView.setFontSize(14F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(15F.dpToPx(), rootView.child(0).height())

        //////////////////////////////////////////////////////////////////////////////////////////////////

        templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["text"] = "300"
        })

        val rootView1 = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView1, templateData)
        val textView1 = GXText(GXMockUtils.context)
        textView1.text = "300"
        textView1.setFontSize(14F.dpToPx())
        textView1.measure(0, 0)

        Assert.assertEquals(textView1.measuredWidth.toFloat(), rootView1.child(0).width())
        Assert.assertEquals(15F.dpToPx(), rootView1.child(0).height())
    }

    @Test
    fun template_view_property_display_none() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_view_property_display_none"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(View.GONE, rootView.child(0).visibility)

        Assert.assertEquals(0F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(0F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_view_property_display_flex() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_view_property_display_flex"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(View.VISIBLE, rootView.child(0).visibility)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_view_property_hidden_true() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_view_property_hidden_true"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(View.INVISIBLE, rootView.child(0).visibility)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_view_property_hidden_false() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_view_property_hidden_false"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(View.VISIBLE, rootView.child(0).visibility)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_view_property_display_hidden() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_view_property_display_hidden"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(View.VISIBLE, rootView.child(0).visibility)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_view_property_opacity() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_view_property_opacity"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(0F, rootView.child(0).alpha)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_view_property_overflow_visible() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_view_property_overflow_visible"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(false, (rootView.child(0) as? ViewGroup)?.clipChildren)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_view_property_overflow_hidden() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_view_property_overflow_hidden"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(true, (rootView.child(0) as? ViewGroup)?.clipChildren)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_view_property_background_color() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_view_property_background_color"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(true, rootView.child(0).background is GradientDrawable)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_view_property_background_image() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_view_property_background_image"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(true, rootView.child(0).background is GradientDrawable)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_property_padding() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_padding"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingLeft.toFloat())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingTop.toFloat())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingRight.toFloat())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingBottom.toFloat())

    }

    @Test
    fun template_text_property_padding_and_padding_bottom() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_padding_and_padding_bottom"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingLeft.toFloat())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingTop.toFloat())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingRight.toFloat())
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).paddingBottom.toFloat())

    }

    @Test
    fun template_text_property_font_size() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_font_size"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(20F.dpToPx(), rootView.child<GXText>(0).textSize)
    }

    @Test
    fun template_text_property_font_family() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_font_family"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(true, rootView.child<GXText>(0).typeface != null)
    }

    @Test
    fun template_text_property_font_weight() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_font_weight"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(true, rootView.child<GXText>(0).typeface.isBold)
    }

    @Test
    fun template_text_property_font_color_default() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_font_color_default"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(null, rootView.child<GXText>(0).paint.shader)
        Assert.assertEquals(Color.BLACK, rootView.child<GXText>(0).currentTextColor)
    }

    @Test
    fun template_text_property_font_color() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_font_color"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        val value = GXColor.create("#00ff00")?.value
        Assert.assertEquals(value, rootView.child<GXText>(0).currentTextColor)
    }

    @Test
    fun template_text_property_text_overflow_clip() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_text_overflow_clip"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(null, rootView.child<GXText>(0).ellipsize)
    }

    @Test
    fun template_text_property_text_overflow() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_text_overflow"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(TextUtils.TruncateAt.END, rootView.child<GXText>(0).ellipsize)
    }

    @Test
    fun template_text_property_text_overflow_ellipsis() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_text_overflow_ellipsis"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(TextUtils.TruncateAt.END, rootView.child<GXText>(0).ellipsize)
    }

    @Test
    fun template_text_property_text_decoration() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_text_decoration"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(1283, rootView.child<GXText>(0).paint.flags)
    }

    @Test
    fun template_text_property_text_decoration_line_through() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_text_decoration_line_through"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(Paint.STRIKE_THRU_TEXT_FLAG, rootView.child<GXText>(0).paint.flags)
    }

    @Test
    fun template_text_property_text_decoration_underline() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_text_decoration_underline"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(Paint.UNDERLINE_TEXT_FLAG, rootView.child<GXText>(0).paint.flags)
    }

    @Test
    fun template_text_property_line_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_line_height"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(50F.dpToPx(), rootView.child<GXText>(0).lineHeight.toFloat())
    }

    @Test
    fun template_text_property_line_height_same_size_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_text_property_line_height_same_size_height"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(50F.dpToPx(), rootView.child<GXText>(0).lineHeight.toFloat())

        Assert.assertEquals(
            Gravity.CENTER_VERTICAL,
            rootView.child<GXText>(0).gravity.and(Gravity.CENTER_VERTICAL)
        )
    }

    @Test
    fun template_image_property_mode() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val templateItem = GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "integration",
                "template_image_property_mode"
            )

            val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

            val rootView = GXTemplateEngine.instance.createView(templateItem, size)

            GXTemplateEngine.instance.bindData(rootView, templateData)

            Assert.assertEquals(1, rootView.childCount())

            Assert.assertEquals(
                ImageView.ScaleType.FIT_XY,
                rootView.child<GXImageView>(0).scaleType
            )
        }
    }

    @Test
    fun template_image_property_mode_scaleToFill() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val templateItem = GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "integration",
                "template_image_property_mode_scaleToFill"
            )

            val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

            val rootView = GXTemplateEngine.instance.createView(templateItem, size)

            GXTemplateEngine.instance.bindData(rootView, templateData)

            Assert.assertEquals(1, rootView.childCount())

            Assert.assertEquals(
                ImageView.ScaleType.FIT_XY,
                rootView.child<GXImageView>(0).scaleType
            )
        }
    }

    @Test
    fun template_image_property_mode_aspectFit() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val templateItem = GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "integration",
                "template_image_property_mode_aspectFit"
            )

            val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

            val rootView = GXTemplateEngine.instance.createView(templateItem, size)

            GXTemplateEngine.instance.bindData(rootView, templateData)

            Assert.assertEquals(1, rootView.childCount())

            Assert.assertEquals(
                ImageView.ScaleType.FIT_CENTER,
                rootView.child<GXImageView>(0).scaleType
            )
        }
    }

    @Test
    fun template_image_property_mode_aspectFill() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val templateItem = GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "integration",
                "template_image_property_mode_aspectFill"
            )

            val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

            val rootView = GXTemplateEngine.instance.createView(templateItem, size)

            GXTemplateEngine.instance.bindData(rootView, templateData)

            Assert.assertEquals(1, rootView.childCount())

            Assert.assertEquals(
                ImageView.ScaleType.CENTER_CROP,
                rootView.child<GXImageView>(0).scaleType
            )
        }
    }

    @Test
    fun template_image_property_mode_left_scale() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val templateItem = GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "integration",
                "template_image_property_mode_left_scale"
            )

            val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

            val rootView = GXTemplateEngine.instance.createView(templateItem, size)

            GXTemplateEngine.instance.bindData(rootView, templateData)

            Assert.assertEquals(1, rootView.childCount())

            Assert.assertEquals(
                ImageView.ScaleType.MATRIX,
                rootView.child<GXImageView>(0).scaleType
            )
        }
    }

    @Test
    fun template_image_property_mode_left_crop() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {

            val templateItem = GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "integration",
                "template_image_property_mode_left_crop"
            )

            val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

            val rootView = GXTemplateEngine.instance.createView(templateItem, size)

            GXTemplateEngine.instance.bindData(rootView, templateData)

            Assert.assertEquals(1, rootView.childCount())

            Assert.assertEquals(
                ImageView.ScaleType.MATRIX,
                rootView.child<GXImageView>(0).scaleType
            )
        }
    }

    @Test
    fun template_shadow() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "integration", "template_shadow")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(200F.dpToPx(), rootView.height())

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(true, rootView.child(0) is GXShadowLayout)
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(true, rootView.child(1) is GXView)
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_create_bind_finalize_release() {

        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "integration", "template_normal")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(true, GXTemplateContext.getContext(rootView) != null)

        // manual call finalized
        val context = (rootView as? GXIRootView)?.getTemplateContext()
        (rootView as? GXIRootView)?.manualRelease()

        Assert.assertEquals(true, GXTemplateContext.getContext(rootView) == null)

        Assert.assertEquals(null, context?.rootNode)
        Assert.assertEquals(null, context?.rootView)
        Assert.assertEquals(null, context?.data)
        Assert.assertEquals(null, context?.visualTemplateNode)
    }

    @Test
    fun template_event_tap_listener() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_event_tap_listener"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var gesture: GXTemplateEngine.GXGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        targetView?.performClick()

        Assert.assertEquals(true, targetView != null)
        Assert.assertEquals(true, gesture != null)
        Assert.assertEquals("tap", gesture?.gestureType)
        Assert.assertEquals(true, gesture?.view == targetView)
        Assert.assertEquals(-1, gesture?.index)
        Assert.assertEquals("target", gesture?.nodeId)
        Assert.assertEquals("template_event_tap_listener", gesture?.templateItem?.templateId)
        Assert.assertEquals(JSONObject().apply {
            this["type"] = "tap"
            this["value"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        }.toJSONString(), gesture?.eventParams?.toJSONString())
    }

    @Test
    fun template_event_tap_listener_display_none() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_event_tap_listener_display_none"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var gesture: GXTemplateEngine.GXGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        targetView?.performClick()

        Assert.assertEquals(null, gesture)
    }

    @Test
    fun template_event_tap_listener_hidden_true() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_event_tap_listener_hidden_true"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var gesture: GXTemplateEngine.GXGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        targetView?.performClick()

        Assert.assertEquals(null, gesture)
    }

    @Test
    fun template_event_longpress_listener() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_event_longpress_listener"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var gesture: GXTemplateEngine.GXGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        targetView?.performLongClick()

        Assert.assertEquals(true, targetView != null)
        Assert.assertEquals(true, gesture != null)
        Assert.assertEquals("longpress", gesture?.gestureType)
        Assert.assertEquals(true, gesture?.view == targetView)
        Assert.assertEquals(-1, gesture?.index)
        Assert.assertEquals("target", gesture?.nodeId)
        Assert.assertEquals(JSONObject().apply {
            this["type"] = "longpress"
            this["value"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        }.toJSONString(), gesture?.eventParams?.toJSONString())
    }

    @Test
    fun template_event_track_listener() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_event_track_listener"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var track: GXTemplateEngine.GXTrack? = null

        templateData.trackListener = object : GXTemplateEngine.GXITrackListener {
            override fun onTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                track = gxTrack
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        Assert.assertEquals(true, track != null)
        Assert.assertEquals(true, track?.view == targetView)
        Assert.assertEquals(-1, track?.index)
        Assert.assertEquals("target", track?.nodeId)
        Assert.assertEquals(JSONObject().apply {
            this["type"] = "tap"
            this["value"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        }.toJSONString(), track?.trackParams?.toJSONString())
    }

    @Test
    fun template_event_track_listener_display_none() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_event_track_listener_display_none"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var track: GXTemplateEngine.GXTrack? = null

        templateData.trackListener = object : GXTemplateEngine.GXITrackListener {
            override fun onTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                track = gxTrack
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(null, track)
    }

    @Test
    fun template_event_track_listener_hidden_true() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_event_track_listener_hidden_true"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var track: GXTemplateEngine.GXTrack? = null

        templateData.trackListener = object : GXTemplateEngine.GXITrackListener {
            override fun onTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                track = gxTrack
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(null, track)
    }

    @Test
    fun template_lifecycle() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "integration", "template_normal")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        val templateContext = GXTemplateEngine.instance.getGXTemplateContext(rootView)

        Assert.assertEquals(GXTemplateContext.LIFE_ON_CREATE, templateContext?.lifeStatus)
        Assert.assertEquals(GXTemplateContext.LIFE_ON_NONE, templateContext?.visibleStatus)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(GXTemplateContext.LIFE_ON_READY, templateContext?.lifeStatus)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(GXTemplateContext.LIFE_ON_REUSE, templateContext?.lifeStatus)

        GXTemplateEngine.instance.onAppear(rootView)

        Assert.assertEquals(GXTemplateContext.LIFE_ON_VISIBLE, templateContext?.visibleStatus)

        GXTemplateEngine.instance.onDisappear(rootView)

        Assert.assertEquals(GXTemplateContext.LIFE_ON_INVISIBLE, templateContext?.visibleStatus)

        // manual call finalized
        (rootView as? GXIRootView)?.manualRelease()

        Assert.assertEquals(GXTemplateContext.LIFE_ON_NONE, templateContext?.lifeStatus)
        Assert.assertEquals(GXTemplateContext.LIFE_ON_NONE, templateContext?.visibleStatus)

    }

    @Test
    fun template_animation_lottie_local() {

    }

    @Test
    fun template_animation_lottie_remote() {

        // 库有一些BUG，等待官方处理
        // htts://github.com/android/android-test/issues/1199
//        val scenario = ActivityScenario.launch(GXEngineActivityTest::class.java)

//        Assert.assertEquals(Activity.RESULT_OK, scenario.result.resultCode)

//        scenario.onActivity { activity ->
//
//            val latch = CountDownLatch(1)
//
//            val templateItem = GXEngine.GXTemplateItem(GXMockUtils.context, "integration", "template_animation_lottie_remote")
//
//            val templateData = GXEngine.GXTemplateData(JSONObject())
//
//            var gxAnimation: GXEngine.GXAnimation? = null
//
//            templateData.animationListener = object : GXEngine.GXAnimationListener {
//
//                override fun onAnimation(animation: GXEngine.GXAnimation) {
//                    gxAnimation = animation
//                    latch.countDown()
//                }
//            }
//
//            val rootView = GXEngine.instance.createView(templateItem, size)
//
//            activity.testRootView.addView(rootView)
//
//            GXEngine.instance.bindData(rootView, templateData)
//
//            val targetView = GXEngine.instance.findViewById(rootView, "target")
//
//            Assert.assertEquals(false, latch.await(20, TimeUnit.SECONDS))
//
////        Assert.assertEquals(true, targetView != null)
////        Assert.assertEquals(true, gxAnimation != null)
//        }
    }

    @Test
    fun template_richtext_property_font_color_default() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_richtext_property_font_color_default"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(null, rootView.child<GXText>(0).paint.shader)
        Assert.assertEquals(Color.BLACK, rootView.child<GXText>(0).currentTextColor)
    }

    @Test
    fun template_richtext_property_font_color() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_richtext_property_font_color"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        val value = GXColor.create("#00ff00")?.value
        Assert.assertEquals(value, rootView.child<GXText>(0).currentTextColor)
    }

    @Test
    fun template_iconfont_property_font_color() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_iconfont_property_font_color"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        val value = GXColor.create("#00ff00")?.value
        Assert.assertEquals(value, rootView.child<GXText>(0).currentTextColor)
    }

    @Test
    fun template_iconfont_property_font_color_default() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_iconfont_property_font_color_default"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(null, rootView.child<GXText>(0).paint.shader)
        Assert.assertEquals(Color.BLACK, rootView.child<GXText>(0).currentTextColor)
    }

    @Test(expected = IllegalArgumentException::class)
    fun template_iconfont_property_font_family_null() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_iconfont_property_font_family_null"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)
    }

    @Test
    fun template_scroll_css_extend_modify_item() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_css_extend_modify_item"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(300F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_child_count() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_child_count"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(5, rootView.childCount())
        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_scroll_multi_type_item_two() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_multi_type_item_two"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject().apply {
                    this["type"] = 0
                })
                this.add(JSONObject().apply {
                    this["type"] = 1
                })
            }
        })
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(200F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_scroll_multi_type_item_one() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_multi_type_item_one"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_scroll_width_auto() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_width_auto"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_scroll_vertical_height_auto() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_vertical_height_auto"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(0F, rootView.height())
    }

    @Test
    fun template_scroll_item_spacing() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_item_spacing"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(5, rootView.childCount())
        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(0F, rootView.child(0).x)
        Assert.assertEquals(0F, rootView.child(0).y)

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
        Assert.assertEquals(9F.dpToPx() + 100F.dpToPx(), rootView.child(1).x)
        Assert.assertEquals(0F, rootView.child(1).y)
    }

    @Test
    fun template_scroll_edge_item_spacing() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_edge_item_spacing"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(5, rootView.childCount())
        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(9F.dpToPx(), rootView.child(0).x)
        Assert.assertEquals(0F, rootView.child(0).y)

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
        Assert.assertEquals(9F.dpToPx() + 9F.dpToPx() + 100F.dpToPx(), rootView.child(1).x)
        Assert.assertEquals(0F, rootView.child(1).y)
    }

    @Test
    fun template_scroll_modify_item() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_modify_item"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(200F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_height_auto() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_height_auto"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_height_percent_100_limit_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_height_percent_100"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, 100F.dpToPx())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_height_percent_100() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_height_percent_100"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(0F, rootView.height())
    }

    @Test
    fun template_scroll_height_fixed() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_scroll_height_fixed"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(200F.dpToPx(), rootView.height())
    }

    @Test
    fun template_grid_height_100pt_scroll_enable_true() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_grid_height_100pt_scroll_enable_true"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, 200F.dpToPx())
        )
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(200F.dpToPx(), rootView.height())
        Assert.assertEquals(true, (rootView as? GXGridView)?.layoutManager?.canScrollVertically())
        Assert.assertEquals(
            false,
            (rootView as? GXGridView)?.layoutManager?.canScrollHorizontally()
        )
    }

    @Test
    fun template_grid_height_flow() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_grid_height_flow"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, 200F.dpToPx())
        )
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(200F.dpToPx(), rootView.height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())

        Assert.assertEquals(
            true,
            (rootView.child(1) as? GXGridView)?.layoutManager?.canScrollVertically()
        )
        Assert.assertEquals(
            false,
            (rootView.child(1) as? GXGridView)?.layoutManager?.canScrollHorizontally()
        )
    }

    @Test
    fun template_grid_height_100px_scroll_enable_true() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_grid_height_100px_scroll_enable_true"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
        Assert.assertEquals(true, (rootView as? GXGridView)?.layoutManager?.canScrollVertically())
        Assert.assertEquals(
            false,
            (rootView as? GXGridView)?.layoutManager?.canScrollHorizontally()
        )
    }

    @Test
    fun template_grid_normal() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_grid_normal"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() * 3 + 9F.dpToPx() * 2, rootView.height())
    }

    @Test
    fun template_grid_extend_column() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_grid_extend_column"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() + 100F.dpToPx() + 9F.dpToPx() * 1, rootView.height())
    }

    @Test
    fun template_grid_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_grid_height_100px"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_grid_height_auto() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_grid_height_auto"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() * 3 + 9F.dpToPx() * 2, rootView.height())
    }

    @Test
    fun template_grid_item_spacing_raw_spacing() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_grid_item_spacing_raw_spacing"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(5, rootView.childCount())
        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() * 3 + 20F.dpToPx() * 2, rootView.height())

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(
            1080F.dpToPx() / 2 - 9F.dpToPx() / 2 - GXMockUtils.deviceGap(),
            rootView.child(0).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(0F, rootView.child(0).x)
        Assert.assertEquals(0F.dpToPx(), rootView.child(0).y)

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(
            1080F.dpToPx() / 2 - 9F.dpToPx() / 2 - GXMockUtils.deviceGap(),
            rootView.child(1).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
        Assert.assertEquals(
            1080F.dpToPx() / 2 + 9F.dpToPx() / 2 - GXMockUtils.deviceGap(),
            rootView.child(1).x
        )
        Assert.assertEquals(0F.dpToPx(), rootView.child(1).y)

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(
            1080F.dpToPx() / 2 - 9F.dpToPx() / 2 - GXMockUtils.deviceGap(),
            rootView.child(2).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(2).height())
        Assert.assertEquals(0F, rootView.child(2).x)
        Assert.assertEquals(100F.dpToPx() + 20F.dpToPx(), rootView.child(2).y)
    }

    @Test
    fun template_grid_item_spacing_raw_spacing_edge() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_grid_item_spacing_raw_spacing_edge"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(5, rootView.childCount())
        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(
            100F.dpToPx() * 3 + 20F.dpToPx() * 2 + 30F.dpToPx() * 2,
            rootView.height()
        )

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(
            1080F.dpToPx() / 2 - 9F.dpToPx() / 2 - 30F.dpToPx() - GXMockUtils.deviceGap(),
            rootView.child(0).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(30F.dpToPx(), rootView.child(0).x)
        Assert.assertEquals(30F.dpToPx(), rootView.child(0).y)

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(
            1080F.dpToPx() / 2 - 9F.dpToPx() / 2 - 30F.dpToPx() - GXMockUtils.deviceGap(),
            rootView.child(1).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
        Assert.assertEquals(
            1080F.dpToPx() / 2 + 9F.dpToPx() / 2 - GXMockUtils.deviceGap(),
            rootView.child(1).x
        )
        Assert.assertEquals(30F.dpToPx(), rootView.child(1).y)

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(
            1080F.dpToPx() / 2 - 9F.dpToPx() / 2 - 30F.dpToPx() - GXMockUtils.deviceGap(),
            rootView.child(2).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(2).height())
        Assert.assertEquals(30F.dpToPx(), rootView.child(2).x)
        Assert.assertEquals(100F.dpToPx() + 20F.dpToPx() + 30F.dpToPx(), rootView.child(2).y)
    }

    @Test
    fun template_grid_item_spacing_raw_spacing_edge_column_3() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_grid_item_spacing_raw_spacing_edge_column_3"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(5, rootView.childCount())
        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() * 2 + 20F.dpToPx() + 30F.dpToPx() * 2, rootView.height())

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(
            ((1080F.dpToPx() - 9F.dpToPx() * 2 - 30F.dpToPx() * 2) / 3).roundToInt().toFloat(),
            rootView.child(0).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(30F.dpToPx(), rootView.child(0).x)
        Assert.assertEquals(30F.dpToPx(), rootView.child(0).y)

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(
            ((1080F.dpToPx() - 9F.dpToPx() * 2 - 30F.dpToPx() * 2) / 3).roundToInt().toFloat(),
            rootView.child(3).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(3).height())
        Assert.assertEquals(30F.dpToPx(), rootView.child(3).x)
        Assert.assertEquals(100F.dpToPx() + 20F.dpToPx() + 30F.dpToPx(), rootView.child(3).y)
    }

    @Test
    fun template_nest() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "integration", "template_nest")

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() + 100F.dpToPx(), rootView.height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())

        Assert.assertEquals(2, rootView.child(0).childCount())
        Assert.assertEquals(0, rootView.child(1).childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).height())

        Assert.assertEquals(1080F.dpToPx() - 100F.dpToPx(), rootView.child(0).child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(1).height())

        Assert.assertEquals(2, rootView.child(0).child(1).childCount())

        Assert.assertEquals(
            1080F.dpToPx() - 100F.dpToPx(),
            rootView.child(0).child(1).child(0).width()
        )
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).child(1).child(0).height())

        Assert.assertEquals(
            1080F.dpToPx() - 100F.dpToPx(),
            rootView.child(0).child(1).child(1).width()
        )
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).child(1).child(1).height())
    }

    @Test
    fun template_nest_child_databinding_update_property_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_nest_child_databinding_update_property_height"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this[GXTemplateKey.FLEXBOX_SIZE_HEIGHT] = "200px"
        })

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() + 200F.dpToPx(), rootView.height())

        Assert.assertEquals(300F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(200F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_nest_parent_databinding_update_property_height_and_child_databinding_update_property_width() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_nest_parent_databinding_update_property_height_and_child_databinding_update_property_width"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this[GXTemplateKey.FLEXBOX_SIZE_HEIGHT] = "200px"
            this[GXTemplateKey.FLEXBOX_SIZE_WIDTH] = "300px"
        })

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() + 200F.dpToPx(), rootView.height())

        Assert.assertEquals(300F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(200F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_nest_parent_databinding_update_property_height_and_child_databinding_update_property_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_nest_parent_databinding_update_property_height_and_child_databinding_update_property_height"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this[GXTemplateKey.FLEXBOX_SIZE_HEIGHT] = "200px"
            this["data"] = JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_SIZE_HEIGHT] = "300px"
            }
        })

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(200F.dpToPx() + 100F.dpToPx(), rootView.height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(200F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_nest_databinding_override_width_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_nest_databinding_override_width_height"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() + 150F.dpToPx(), rootView.height())

        Assert.assertEquals(300F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(150F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())

        Assert.assertEquals(2, rootView.child(0).childCount())
        Assert.assertEquals(0, rootView.child(1).childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).height())

        Assert.assertEquals(300F.dpToPx() - 100F.dpToPx(), rootView.child(0).child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(1).height())

        Assert.assertEquals(2, rootView.child(0).child(1).childCount())

        Assert.assertEquals(
            300F.dpToPx() - 100F.dpToPx(),
            rootView.child(0).child(1).child(0).width()
        )
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).child(1).child(0).height())

        Assert.assertEquals(
            300F.dpToPx() - 100F.dpToPx(),
            rootView.child(0).child(1).child(1).width()
        )
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).child(1).child(1).height())
    }

    @Test
    fun template_nest_databinding_override_both() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_nest_databinding_override_both"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() + 200F.dpToPx(), rootView.height())

        Assert.assertEquals(300F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(200F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_nest_databinding_override_only_child() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_nest_databinding_override_only_child"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() + 200F.dpToPx(), rootView.height())

        Assert.assertEquals(300F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(200F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_nest_databinding_override_only_child_value() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_nest_databinding_override_only_child_value"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_SIZE_HEIGHT] = "200px"
            })
        )

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() + 200F.dpToPx(), rootView.height())

        Assert.assertEquals(300F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(200F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_nest_databinding_override_both_value() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_nest_databinding_override_both_value"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_SIZE_HEIGHT] = "200px"
                this["data"] = JSONObject().apply {
                    this[GXTemplateKey.FLEXBOX_SIZE_WIDTH] = "300px"
                }
            })
        )

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() + 200F.dpToPx(), rootView.height())

        Assert.assertEquals(300F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(200F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test(expected = NoSuchElementException::class)
    fun template_nest_scroll_nodes_self() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_nest_scroll_nodes_self"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        rootView.child(0).executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).height())
    }

    @Test
    fun template_nest_scroll_self_nodes() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_nest_scroll_self_nodes"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        rootView.child(0).executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).height())
    }

    @Test
    fun template_nest_css_override_width_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_nest_css_override_width_height"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() + 150F.dpToPx(), rootView.height())

        Assert.assertEquals(300F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(150F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())

        Assert.assertEquals(2, rootView.child(0).childCount())
        Assert.assertEquals(0, rootView.child(1).childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).height())

        Assert.assertEquals(300F.dpToPx() - 100F.dpToPx(), rootView.child(0).child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(1).height())

        Assert.assertEquals(2, rootView.child(0).child(1).childCount())

        Assert.assertEquals(
            300F.dpToPx() - 100F.dpToPx(),
            rootView.child(0).child(1).child(0).width()
        )
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).child(1).child(0).height())

        Assert.assertEquals(
            300F.dpToPx() - 100F.dpToPx(),
            rootView.child(0).child(1).child(1).width()
        )
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).child(1).child(1).height())
    }

    @Test
    fun template_merge_empty_nodes() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_merge_empty_nodes"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        Assert.assertEquals(true, rootView is GXView)
        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(3, rootView.childCount())

        Assert.assertEquals(true, rootView.child(0) is GXImageView)
        Assert.assertEquals(true, rootView.child(1) is GXText)
        Assert.assertEquals(true, rootView.child(2) is GXText)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx() - 100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(20F.dpToPx(), rootView.child(1).height())

        Assert.assertEquals(1080F.dpToPx() - 100F.dpToPx(), rootView.child(2).width())
        Assert.assertEquals(20F.dpToPx(), rootView.child(2).height())
    }

    @Test
    fun template_merge_empty_nodes_exclude_container_type() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "integration",
            "template_merge_empty_nodes_exclude_container_type"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(true, rootView.child(0) is GXImageView)
        Assert.assertEquals(true, rootView.child(1) is GXContainer)
    }
}