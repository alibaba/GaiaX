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

package com.alibaba.gaiax.template

/**
 * 尺寸：px，pt，百分比，
 * 颜色：16进制，rgb，designToken
 * @suppress
 */
object GXTemplateKey {

    /**
     * https://developer.mozilla.org/en-US/docs/Web/CSS/backdrop-filter
     */
    const val STYLE_BACKDROP_FILTER = "backdrop-filter"

    /**
     * https://developer.mozilla.org/en-US/docs/Web/CSS/box-shadow
     */
    const val STYLE_BOX_SHADOW = "box-shadow"

    /**
     * https://www.runoob.com/cssref/pr-font-font-size.html
     * font-size 属性用于设置字体大小。
     */
    const val STYLE_FONT_SIZE = "font-size"

    /**
     * https://www.runoob.com/cssref/pr-font-font-family.html
     * font - family属性指定一个元素的字体。
     */
    const val STYLE_FONT_FAMILY = "font-family"

    /**
     * https://www.runoob.com/cssref/pr-text-color.html
     *
     * Color属性指定文本的颜色。
     */
    const val STYLE_FONT_COLOR = "color"

    /***
     * https://developer.mozilla.org/zh-CN/docs/Web/CSS/font-weight
     */
    const val STYLE_FONT_WEIGHT = "font-weight"

    /**
     * https://developer.mozilla.org/zh-CN/docs/Web/CSS/background-color
     */
    const val STYLE_BACKGROUND_COLOR = "background-color"


    const val STYLE_HIDDEN = "hidden"

    /**
     * https://www.runoob.com/cssref/pr-background-image.html
     * background-image 属性设置一个元素的背景图像。
     */
    const val STYLE_BACKGROUND_IMAGE = "background-image"

    /**
     * Gaiax 自定义属性
     * 行数
     */
    const val STYLE_FONT_LINES = "lines"

    /**
     * https://www.runoob.com/cssref/css3-pr-text-overflow.html
     * text-overflow属性指定当文本溢出包含它的元素，应该发生什么。
     */
    const val STYLE_FONT_TEXT_OVERFLOW = "text-overflow"

    /**
     * https://developer.mozilla.org/zh-CN/docs/Web/CSS/text-align
     * text-align属性指定元素文本的水平对齐方式。
     */
    const val STYLE_FONT_TEXT_ALIGN = "text-align"

    /**
     * https://developers.weixin.qq.com/miniprogram/dev/component/image.html
     * 图片的裁剪方式
     */
    const val STYLE_MODE = "mode"

    /**
     * 图片的裁剪方式类型
     */
    const val STYLE_MODE_TYPE = "mode-type"

    /**
     * https://developer.mozilla.org/zh-CN/docs/Web/CSS/border-radius
     * border-radius 属性是一个最多可指定四个 border -*- radius 属性的复合属性
     */
    const val STYLE_BORDER_RADIUS = "border-radius"

    const val STYLE_BORDER_BOTTOM_LEFT_RADIUS = "border-bottom-left-radius"

    const val STYLE_BORDER_BOTTOM_RIGHT_RADIUS = "border-bottom-right-radius"

    const val STYLE_BORDER_TOP_LEFT_RADIUS = "border-top-left-radius"

    const val STYLE_BORDER_TOP_RIGHT_RADIUS = "border-top-right-radius"

    /**
     * https://developer.mozilla.org/zh-CN/docs/Web/CSS/border-width
     * border-width属性设置一个元素的四个边框的宽度。。
     */
    const val STYLE_BORDER_WIDTH = "border-width"

    /**
     * https://developer.mozilla.org/zh-CN/docs/Web/CSS/border-color
     * border-color属性设置一个元素的四个边框颜色
     */
    const val STYLE_BORDER_COLOR = "border-color"

    /**
     * https://developer.mozilla.org/zh-CN/docs/Web/CSS/opacity
     * 设置一个div元素的透明度级别
     */
    const val STYLE_OPACITY = "opacity"

