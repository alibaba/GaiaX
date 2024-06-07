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

import GXTemplateInfo from './GXTemplateInfo';
import GXTemplateItem from '../context/GXTemplateItem';

export default class GXTemplateLRUCache {
  capacity: number;
  cache: Map<string, GXTemplateInfo>;

  constructor(capacity: number) {
    this.capacity = capacity;
    this.cache = new Map();
  }

  // 读取方法
  get(templateItem: GXTemplateItem): GXTemplateInfo | null {
    let key: string = "unknown"
    if (templateItem.templateId.length > 0) {
      let templateVersion: string = templateItem.templateVersion.length > 0 ? templateItem.templateVersion : ''
      let templateBizId: string = templateItem.bizId.length > 0 ? templateItem.bizId : ''
      key = `${templateItem.templateId}+${templateVersion}+${templateBizId}`
    } else {
      return null
    }
    if (!this.cache.has(key)) {
      return null;
    }
    const value: GXTemplateInfo | undefined = this.cache.get(key);
    if (value != undefined) {
      this.cache.delete(key); // 删除键，因为Map会保留键的插入顺序
      this.cache.set(key, value); // 重新设置键，将其移动到最近使用的位置
      return null
    }
    return null
  }

  // 存方法
  put(templateItem: GXTemplateItem, value: GXTemplateInfo) {
    let key: string = "unknown"
    if (templateItem.templateId.length > 0) {
      let templateVersion: string = templateItem.templateVersion.length > 0 ? templateItem.templateVersion : ''
      let templateBizId: string = templateItem.bizId.length > 0 ? templateItem.bizId : ''
      key = `${templateItem.templateId}+${templateVersion}+${templateBizId}`
    } else {
      return
    }
    if (this.cache.has(key)) {
      this.cache.delete(key); // 如果键已存在，先删除
    } else if (this.cache.size >= this.capacity) {
      // 如果达到容量，删除最少使用的键
      const leastRecentlyUsed: string = this.cache.keys().next().value;
      this.cache.delete(leastRecentlyUsed);
    }
    // 设置新的键值对或更新现有的键
    this.cache.set(key, value);
  }
}