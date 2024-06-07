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
import GXTemplateItem from '../context/GXTemplateItem';
import { GXRecord } from '../common/GXCommon';
import { List } from '@kit.ArkTS';

export default class GXNodeExtension {
  // 节点类型
  type: string | null = null;
  subType: string | null = null;
  // 层级内容
  layerInfo?: GXRecord;
  // 样式内容
  styleInfo?: GXRecord;
  // 数据绑定内容
  data?: GXRecord;
  event?: GXRecord;
  track?: GXRecord
  animation?: GXRecord;
  // 嵌套模板数据，虚拟数据内容
  virtualData?: GXRecord;
  virtualExtend: GXRecord | null = null;
  // 引用关系
  templateContext?: GXTemplateContext;
  // 嵌套的子模板
  subTemplates?: List<GXTemplateItem> | null = null

  addSubItems(subItem: GXTemplateItem) {
    if (this.subTemplates == null) {
      this.subTemplates = new List();
    }
    this.subTemplates.add(subItem)
  }
}