    /**
     * https://developer.mozilla.org/zh-CN/docs/Web/CSS/line-height
     * CSS 属性用于设置多行元素的空间量，如多行文本的间距。
     */
    const val STYLE_FONT_LINE_HEIGHT = "line-height"

    /**
     * https://developer.mozilla.org/zh-CN/docs/Web/CSS/text-decoration
     */
    const val STYLE_FONT_TEXT_DECORATION = "text-decoration"

    /**
     * 文字自适应
     */
    const val STYLE_FIT_CONTENT = "fit-content"

    /**
     * 弹性容器通过设置 display 属性的值为 flex 或 inline-flex将其定义为弹性容器。
     */
    const val FLEXBOX_DISPLAY = "display"

    /**
     * 设置页面布局方式，如果我们设置 direction 属性为 rtl (right-to-left),弹性子元素的排列方式也会改变，页面布局也跟着改变:
     */
    const val FLEXBOX_DIRECTION = "direction"

    /**
     * flex-direction 属性指定了弹性子元素在父容器中的位置。
     */
    const val FLEXBOX_FLEX_DIRECTION = "flex-direction"

    /**
     * flex-wrap 属性用于指定弹性盒子的子元素换行方式。
     */
    const val FLEXBOX_FLEX_WRAP = "flex-wrap"

    /**
     * overflow 属性规定当内容溢出元素框时发生的事情。
     */
    const val FLEXBOX_OVERFLOW = "overflow"

    /**
     * align-items 设置或检索弹性盒子元素在侧轴（纵轴）方向上的对齐方式。
     */
    const val FLEXBOX_ALIGN_ITEMS = "align-items"

    /**
     * align-self 属性用于设置弹性元素自身在侧轴（纵轴）方向上的对齐方式。
     */
    const val FLEXBOX_ALIGN_SELF = "align-self"

    /**
     * align-content 属性用于修改 flex-wrap 属性的行为。类似于 align-items, 但它不是设置弹性子元素的对齐，而是设置各个行的对齐。
     */
    const val FLEXBOX_ALIGN_CONTENT = "align-content"

    /**
     * 内容对齐（justify-content）属性应用在弹性容器上，把弹性项沿着弹性容器的主轴线（main axis）对齐。
     */
    const val FLEXBOX_JUSTIFY_CONTENT = "justify-content"

    /**
     * 规定元素的定位类型。
     */
    const val FLEXBOX_POSITION_TYPE = "position"

    const val FLEXBOX_POSITION_LEFT = "left"

    const val FLEXBOX_POSITION_RIGHT = "right"

    const val FLEXBOX_POSITION_TOP = "top"

    const val FLEXBOX_POSITION_BOTTOM = "bottom"

    /**
     * 外边距属性。
     */
    const val FLEXBOX_MARGIN = "margin"

    const val FLEXBOX_MARGIN_LEFT = "margin-left"

    const val FLEXBOX_MARGIN_RIGHT = "margin-right"

    const val FLEXBOX_MARGIN_TOP = "margin-top"

    const val FLEXBOX_MARGIN_BOTTOM = "margin-bottom"

    /**
     * 内边距属性
     */
    const val FLEXBOX_PADDING = "padding"

    const val FLEXBOX_PADDING_LEFT = "padding-left"

    const val FLEXBOX_PADDING_RIGHT = "padding-right"

    const val FLEXBOX_PADDING_TOP = "padding-top"

    const val FLEXBOX_PADDING_BOTTOM = "padding-bottom"

    /**
     * 边框属性
     */
    const val FLEXBOX_BORDER = "border"

    const val FLEXBOX_BORDER_LEFT = "border-left"

    const val FLEXBOX_BORDER_RIGHT = "border-right"

    const val FLEXBOX_BORDER_TOP = "border-top"

    const val FLEXBOX_BORDER_BOTTOM = "border-bottom"

    /**
     * flex 属性用于指定弹性子元素如何分配空间。
     * [ flex-grow ]：定义弹性盒子元素的扩展比率。
     */
    const val FLEXBOX_FLEX_GROW = "flex-grow"

