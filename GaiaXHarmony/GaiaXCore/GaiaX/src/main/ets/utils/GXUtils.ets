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

import { util } from '@kit.ArkTS';

export default class GXUtils {
  /**
   * 是否是null或者undefined
   * @param obj
   * @returns
   */
  static isNullOrUndefined(obj: ESObject): boolean {
    return obj == null || obj == undefined
  }

  /**
   * 是否为string类型
   * @param obj
   * @returns boolean
   */
  static isString(obj: ESObject): boolean {
    return typeof obj === 'string';
  }

  /**
   * 是否为number类型
   * @param obj
   * @returns boolean
   */
  static isNumber(obj: ESObject): boolean {
    return typeof obj === 'number';
  }

  /**
   * 是否为boolean类型
   * @param obj
   * @returns boolean
   */
  static isBoolean(obj: ESObject): boolean {
    return typeof obj === 'boolean';
  }

  /**
   * 是否为对象
   * @param obj
   * @returns boolean
   */
  static isObject(obj: ESObject): boolean {
    return typeof obj === 'object'
  }

  /**
   * 是否为数组
   * @param obj
   * @returns boolean
   */
  static isArray(obj: ESObject): boolean {
    return Array.isArray(obj)
  }

  /**
   * 有效性判断
   * @param obj
   * @returns boolean
   */
  static isValid(obj: ESObject): boolean {
    return !(obj === undefined || obj === null);
  }

  /**
   * Bool转换
   *
   * @param value
   * @returns boolean
   */
  static convertToBool(value: boolean | string): boolean {
    if (GXUtils.isBoolean(value)) {
      return value as boolean;
    } else if (GXUtils.isString(value)) {
      const string = value as string;
      return string.toLowerCase() === "true"
    } else  {
      return false;
    }
  }

  /**
   * 数字转换
   *
   * @param value
   * @returns number | undefined
   */
  static convertToNumber(value: ESObject): number | undefined {
    if (typeof value === 'number') {
      return value;
    } else if (!isNaN(Number(value))) {
      return Number(value);
    } else {
      return undefined;
    }
  }

  /**
   * 字符串转成字节数组
   *
   * @param str
   * @returns Uint8Array
   */
  static stringToUint8Array(str: string): Uint8Array {
    const textEncoder = new util.TextEncoder("utf-8")
    return textEncoder.encodeInto(str)
  }

  /**
   * 字节流转成可理解的字符串
   */
  static uint8ArrayToString(uint8Array: Uint8Array): string {
    const textDecoder = util.TextDecoder.create("utf-8")
    return textDecoder.decodeWithStream(uint8Array, {
      stream: false
    })
  }

  static uint8ArrayToNumber(uint8Array: Uint8Array): number {
    const buffer = uint8Array.buffer;
    // 使用DataView来处理 ArrayBuffer，并且指定字节序
    const view = new DataView(buffer);
    // 获取16位整数，小端序
    const numLittleEndian = view.getUint16(0, true); // true 表示小端序
    return numLittleEndian
  }
}