package com.alibaba.gaiax

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.GXSize.Companion.ptToPx
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXCommonTest : GXBaseTest() {

    /**
     * TODO: bad case
     * https://github.com/alibaba/GaiaX/issues/131
     */
    // @Test
    fun template_root_margin_flex_grow() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_root_margin_flex_grow"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject())
        )

        Assert.assertEquals(100F.dpToPx(), rootView.height())
        Assert.assertEquals(375F.dpToPx() - 20F.dpToPx(), rootView.width())
    }

    @Test
    fun template_position_relative_left() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_position_relative_left"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject())
        )

        Assert.assertEquals(0F.dpToPx(), rootView.child(0).x())
    }

    @Test
    fun template_position_relative_databinding_left() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_position_relative_databinding_left"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_POSITION_LEFT] = "30px"
            })
        )

        Assert.assertEquals(0F.dpToPx(), rootView.child(0).x())
    }

    @Test
    fun template_position_absolute_databinding_left() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_position_absolute_databinding_left"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_POSITION_LEFT] = "30px"
            })
        )

        Assert.assertEquals(30F.dpToPx(), rootView.child(0).x())
    }

    @Test
    fun template_design_token_color() {
        GXRegisterCenter.instance.extensionColor = GXProcessorColor()

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_design_token_color"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(true, rootView?.background is GradientDrawable)
        Assert.assertEquals(Color.RED, (rootView?.background as GradientDrawable).colors?.get(0))

        GXRegisterCenter.instance.extensionColor = null
    }

    @Test
    fun template_design_token_dimen() {
        GXRegisterCenter.instance.extensionSize = GXExtensionSize()

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_design_token_dimen"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F, rootView.height())

        GXRegisterCenter.instance.extensionSize = null
    }

    @Test
    fun template_normal_nest_normal() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_normal_nest_normal"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

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
            1080F.dpToPx() - 100F.dpToPx(), rootView.child(0).child(1).child(0).width()
        )
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).child(1).child(0).height())

        Assert.assertEquals(
            1080F.dpToPx() - 100F.dpToPx(), rootView.child(0).child(1).child(1).width()
        )
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).child(1).child(1).height())
    }

    @Test
    fun template_normal_nest_container() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_normal_nest_container"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["nodes"] = JSONArray().apply {
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                }
            })
        )

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        rootView.child(0).executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).child(0).height())
    }

    @Test
    fun template_container_nest_template_judegment_condition() {
        GXRegisterCenter.instance.registerExtensionCompatibility(
            GXRegisterCenter.GXExtensionCompatibilityConfig().apply {
                this.isCompatibilityContainerNestTemplateJudgementCondition = true
            })

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_container_nest_template_judegment_condition"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["nodes"] = JSONArray().apply {
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                }
            })
        )

        rootView.child(0).executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).child(0).height())

        GXRegisterCenter.instance.registerExtensionCompatibility(
            GXRegisterCenter.GXExtensionCompatibilityConfig().apply {
                this.isCompatibilityContainerNestTemplateJudgementCondition = false
            })
    }

    @Test
    fun template_databinding_nest_scroll_nodes_self_youku_version() {
        GXRegisterCenter.instance.registerExtensionCompatibility(
            GXRegisterCenter.GXExtensionCompatibilityConfig().apply {
                this.isCompatibilityContainerDataPassSequence = true
            })

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_databinding_nest_scroll_nodes_self"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["nodes"] = JSONArray().apply {
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                }
            })
        )

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        rootView.child(0).executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).child(0).height())

        GXRegisterCenter.instance.registerExtensionCompatibility(
            GXRegisterCenter.GXExtensionCompatibilityConfig().apply {
                this.isCompatibilityContainerDataPassSequence = false
            })
    }

    @Test(expected = IllegalArgumentException::class)
    fun template_databinding_nest_scroll_nodes_self() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_databinding_nest_scroll_nodes_self"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["nodes"] = JSONArray().apply {
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                }
            })
        )

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        rootView.child(0).executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).height())
    }

    @Test
    fun template_databinding_nest_scroll_self_nodes() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_databinding_nest_scroll_self_nodes"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["nodes"] = JSONArray().apply {
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                }
            })
        )

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        rootView.child(0).executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).child(0).height())
    }

    @Test
    fun template_nest_css_override_width_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_nest_css_override_width_height"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["nodes"] = JSONArray().apply {
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                }
            })
        )

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
            300F.dpToPx() - 100F.dpToPx(), rootView.child(0).child(1).child(0).width()
        )
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).child(1).child(0).height())

        Assert.assertEquals(
            300F.dpToPx() - 100F.dpToPx(), rootView.child(0).child(1).child(1).width()
        )
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).child(1).child(1).height())
    }

    @Test
    fun template_nest_databinding_override_width_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_nest_databinding_override_width_height"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["nodes"] = JSONArray().apply {
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                }
            })
        )

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
            300F.dpToPx() - 100F.dpToPx(), rootView.child(0).child(1).child(0).width()
        )
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).child(1).child(0).height())

        Assert.assertEquals(
            300F.dpToPx() - 100F.dpToPx(), rootView.child(0).child(1).child(1).width()
        )
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).child(1).child(1).height())
    }

    @Test
    fun template_nest_databinding_override_both() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_nest_databinding_override_both"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["nodes"] = JSONArray().apply {
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
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

    @Test
    fun template_nest_databinding_override_only_child() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_nest_databinding_override_only_child"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["nodes"] = JSONArray().apply {
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
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

    @Test
    fun template_nest_databinding_override_only_child_value() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_nest_databinding_override_only_child_value"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
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
            GXMockUtils.context, "common", "template_nest_databinding_override_both_value"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
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

    @Test
    fun template_nest_databinding_override_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_nest_databinding_override_height"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
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
    fun template_nest_parent_databinding_update_property_height_and_child_databinding_update_property_width() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "common",
            "template_nest_databinding_parent_update_property_height_and_child_update_property_width"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_SIZE_HEIGHT] = "200px"
                this[GXTemplateKey.FLEXBOX_SIZE_WIDTH] = "300px"
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
    fun template_nest_databinding_parent_update_property_height_and_child_update_property_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "common",
            "template_nest_databinding_parent_update_property_height_and_child_update_property_height"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_SIZE_HEIGHT] = "200px"
                this["data"] = JSONObject().apply {
                    this[GXTemplateKey.FLEXBOX_SIZE_HEIGHT] = "300px"
                }
            })
        )

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(200F.dpToPx() + 100F.dpToPx(), rootView.height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(200F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    /**
     * 验证数据绑定
     */
    @Test
    fun template_databinding() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "common", "template_databinding")
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["title"] = "HelloWorld"
            })
        )

        Assert.assertEquals("HelloWorld", (rootView.child(0) as TextView).text)
    }

    /**
     * 验证数据绑定，普通嵌套模板的数据传递
     */
    @Test
    fun template_databinding_nest_normal_template() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_databinding_nest_normal_template"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView, GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["data"] = JSONObject().apply {
                    this["title"] = "HelloWorld"
                }
            })
        )

        val text1 = GXTemplateEngine.instance.getGXViewById(rootView, "text1") as? TextView
        Assert.assertEquals("HelloWorld", text1?.text)
    }

    @Test
    fun template_pt_change_screen_width() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_pt_change_screen_width"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(100f.ptToPx(), rootView.height())

        GXScreenUtils.isDebug = true
        GXScreenUtils.screenWidth = 1080F
        GXScreenUtils.screenHeight = 1080F

        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject()),
            GXTemplateEngine.GXMeasureSize(300F.dpToPx(), null)
        )

        Assert.assertEquals(100f.ptToPx().toInt().toFloat(), rootView.height())

        GXScreenUtils.isDebug = true
    }

    @Test
    fun template_pt_change_screen_width_scroll() {
        GXScreenUtils.isDebug = true

        GXScreenUtils.screenWidth = 750F.dpToPx()
        GXScreenUtils.screenHeight = 750F.dpToPx()

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "common", "template_pt_change_screen_width_scroll"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(750F.dpToPx(), null)
        )
        val gxTemplateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })
        GXTemplateEngine.instance.bindData(rootView, gxTemplateData)
        rootView.executeRecyclerView()

        Assert.assertEquals(rootView.height(), rootView.child(0).height())
        Assert.assertEquals(rootView.child(0).height(), rootView.child(0).child(0).height())

        GXScreenUtils.screenWidth = 375F.dpToPx()
        GXScreenUtils.screenHeight = 750F.dpToPx()

        GXTemplateEngine.instance.bindData(
            rootView, gxTemplateData, GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        rootView.executeRecyclerView()

        Assert.assertEquals(rootView.height(), rootView.child(0).height())
        Assert.assertEquals(rootView.child(0).height(), rootView.child(0).child(0).height())

        GXScreenUtils.isDebug = false
    }

    @Test
    fun ext_json() {

        val data = JSONObject().apply {
            this["title"] = "title"

            this["int"] = 0
            this["int_string"] = "0"

            this["long"] = 1L
            this["long_string"] = "1"

            this["float"] = 2.0
            this["float_string"] = "2"

            this["double"] = 2.0
            this["double_string"] = "2"

            this["boolean"] = true
            this["boolean_string"] = "true"

            this["string"] = "title"
            this["string_int"] = 1
            this["string_float"] = 2.0
            this["string_true"] = true
            this["string_long"] = 1L

            this["data"] = JSONObject().apply {
                this["title"] = "data.title"
                this["data"] = JSONObject().apply {
                    this["title"] = "data.data.title"
                }
            }
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject().apply {
                    this["title"] = "nodes[0].title"
                })
                this.add(JSONObject().apply {
                    this["title"] = "nodes[1].title"
                    this["nodes"] = JSONArray().apply {
                        this.add(JSONObject().apply {
                            this["title"] = "nodes[1].nodes[0].title"
                        })
                        this.add(JSONObject().apply {
                            this["title"] = "nodes[1].nodes[1].title"
                        })
                    }
                })
            }
        }
        Assert.assertEquals("title", data.getAnyExt("title"))
        Assert.assertEquals("data.data.title", data.getAnyExt("data.data.title"))
        Assert.assertEquals("nodes[0].title", data.getAnyExt("nodes[0].title"))
        Assert.assertEquals("nodes[1].nodes[1].title", data.getAnyExt("nodes[1].nodes[1].title"))
        Assert.assertEquals(true, data.getAnyExt("nodes[1].nodes") is JSONArray)
        Assert.assertEquals(null, data.getAnyExt("nodes[2]"))

        Assert.assertEquals(0, data.getIntExt("int"))
        Assert.assertEquals(0, data.getIntExt("int_string"))
        Assert.assertEquals(-1, data.getIntExt("int_null"))

        Assert.assertEquals(1L, data.getLongExt("long"))
        Assert.assertEquals(1L, data.getLongExt("long_string"))
        Assert.assertEquals(-1L, data.getLongExt("long_null"))

        Assert.assertEquals(true, data.getFloatExt("float") == 2.0.toFloat())
        Assert.assertEquals(true, data.getFloatExt("float_string") == 2.0.toFloat())
        Assert.assertEquals(true, data.getFloatExt("float_null") == -1F)

        Assert.assertEquals(true, data.getDoubleExt("double") == 2.0.toDouble())
        Assert.assertEquals(true, data.getDoubleExt("double_string") == 2.0.toDouble())
        Assert.assertEquals(true, data.getDoubleExt("double_null") == -1.0)

        Assert.assertEquals(true, data.getBooleanExt("boolean"))
        Assert.assertEquals(true, data.getBooleanExt("boolean_string"))
        Assert.assertEquals(false, data.getBooleanExt("boolean_null"))

        Assert.assertEquals("title", data.getStringExtCanNull("string"))
        Assert.assertEquals("1", data.getStringExtCanNull("string_int"))
        Assert.assertEquals("2.0", data.getStringExtCanNull("string_float"))
        Assert.assertEquals("true", data.getStringExtCanNull("string_true"))
        Assert.assertEquals("1", data.getStringExtCanNull("string_long"))
        Assert.assertEquals(null, data.getStringExtCanNull("string_null"))
    }

}