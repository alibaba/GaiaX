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

import util from '@ohos.util';
import resourceManager from '@ohos.resourceManager';

export async function getJson(resourceManager: resourceManager.ResourceManager, jsonPath: string) {
  let rawFile: Uint8Array;
  rawFile = resourceManager.getRawFileContentSync(jsonPath);
  let jsonStr: string = util.TextDecoder.create("utf-8", { ignoreBOM: true }).decodeWithStream(rawFile);
  let jsonObj: Record<string, Object> = JSON.parse(jsonStr);
  return jsonObj;
}
