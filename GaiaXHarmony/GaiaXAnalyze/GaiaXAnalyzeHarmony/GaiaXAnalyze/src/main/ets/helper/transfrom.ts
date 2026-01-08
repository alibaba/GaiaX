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

enum JSGXValueType {
  nil = 'nil',
  object = 'object',
  boolean = 'boolean',
  number = 'number',
  string = 'string',
  array = 'array'
}

export class JSGXValue {
  objectValue: object
  numberValue: number
  boolValue: boolean
  stringValue: string
  arrayValue: Array<any>
  valueType: string = JSGXValueType.nil

  constructor(value: any, type: JSGXValueType) {
    this.valueType = type
    switch (type) {
      case JSGXValueType.object:
        this.objectValue = value
        break;
      case JSGXValueType.boolean:
        this.boolValue = value
        break;
      case JSGXValueType.number:
        this.numberValue = value
        break;
      case JSGXValueType.string:
        this.stringValue = value
        break;
      case JSGXValueType.array:
        this.arrayValue = value
      default:
        break;
    }
  }
}

export function getSourceValue(valuePath: string, source: object): JSGXValue {
  if (valuePath === '$$') {
    return new JSGXValue(source, JSGXValueType.object)
  }
  let nilValue = new JSGXValue('', JSGXValueType.nil)
  // 使用正则表达式将路径拆分成键数组，以点和方括号为分隔符
  const keys = valuePath.replace(/\[(\d+)\]/g, '.$1').split('.');
  let result = source;
  for (const key of keys) {
    // 如果结果已经是undefined或null则停止
    if (result === undefined || result === null) {
      return nilValue;
    }
    if (Array.isArray(result)) {
      // 尝试将key转换为数值索引
      const index = Number(key);
      // 如果成功转换为数值索引，且不越界，在数组中获取元素
      if (!isNaN(index) && index >= 0 && index < result.length) {
        result = result[index];
      } else {
        return nilValue;
      }
    } else {
      // 对于非数组对象，检查是否是对象自身的属性
      if (Object.prototype.hasOwnProperty.call(result, key)) {
        result = result[key];
      } else {
        return nilValue;
      }
    }
  }
  let typeStr = typeof result
  if (Array.isArray(result)) {
    // JS中的数组即object，此次通过isArray判断是否是数组
    return new JSGXValue(result, JSGXValueType.array);
  }
  switch (typeof result) {
    case JSGXValueType.object:
      return new JSGXValue(result, JSGXValueType.object);
    case JSGXValueType.boolean:
      return new JSGXValue(result, JSGXValueType.boolean);
    case JSGXValueType.number:
      return new JSGXValue(result, JSGXValueType.number);
    case JSGXValueType.string:
      return new JSGXValue(result, JSGXValueType.string);
    default:
      break;
  }
  return nilValue
}

export function getFunctionValue(funcName: string, paramPointers: Array<any>, paramsSize: number): JSGXValue {
  if (funcName == "size") {
    return callFunctionForSize(paramPointers)
  }
  return new JSGXValue(0, JSGXValueType.number);
}

function callFunctionForSize(paramPointers: Array<any>): JSGXValue {
  if (paramPointers.length > 0) {
    let element = paramPointers[0]
    if (Array.isArray(element)) {
      let elementIsArray = element as Array<any>
      return new JSGXValue(elementIsArray.length, JSGXValueType.number);
    }
    switch (typeof element) {
      case JSGXValueType.object:
        const propNames = Object.getOwnPropertyNames(element);
        const propertyCount = propNames.length;
        return new JSGXValue(propertyCount, JSGXValueType.number);
      case JSGXValueType.string:
        let elementIsString = element as String
        return new JSGXValue(elementIsString.length, JSGXValueType.number);
      default:
        break;
    }
    return new JSGXValue(0, JSGXValueType.number);
  }
}