    /**
     * flex 属性用于指定弹性子元素如何分配空间。
     * [ flex-shrink ]：定义弹性盒子元素的收缩比率。
     */
    const val FLEXBOX_FLEX_SHRINK = "flex-shrink"

    /**
     * flex 属性用于指定弹性子元素如何分配空间。
     * [ flex-basis ]：定义弹性盒子元素的默认基准值。
     */
    const val FLEXBOX_FLEX_BASIS = "flex-basis"

    /**
     * 规定页面内容包含框的尺寸和方向。
     */
    const val FLEXBOX_SIZE = "size"

    const val FLEXBOX_MIN_SIZE = "min-size"

    const val FLEXBOX_MAX_SIZE = "max-size"

    const val FLEXBOX_SIZE_WIDTH = "width"

    const val FLEXBOX_SIZE_HEIGHT = "height"

    const val FLEXBOX_MIN_WIDTH = "min-width"

    const val FLEXBOX_MIN_HEIGHT = "min-height"

    const val FLEXBOX_MAX_WIDTH = "max-width"

    const val FLEXBOX_MAX_HEIGHT = "max-height"

    /**
     * 纵横比
     */
    const val FLEXBOX_ASPECT_RATIO = "aspect-ratio"

    ///////////////////////////////////////
    ///////////////////////////////////////
    ///////////////////////////////////////

    const val GAIAX_DATA = "data"

    const val GAIAX_EVENT = "event"

    const val GAIAX_TRACK = "track"

    const val GAIAX_CONFIG = "config"

    const val GAIAX_ANIMATION = "animation"

    const val GAIAX_LAYER_EDGE_INSETS = "edge-insets"

    @Deprecated("Compatible")
    const val GAIAX_LAYER_LINE_SPACING = "line-spacing"

    @Deprecated("Compatible")
    const val GAIAX_LAYER_INTERITEM_SPACING = "interitem-spacing"

    const val GAIAX_LAYER_ITEM_SPACING = "item-spacing"

    const val GAIAX_LAYER_ROW_SPACING = "row-spacing"

    const val GAIAX_LAYER_COLUMN = "column"

    const val GAIAX_LAYER_SCROLL_ENABLE = "scroll-enable"

    const val GAIAX_LAYER_GRAVITY = "gravity"

    const val GAIAX_LAYER_ID = "id"

    const val GAIAX_LAYERS = "layers"

    const val GAIAX_LAYER_TYPE = "type"

    const val GAIAX_LAYER_SUB_TYPE = "sub-type"

    const val GAIAX_LAYER_CUSTOM_VIEW_CLASS = "view-class-android"

    const val GAIAX_LAYER_CLASS = "class"

    const val GAIAX_LAYER_EXP_VERSION = "exp-version"

    const val GAIAX_PX = "px"

    const val GAIAX_PT = "pt"

    const val GAIAX_PE = "%"

    const val GAIAX_AUTO = "auto"

    const val GAIAX_LAYER_DIRECTION = "direction"
    const val GAIAX_HORIZONTAL = "horizontal"
    const val GAIAX_VERTICAL = "vertical"

    const val GAIAX_HIGHLIGHT_REGEX = "(.*?)"

    const val GAIAX_HIGHLIGHT_COLOR = "highlight-color"
    const val GAIAX_HIGHLIGHT_FONT_WEIGHT = "highlight-font-weight"
    const val GAIAX_HIGHLIGHT_FONT_SIZE = "highlight-font-size"
    const val GAIAX_HIGHLIGHT_FONT_FAMILY = "highlight-font-family"
    const val GAIAX_HIGHLIGHT_TAG = "highlight-tag"

    const val GAIAX_VALUE = "value"
    const val GAIAX_ACCESSIBILITY_DESC = "accessibilityDesc"
    const val GAIAX_ACCESSIBILITY_ENABLE = "accessibilityEnable"
    const val GAIAX_ACCESSIBILITY_TRAITS = "accessibilityTraits"
    const val GAIAX_PLACEHOLDER = "placeholder"
    const val GAIAX_EXTEND = "extend"

