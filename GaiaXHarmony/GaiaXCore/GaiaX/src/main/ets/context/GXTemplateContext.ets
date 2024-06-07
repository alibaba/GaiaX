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

import GXTemplateInfo from '../template/GXTemplateInfo';
import GXTemplateData from './GXTemplateData';
import GXTemplateItem from './GXTemplateItem';
import GXNode from '../creator/GXNode';
import { Size } from 'gxstretch';

@Observed
export default class GXTemplateContext {
  // item
  templateItem: GXTemplateItem;
  // data
  templateData: GXTemplateData;
  // size
  measureSize: Size<number>;

  // 模板信息
  // templateInfo: GXTemplateInfo | undefined;
  templateInfo?: GXTemplateInfo;
  // fit-content节点
  specialNodes: Array<GXNode> = [];
  // 刷新标志
  isNeedLayout = false;

  // 构造函数
  constructor(templateItem: GXTemplateItem, templateData: GXTemplateData, measureSize: Size<number>) {
    this.measureSize = measureSize;
    this.templateItem = templateItem;
    this.templateData = templateData;
  }
}
