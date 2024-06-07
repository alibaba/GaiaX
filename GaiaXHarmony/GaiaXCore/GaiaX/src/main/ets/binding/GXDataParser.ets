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
import { analyzeGetValue } from 'GaiaXAnalyze';
import GXUtils from '../utils/GXUtils';
import hilog from '@ohos.hilog';

export default class GXDataParser {
  /**
   * 表达式解析数据源
   * @param expData 表达式相关的信息
   * @param sourceData 数据源
   * @returns 解析的结果
   */
  static parseData(expData: GXRecord | undefined, sourceData: GXRecord): GXRecord | null {
    if (GXUtils.isObject(expData)) {
      const expRecord = expData as GXRecord;
      return GXDataParser.gxHandleData(expRecord, sourceData);
    } else {
      return null;
    }
  }

  /**
   * 表达式具体的解析操作
   * @param expData
   * @param sourceData
   * @returns 解析结果
   */
  private static gxHandleData(expData: GXRecord, sourceData: GXRecord): GXRecord {
    let resultData: Record<string, GXJSONValue> = {};
    Object.entries(expData).forEach((item) => {
      const key = item[0];
      const value = item[1];

      if (GXUtils.isNumber(value)) {
        // number类型
        const number = value as number;
        resultData[key] = number;

      } else if (GXUtils.isString(value)) {
        // string类型
        const string = value as string;
        const result = GXDataParser.gxHandleExp(string, sourceData) as GXJSONValue;
        resultData[key] = result;

        hilog.debug(0x0000, 'GXC', `GXDataParser gxHandleData key=${key} value=${value} result=${result}`);

      } else if (GXUtils.isObject(value) && GXUtils.isArray(value)) {
        // Array类型
        const array = value as Array<GXJSONValue>;
        if (array.length > 0) {
          resultData[key] = GXDataParser.gx_handleArray(array, sourceData);
        }

      } else if (GXUtils.isObject(value)) {
        // Record类型
        const record = value as GXRecord;
        let result = GXDataParser.gxHandleData(record, sourceData) as GXJSONValue;
        resultData[key] = result;

      }
    })
    // 返回计算结果
    return resultData;
  }

  /**
   * 处理数据组解析
   * @param array
   * @param sourceData
   * @returns 解析数组
   */
  private static gx_handleArray(array: Array<GXJSONValue>, sourceData: GXRecord): GXJSONValue[] {
    let resultArray: GXJSONValue[] = [];
    array.forEach(item => {
      if (GXUtils.isNumber(item)) {
        // number类型处理
        const number = item as number;
        resultArray.push(number);

      } else if (GXUtils.isString(item)) {
        // string类型处理
        const string = item as string;
        const result = GXDataParser.gxHandleExp(string, sourceData) as GXJSONValue;
        resultArray.push(result);

      } else if (GXUtils.isObject(item) && !GXUtils.isArray(item)) {
        // Record类型
        const record = item as GXRecord;
        let result = GXDataParser.gxHandleData(record, sourceData) as GXJSONValue;
        resultArray.push(result);
      } else {
        // 不支持数组嵌套
      }
    })
    return resultArray;
  }

  /**
   * 调用C++表达式进行交互
   * @param exp
   * @param data
   * @returns 解析内容
   */
  public static gxHandleExp(exp: string, data: GXRecord): GXJSONValue {
    const value = analyzeGetValue(exp, data) as GXJSONValue;
    return value;
  }
}