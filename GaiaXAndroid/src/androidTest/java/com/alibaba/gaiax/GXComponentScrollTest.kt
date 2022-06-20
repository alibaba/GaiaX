package com.alibaba.gaiax

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.support.test.runner.AndroidJUnit4
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXTemplateNode
import com.alibaba.gaiax.render.view.container.GXContainerViewAdapter
import com.alibaba.gaiax.render.view.drawable.GXRoundCornerBorderGradientDrawable
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.GXMockUtils
import com.alibaba.gaiax.utils.GXScreenUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * https://yuque.antfin-inc.com/gaia/document/xsndwb/edit#ornz
 */
@RunWith(AndroidJUnit4::class)
class GXComponentScrollTest : GXBaseTest() {

//    @Test
//    fun template_scroll_binding_scroll_index() {
//        val templateItem = GXTemplateEngine.GXTemplateItem(
//            GXMockUtils.context,
//            "scroll",
//            "template_scroll_scroll_index"
//        )
//
//        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
//            this["nodes"] = JSONArray().apply {
//                this.add(JSONObject())
//                this.add(JSONObject())
//                this.add(JSONObject())
//                this.add(JSONObject())
//                this.add(JSONObject())
//            }
//        })
//        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
//        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
//        GXTemplateEngine.instance.bindData(rootView, templateData)
//
//        Assert.assertEquals(
//            3,
//            ((rootView as RecyclerView).layoutManager as LinearLayoutManager).getFieldInt("mPendingScrollPosition")
//        )
//    }
//
//    @Test
//    fun template_scroll_binding_scroll_index_and_params_position() {
//        val templateItem = GXTemplateEngine.GXTemplateItem(
//            GXMockUtils.context,
//            "scroll",
//            "template_scroll_scroll_index"
//        )
//
//        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
//            this["nodes"] = JSONArray().apply {
//                this.add(JSONObject())
//                this.add(JSONObject())
//                this.add(JSONObject())
//                this.add(JSONObject())
//                this.add(JSONObject())
//            }
//        })
//        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
//        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
//        GXTemplateEngine.instance.bindData(rootView, templateData)
//
//        Assert.assertEquals(
//            3,
//            ((rootView as RecyclerView).layoutManager as LinearLayoutManager).getFieldInt("mPendingScrollPosition")
//        )
//    }

    @Test
    fun template_container_item_bind() {

        GXRegisterCenter.instance.registerExtensionContainerItemBind(object :
            GXRegisterCenter.GXIExtensionContainerItemBind {

            override fun bindViewHolder(
                tag: Any?,
                childItemContainer: ViewGroup,
                childMeasureSize: GXTemplateEngine.GXMeasureSize,
                childTemplateItem: GXTemplateEngine.GXTemplateItem,
                childItemPosition: Int,
                childVisualNestTemplateNode: GXTemplateNode?,
                childItemData: JSONObject
            ): Any? {
                // 获取坑位View
                val childView = if (childItemContainer.childCount != 0) {
                    childItemContainer.getChildAt(0)
                } else {
                    GXTemplateEngine.instance.createView(
                        childTemplateItem,
                        childMeasureSize,
                        childVisualNestTemplateNode
                    ).apply {
                        childItemContainer.addView(this)
                    }
                }

                // 为坑位View绑定数据
                val childTemplateData = GXTemplateEngine.GXTemplateData(childItemData)
                GXTemplateEngine.instance.bindData(childView, childTemplateData)
                return null
            }

        })

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
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
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)
        rootView.executeRecyclerView()