    const val GAIAX_LAYER = "layer"
    const val GAIAX_DATABINDING = "databinding"
    const val GAIAX_CSS = "css"
    const val GAIAX_JS = "js"

    const val GAIAX_INDEX_DATABINDING = "index.databinding"
    const val GAIAX_INDEX_CSS = "index.css"
    const val GAIAX_INDEX_JSON = "index.json"
    const val GAIAX_INDEX_JS = "index.js"
    const val GAIAX_INDEX_MOCK = "index.mock"

    const val GAIAX_TEMPLATE_ID = "templateId"
    const val GAIAX_TEMPLATE_VERSION = "templateVersion"
    const val GAIAX_TEMPLATE_BIZ = "templateBiz"
    const val GAIAX_TEMPLATE_TYPE = "templateType"

    const val GAIAX_GESTURE_TYPE = "type"
    const val GAIAX_GESTURE_TYPE_TAP = "tap"
    const val GAIAX_GESTURE_TYPE_JS_TAP = "click"
    const val GAIAX_GESTURE_TYPE_LONGPRESS = "longpress"

    const val GAIAX_CUSTOM_PROPERTY_VIEW_PORT_WIDTH = "view-port-width"
    const val GAIAX_CUSTOM_PROPERTY_GRID_COMPUTE_CONTAINER_HEIGHT = "grid-compute-container-height"
    const val GAIAX_CUSTOM_PROPERTY_SCROLL_COMPUTE_CONTAINER_HEIGHT =
        "scroll-compute-container-height"

    const val GAIAX_ICONFONT_FONT_FAMILY_DEFAULT_NAME = "iconfont"

    const val GAIAX_DATABINDING_HOLDING_OFFSET = "holding-offset"
    const val GAIAX_DATABINDING_ITEM_TYPE = "item-type"
    const val GAIAX_DATABINDING_ITEM_TYPE_PATH = "path"
    const val GAIAX_DATABINDING_ITEM_TYPE_CONFIG = "config"

    const val GAIAX_CONTAINER_FOOTER = "item-footer-type"
    const val GAIAX_CONTAINER_HAS_MORE = "hasMore"

    const val GAIAX_ANIMATION_TYPE_LOTTIE = "LOTTIE"
    const val GAIAX_ANIMATION_TYPE_PROP = "PROP"


    // GXSliderView
    const val GAIAX_LAYER_SLIDER_SCROLL_TIME_INTERVAL = "slider-scroll-time-interval"
    const val GAIAX_LAYER_SLIDER_INFINITY_SCROLL = "slider-infinity-scroll"
    const val GAIAX_LAYER_SLIDER_HAS_INDICATOR = "slider-has-indicator"
    const val GAIAX_LAYER_SLIDER_SELECTED_INDEX = "slider-selected-index"
    const val GAIAX_LAYER_SLIDER_INDICATOR_SELECTED_COLOR = "slider-indicator-selected-color"
    const val GAIAX_LAYER_SLIDER_INDICATOR_UNSELECTED_COLOR = "slider-indicator-unselected-color"
    const val GAIAX_LAYER_SLIDER_INDICATOR_MARGIN = "slider-indicator-margin"
    const val GAIAX_LAYER_SLIDER_INDICATOR_POSITION = "slider-indicator-position"
    const val GAIAX_LAYER_SLIDER_INDICATOR_CLASS = "slider-indicator-class-android"

    // GXProgressView
    const val GAIAX_LAYER_PROGRESS_STROKE_COLOR = "stroke-color"
    const val GAIAX_LAYER_PROGRESS_TRAIL_COLOR = "trail-color"
    const val GAIAX_LAYER_PROGRESS_TYPE = "progress-type"
    const val GAIAX_LAYER_PROGRESS_ANIMATED = "animated"
}
