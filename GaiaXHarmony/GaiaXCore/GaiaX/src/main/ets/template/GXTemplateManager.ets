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

import { GXRecord, GXTemplateFileType } from '../common/GXCommon';
import GXTemplateItem from '../context/GXTemplateItem';
import GXTemplateLRUCache from './GXTemplateLRUCache';
import GXTemplateInfo from './GXTemplateInfo';
import { List } from '@kit.ArkTS';
import { GXIExtensionTemplateSource } from '../source/GXExtensionTemplateSource';

export default class GXTemplateManager {
  static instance: GXTemplateManager = new GXTemplateManager()
  cache: GXTemplateLRUCache = new GXTemplateLRUCache(256)
  templateSources: List<GXIExtensionTemplateSource> = new List();

  // 读取模板信息
  public loadTemplateContent(templateItem: GXTemplateItem): GXTemplateInfo {
    // 优先读取缓存
    let cacheTemplateContent = this.cache.get(templateItem)
    if (cacheTemplateContent != null) {
      return cacheTemplateContent
    }

    for (let i = this.templateSources.length - 1; i >= 0; i--) {
      let source = this.templateSources.get(i)
      let contentInfo = source.getTemplate(templateItem)
      if (contentInfo) {
        const templateInfo = new GXTemplateInfo(templateItem, contentInfo);
        this.cache.put(templateItem, templateInfo)
        return templateInfo;
      }
    }

    throw new Error("GXTemplateMananger not found template");
  }

  // 读取模板中某个文件
  public loadTemplateFileContent(templateItem: GXTemplateItem, fileType: GXTemplateFileType): GXRecord | undefined {
    const templateInfo = this.loadTemplateContent(templateItem);
    // 读取对应文件
    let result: GXRecord | undefined;
    switch (fileType) {
      case GXTemplateFileType.cssFile:
        result = templateInfo.styleInfo;
        break;
      case GXTemplateFileType.jsonFile:
        result = templateInfo.layerInfo;
        break;
      case GXTemplateFileType.dataFile:
        result = templateInfo.dataInfo;
        break;
      default:
        break;
    }

    return result;
  }
}


