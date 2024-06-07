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

import GXTemplateItem from '../context/GXTemplateItem';
import { GXRecord } from '../common/GXCommon';
import GXUtils from '../utils/GXUtils';

export default class GXTemplateInfo {
  // 模板是否有效
  isValid = false;
  // 模板数据绑定信息
  dataInfo: GXRecord;
  // 模板样式信息
  styleInfo: GXRecord;
  // 模板层级信息
  layerInfo: GXRecord;
  // templateInfo
  contentInfo: GXRecord;
  // item
  templateItem: GXTemplateItem;
  // children
  children?: Array<GXTemplateInfo>;

  constructor(templateItem: GXTemplateItem, contentInfo: GXRecord) {
    this.templateItem = templateItem;
    this.contentInfo = contentInfo;

    // default value {}
    const data: GXRecord = contentInfo['data'] as GXRecord ?? {};
    this.dataInfo = data;

    // must
    const layer: GXRecord = contentInfo['layer'] as GXRecord;
    if (GXUtils.isNullOrUndefined(layer)) {
      throw new Error('GXTemplateInfo create but layer is undefined')
    }
    this.layerInfo = layer;

    // must
    const style: GXRecord = contentInfo['style'] as GXRecord;
    if (GXUtils.isNullOrUndefined(layer)) {
      throw new Error('GXTemplateInfo create but style is undefined')
    }
    this.styleInfo = style;

    // 模板是否有效
    this.isValid = (this.layerInfo != null && this.styleInfo != null && this.dataInfo != null);
  }
}