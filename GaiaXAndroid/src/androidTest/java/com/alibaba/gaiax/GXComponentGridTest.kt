package com.alibaba.gaiax

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.drawable.GXRoundCornerBorderGradientDrawable
import com.alibaba.gaiax.template.GXGridConfig
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * https://yuque.antfin-inc.com/gaia/document/xsndwb#ornz
 */
@RunWith(AndroidJUnit4::class)
class GXComponentGridTest : GXBaseTest() {

    @Test
    fun template_grid_single_line_same_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_single_line_same_height"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        val rect = rootView.child(0).layoutParams.getSuperFieldAny("mDecorInsets") as Rect
        Assert.assertEquals(0F, rect.top.toFloat())
        Assert.assertEquals(0F, rect.bottom.toFloat())
    }

    @Test
    fun template_grid_single_line_different_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_single_line_different_height"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(200F.dpToPx(), rootView.height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        val rect = rootView.child(0).layoutParams.getSuperFieldAny("mDecorInsets") as Rect
        // 275/2=137.5 四舍五入 138
        Assert.assertEquals(50F.dpToPx(), rect.top.toFloat() + 1)
        Assert.assertEquals(50F.dpToPx(), rect.bottom.toFloat() + 1)
    }

    @Test
    fun template_grid_load_more_fixed_footer_size() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_load_more_fixed_footer_size"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
            this["isHasMore"] = true
        })

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(100F.dpToPx(), rootView.child(3).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(3).height())
    }


    /**
     * 验证grid纵向滑动，footer是否加载正确
     */
    @Test
    fun template_grid_load_more_padding() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_load_more_padding"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
            this["isHasMore"] = true
        })

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx() - 10F.dpToPx() - 10F.dpToPx(), rootView.child(3).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(3).height())
    }

    /**
     * 验证grid纵向滑动，footer是否加载正确
     */
    @Test
    fun template_grid_load_more_hasMore_true() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_load_more"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
            this["isHasMore"] = true
        })

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(150F.dpToPx(), rootView.height())

        Assert.assertEquals(4, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(2).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(2).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(3).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(3).height())
    }

    @Test
    fun template_grid_load_more_hasMore_false() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_load_more"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
            this["isHasMore"] = false
        })

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(150F.dpToPx(), rootView.height())

        Assert.assertEquals(3, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(2).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(2).height())
    }

    @Test
    fun template_grid_load_more_hasMore_with_column_4() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_load_more_column_4"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
            this["isHasMore"] = true
        })

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(150F.dpToPx(), rootView.height())

        Assert.assertEquals(5, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(2).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(2).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(3).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(3).height())

        Assert.assertEquals(1080F.dpToPx(), rootView.child(4).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(4).height())
    }

    @Test
    fun template_grid_column_responsive() {
        GXRegisterCenter.instance.registerExtensionGrid(object : GXRegisterCenter.GXIExtensionGrid {
            override fun convert(
                propertyName: String,
                gxTemplateContext: GXTemplateContext,
                gridConfig: GXGridConfig
            ): Any? {
                if (propertyName == GXTemplateKey.GAIAX_LAYER_COLUMN && gridConfig.data.getBooleanValue(
                        "responsive-enable"
                    )
                ) {
                    if (gridConfig.column == 2) {
                        return 3
                    }
                }
                return null
            }

        })

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_column_responsive"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(360F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(360F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())

        Assert.assertEquals(360F.dpToPx(), rootView.child(2).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(2).height())
    }

    @Test
    fun template_grid_column_responsive_enable_false() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_column_responsive_enable_false"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(540F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(540F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_grid_column() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "grid", "template_grid_column")

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

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() + 100F.dpToPx() + 9F.dpToPx() * 1, rootView.height())
    }

    @Test
    fun template_grid_column_extend() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_column_extend"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() + 100F.dpToPx() + 9F.dpToPx() * 1, rootView.height())
    }

    @Test
    fun template_grid_column_item_width_average() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_column_item_width_average"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())

        Assert.assertEquals(1080F.dpToPx() / 3, rootView.child(0).width())
        Assert.assertEquals(1080F.dpToPx() / 3, rootView.child(1).width())
        Assert.assertEquals(1080F.dpToPx() / 3, rootView.child(2).width())
    }

    @Test
    fun template_grid_height_min_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_height_min_100px"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_grid_height_max_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_height_max_100px"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    /**
     * DIFF:
     * 优酷版本，在不可滑动的状态下，高度为坑位高度之和
     * 开源版本，高度为容器设置的高度
     */
    @Test
    fun template_grid_height_100px_youku_version() {

        GXRegisterCenter.instance.registerExtensionDynamicProperty(object :
            GXRegisterCenter.GXIExtensionDynamicProperty {

            override fun convert(params: GXRegisterCenter.GXIExtensionDynamicProperty.GXParams): Any? {
                if (params.propertyName == GXTemplateKey.GAIAX_CUSTOM_PROPERTY_GRID_COMPUTE_CONTAINER_HEIGHT) {
                    if (params.value == false) {
                        return true
                    }
                }
                return null
            }
        })

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
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

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() * 3, rootView.height())
    }

    /**
     * DIFF:
     * 优酷版本，在不可滑动的状态下，高度为坑位高度之和
     * 开源版本，高度为容器设置的高度
     */
    @Test
    fun template_grid_height_100px_opensource_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
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

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    /**
     * DIFF:
     * 优酷版本，在不可滑动的状态下，高度为坑位高度之和
     * 开源版本，高度为容器设置的高度
     */
    @Test
    fun template_grid_height_auto() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
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

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx() * 3, rootView.height())
    }

    @Test
    fun template_grid_width_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_width_100px"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(100F.dpToPx(), rootView.width())
    }

    @Test
    fun template_grid_width_min_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_width_min_100px"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(100F.dpToPx(), rootView.width())
    }

    @Test
    fun template_grid_width_100_percent() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_width_100_percent"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
    }

    @Test
    fun template_grid_width_flex_grow_horizontal() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_width_flex_grow_horizontal"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(0F.dpToPx(), rootView.width())
    }

    @Test
    fun template_grid_width_max_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_width_max_100px"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(0F.dpToPx(), rootView.width())
    }

    @Test
    fun template_grid_edge_insets() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_edge_insets"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())

        Assert.assertEquals((1080F.dpToPx() - 9F.dpToPx() * 2) / 2, rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(9F.dpToPx(), rootView.child(0).x)
        Assert.assertEquals(9F.dpToPx(), rootView.child(0).y)
    }

    @Test
    fun template_grid_item_spacing() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_item_spacing"
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

        rootView.executeRecyclerView()

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(
            (1080F.dpToPx() - 9F.dpToPx()) / 2 - GXMockUtils.deviceGap(),
            rootView.child(0).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(0F, rootView.child(0).x)
        Assert.assertEquals(0F.dpToPx(), rootView.child(0).y)

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(
            (1080F.dpToPx() - 9F.dpToPx()) / 2 - GXMockUtils.deviceGap(),
            rootView.child(1).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
        Assert.assertEquals(
            1080F.dpToPx() / 2 + 9F.dpToPx() / 2 - GXMockUtils.deviceGap(),
            rootView.child(1).x
        )
        Assert.assertEquals(0F.dpToPx(), rootView.child(1).y)
    }

    @Test
    fun template_grid_row_spacing() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_row_spacing"
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

        rootView.executeRecyclerView()

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(1080F.dpToPx() / 2, rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(0F, rootView.child(0).x)
        Assert.assertEquals(0F.dpToPx(), rootView.child(0).y)

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(1080F.dpToPx() / 2, rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
        Assert.assertEquals(1080F.dpToPx() / 2, rootView.child(1).x)
        Assert.assertEquals(0F.dpToPx(), rootView.child(1).y)

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(1080F.dpToPx() / 2, rootView.child(2).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(2).height())
        Assert.assertEquals(0F, rootView.child(2).x)
        Assert.assertEquals(100F.dpToPx() + 20F.dpToPx(), rootView.child(2).y)
    }

    /**
     * 未实现
     */
    @Test
    fun template_grid_aspect_ratio() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_aspect_ratio"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(100F.dpToPx(), rootView.width())
        Assert.assertEquals(0F.dpToPx(), rootView.height())
    }

    @Test
    fun template_grid_item_spacing_row_spacing() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_item_spacing_row_spacing"
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

        rootView.executeRecyclerView()

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(
            (1080F.dpToPx() - 9F.dpToPx()) / 2 - GXMockUtils.deviceGap(),
            rootView.child(0).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(0F, rootView.child(0).x)
        Assert.assertEquals(0F.dpToPx(), rootView.child(0).y)

        // 有0.5F被Stretch抹去了
        Assert.assertEquals(
            (1080F.dpToPx() - 9F.dpToPx()) / 2 - GXMockUtils.deviceGap(),
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
            (1080F.dpToPx() - 9F.dpToPx()) / 2 - GXMockUtils.deviceGap(),
            rootView.child(2).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(2).height())
        Assert.assertEquals(0F, rootView.child(2).x)
        Assert.assertEquals(100F.dpToPx() + 20F.dpToPx(), rootView.child(2).y)
    }

    /**
     * TODO:
     * 优酷版本
     * 开源版本，未实现
     */
    @Test
    fun template_grid_scrollable_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_scrollable_height_100px"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
        Assert.assertEquals(true, (rootView as? RecyclerView)?.layoutManager?.canScrollVertically())
        Assert.assertEquals(
            false,
            (rootView as? RecyclerView)?.layoutManager?.canScrollHorizontally()
        )
    }

    @Test
    fun template_grid_scrollable_height_100_percent() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_scrollable_height_100_percent"
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

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, 200F.dpToPx())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(200F.dpToPx(), rootView.height())
        Assert.assertEquals(true, (rootView as? RecyclerView)?.layoutManager?.canScrollVertically())
        Assert.assertEquals(
            false,
            (rootView as? RecyclerView)?.layoutManager?.canScrollHorizontally()
        )
    }

    @Test
    fun template_grid_scrollable_height_flow_root() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_scrollable_height_flow_root"
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

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, 200F.dpToPx())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

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
            (rootView.child(1) as? RecyclerView)?.layoutManager?.canScrollVertically()
        )
        Assert.assertEquals(
            false,
            (rootView.child(1) as? RecyclerView)?.layoutManager?.canScrollHorizontally()
        )
    }

    @Test
    fun template_grid_background_color() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "grid",
            "template_grid_background_color"
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

        rootView.executeRecyclerView()

        Assert.assertEquals(
            Color.parseColor("#e4e4e4"),
            (rootView.background as? GradientDrawable)?.colors?.get(0)
        )
    }

    @Test
    fun template_grid_shadow() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "grid", "template_grid_shadow")

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

        rootView.executeRecyclerView()

        Assert.assertEquals(1, rootView.childCount())
    }

    @Test
    fun template_grid_border() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "grid", "template_grid_border")

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

        rootView.executeRecyclerView()

        Assert.assertEquals(
            true,
            rootView.child(0).foreground is GXRoundCornerBorderGradientDrawable
        )
        Assert.assertEquals(
            0F,
            (rootView.child(0).foreground as? GradientDrawable)?.cornerRadii?.get(0)
        )
    }

    @Test
    fun template_grid_radius() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "grid", "template_grid_radius")

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

        rootView.executeRecyclerView()

        Assert.assertEquals(true, (rootView.child(0).clipToOutline))
    }
}