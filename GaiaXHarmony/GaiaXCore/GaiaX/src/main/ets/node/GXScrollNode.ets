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
import GXTemplateItem from '../context/GXTemplateItem';
import { getValuePath } from '../utils/GXValuePath';
import GXDataParser from '../binding/GXDataParser';
import GXUtils from '../utils/GXUtils';
import GXBaseNode from './GXBaseNode';
import { Size } from 'gxstretch';
import hilog from '@ohos.hilog';
import { JSON } from '@kit.ArkTS';

@Observed
export default class GXScrollNode extends GXBaseNode {
  scrollDirection: Axis = Axis.Horizontal;
  startOffset: number = 0;
  endOffset: number = 0;
  itemSpace: number = 0;
  index: number = 0;
  // 数据源
  items: GXTemplateContext[] = [];
  item_type_path: string | null = null;
  item_type_config: GXRecord | null = null;
  gravity: ListItemAlign = ListItemAlign.Start;

  // 节点类型
  nodeType(): GXNodeType {
    return GXNodeType.scroll;
  }

  // 绑定数据
  setData(dataInfo: GXRecord): void {
    const nodes = dataInfo['value'] as Array<GXRecord>;
    if (nodes && nodes.length > 0) {

      hilog.debug(0x0000, 'GXC', `GXScrollNode setData length=${nodes.length}`);

      // init items gxContext
      this.initMultiItemType(nodes);

      // init item-footer-type
      this.initFooterType();
    }
    // 处理extend
    this.handleExtend(dataInfo);
  }

  private initMultiItemType(nodes: GXRecord[]) {
    for (let index = 0; index < nodes.length; index++) {
      const childJsonData = nodes[index];

      if (this.extension.subTemplates == null || this.extension.subTemplates.length == 0) {
        throw new Error("GXScrollNode initMultiItemType but childTemplate is null, it must be set");
      }

      // 初始化多Type类型
      if (this.extension.subTemplates && this.extension.subTemplates.length > 1 && !this.item_type_path && !this.item_type_path) {
        let item_type = getValuePath("extend.item-type", this.extension.data) as GXRecord;
        if (item_type) {
          this.item_type_path = item_type["path"] as string;
          this.item_type_config = item_type["config"] as GXRecord;
          hilog.debug(0x0000, 'GXC', `GXScrollNode initMultiItemType item_type=${JSON.stringify(item_type)}`);
        }
      }

      // 根据extend信息和layer信息获取子模板信息
      let childTemplate: GXTemplateItem | null = null;
      if (this.item_type_path && this.item_type_config) {
        let path_value = GXDataParser.gxHandleExp(this.item_type_path, childJsonData) as string;
        let config_value = GXDataParser.gxHandleExp(this.item_type_config[path_value] as string, childJsonData) as string;

        hilog.debug(0x0000, 'GXC', `GXScrollNode initMultiItemType path_value=${path_value} config_value=${config_value}}`);

        if (config_value) {
          for (let i = 0; i < this.extension.subTemplates.length; i++) {
            let item = this.extension.subTemplates.get(i);
            if (item.templateId === config_value) {
              childTemplate = item;
              break;
            }
          }
        }
      } else {
        childTemplate = this.extension.subTemplates?.get(0);
      }

      // 合法性检查
      if (!childTemplate) {
        throw new Error("GXScrollNode initMultiItemType childTemplate is null");
      }

      // 构建坑位上下文
      const childData = new GXTemplateData();
      childData.jsonData = childJsonData;
      const childMeasureSize: Size<number> = new Size(NaN, NaN);
      const childContext = new GXTemplateContext(childTemplate, childData, childMeasureSize);

      //
      this.items.push(childContext);

      hilog.debug(0x0000, 'GXC', `GXScrollNode initMultiItemType index=${index} data=${JSON.stringify(childJsonData)} childTemplate=${JSON.stringify(childTemplate)}`);
    }
  }

  private initFooterType() {
    if (!this.extension.data) {
      return;
    }

    let item_footer_type = getValuePath("extend.item-footer-type", this.extension.data) as GXRecord;
    if (!item_footer_type) {
      return;
    }

    let bizId = this.extension?.templateContext?.templateItem?.bizId ?? null;
    let containerData = this.extension?.templateContext?.templateData?.jsonData ?? null;
    if (!bizId || !containerData) {
      return;
    }

    let item_footer_id = GXDataParser.gxHandleExp(item_footer_type["id"] as string, containerData) as string;
    let item_footer_has_more = GXDataParser.gxHandleExp(item_footer_type["hasMore"] as string, containerData) as boolean;

    hilog.debug(0x0000, 'GXC', `GXScrollNode initFooterType item_footer_type=${JSON.stringify(item_footer_type)} item_footer_has_more=${item_footer_has_more}`);

    if (item_footer_has_more) {
      const childMeasureSize: Size<number> = new Size(NaN, NaN);
      const childData = new GXTemplateData();
      childData.jsonData = {};
      const childTemplate = new GXTemplateItem(bizId, item_footer_id, "");
      const childContext = new GXTemplateContext(childTemplate, childData, childMeasureSize);
      this.items.push(childContext);
    }
  }

  // 处理styleInfo
  setStyleInfo(styleInfo: GXRecord): void {
    super.setStyleInfo(styleInfo);
    //获取坑位表达式
  }

  // 处理layerInfo
  setLayerInfo(layerInfo: GXRecord): void {
    // direction
    const direction = layerInfo['direction'] as string;
    this.scrollDirection = (direction == 'vertical' ? Axis.Vertical : Axis.Horizontal);
    const gravity = layerInfo['gravity'] as string
    if (gravity) {
      if (gravity === 'top') {
        this.gravity = ListItemAlign.Start;
      } else if (gravity === 'center') {
        this.gravity = ListItemAlign.Center;
      } else if (gravity === 'bottom') {
        this.gravity = ListItemAlign.End;
      }
    }

    // padding
    const padding = this.style?.padding;
    if (padding != null) {
      // 区分横竖屏
      if (this.scrollDirection == Axis.Horizontal) {
        if (padding.start.type == 0) {
          this.startOffset = padding.start.value;
        }
        if (padding.end.type == 0) {
          this.endOffset = padding.end.value;
        }
      } else {
        if (padding.top.type == 0) {
          this.startOffset = padding.top.value;
        }
        if (padding.bottom.type == 0) {
          this.endOffset = padding.bottom.value;
        }
      }
    }

    // item间距
    const itemSpacing = layerInfo['item-spacing'] as string;
    if (itemSpacing != null) {
      this.itemSpace = GXUtils.convertToNumber(itemSpacing) ?? 0;
    }

  }
}