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

import { GXRecord } from '../common/GXCommon';

export default class GXTemplateItem {
  // 业务
  bizId: string;
  // 模板id
  templateId: string;
  // 模板version
  templateVersion: string = '';
  // 嵌套模板虚拟样式信息，需要合并进去
  virtualStyleInfo?: GXRecord

  constructor(bizId: string, templateId: string, templateVersion = '') {
    this.bizId = bizId;
    this.templateId = templateId;
    this.templateVersion = templateVersion;
  }
}