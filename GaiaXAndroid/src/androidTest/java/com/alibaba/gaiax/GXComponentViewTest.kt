package com.alibaba.gaiax

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.basic.GXImageView
import com.alibaba.gaiax.render.view.basic.GXText
import com.alibaba.gaiax.render.view.basic.GXView
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXComponentViewTest : GXBaseTest() {

    @Test
    fun template_merge_empty_nodes() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "view",
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
            "view",
            "template_merge_empty_nodes_exclude_container_type"
        )

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(true, rootView.child(0) is ImageView)
        Assert.assertEquals(true, rootView.child(1) is androidx.recyclerview.widget.RecyclerView)
    }

    @Test
    fun template_aspect_ratio_height_to_width() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "view",
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
            "view",
            "template_aspect_ratio_width_to_height"
        )
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_width_max_size() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "view", "template_width_max_size")
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_width_flex_grow() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "view", "template_width_flex_grow")
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_height_max_size() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "view", "template_height_max_size")
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_width_min_size() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "view", "template_width_min_size")
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_height_min_size() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "view", "template_height_min_size")
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_height_flex_grow() {
        val rootView = GXTemplateEngine.instance.createView(
            GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "view",
                "template_height_flex_grow"
            ), size
        )
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_shadow() {
        val rootView = GXTemplateEngine.instance.createView(
            GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "view",
                "template_shadow"
            ), size
        )
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(200F.dpToPx(), rootView.height())

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_view_property_display_none() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "view",
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
    fun template_view_property_display_databinding_flex() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "view",
            "template_view_property_display_databinding"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["isFlex"] = true
        })
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(View.VISIBLE, rootView.child(1).visibility)

        Assert.assertEquals(1080F.dpToPx() - 100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_view_property_display_databinding_none() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "view",
            "template_view_property_display_databinding"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["isFlex"] = false
        })
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(View.GONE, rootView.child(1).visibility)

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(0F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(0F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_view_property_display_flex() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "view",
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
            "view",
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
            "view",
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
            "view",
            "template_view_property_display_hidden"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(View.INVISIBLE, rootView.child(0).visibility)
    }

    @Test
    fun template_view_property_opacity() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "view",
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
            "view",
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
            "view",
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
            "view",
            "template_view_property_background_color"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        Assert.assertEquals(true, rootView.child(0).background is GradientDrawable)
        Assert.assertEquals(
            Color.parseColor("#e4e4e4"),
            (rootView.child(0).background as GradientDrawable).colors?.get(0)
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_view_property_background_image() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "view",
            "template_view_property_background_image"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        Assert.assertEquals(true, rootView.child(0).background is GradientDrawable)
        Assert.assertEquals(
            Color.parseColor("#000000"),
            (rootView.child(0).background as GradientDrawable).colors?.get(0)
        )
        Assert.assertEquals(
            Color.parseColor("#ffffff"),
            (rootView.child(0).background as GradientDrawable).colors?.get(1)
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_border() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "view", "template_border")
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(null, (rootView.child(0).foreground as? GradientDrawable)?.shape)
        Assert.assertEquals(
            null,
            (rootView.child(0).foreground as? GradientDrawable)?.cornerRadii?.get(0)
        )
    }

    @Test
    fun template_radius() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "view", "template_radius")
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(true, (rootView.child(0).clipToOutline))
    }
}