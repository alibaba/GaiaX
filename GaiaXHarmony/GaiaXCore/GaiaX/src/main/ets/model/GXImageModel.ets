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

export default class GXImageModel {
  // url 信息
  placeHolder: ResourceStr | null = null;
  url: ResourceStr | null = null;
  // 腰封信息
  summaryInfo?: GXRecord
  // 角标信息
  markInfo?: GXRecord
}