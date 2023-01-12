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

package com.alibaba.gaiax.render.view.container.slider

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * @suppress
 */
abstract class GXSliderBaseIndicatorView : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * 设置指示器数量
     */
    abstract fun setIndicatorCount(count: Int)

    /**
     * 更新选中 index
     */
    abstract fun updateSelectedIndex(index: Int)

    /**
     * 设置指示器颜色
     */
    abstract fun setIndicatorColor(selectedColor: Int?, unselectedColor: Int?)
}
