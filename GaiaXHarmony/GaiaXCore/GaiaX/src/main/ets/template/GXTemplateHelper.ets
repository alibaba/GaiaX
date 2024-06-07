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

import { GXJSONValue, GXRecord } from '../common/GXCommon';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { parseCSS } from './transform';
import GXUtils from '../utils/GXUtils';
import GXTemplateItem from '../context/GXTemplateItem';

enum TemplateFileType {
  sourceFile,
  binaryFile,
  unKnow
}

interface BinarySectionInfo {
  cursor: number
  value: string
}

interface BinarySubFileInfo {
  fileName: string
  fileContent: string
}

export default class GXTemplateHelper {
  static FileHeadNumByte: number = 100
  static FileNumByte: number = 4

  static loadFileTemplate(gxTemplateItem: GXTemplateItem): GXRecord | null {
    let fileType: TemplateFileType = TemplateFileType.unKnow
    let binaryFileName: string = ''
    try {
      let fileList = getContext().resourceManager.getRawFileListSync(`${gxTemplateItem.bizId}`)
      if (fileList.includes(`${gxTemplateItem.templateId}.gaiax`)) {
        fileType = TemplateFileType.binaryFile
        binaryFileName = `${gxTemplateItem.bizId}/${gxTemplateItem.templateId}.gaiax`
      } else if (fileList.includes(`${gxTemplateItem.templateId}`)) {
        try {
          let jonsFileList = getContext().resourceManager.getRawFileListSync(`${gxTemplateItem.bizId}/${gxTemplateItem.templateId}`)
          if (jonsFileList.includes('index.json') == true) {
            fileType = TemplateFileType.sourceFile
          }
        } catch (err) {
          fileType = TemplateFileType.binaryFile
          binaryFileName = `${gxTemplateItem.bizId}/${gxTemplateItem.templateId}`
        }
      }
    } catch (err) {
      hilog.error(0x0000, 'Template', 'Failed to load the content. Cause: %{public}s', JSON.stringify(err) ?? '');
      fileType = TemplateFileType.unKnow
    }
    switch (fileType) {
      case TemplateFileType.sourceFile:
        return GXTemplateHelper.loadJSONFileTemplate(`${gxTemplateItem.bizId}/${gxTemplateItem.templateId}`)
      case TemplateFileType.binaryFile:
        return GXTemplateHelper.loadBinaryFileTemplate(binaryFileName)
      case TemplateFileType.unKnow:
        return null
    }
  }

  // 读取二进制模板信息
  static loadBinaryFileTemplate(fileName: string): GXRecord | null {
    let contentInfo: GXRecord = {}
    let fileContent: Uint8Array = getContext().resourceManager.getRawFileContentSync(fileName);
    let templateFileList = GXTemplateHelper.parseBinaryFile(fileContent)
    templateFileList.forEach((file) => {
      if (file.fileName.length > 0 && file.fileName.includes('index.json') == true) {
        contentInfo['layer'] = JSON.parse(file.fileContent) as GXJSONValue;
      }
      if (file.fileName.length > 0 && file.fileName.includes('index.databinding') == true) {
        contentInfo['data'] = JSON.parse(file.fileContent) as GXJSONValue;
      }
      if (file.fileName.length > 0 && file.fileName.includes('index.css') == true) {
        contentInfo['style'] = parseCSS(file.fileContent)
      }
    })
    return Object.keys(contentInfo).length == 3 ? contentInfo : null;
  }

  private static parseBinaryFile(fileContent: Uint8Array): Array<BinarySubFileInfo> {
    let result: Array<BinarySubFileInfo> = []
    let cursor: number = 0
    if (fileContent.length <= GXTemplateHelper.FileHeadNumByte) {
      return result
    }
    // 读取文件头
    fileContent.slice(cursor, GXTemplateHelper.FileHeadNumByte)
    cursor += GXTemplateHelper.FileHeadNumByte
    // 循环读取文件信息
    while (cursor < fileContent.length) {
      let subResult: BinarySubFileInfo
      // 读取子文件名称
      let nameInfo = GXTemplateHelper.parseBinaryInfo(fileContent, cursor)
      cursor = nameInfo.cursor
      // 读取子文件内容
      let contentInfo = GXTemplateHelper.parseBinaryInfo(fileContent, cursor)
      cursor = contentInfo.cursor
      result.push({
        fileName: nameInfo.value,
        fileContent: contentInfo.value
      })
    }
    return result
  }

  private static parseBinaryInfo(fileContent: Uint8Array, cursor: number): BinarySectionInfo {
    let currentCursor = cursor
    let valueLength: number =
      GXUtils.uint8ArrayToNumber(fileContent.slice(currentCursor, currentCursor + GXTemplateHelper.FileNumByte))
    currentCursor += GXTemplateHelper.FileNumByte
    let valueStr: string = GXUtils.uint8ArrayToString(fileContent.slice(currentCursor, currentCursor + valueLength))
    currentCursor += valueLength
    return {
      cursor: currentCursor,
      value: valueStr
    }
  }

  // 读取明文模板信息
  static loadJSONFileTemplate(fileName: string): GXRecord | null {
    let contentInfo: GXRecord = {}
    // layerInfo
    let layerData: GXJSONValue = GXTemplateHelper.parseJsonFileValue(`${fileName}/index.json`)
    if (layerData != null) {
      contentInfo['layer'] = layerData
    } else {
      return null;
    }
    // cssInfo
    let cssContent = GXTemplateHelper.parseCssFileValue(`${fileName}/index.css`);
    if (cssContent != null) {
      contentInfo['style'] = cssContent
    } else {
      return null;
    }
    // databindingInfo
    let databindingContent = GXTemplateHelper.parseJsonFileValue(`${fileName}/index.databinding`);
    if (databindingContent != null) {
      contentInfo['data'] = databindingContent
    }
    return contentInfo;
  }

  // 解析jsonFile
  private static parseJsonFileValue(fileName: string): GXJSONValue {
    try {
      let fileContent = getContext().resourceManager.getRawFileContentSync(fileName);
      let value = GXUtils.uint8ArrayToString(fileContent);
      let layerInfo: GXJSONValue = JSON.parse(value) as GXJSONValue;
      return layerInfo
    } catch (e) {
      throw new Error(`parseJsonFileValue ${fileName} exception = ${e}`)
    }
  }

  // 解析cssFile
  private static parseCssFileValue(fileName: string): GXJSONValue {
    try {
      let fileContent = getContext().resourceManager.getRawFileContentSync(fileName);
      let value = GXUtils.uint8ArrayToString(fileContent);
      let cssJson = parseCSS(value) as GXJSONValue
      return cssJson
    } catch (e) {
      throw new Error(`parseJsonFileValue ${fileName} exception = ${e}`)
    }
  }
}