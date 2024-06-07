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

import GXTemplateContext from '../context/GXTemplateContext';
import { GXNodeType, GXRecord } from '../common/GXCommon';
import GXTemplateData from '../context/GXTemplateData';
import GXUtils from '../utils/GXUtils';
import GXBaseNode from './GXBaseNode';
import { Size } from 'gxstretch';

export default class GXSliderNode extends GXBaseNode {
  // 指示器
  indicatorStyle?: Record<string, string>;
  showIndicator: boolean = true;
  // 滚动设置
  infiniteLoop: boolean = true;
  autoScroll: boolean = false;
  interval: number = 3000;
  // item间距
  itemSpace: number = 0;
  // padding
  contentInset: Padding = {
    top: 0,
    left: 0,
    bottom: 0,
    right: 0
  };
  // 数据源
  items: GXTemplateContext[] = [];

  // 节点类型
  nodeType(): GXNodeType {
    return GXNodeType.slider;
  }

  // 设置数据
  setData(dataInfo: GXRecord): void {
    const nodes = dataInfo['value'] as Array<GXRecord>;
    if (nodes && nodes.length > 0) {
      const childTemplate = this.extension.subTemplates?.get(0)
      if (childTemplate == null) {
        throw new Error("GXSliderNode setData but childTemplate is null, it must be set")
      }
      // 遍历数组
      for (let index = 0; index < nodes.length; index++) {
        const jsonData = nodes[index];
        // 创建模板信息
        const measureSize: Size<number> = new Size(NaN, NaN);
        const childData = new GXTemplateData();
        childData.jsonData = jsonData;
        // 创建context
        const context = new GXTemplateContext(childTemplate, childData, measureSize);
        this.items.push(context);
      }
    }

    // 处理extend
    this.handleExtend(dataInfo);
  }

  // 处理styleInfo
  setStyleInfo(styleInfo: GXRecord): void {
    super.setStyleInfo(styleInfo);
  }

  // 处理layerInfo
  setLayerInfo(layerInfo: GXRecord): void {
    // showIndicator
    const showIndicator = layerInfo['slider-has-indicator'] as boolean;
    if (showIndicator != null) {
      this.showIndicator = showIndicator;
    }

    // 指示器样式
    const indicatorStyle = layerInfo['slider-indicator-style'] as Record<string, string>;
    if (indicatorStyle != null) {
      this.indicatorStyle = indicatorStyle;
    }

    // infiniteLoop
    const infiniteLoop = layerInfo['slider-infinity-scroll'] as boolean;
    if (infiniteLoop != null) {
      this.infiniteLoop = infiniteLoop;
    }

    // item间距
    const itemSpacing = layerInfo['item-spacing'] as string;
    if (itemSpacing != null) {
      this.itemSpace = GXUtils.convertToNumber(itemSpacing) ?? 0;
    }

    // interval
    const interval = layerInfo['slider-scroll-time-interval'] as string;
    if (interval != null) {
      const intervalNum = GXUtils.convertToNumber(interval) ?? 0;
      this.interval = intervalNum > 0 ? intervalNum : 3000;
    }

    // autoScroll
    this.autoScroll = this.interval > 0 ? true : false;

    // padding
    const padding = this.style?.padding;
    if (padding != null) {
      // top
      if (padding.top.type == 0) {
        this.contentInset.top = padding.top.value;
      }
      // left
      if (padding.start.type == 0) {
        this.contentInset.left = padding.start.value;
      }
      // bottom
      if (padding.bottom.type == 0) {
        this.contentInset.top = padding.top.value;
      }
      // right
      if (padding.end.type == 0) {
        this.contentInset.right = padding.end.value;
      }
    }
  }
}