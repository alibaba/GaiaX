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

export default class GXGridNode extends GXBaseNode {
  columns: number = 0;
  rowSpace: number = 0;
  columnSpace: number = 0;
  scrollEnable: boolean = false;
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
    return GXNodeType.grid;
  }

  // 绑定数据
  setData(dataInfo: GXRecord): void {
    const nodes = dataInfo['value'] as Array<GXRecord>;
    if (nodes && nodes.length > 0) {
      const childTemplate = this.extension.subTemplates?.get(0)
      if (childTemplate == null) {
        throw new Error("GXGridNode setData but childTemplate is null, it must be set")
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

  // 设置样式信息
  setLayerInfo(layerInfo: GXRecord): void {
    // column 只有竖滑生效
    const columns = layerInfo['column'] as string;
    if (columns != null) {
      this.columns = GXUtils.convertToNumber(columns) ?? 0;
    }

    // row间距
    const rowSpacing = layerInfo['row-spacing'] as string;
    if (rowSpacing != null) {
      this.rowSpace = GXUtils.convertToNumber(rowSpacing) ?? 0;
    }

    // column间距
    const columnSpacing = layerInfo['item-spacing'] as string;
    if (columnSpacing != null) {
      this.columnSpace = GXUtils.convertToNumber(columnSpacing) ?? 0;
    }

    // 是否可滚动
    const scrollEnable = layerInfo['scroll-enable'] as boolean;
    if (scrollEnable != null) {
      this.scrollEnable = scrollEnable;
    }

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