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

import GXRawBinaryWithoutSuffixTemplateSource from './source/GXRawBinaryWithouSuffixTemplateSource';
import GXRawFileTemplateSource from './source/GXRawFileTemplateSource';
import GXRegisterCenter from './GXRegisterCenter';
import { configAnalyzeIfNeeded } from 'GaiaXAnalyze';
import { Stretch } from 'gxstretch';
import { GXInjector,GXImageBuilderParams } from './components/injector/GXInjector';

export default class GXTemplateEngine {
  static instance = new GXTemplateEngine();
  isInit = false;

  init() {
    if (!this.isInit) {
      this.isInit = true;
      // stretch初始化
      Stretch.init();
      // 表达式初始化
      configAnalyzeIfNeeded();
      // 注册业务实现（数据源相关）
      GXRegisterCenter.instance.registerExtensionTemplateSource(0, new GXRawBinaryWithoutSuffixTemplateSource())
      GXRegisterCenter.instance.registerExtensionTemplateSource(1, new GXRawFileTemplateSource())
    }
  }

  registerImage(imageBuilder:WrappedBuilder<[GXImageBuilderParams]>) {
    GXInjector.instance.registerImage(imageBuilder)
  }
}