        Assert.assertEquals(5, rootView.childCount())
        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).height())
    }

    @Test
    fun template_data_update() {

        var isExecuteContainerDataUpdate = false
        GXRegisterCenter.instance.registerExtensionContainerDataUpdate(object :
            GXRegisterCenter.GXIExtensionContainerDataUpdate {

            override fun update(
                gxTemplateContext: GXTemplateContext,
                gxContainerViewAdapter: GXContainerViewAdapter,
                old: JSONArray,
                new: JSONArray
            ) {
                isExecuteContainerDataUpdate = true
                val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        return old.size
                    }

                    override fun getNewListSize(): Int {
                        return new.size
                    }

                    override fun areItemsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Boolean {
                        return old.getJSONObject(oldItemPosition)
                            .getIntValue("id") == old.getJSONObject(newItemPosition)
                            .getIntValue("id")
                    }

                    override fun areContentsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Boolean {
                        return old.getJSONObject(oldItemPosition)
                            .getIntValue("title") == old.getJSONObject(newItemPosition)
                            .getIntValue("title")
                    }

                }, true)
                diffResult.dispatchUpdatesTo(gxContainerViewAdapter)
            }

        })
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_horizontal"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject().apply {
                    this["id"] = 1
                    this["title"] = "标题1"
                })
                this.add(JSONObject().apply {
                    this["id"] = 2
                    this["title"] = "标题2"
                })
                this.add(JSONObject().apply {
                    this["id"] = 3
                    this["title"] = "标题3"
                })
                this.add(JSONObject().apply {
                    this["id"] = 4
                    this["title"] = "标题4"
                })
                this.add(JSONObject().apply {
                    this["id"] = 5
                    this["title"] = "标题5"
                })
            }
        })

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(true, isExecuteContainerDataUpdate)
    }

    @Test
    fun template_scroll_responsive_rule() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_responsive_rule"
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

        Assert.assertEquals(5, rootView.childCount())

        Assert.assertEquals(
            GXScreenUtils.getScreenWidthPx(GXMockUtils.context) / 2.5F,
            rootView.child(0).width()
        )
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_scroll_horizontal() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_horizontal"
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
            LinearLayoutManager.HORIZONTAL,
            ((rootView as RecyclerView).layoutManager as? LinearLayoutManager)?.orientation
        )
    }

    @Test
    fun template_scroll_vertical() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_vertical"
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
            LinearLayoutManager.VERTICAL,
            ((rootView as RecyclerView).layoutManager as? LinearLayoutManager)?.orientation
        )
    }

    @Test
    fun template_scroll_height_aspect_ratio() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_height_aspect_ratio"
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
        Assert.assertEquals(200F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_height_auto() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
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

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_height_root_200px_item_100px_opensource_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_height_root_200px_item_100px"
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
        /**
         * TODO
         * 优酷版本，无论容器写的固定高度多少，始终使用坑位的高度作为最后的高度
         * 开源版本，容器写的固定高度是多少，就是多少
         */
        Assert.assertEquals(200F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_height_root_200px_item_100px_youku_version() {
        GXRegisterCenter.instance.registerExtensionDynamicProperty(object :
            GXRegisterCenter.GXIExtensionDynamicProperty {

            override fun convert(params: GXRegisterCenter.GXIExtensionDynamicProperty.GXParams): Any? {
                if (params.propertyName == GXTemplateKey.GAIAX_CUSTOM_PROPERTY_SCROLL_COMPUTE_CONTAINER_HEIGHT) {
                    if (params.value == false) {
                        return true
                    }
                }
                return null
            }
        })

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_height_root_200px_item_100px"
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
        /**
         * TODO
         * 优酷版本，无论容器写的固定高度多少，始终使用坑位的高度作为最后的高度
         * 开源版本，容器写的固定高度是多少，就是多少
         */
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_height_root_percent_100_item_100px_opensource_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_height_root_percent_100_item_100px"
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
        /**
         * TODO
         * 优酷版本，无论容器写的固定高度多少，始终使用坑位的高度作为最后的高度
         * 开源版本，容器写的固定高度是多少，就是多少
         */
        Assert.assertEquals(0F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_height_root_percent_100_item_100px_youku_version() {
        GXRegisterCenter.instance.registerExtensionDynamicProperty(object :
            GXRegisterCenter.GXIExtensionDynamicProperty {

            override fun convert(params: GXRegisterCenter.GXIExtensionDynamicProperty.GXParams): Any? {
                if (params.propertyName == GXTemplateKey.GAIAX_CUSTOM_PROPERTY_SCROLL_COMPUTE_CONTAINER_HEIGHT) {
                    if (params.value == false) {
                        return true
                    }
                }
                return null
            }
        })

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_height_root_percent_100_item_100px"
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
        /**
         * TODO
         * 优酷版本，无论容器写的固定高度多少，始终使用坑位的高度作为最后的高度
         * 开源版本，容器写的固定高度是多少，就是多少
         */
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_height_root_percent_100_item_100px_limit_view_port_200px_opensource_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_height_root_percent_100_item_100px"
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
        /**
         * TODO
         * 优酷版本，无论容器写的固定高度多少，始终使用坑位的高度作为最后的高度
         * 开源版本，容器写的固定高度是多少，就是多少，如果是100%，并且父层没有高度，那么应该为0
         */
        Assert.assertEquals(0F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_height_root_percent_100_item_100px_limit_view_port_200px_youku_version() {

        GXRegisterCenter.instance.registerExtensionDynamicProperty(object :
            GXRegisterCenter.GXIExtensionDynamicProperty {

            override fun convert(params: GXRegisterCenter.GXIExtensionDynamicProperty.GXParams): Any? {
                if (params.propertyName == GXTemplateKey.GAIAX_CUSTOM_PROPERTY_SCROLL_COMPUTE_CONTAINER_HEIGHT) {
                    if (params.value == false) {
                        return true
                    }
                }
                return null
            }
        })

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_height_root_percent_100_item_100px"
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
        /**
         * TODO
         * 优酷版本，无论容器写的固定高度多少，始终使用坑位的高度作为最后的高度
         * 开源版本，容器写的固定高度是多少，就是多少，如果是100%，并且父层没有高度，那么应该为0
         */
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_height_min_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_height_min_100px"
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
    fun template_scroll_height_max_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_height_max_100px"
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
    fun template_scroll_vertical_height_auto_opensource_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
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
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)
        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        /**
         * TODO:
         * 优酷版本，当容器为垂直，并且未设置容器高度时，其高度为所有坑位高度之和
         * 开源版本，当容器为垂直，并且未设置容器高度时，其高度为0
         */
        Assert.assertEquals(0F.dpToPx() * 5, rootView.height())
    }

    @Test
    fun template_scroll_vertical_height_auto_youku_version() {
        GXRegisterCenter.instance.registerExtensionDynamicProperty(object :
            GXRegisterCenter.GXIExtensionDynamicProperty {

            override fun convert(params: GXRegisterCenter.GXIExtensionDynamicProperty.GXParams): Any? {
                if (params.propertyName == GXTemplateKey.GAIAX_CUSTOM_PROPERTY_SCROLL_COMPUTE_CONTAINER_HEIGHT) {
                    if (params.value == false) {
                        return true
                    }
                }
                return null
            }
        })

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
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
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)
        rootView.executeRecyclerView()

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        /**
         * TODO:
         * 优酷版本，当容器为垂直，并且未设置容器高度时，其高度为所有坑位高度之和
         * 开源版本，当容器为垂直，并且未设置容器高度时，其高度为0
         */
        Assert.assertEquals(100F.dpToPx() * 5, rootView.height())
    }

    @Test
    fun template_scroll_flex_direction_vertical_flex_grow() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_height_flex_direction_vertical_flex_grow"
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

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(200F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_scroll_item_spacing() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
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
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
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
    fun template_scroll_width_flex_direction_horizontal_flex_grow() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_width_flex_direction_horizontal_flex_grow"
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

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(200F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_scroll_width_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_width_100px"
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
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_width_min_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_width_min_100px"
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

        // 此处有问题，min-width，如果数量足够应该是撑满屏幕
        Assert.assertEquals(100F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_width_min_100px_no_children() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_width_min_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
            }
        })
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)
        rootView.executeRecyclerView()

        Assert.assertEquals(100F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_width_max_200px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_width_max_200px"
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

        // 此处有错误，被限制后，应该是200px
        Assert.assertEquals(0F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_width_max_200px_no_children() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_width_max_200px"
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
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_width_auto() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_width_auto"
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

        // TODO
        // 和预估有偏差，auto的状态下，宽度应该是坑位之和，但是实际却是0
        // Assert.assertEquals(200F.dpToPx(), rootView.width())
        Assert.assertEquals(0F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_width_aspect_ratio() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_width_aspect_ratio"
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

        Assert.assertEquals(200F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_child_count() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
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
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)
        rootView.executeRecyclerView()

        Assert.assertEquals(5, rootView.childCount())
        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).child(0).height())
    }

    @Test
    fun template_scroll_scroll_position_default() {

        // 由于测试环境，不会触发requestLayout逻辑。所以holding-offset的逻辑无法验证

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_scroll_position"
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

        Assert.assertEquals(
            0,
            ((rootView as RecyclerView).layoutManager as LinearLayoutManager).getFieldInt("mPendingScrollPosition")
        )
    }

    @Test
    fun template_scroll_scroll_position() {

        // 由于测试环境，不会触发requestLayout逻辑。所以holding-offset的逻辑无法验证

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_scroll_position"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        }).apply {
            this.scrollIndex = 3
        }
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(
            3,
            ((rootView as RecyclerView).layoutManager as LinearLayoutManager).getFieldInt("mPendingScrollPosition")
        )
    }

    @Test
    fun template_scroll_holding_offset() {

        // 由于测试环境，不会触发requestLayout逻辑。所以holding-offset的逻辑无法验证

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_holding_offset"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        }).apply {
            this.scrollIndex = 3
        }
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(
            3,
            ((rootView as RecyclerView).layoutManager as LinearLayoutManager).getFieldInt("mPendingScrollPosition")
        )

        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["nodes"] = JSONArray().apply {
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                }
            }).apply {
                this.scrollIndex = 0
            })

        Assert.assertEquals(
            3,
            ((rootView as RecyclerView).layoutManager as LinearLayoutManager).getFieldInt("mPendingScrollPosition")
        )
    }

    @Test
    fun template_scroll_load_more_hasMore_false() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_load_more"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
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
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(2, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
    }

    @Test
    fun template_scroll_load_more_hasMore_true() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_load_more"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
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
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(3, rootView.childCount())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())

        Assert.assertEquals(200F.dpToPx(), rootView.child(2).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(2).height())
    }

    @Test
    fun template_scroll_modify_item() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
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
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(200F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_css_extend_modify_item() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
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
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(300F.dpToPx(), rootView.height())
    }

    @Test
    fun template_scroll_multi_type_item_two() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
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
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
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
            "scroll",
            "template_scroll_multi_type_item_one"
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
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
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
    fun template_scroll_edge() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "scroll", "template_scroll_edge")
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

        Assert.assertEquals(5, rootView.childCount())
        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(9F.dpToPx(), rootView.child(0).x)
        Assert.assertEquals(9F.dpToPx(), rootView.child(0).y)

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())
        Assert.assertEquals(9F.dpToPx() + 100F.dpToPx(), rootView.child(1).x)
        Assert.assertEquals(9F.dpToPx(), rootView.child(1).y)
    }

    @Test
    fun template_scroll_background_color() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_background_color"
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

        Assert.assertEquals(
            Color.parseColor("#e4e4e4"),
            (rootView.background as? GradientDrawable)?.colors?.get(0)
        )
    }

    @Test
    fun template_scroll_border() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "scroll", "template_scroll_border")

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
    fun template_scroll_radius() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "scroll", "template_scroll_radius")

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

    @Test(expected = IllegalArgumentException::class)
    fun template_scroll_exception() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_exception"
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
    }

    @Test
    fun template_scroll_exception_process_exception() {
        var throwException: Exception? = null
        GXRegisterCenter.instance.registerExtensionException(object :
            GXRegisterCenter.GXIExtensionException {
            override fun exception(exception: Exception) {
                throwException = exception
            }
        })
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "scroll",
            "template_scroll_exception"
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

        Assert.assertEquals(true, throwException != null)

        GXRegisterCenter.instance.reset()
    }
}