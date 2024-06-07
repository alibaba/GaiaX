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

export function getValuePath(valuePath: string, targetObject: ESObject | null | undefined): ESObject | null {
  try {
    if (!targetObject) {
      return null;
    }

    // 表达式
    const keyIndex = valuePath.indexOf('.')
    const indexLeft = valuePath.indexOf('[')
    const indexRight = valuePath.indexOf(']')

    // 取数组
    if (keyIndex == -1 && indexLeft != -1 && indexRight != -1) {
      const arrayName = valuePath.substring(0, indexLeft).trim()
      const arrayIndex = valuePath.substring(indexLeft + 1, indexRight).trim()
      return targetObject[arrayName][arrayIndex];
    }

    // 表达式
    if (keyIndex == -1 && indexLeft == -1 && indexRight == -1) {
      return targetObject[valuePath];
    }

    // 取值
    const firstKey = valuePath.substring(0, keyIndex).trim();
    const restKey = valuePath.substring(keyIndex + 1, valuePath.length).trim();
    return getValuePath(restKey, targetObject[firstKey]);
  } catch (error) {
    return null;
  }
}
