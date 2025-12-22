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

import {
  gxInjectGetSourceValue, gxInjectGetFunctionValue, gxAnalyzeGetValue
} from 'libGXAnalyze.so';

import {
  getSourceValue, JSGXValue, getFunctionValue,
} from './src/main/ets/helper/transfrom'

function init() {
  gxInjectGetSourceValue((valuePath: string, source: object) => {
    let result: JSGXValue = getSourceValue(valuePath, source)
    return result
  });
  gxInjectGetFunctionValue((funcName: string, paramPointers: Array<object>, paramsSize: number) => {
    let result: JSGXValue = getFunctionValue(funcName, paramPointers, paramsSize)
    return result
  });
}

let configAnalyzeIfNeeded = (() => {
  let executed = false;
  return () => {
    if (!executed) {
      executed = true;
      init()
    }
  };
})();

function analyzeGetValue(valuePath: string, source: object): object | number | string | boolean {
  if (valuePath === '$$') {
    return source
  }
  return gxAnalyzeGetValue(valuePath, source)
}

export {
  configAnalyzeIfNeeded, JSGXValue, analyzeGetValue
}