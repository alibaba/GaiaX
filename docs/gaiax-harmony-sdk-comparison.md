# GaiaX Harmony SDK 功能对比与完善方案

## 一、文档概述

### 1.1 背景与目标

GaiaX 是由阿里巴巴优酷技术团队研发的一套轻量级跨平台纯原生动态化卡片解决方案。该项目旨在通过高性能的原生渲染能力,帮助客户端开发实现低代码,同时保证原生体验与性能。GaiaX 不仅提供了客户端 SDK,还配套了模板可视化搭建工具 Studio 和 Demo 工程,支持从模板创建、编辑、真机调试到预览的全链路技术。

目前 GaiaX 已在 Android 和 iOS 平台上实现了较为完备的 SDK。HarmonyOS 作为新兴的操作系统平台,GaiaX Harmony SDK 基于 ArkTS (Extended TypeScript) 开发,功能仍需进一步完善以达到与 Android/iOS SDK 的功能对齐。

本文档旨在:
- 全面对比 GaiaX Android SDK 与 GaiaX Harmony SDK 的功能差异
- 基于 GaiaX 官方 Wiki 文档,识别需要补充的功能模块
- 评估各功能模块的开发工作量
- 提供功能完善的优先级建议和实施路径

### 1.2 对比范围说明

基于 GaiaX 官方技术架构,本次对比将聚焦于以下几个核心维度:

1. **模板引擎**: 模板加载、解析、缓存、预编译等核心能力
2. **组件系统**: 基础组件、容器组件、自定义组件支持
3. **样式系统**: FlexBox 布局、样式属性、样式计算与转换
4. **数据绑定**: 数据绑定能力、表达式计算、事件系统
5. **扩展机制**: 适配器系统、JS 引擎集成、表达式分析
6. **性能优化**: 布局缓存、视图复用、异步渲染、内存优化
7. **开发者工具**: GaiaX Studio 连接、实时预览、调试工具

### 1.3 参考文档

#### 官方文档
- GaiaX 项目概述文档
- GaiaX 快速入门指南
- GaiaX 核心概念文档
- GaiaX 核心模块详解
- GaiaX 扩展功能文档
- GaiaX Android 实现文档
- GaiaX HarmonyOS 实现文档

#### 开发环境
- HarmonyOS 开发文档: https://developer.huawei.com/consumer/cn/doc/harmonyos-guides-V3/document-outline-js-ets-0000001282486428-V3
- GaiaX GitHub 仓库: https://github.com/alibaba/GaiaX

#### 技术栈
- Android: Kotlin、Java、Gradle
- iOS: Objective-C、Swift、CocoaPods  
- HarmonyOS: ArkTS (Extended TypeScript)、Hvigor
- JS 引擎: QuickJS (Android)
- 布局引擎: Stretch (Rust)

## 二、GaiaX SDK 功能对比

GaiaX SDK 是框架的核心部分,负责模板的加载、解析、渲染和事件处理。本章节对比 Android SDK 和 Harmony SDK 在核心引擎、模板系统、组件系统、样式系统、表达式分析等方面的差异。

### 2.1 核心引擎能力对比

#### 2.1.1 模板引擎

| 功能项         | GaiaX Android          | GaiaX Harmony       | 差异说明                                                       |
| -------------- | ---------------------- | ------------------- | -------------------------------------------------------------- |
| 模板加载与解析 | ✅ 完整支持             | ✅ 基础支持          | 都支持从 assets 加载模板,Harmony 支持从 rawfile 加载           |
| 模板缓存机制   | ✅ 多级缓存             | ⚠️ LRU 缓存          | Android 采用 ConcurrentHashMap,Harmony 采用 GXTemplateLRUCache |
| 模板预编译     | ✅ 支持                 | ⚠️ 部分支持          | Android 与 Harmony 均支持二进制模板加                          |
| 模板热更新     | ✅ 支持                 | ❌ 不支持            | 需要支持运行时模板动态更新                                     |
| 多版本管理     | ✅ 支持                 | ❌ 不支持            | 模板版本控制能力缺失                                           |
| 模板信息源     | ✅ GXTemplateInfoSource | ✅ GXTemplateManager | 两者类似,但 Android 功能更完善                                 |
| 模板锁机制     | ✅ 支持                 | ⚠️ 部分支持          | 防止多线程同时加载同一模板                                     |

**功能细节分析**:

1. **模板加载流程**
   - Android: GXTemplateEngine → GXTemplateInfoSource → GXTemplateManager
   - Harmony: GXTemplateEngine → GXTemplateManager → GXTemplateLRUCache
   - Harmony 缺少 GXTemplateInfoSource 中间层,缓存策略相对简单

2. **模板缓存策略**
   - Android: 模板信息缓存 + 布局结果缓存 + 视图节点缓存
   - Harmony: 仅有基础的 LRU 模板缓存,缺少布局和视图节点缓存

**缺失功能**:
1. 模板预编译工具链与打包流程对齐 (与 Android 的 GXAssetsBinaryWithoutSuffixTemplate 能力保持一致)
2. 模板热更新能力
3. 多版本模板管理
4. 完善的多级缓存策略
5. 模板锁机制完善

#### 2.1.2 注册中心

| 功能项         | GaiaX Android                     | GaiaX Harmony                     | 差异说明                                     |
| -------------- | --------------------------------- | --------------------------------- | -------------------------------------------- |
| 组件注册       | ✅ GXRegisterCenter                | ✅ GXRegisterCenter                | 基本功能已实现                               |
| 扩展点管理     | ✅ 多种扩展接口                    | ⚠️ 基础扩展接口                    | Android 支持表达式、数据绑定、颜色等多种扩展 |
| 自定义视图注册 | ✅ registerExtensionViewSupport    | ❌ 不支持                          | 缺少自定义组件注册能力                       |
| 适配器注册     | ✅ registerExtensionXXX            | ❌ 不支持                          | 无适配器扩展机制                             |
| 模板源注册     | ✅ registerExtensionTemplateSource | ✅ registerExtensionTemplateSource | 都支持,但 Android 支持优先级配置             |
| 全局配置管理   | ✅ 完整支持                        | ⚠️ 部分支持                        | 配置能力有限                                 |

**功能细节分析**:

1. **Android GXRegisterCenter 支持的扩展接口**:
   - GXIExtensionExpression: 自定义表达式解析器
   - GXIExtensionDataBinding: 自定义数据绑定逻辑
   - GXIExtensionColor: 颜色转换器
   - GXIExtensionSize: 尺寸转换器
   - GXIExtensionDynamicProperty: 动态属性支持

2. **Harmony GXRegisterCenter 当前支持**:
   - 基础的模板源注册
   - 缺少上述多种扩展接口

**缺失功能**:
1. 自定义视图注册机制 (registerExtensionViewSupport)
2. 适配器注册系统 (registerExtensionLottieAnimation 等)
3. 完善的扩展点管理 (表达式、数据绑定、颜色等扩展接口)
4. 全局配置能力扩展
5. 优先级管理机制

### 2.2 模板系统对比

#### 2.2.1 基础组件

| 组件类型            | GaiaX Android | GaiaX Harmony | 差异说明             |
| ------------------- | ------------- | ------------- | -------------------- |
| View (容器视图)     | ✅ 完整支持    | ✅ 完整支持    | 已对齐               |
| Text (文本)         | ✅ 完整支持    | ✅ 完整支持    | 已对齐               |
| Image (图片)        | ✅ 完整支持    | ✅ 完整支持    | 已对齐               |
| RichText (富文本)   | ✅ 完整支持    | ✅ 完整支持    | 已对齐               |
| Lottie (动画)       | ✅ 完整支持    | ❌ 不支持      | 缺少 Lottie 动画支持 |
| Custom (自定义组件) | ✅ 完整支持    | ❌ 不支持      | 无自定义组件支持     |
| Progress (进度条)   | ✅ 完整支持    | ❌ 不支持      | 缺少进度条组件       |
| IconFont (图标字体) | ✅ 完整支持    | ❌ 不支持      | 缺少图标字体支持     |
| Lottie支持 | ✅ 完整支持    | ❌ 不支持      |                                                  |

**缺失功能**:
1. Lottie 动画组件
   1. https://ohpm.openharmony.cn/#/cn/detail/@ohos%2Flottie-turbo
2. 自定义组件扩展能力
   1. 
3. Progress 进度条组件
4. IconFont 图标字体支持
   1. https://developer.huawei.com/consumer/cn/doc/harmonyos-faqs/faqs-arkui-216

#### 2.2.2 容器组件

| 组件类型          | GaiaX Android | GaiaX Harmony | 差异说明                |
| ----------------- | ------------- | ------------- | ----------------------- |
| Scroll (滚动容器) | ✅ 完整支持    | ✅ 基础支持    | Harmony 功能较简单      |
| Grid (网格容器)   | ✅ 完整支持    | ✅ 基础支持    | Harmony 功能较简单      |
| Slider (轮播容器) | ✅ 完整支持    | ✅ 基础支持    | Harmony 功能较简单      |
| Footer 支持       | ✅ 完整支持    | ❌ 不支持      | Grid/Scroll 缺少 Footer |
| Header 支持       | ✅ 完整支持    | ❌ 不支持      | Grid/Scroll 缺少 Header |

**"分页加载"能力说明**:

分页加载(Load More)是 Grid/Scroll 容器的一个高级特性,用于处理大量数据的分批加载场景。在 Android SDK 中,通过以下机制实现:

1. **Footer 模板机制**:
   - 通过 `item-footer-type` 配置指定 footer 模板 ID
   - 通过 `hasMore` 参数控制 footer 是否显示
   - Footer 会自动添加到列表末尾

2. **数据绑定示例**:
   ```
   {
     "data": {
       "gx-grid": {
         "value": "$nodes",
         "extend": {
           "item-footer-type": {
             "id": "'footer-template-id'",
             "hasMore": "$isHasMore"
           }
         }
       }
     }
   }
   ```

3. **应用场景**:
   - 长列表滚动到底部时显示"加载更多"提示
   - 根据 `hasMore` 状态动态显示/隐藏 footer
   - 常用于实现无限滚动、下拉加载等交互

4. **Harmony SDK 现状**:
   - ❌ 缺少 `item-footer-type` 配置支持
   - ❌ 缺少 `hasMore` 状态管理
   - ❌ 缺少 Footer 自动插入机制

**缺失功能**:
1. 完善的容器配置能力
3. Header/Footer 支持(包括分页加载 Footer)、
5. 容器事件处理增强

### 2.3 样式系统对比

#### 2.3.1 样式属性支持

| 功能项       | GaiaX Android | GaiaX Harmony | 差异说明                                                                                                                              |
| ------------ | ------------- | ------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| FlexBox 布局 | ✅ 完整支持    | ✅ 完整支持    |                                                                                                                                       |
| 基础样式属性 | ✅ 完整支持    | ✅ 基础支持    | 已支持宽高、margin/padding、颜色、字体、透明度、行高、行数等常用样式，但 hidden、部分 display 取值、mode/mode-type 等高级样式尚未对齐 |
| 渐变背景     | ✅ 完整支持    | ✅ 完整支持    |                                                                                                                                       |
| 阴影效果     | ✅ 完整支持    | ✅ 完整支持    |                                                                                                                                       |
| 圆角处理     | ✅ 完整支持    | ✅ 完整支持    |                                                                                                                                       |
| 边框样式     | ✅ 完整支持    | ✅ 基础支持    | 支持统一 border-width/border-color/border-radius，暂不支持 border-style 以及单边边框等复杂样式                                        |

差异包括：
- 基础样式属性：
  - Android 通过 `GXStyleConvert` + `GXStyle` 完整解析布局、字体、颜色、行高、行数、文本溢出等样式，并支持 `hidden`、`mode/mode-type` 等扩展属性；
  - Harmony 使用 `GXStyleHelper.ets` + 各 Node（如 `GXBaseNode.ets`、`GXTextNode.ets`）直接解析 ArkTS 侧样式，已支持宽高、margin/padding、颜色、字体、行高、行数等，但暂未处理 `hidden`，且部分 `display` / 图片裁剪模式等行为更简化。
- 边框样式：
  - Android 支持 `border-width` / `border-color` / `border-radius` 以及 `border-style`、单边边框等组合能力；
  - Harmony 目前只解析统一的 `border-width`、`border-color` 和 `border-radius` / `border-*-radius`，模板里的 `border-style` 不参与解析，无法表达虚线、单边边框等复杂样式。
- hidden 属性：
  - Android 通过样式系统解析 `hidden` 并结合 `display` 控制可见性。
  - Harmony 仅在模板中存在 `hidden:true/false`，但样式解析代码未处理该字段，实际以 `display` / `overflow` 为准。
- `display` 取值：
  - Android 侧对 `display:absolute` 等取值有兼容处理，并配合 `position` 一起使用；
  - Harmony 仅区分 `none` 与普通 `flex`，`absolute` 依赖 `position` 而非 `display`，行为上更简化。
- 图片裁剪模式：
  - Android 支持 `mode` + `mode-type` 等丰富裁剪模式；
  - Harmony 仅在图片节点中解析 `mode`，并只映射 `cover` / `contain` / 其它 → `fill`，高级模式未对齐。
- 阴影参数：
  - Android 的 `box-shadow` 支持完整参数和多种组合；
  - Harmony 在 `GXBaseNode` 中仅按固定五段字符串取偏移、半径和颜色，部分高级阴影效果无法直接表达。
- 背景滤镜：
  - Android 将 `backdrop-filter` 解析为结构化对象并参与渲染；
  - Harmony 目前仅保存 `backdrop-filter` 字符串，ArkTS 组件侧尚未完整消费该属性。
    - 可通过backgroundBlurStyle属性实现
    - https://developer.huawei.com/consumer/cn/doc/harmonyos-references/ts-universal-attributes-background#backgroundblurstyle19


#### 2.3.3 Stretch 布局引擎说明

**重要说明**: GaiaX 的 FlexBox 布局能力由 **Stretch 布局引擎**提供,Stretch 是使用 **Rust 语言编写**的高性能布局引擎,并以 **.so 动态库**形式直接集成到各平台。

**Stretch 跨平台架构**:

```
┌─────────────────────────────────────────────┐
│      Android 平台 (Kotlin/Java)             │
│   ┌──────────────────────────────────┐      │
│   │ GXStretchNode.kt (JNI桥接层)     │      │
│   └──────────────┬───────────────────┘      │
└──────────────────┼──────────────────────────┘
                   │ JNI 调用
                   ▼
┌─────────────────────────────────────────────┐
│   Rust 核心引擎 (libgxstretch.so)           │
│  ┌──────────────────────────────────────┐  │
│  │ node.rs (节点管理)                   │  │
│  │ algo.rs (FlexBox 布局算法)           │  │
│  │ style.rs (样式定义)                  │  │
│  │ forest.rs (节点树结构)               │  │
│  └──────────────────────────────────────┘  │
└──────────────────┬──────────────────────────┘
                   │ NAPI 调用
                   ▼
┌─────────────────────────────────────────────┐
│      HarmonyOS 平台 (ArkTS)                 │
│   ┌──────────────────────────────────┐      │
│   │ napi_init.cpp (NAPI桥接层)       │      │
│   └──────────────────────────────────┘      │
└─────────────────────────────────────────────┘
```

**技术实现细节**:

1. **核心引擎**: Stretch 使用 Rust 实现,提供:
   - 完整的 FlexBox 规范实现
   - 高性能布局计算算法
   - 内存安全保证
   - 跨平台一致性

2. **平台桥接**:
   - **Android**: 通过 Rust FFI + JNI 桥接,binding 代码位于 `bindings/kotlin`
   - **HarmonyOS**: 通过 Rust FFI + NAPI 桥接,binding 代码位于 `bindings/harmony`
   - **iOS**: 通过 Rust FFI + Objective-C 桥接,binding 代码位于 `bindings/swift`

3. **库集成方式**:
   - Android: `libgxstretch.so` 通过 JNI 加载
   - HarmonyOS: `libgxstretch.so` 通过 NAPI 加载
   - iOS: 静态库形式集成

**Stretch 布局能力对齐情况**:

| 功能项        | GaiaX Android | GaiaX Harmony | 差异说明             |
| ------------- | ------------- | ------------- | -------------------- |
| Rust 核心引擎 | ✅ 完整支持    | ✅ 完整支持    | 共用同一套 Rust 代码 |
| FlexBox 算法  | ✅ 完整支持    | ✅ 完整支持    | Rust 层实现,功能一致 |
| 布局计算      | ✅ 完整支持    | ✅ 完整支持    | Rust 层实现,功能一致 |
| 节点管理      | ✅ 完整支持    | ✅ 完整支持    | Rust 层实现,功能一致 |
| 样式属性      | ✅ 完整支持    | ✅ 完整支持    | Rust 层实现,功能一致 |
| 布局缓存      | ✅ 完整支持    | ✅ 完整支持    | Rust 层实现,功能一致 |


✅ **核心布局能力已对齐**: 由于 Android 和 HarmonyOS 都使用相同的 Stretch Rust 核心代码,FlexBox 布局算法在两个平台上完全一致。

### 2.4 表达式分析引擎

表达式分析引擎(GXAnalyze)是 GaiaX SDK 的核心组件,基于 **C++ 实现**,并通过 **.so 动态库**形式直接集成到各平台。**注意**:GXAnalyze 并非平台特定实现,而是通过 JNI/NAPI 桥接方式在 Android 和 HarmonyOS 平台上调用同一套 C++ 核心代码。

#### 2.4.1 技术架构说明

**GXAnalyze 跨平台架构**:

```
┌─────────────────────────────────────────────┐
│         Android 平台 (Java/Kotlin)          │
│    ┌─────────────────────────────────┐     │
│    │  GXAnalyze.kt (JNI桥接层)       │     │
│    └──────────────┬──────────────────┘     │
└───────────────────┼────────────────────────┘
                    │ JNI 调用
                    ▼
┌─────────────────────────────────────────────┐
│     C++ 核心引擎 (libGXAnalyze.so)          │
│  ┌──────────────────────────────────────┐  │
│  │ GXAnalyze.cpp (表达式分析核心)       │  │
│  │ GXWordAnalyze.cpp (词法分析器)       │  │
│  │ GXATSNode.cpp (语法树节点)           │  │
│  └──────────────────────────────────────┘  │
└───────────────────┬─────────────────────────┘
                    │ NAPI 调用
                    ▼
┌─────────────────────────────────────────────┐
│      HarmonyOS 平台 (ArkTS)                 │
│    ┌─────────────────────────────────┐     │
│    │ GXNapiAnalyze.cpp (NAPI桥接层)  │     │
│    └─────────────────────────────────┘     │
└─────────────────────────────────────────────┘
```

**技术实现细节**:
1. **核心引擎**: GXAnalyzeCore 使用纯 C++ 实现,包含:
   - `GXAnalyze.cpp`: 表达式求值核心算法
   - `GXWordAnalyze.cpp`: 词法分析器
   - `GXATSNode.cpp`: 抽象语法树节点实现
   - `GXValue.h`: 数据类型定义

2. **平台桥接**:
   - **Android**: 通过 JNI 桥接,GXJniAnalyze.cpp 实现 Java/Kotlin 与 C++ 的互操作
   - **HarmonyOS**: 通过 NAPI 桥接,GXNapiAnalyze.cpp 实现 ArkTS 与 C++ 的互操作
   - **iOS**: 通过 Objective-C++ 桥接,GXAnalyzeImpl.hpp 实现 OC 与 C++ 的互操作

3. **库集成方式**:
   - Android: `libGXAnalyze.so` 通过 JNI 加载
   - HarmonyOS: `libGXAnalyze.so` 通过 NAPI 加载
   - iOS: 静态库形式集成

#### 2.4.2 功能对比

| 功能项         | GaiaX Android | GaiaX Harmony | 差异说明                |
| -------------- | ------------- | ------------- | ----------------------- |
| C++ 核心引擎   | ✅ 完整支持    | ✅ 完整支持    | 共用同一套 C++ 代码     |
| 基础表达式     | ✅ 完整支持    | ✅ 完整支持    | 已对齐                  |
| 复杂表达式     | ✅ 完整支持    | ✅ 完整支持    | C++ 层实现,功能一致     |
| 函数调用       | ✅ 完整支持    | ✅ 完整支持    | C++ 层实现,功能一致     |
| 表达式缓存     | ✅ 完整支持    | ✅ 完整支持    | C++ 层实现,功能一致     |
| 错误处理       | ✅ 完整支持    | ✅ 完整支持    | C++ 层实现,功能一致     |
| AST 解析       | ✅ 完整支持    | ✅ 完整支持    | C++ 层实现,功能一致     |
| JNI/NAPI 桥接  | ✅ JNI         | ✅ NAPI        | 桥接层不同,核心逻辑相同 |
| 自定义函数扩展 | ✅ 完整支持    | ✅ 部分支持    | 桥接层需支持回调机制    |

#### 2.4.3 已对齐能力

由于 Android 和 HarmonyOS 都使用 **相同的 C++ 核心代码**(`libGXAnalyze.so`),因此表达式分析引擎的核心功能在两个平台上完全一致:

✅ **核心能力已对齐**:
1. 表达式词法分析
2. 语法解析(LR 分析法)
3. AST 构建与求值
4. 内置函数库
5. 表达式缓存机制
6. 错误处理机制

#### 2.4.4 需验证能力

⚠️ **需要在 HarmonyOS 上验证的功能**:

1. **自定义函数注册机制**:
   - Android 通过 JNI 回调实现自定义函数
   - HarmonyOS 已通过 `gxInjectGetFunctionValue` + ArkTS 回调（见 `Index.ets` 与 `transfrom.ts`）支持自定义函数，当前仅内置实现 `size` 函数，其他函数需在 ArkTS 侧按需补齐
     - NAPI 回调链路已打通，后续主要工作是扩展函数映射与完善测试

2. **桥接层性能**:
   - JNI 性能已验证
   - NAPI 调用性能需实测对比
   - 数据类型转换开销需评估

3. **扩展接口完整性**:
   - 验证 `GXNapiAnalyze.cpp` 是否暴露了所有必要的 C++ 接口
   - 确认 `IComputeExtend` 等扩展接口在 HarmonyOS 上的可用性

**需要补充的工作量**:
- 验证 NAPI 桥接层功能完整性
- 完善自定义函数扩展机制
- 设计 Harmony 端计算扩展挂钩（ArkTS 层 ComputeExtend 能力）

#### 2.4.5 计算逻辑扩展能力差异与建议

- **平台差异概览**:
  - Android 端通过 `GXAnalyze` 暴露 `IComputeExtend` 接口，业务方可以按实例注入 `computeValueExpression()` 与 `computeFunctionExpression()`，实现表达式取值和函数计算逻辑的外部扩展；
  - HarmonyOS 端采用 `GXNapiAnalyze.cpp` + ArkTS `transfrom.ts` 方案，C++ 通过 `gxInjectGetSourceValue` / `gxInjectGetFunctionValue` 注入 ArkTS 回调，目前实际计算逻辑集中在 `getSourceValue()` 与 `getFunctionValue()` 中，仅内置实现 `size()` 函数，业务侧缺少与 `IComputeExtend` 等价的扩展挂钩。
- **约束与影响**:
  - Android 可以根据业务场景在宿主层按需实现不同的扩展计算逻辑（例如：灰度开关、埋点、复杂函数库等）；
  - Harmony 当前只能通过修改框架 ArkTS 代码扩展表达式能力，无法在业务侧按实例 / 按场景注入计算逻辑，扩展和灰度能力相对受限。
- **建议的 Harmony 扩展思路**（不改动 C++ / NAPI 的前提下）:
  - 在 `transfrom.ts` 中新增类似 Android `IComputeExtend` 的 ArkTS 接口，如 `ComputeExtend { computeValueExpression?, computeFunctionExpression? }`，并提供 `initComputeExtend()` 注册方法；
  - 在 `getSourceValue()` / `getFunctionValue()` 内优先调用注册的 `ComputeExtend`，只有在扩展未处理或返回空结果时才回落到当前内置逻辑（如 `size()`）；
  - 业务侧在初始化 GaiaXAnalyze 模块时调用 `initComputeExtend()` 注入自身的计算扩展，实现与 Android 端相近的“外部计算逻辑扩展”能力。


### 2.5 数据绑定与事件系统

#### 2.5.2 事件系统

| 功能项     | GaiaX Android | GaiaX Harmony | 差异说明               |
| ---------- | ------------- | ------------- | ---------------------- |
| 点击事件   | ✅ 完整支持    | ✅ 完整支持    | 已对齐                 |
| 长按事件   | ✅ 完整支持    | ❌ 不支持      | 缺少长按手势与事件绑定 |
| 滑动事件   | ✅ 完整支持    | ⚠️ 部分支持    | 事件细节不完整         |
| 自定义事件 | ✅ 完整支持    | ❌ 不支持      | 缺少自定义事件         |
| 事件冒泡   | ✅ 完整支持    | ❌ 不支持      | 缺少事件冒泡机制       |
| 事件拦截   | ✅ 完整支持    | ❌ 不支持      | 缺少事件拦截           |
| 曝光埋点   | ✅ 完整支持    | ❌ 不支持      | 缺少曝光跟踪           |

**缺失功能**:
1. 自定义事件支持
2. 事件冒泡机制
3. 事件拦截能力
4. 曝光埋点跟踪

**对齐方案（Harmony 事件系统）**:
- **长按与基础手势对齐**: 基于 `GXEventType` 已包含 `longPress` 的设计，在 ArkTS 视图构建层（如 `GXViewBuilder` 及各组件 Node 对应的 ArkUI 组件）同时注册 `tap` / `longPress` 手势，将绑定 JSON 中 `"type": "'longpress'"` 解析为 `GXEvent.eventType = GXEventType.longPress`，并复用 `GXNode.handleEvent()` → `GXTemplateData.clickEventCallback` 的链路，补齐长按事件能力。
- **滑动事件完善**: 利用 `GXScrollView.ets` 中的 `.onDidScroll` / `.onScrollStop` 回调，在滚动发生与结束时构造 `GXEvent`（`GXEventType.scroll` / `scrollEnd`），通过关联的 `GXScrollNode` 调用 `handleEvent()`，并在模板中对齐 Android `GXScroll` 事件字段（如偏移量、方向），统一业务在 Android/Harmony 上的滚动事件处理方式。
- **自定义事件与事件扩展**: 在 `GXEvent` 结构中新增自定义事件名字段（如 `name` 或在 `eventParams` 中约定 `eventName`），解析模板绑定 JSON 中的扩展事件类型（如 `'js'`、业务自定义字符串），并在 `GXTemplateData` 中提供通用事件回调或在现有 click 回调中透出事件名，由业务侧按需路由，实现与 Android 自定义事件机制功能等价。
- **事件冒泡与拦截**: 复用 `GXNode` 已有的 `parent` 链，在 `handleEvent()` 中增加“当前节点未消费则向父节点继续分发”的逻辑，并在事件绑定配置中对齐 Android 侧 `option.level` / `option.cover` 等语义，引入简单的 `stopPropagation` / 事件级别标志，完成基础的事件冒泡与拦截能力，参考 Wiki《组件系统》《事件处理》中对事件冒泡、事件分发的设计。
- **曝光埋点对齐**: 基于现有 `GXTrack` / `trackEventCallback`，在 Scroll/Grid 等容器 Node 中通过 ArkUI 的 `onAppear` / `onDisappear` 或首帧布局结果，构造曝光/离屏埋点事件并回调业务，埋点字段格式对齐 Android `GXTrackBinding`（如埋点类型、曝光 ID、业务参数），从而让同一模板的曝光埋点配置在 Android/Harmony 间保持一致。
  
### 2.6 扩展机制对比

#### 2.6.1 适配器系统

| 功能项        | GaiaX Android | GaiaX Harmony | 差异说明                                                |
| ------------- | ------------- | ------------- | -------------------------------------------------------|
| 图片适配器    | ✅ 完整支持    | ⚠️ 基础支持    | Harmony 通过 GXInjector 提供图片构建注入能力,但缺少通用适配器框架和多类型图片适配器 |

| 自定义适配器  | ✅ 完整支持    | ⚠️ 能力有限    | 当前仅支持图片注入,尚未提供统一的自定义适配器扩展机制          |

**缺失功能**:
1. 完整的适配器架构
2. 图片加载适配器
3. 动画适配器
4. 自定义适配器注册机制
   

### 2.8 测试覆盖对比

Android SDK 包含非常完善的测试用例,位于 `GaiaXAndroid/src/androidTest/java/com/alibaba/gaiax/`,这些测试用例是功能验证的重要依据。Harmony SDK 需要建立同等级别的测试体系。

#### 2.8.1 组件测试覆盖

| 测试类别    | Android 测试文件            | 测试用例数 | Harmony 状态 | 差异说明                                          |
| ----------- | --------------------------- | ---------- | ------------ | ------------------------------------------------- |
| Grid 容器   | GXComponentGridTest.kt      | 48+        | ❌ 无测试     | 测试包含: 动态列、Padding、Footer、Header、分页等 |
| Scroll 容器 | GXComponentScrollTest.kt    | 66+        | ❌ 无测试     | 测试包含: 水平/垂直滚动、Item 绑定、数据更新等    |
| Text 组件   | GXComponentTextTest.kt      | 63+        | ❌ 无测试     | 测试包含: 字体、颜色、行高、省略、对齐等          |
| Slider 组件 | GXComponentSliderTest.kt    | 5+         | ❌ 无测试     | 测试包含: 轮播配置、指示器、自动播放等            |
| View 组件   | GXComponentViewTest.kt      | 21+        | ❌ 无测试     | 测试包含: 背景、边框、圆角、阴影等                |
| Event 事件  | GXComponentEventTest.kt     | 27+        | ❌ 无测试     | 测试包含: 点击、长按、JS 事件、事件冒泡等         |
| Animation   | GXComponentAnimationTest.kt | 11+        | ❌ 无测试     | 测试包含: Lottie 动画、动画控制等                 |
| Image 组件  | GXComponentImageTest.kt     | 1+         | ❌ 无测试     | 测试包含: 图片加载、缩放模式等                    |
| RichText    | GXComponentRichTextTest.kt  | 2+         | ❌ 无测试     | 测试包含: HTML 渲染、样式等                       |
| Progress    | GXComponentProgressTest.kt  | 1+         | ❌ 无测试     | 测试包含: 进度条显示等                            |
| IconFont    | GXComponentIconFontTest.kt  | 1+         | ❌ 无测试     | 测试包含: 图标字体显示等                          |
| Custom 组件 | GXComponentCustomTest.kt    | 1+         | ❌ 无测试     | 测试包含: 自定义组件注册和使用                    |

#### 2.8.2 核心功能测试

| 测试类别     | Android 测试文件          | 测试用例数 | Harmony 状态 | 差异说明                                   |
| ------------ | ------------------------- | ---------- | ------------ | ------------------------------------------ |
| 通用功能     | GXCommonTest.kt           | 33+        | ❌ 无测试     | 测试包含: 布局、嵌套、响应式、设计令牌等   |
| 表达式       | GXYKExpressionTest.kt     | 49+        | ❌ 无测试     | 测试包含: 三元表达式、函数调用、逻辑运算等 |
| 表达式通用   | GXCommonExpressionTest.kt | 3+         | ❌ 无测试     | 测试包含: 表达式通用功能                   |
| API 测试     | GXCommonApiTest.kt        | 1+         | ❌ 无测试     | 测试包含: 常用 API 调用                    |
| CSS 样式     | GXCssTest.kt              | 1+         | ❌ 无测试     | 测试包含: CSS 解析和应用                   |
| Stretch 布局 | GXStretchTest.kt          | 1+         | ❌ 无测试     | 测试包含: FlexBox 布局计算                 |
| 重构测试     | GXRefactorTest.kt         | 3+         | ❌ 无测试     | 测试包含: 重构后的功能验证                 |
| 业务测试     | GXBusinessTest.kt         | 9+         | ❌ 无测试     | 测试包含: 业务场景特定测试                 |

#### 2.8.3 样式系统测试

| 测试类别   | Android 测试文件      | 测试用例数 | Harmony 状态 | 差异说明                               |
| ---------- | --------------------- | ---------- | ------------ | -------------------------------------- |
| 样式转换   | GXStyleConvertTest.kt | 24+        | ❌ 无测试     | 测试包含: 颜色、尺寸、渐变、阴影等转换 |
| 样式对象   | GXStyleTest.kt        | 5+         | ❌ 无测试     | 测试包含: 样式对象解析和应用           |
| FlexBox    | GXFlexBoxTest.kt      | 1+         | ❌ 无测试     | 测试包含: FlexBox 属性解析             |
| 颜色处理   | GXColorTest.kt        | 2+         | ❌ 无测试     | 测试包含: 颜色解析和转换               |
| 尺寸处理   | GXSizeTest.kt         | 1+         | ❌ 无测试     | 测试包含: 尺寸单位转换                 |
| Layer 层级 | GXLayerTest.kt        | 4+         | ❌ 无测试     | 测试包含: 层级结构解析                 |

#### 2.8.4 测试用例细节分析

**Grid 组件测试示例** (GXComponentGridTest.kt):
- 动态列数调整 (template_grid_dynamic_column_and_padding)
- 单列 Padding 处理 (template_grid_one_column_padding_left_and_right)
- 多数据处理 (template_grid_one_column_padding_left_and_right_multi_data)
- 单行等高 (template_grid_single_line_same_height)
- Footer 支持 (template_grid_load_more_fixed_footer_size)
- 分页加载、虚拟列表、响应式布局等

**Scroll 组件测试示例** (GXComponentScrollTest.kt):
- 不同 Item 宽度 (template_scroll_different_item_width)
- 容器 Item 绑定 (template_container_item_bind)
- 数据更新 (template_data_update)
- 响应式规则 (template_scroll_responsive_rule)
- 水平/垂直滚动、Footer/Header、DiffUtil 支持等

**Text 组件测试示例** (GXComponentTextTest.kt):
- 背景颜色 (template_text_background_color)
- 边框样式 (template_text_border)
- 圆角处理 (template_text_radius)
- 响应式缩放 (template_text_responsive_scale)
- 字体缩放 (template_text_font_scale)
- 行高缩放 (template_text_line_height_scale)
- 字体族 (template_text_processor_font_family)
- 文本省略、对齐、颜色、装饰线等

**Event 事件测试示例** (GXComponentEventTest.kt):
- 点击事件处理
- 长按事件处理
- JS 事件与数据绑定事件混合 (GXMixNodeEvent)
- 事件冒泡机制
- 事件覆盖逻辑 (jsOptionCover, jsOptionLevel)
- 自定义事件扩展 (GXExtensionNodeEvent)

**表达式测试示例** (GXYKExpressionTest.kt):
- 常量表达式 (expression_constant)
- 变量取值 (expression_value)
- 三元表达式 (expression_ternary_*)
- 嵌套三元表达式
- 空值合并运算符 (?:)
- 逻辑运算、比较运算、算术运算
- 函数调用等

**样式转换测试示例** (GXStyleConvertTest.kt):
- 颜色转换 (Hex, RGB, RGBA)
- 渐变背景 (linear-gradient)
- 阴影效果
- 尺寸单位转换 (px, pt, %)
- 边框样式
- 圆角处理等

#### 2.8.5 测试覆盖统计

| 类别     | Android 测试文件数 | 测试用例总数 | Harmony 状态                                             |
| -------- | ------------------ | ------------ | -------------------------------------------------------- |
| 组件测试 | 11 个              | 240+         | 基础用例少量,尚未形成系统化组件测试                      |
| 核心功能 | 8 个               | 100+         | 基础用例少量,覆盖范围远低于 Android                      |
| 样式系统 | 6 个               | 38+          | 暂无专门样式系统测试                                     |
| **总计** | **25 个**          | **378+**     | **已有基础测试工程,但整体覆盖度与 Android 存在明显差距** |

**缺失功能**:
1. 完整的组件测试体系 (11 个测试类)
2. 核心功能测试 (8 个测试类)
3. 样式系统测试 (6 个测试类)
4. 自动化测试框架 (HarmonyOS 测试框架集成)
5. 测试工具类和辅助方法
6. Mock 数据和模板管理
7. 性能测试和基准测试

### 2.9 开发者工具支持

#### 2.9.1 调试工具

| 功能项      | GaiaX Android | GaiaX Harmony | 差异说明           |
| ----------- | ------------- | ------------- | ------------------ |
| Studio 连接 | ✅ 完整支持    | ❌ 不支持      | 无 Studio 连接能力 |
| 实时预览    | ✅ 完整支持    | ❌ 不支持      | 缺少实时预览       |
| 模板调试    | ✅ 完整支持    | ❌ 不支持      | 缺少调试能力       |
| 日志系统    | ✅ 完整支持    | ⚠️ 部分支持    | 日志能力有限       |

**缺失功能**:
1. GaiaX Studio 连接能力
2. 实时预览功能
3. 模板调试工具
5. 完善的日志系统

## 三、GaiaX JS 功能对比

### 3.1 现有 GaiaX JS Runtime 技术链路主体思路

GaiaX Android 侧的 JS Runtime 以 **QuickJS** 为执行引擎，核心目标是：

- **在 JS 侧铺一层通用的运行时环境(`bootstrap.js`)**，提供模块系统、Page/Component 抽象、事件系统、动画与埋点能力，以及 JS↔Native 的 Bridge 能力。
- **在 Native 侧通过一套 Host 封装层(GXJSEngine/GXHostEngine/GXHostRuntime/GXHostContext)**，屏蔽具体 JS 引擎(QuickJS)差异，对上暴露统一的引擎、上下文、页面/组件生命周期与事件接口。
- **通过 Bridge + ScriptBuilder + ModuleManager** 完成 JS 代码的动态注入、原生模块能力暴露，以及 JS 事件/调用结果的回传。

结合 `gaiax-js-runtime.md`、`gaiax-js-run-demo.md` 和 Android 代码，整体链路可以概括为：

1. **引擎与运行时初始化**
   - App 调用 `GXJSEngine.init(context)` + `startDefaultEngine()`。
   - `GXJSEngine` 创建 `GXHostEngine`，再由 `GXHostEngine.initEngine()` 创建具体引擎实例 `QuickJSEngine`、运行时 `GXHostRuntime`。
   - `GXHostRuntime.initRuntime()` 内部创建 `QuickJSRuntime`(封装 QuickJS 的 JSRuntime)，并创建 `GXHostContext`。
   - `GXHostContext.initContext()` 创建 `QuickJSContext`，完成 JSContext 创建、内置模块注册(`timer` / `GaiaXJSBridge`)、PendingJob 驱动等。

2. **执行 bootstrap，搭建 JS SDK 运行时世界**
   - `GXHostEngine.startEngine()` → `GXHostRuntime.startRuntime()` → `GXHostContext.startContext()`。
   - `GXHostContext.startContext()` 调用 `QuickJSContext.initBootstrap()`：
     - 通过 `GXScriptBuilder` 依次拼接：
       - Import/辅助函数脚本(`buildImportScript`)；
       - 全局上下文标识(`buildGlobalContext`)，写入 `__CONTEXT_ID__`、`__ENGINE_TYPE__` 等；
       - 扩展与赋值脚本(`buildExtendAndAssignScript`)；
       - 从 assets 读取 `bootstrap.js` 全量内容；
       - 模块脚本(`moduleManager.buildModulesScript`)；
       - 样式相关脚本(`buildStyle`)。
   - `QuickJSContext.startBootstrap()` 使用 `JSContext.evaluate(bootstrap, "index.js", EVAL_TYPE_MODULE)` 执行合成脚本。
   - 执行完成后，JS 全局中建立：
     - 模块系统：`__gaiaxjs_modules__` / `__gaiaxjs_require__`；
     - Page/Component 抽象：`Page`/`Component`，内部通过 `IMs(InstancesManager)` 管理实例；
     - 事件体系：`GaiaXJSEvent` + `GaiaXJSEventTarget` + `IMs.dispatchEvent`；
     - 动画、曝光/埋点、工具、粘性事件等对象：`timeline`、`Log2APlus`、`sticky`、`Utils` 等；
     - JS↔Native Bridge：`Bridge` + `window.postMessage` 等封装。

3. **页面/组件脚本注册与生命周期驱动**
   - 当某个模板需要 JS 逻辑时，Native 侧通过 `GXJSEngine.registerPage(...)`/`registerComponent(...)` 将业务 JS 传入，引擎使用 `GXScriptBuilder` 生成调用全局 `Page({...}, meta)` 或 `Component({...}, meta)` 的脚本，在 `QuickJSContext` 中执行。
   - `Page(...)`/`Component(...)` 调用由 `bootstrap.js` 中的实现接管，创建 `__Page__` / `__Component__` 实例并注册到 `IMs`，建立 **模板ID/实例ID ↔ JS 实例对象** 的映射。
   - 模板首帧渲染/复用等生命周期时机，Native 通过 `GXJSEngine.onLoad/onReady/onShow/...` 等接口，最终在 JS 侧调用对应 Page/Component 实例的生命周期方法。

4. **事件链路与 JS ↔ Native 调用**
   - **原生事件 → JS：**
     - 用户在原生 View 上触发点击/滚动等事件，GaiaX 原生事件层(`GXJSRenderProxy` 等)将事件封装成 JSON，通过 `QuickJSBridgeModule`/`GXHostContext.evaluateJS` 在 JS 中调用 `window.postMessage` 或专用入口。
     - `window.postMessage` 在 `bootstrap.js` 内部被实现为：封装为 `GaiaXJSEvent`，再交由 `IMs.dispatchEvent` 路由到对应 Page/Component 实例，最终通过 `GaiaXJSEventTarget.dispatchEvent` 调用在 JS 中通过 `addEventListener` 绑定的回调函数。
   - **JS → 原生：**
     - JS 通过 `gaiax.xxx(...)` 或 `Bridge.callSync/Async/Promise` 调用原生模块能力(比如 Tips/Storage/Track/Animation 等)，Bridge 模块将调用参数与 `contextId` 封装成 JSON，交给 `QuickJSBridgeModule`。
     - `QuickJSBridgeModule` 再通过 `GXHostContext` + `GXModuleManager` 分发到具体的 `GXJSBaseModule` 实现，并在结果返回时回调 JS 侧的 success/failure 回调或 resolve/reject Promise。
   - **定时与微任务：**
     - `setTimeout`/`setInterval` 由 `timer` 模块 + `QuickJSTimer` 完成，映射到 Native 的任务队列；
     - `GXHostContext.initPendingJob()` 通过轮询 `executePendingJob` 驱动 QuickJS 的 microtask 队列，保证 Promise/微任务按顺序执行。

整体来看，**Android 的 GaiaX JS Runtime 是“QuickJS 引擎 + Host 封装 + bootstrap.js 运行时 + Bridge/模块体系”的组合**，实现了模板 JS 逻辑与原生 GaiaX SDK 的解耦与双向通信。

### 3.2 基于 JSVM 的 GaiaX JS Harmony 版本需要实现的模块

在 HarmonyOS 上基于 **JSVM-API** 实现与 Android 等价的 `gaiax-js-runtime`，可以按 **Native(JSVM/C++) 层 → ArkTS 宿主层 → JS(Runtime) 层 → Harmony 原生能力模块** 分层规划，主要模块如下：

#### 3.2.1 Native / JSVM 层模块

- **JSVM 引擎适配层(类比 `QuickJSEngine/QuickJSRuntime/QuickJSContext`)**
  - 封装 `JSVM_VM` / `JSVM_Env` 生命周期管理：`OH_JSVM_Init`、`OH_JSVM_CreateVM/DestroyVM`、`OH_JSVM_CreateEnv/DestroyEnv`、`Open/CloseVMScope`、`Open/CloseEnvScope`、`Open/CloseHandleScope` 等。
  - 提供统一的 `evaluate(script, filename, mode)` 能力：基于 `OH_JSVM_CompileScript` + `OH_JSVM_RunScript`(可选 CodeCache/Context Snapshot)。
  - 封装 `HandleScope`、`JSVM_Ref` 管理，严格遵守《JSVM-API 使用规范》中关于生命周期、逆序关闭、多引擎实例上下文敏感、多线程共享引擎实例的规则。

- **GaiaX JSVM Bridge 模块(类比 `QuickJSBridgeModule`)**
  - 通过 `JSVM_CallbackStruct + JSVM_PropertyDescriptor` 向 JS 上下文注入 `GaiaXJSBridge` 模块：
    - 提供 `bridge.callSync(params)` / `bridge.callAsync(params, success, failure)` / `bridge.callPromise(params)` 等 JS API。
  - 在 C++ 回调中使用 `OH_JSVM_GetCbInfo` 获取参数，解析 `contextId/module/method/args`，通过 NAPI/ArkTS 回调或直接 C++ 调 GaiaX Harmony Native 模块，并将结果封装为 `JSVM_Value` 或调用 JS 回调返回。
  - 处理异常：在出错时利用 `OH_JSVM_ThrowError` 或 `OH_JSVM_GetAndClearLastException` 按指南清理/抛出异常，避免 `JSVM_PENDING_EXCEPTION` 堆积。

- **JSVM Timer / 微任务驱动模块(类比 `QuickJSTimer` + `executePendingJob`)**
  - 在 C++ 或 ArkTS 维度维护定时任务表：`timerId → { env, callbackRef(JSVM_Ref), delay/interval, repeat }`。
  - 暴露与 `bootstrap.js` 中 timer 模块对齐的接口(`setTimeout`/`setInterval`/`clearTimeout`/`clearInterval`)，通过 Bridge 将定时任务注册到 Native。
  - 按时进入 JSVM：`LockWrapper` + `VMScope/EnvScope/HandleScope` 打开后取回 `callbackRef`，执行回调，并定期调用 JSVM 的微任务队列执行接口(参考 JSVM 常见问题中 `RunMicrotasks` 使用方式)。

- **JSVM 值与异常工具模块**
  - 提供 `JSVM_Value` ↔ C++ 基础类型/JSON 的双向转换工具，封装字符串、数字、布尔、对象、数组的创建与解析。
  - 封装错误检查宏(`JSVM_CALL`/`CHECK` 等)，在 JSVM-API 返回非 `JSVM_OK` 时统一记录日志、获取并清理异常(`OH_JSVM_GetAndClearLastException`)。
  - 封装 `JSVM_Ref` 的创建/引用计数/删除，遵守《多线程共享引擎实例》中关于 ref 的线程安全要求。

#### 3.2.2 ArkTS 宿主层模块

- **`GaiaXJSRuntimeManager`(类比 `GXJSEngine/GXHostEngine/GXHostRuntime/GXHostContext`)**
  - 暴露给 Harmony GaiaX 宿主 App 的统一入口：
    - `init(context)`：初始化 NAPI 模块与 JSVM 引擎；
    - `startEngine()` / `stopEngine()`：创建/销毁 VM/Env，执行 bootstrap；
    - 页面/组件 JS 注册接口：`registerPage(templateId, script)`、`registerComponent(templateId, script)`；
    - 生命周期/事件派发接口：`onPageLoad/onPageShow/onPageHide/onPageDestroy`、`onComponentReady/onEvent` 等。
  - 内部通过 NAPI 调度 C++ JSVM 引擎适配层接口，实现“ArkTS 不直接依赖 JSVM，改依赖抽象引擎接口”。

- **ArkTS ↔ JSVM NAPI 封装模块(共享库 `libgaiaxjsvm.so`)**
  - 按 JSVM 官方示例的 NAPI 模式实现，导出：
    - `initGaiaXJSVMEngine()`、`evaluateScript(script: string, fileName: string)`；
    - `initBootstrap()` / `startBootstrap()`；
    - `dispatchEventToJSVM(eventJson: string)`；
    - `callGaiaXModule(paramsJson: string): string | Promise<string>` 等。
  - 在 `oh-package.json5` 中配置 `types` 与 so 文件，提供 ArkTS 侧 `.d.ts` 声明。

- **ArkTS 事件与生命周期适配层**
  - 在 Harmony GaiaX ArkTS 组件/Node 中拦截点击、长按、滚动、曝光等事件，将其封装为带 `templateId/instanceId/targetId/type/userData` 的 JSON，调用 `dispatchEventToJSVM`。
  - 在 GaiaX 模板生命周期时机(首帧完成、显示/隐藏、销毁等)调用 `GaiaXJSRuntimeManager` 的 lifecycle 接口，通过 C++ → JSVM 在 JS 中调用对应 Page/Component 实例的生命周期方法。

#### 3.2.3 JS(Runtime) 层模块

- **Bootstrap & 模块脚本管理(类比 `GXScriptBuilder + bootstrap.js`)**
  - 复用 Android 侧的 `bootstrap.js` 和各模块 JS 源码(尽量保持一致)，在 JSVM 中构造相同的运行时环境：
    - 全局对象：`Component/Page/Event/EventTarget/IMs/window/timeline/sticky/Utils/Bridge/gaiax` 等；
    - 模块系统：`__gaiaxjs_modules__` + `__gaiaxjs_require__`；
    - 实例管理、事件系统、动画与埋点框架。
  - 在 Native 端实现脚本拼接流程：
    - 读取 `bootstrap.js` + 模块脚本；
    - 拼接 import/globalContext/extend/style 等辅助代码；
    - 使用 `OH_JSVM_CompileScript` + `OH_JSVM_RunScript` 执行；
    - 如有需要，结合 Context Snapshot/CodeCache 优化启动速度。

- **GaiaX JS 模块集合**
  - 复用 Android 端 GaiaX JS SDK 的模块实现(尽量修改较少)，例如：
    - `gaiax.showAlert`/`showToast`/`getStorage`/`setStorage`/`request`/`track`/`animation` 等。
  - 这些模块内部统一通过 `Bridge.callAsync/callPromise` 调用 `GaiaXJSBridge`，对上层业务屏蔽平台差异。

#### 3.2.4 Harmony Native 能力模块

- **与 Android 对齐的 GaiaX 原生模块实现**
  - 在 ArkTS 或 C++ 中实现与 Android 端同名/同语义的模块：
    - Tips 模块：提示弹窗、Toast；
    - Storage 模块：本地存储读写；
    - Network/MTOP 模块(如需要)：网络请求；
    - Track/Log 模块：埋点与日志上报；
    - Animation/Timer 等可复用或增强 Harmony 原生能力的模块。
  - 每个模块通过 GaiaX JSVM Bridge 接收 `module/method/args/contextId`，执行 HarmonyOS 对应能力后将结果回调至 JSVM(同步返回或异步回调/Promise)。

- **开发者工具与调试支持(后续增强)**
  - 在完成基础 Runtime 后，可逐步补充：
    - JS 调试(Harmony JS 调试工具 + JSVM 调试接口)；
    - 性能分析(结合 JSVM CPU Profiler/Heap Snapshot、GaiaX 性能埋点)；
    - Studio 连接/实时预览链路的 JSVM 版本。

整体上，**GaiaX JS Harmony 版本的实现目标是：在 JSVM 上重建现有 `gaiax-js-runtime` 的“QuickJS + bootstrap + Bridge + 模块”体系，并通过 ArkTS/NAPI 把它与 Harmony GaiaX 核心 SDK 拼合成一条完整的 JS 运行链路**，这样可以在 HarmonyOS 上获得与 Android 基本一致的 JS 能力与扩展生态。

### 3.3 Android 与 Harmony JS Runtime 差异点概览

#### 3.3.1 能力对比表

| 能力项                     | GaiaX Android JS Runtime                                       | GaiaX Harmony JS(当前)                 | 差异说明                                                     |
|----------------------------|----------------------------------------------------------------|----------------------------------------|--------------------------------------------------------------|
| JS 引擎                    | ✅ QuickJS 集成完整，运行稳定                                   | ⚠️ JSVM 规划中，尚未形成 GaiaX 专用封装 | 引擎类型不同，Harmony 侧缺少面向 GaiaX 的统一 JS 引擎抽象     |
| Host 封装层                | ✅ `GXJSEngine/GXHostEngine/GXHostRuntime/GXHostContext` 完整   | ❌ 未有对等 Host 层                     | Harmony 仅有模板/布局/表达式 Host 封装，缺少 JS Host 管理     |
| Runtime 链路(`bootstrap.js`)| ✅ 已落地，调用链与模板生命周期、事件系统紧密绑定             | ❌ 未接入                               | Harmony 侧尚未执行 `bootstrap.js`，没有 Page/Component/IMs 等 |
| Bridge 模块                | ✅ `QuickJSBridgeModule` + `Bridge.callSync/Async/Promise` 完整 | ❌ 无对应模块                           | JS↔Native 模块调用、回调管理、Promise 支持均未建立           |
| Timer & 微任务驱动         | ✅ `timer` 模块 + `QuickJSTimer` + `executePendingJob` 完整     | ❌ 未实现                               | Harmony 侧目前只依赖 ArkTS 自身 timer，与 JSVM 未打通        |
| JS API 能力(`gaiax.*`)     | ✅ Tips/Storage/Network/Track/Animation 等丰富模块             | ❌ 未对齐                               | 业务常用 JS API 尚未在 Harmony JS 运行时暴露                 |
| Page/Component 抽象与 IMs  | ✅ 完整 Page/Component 抽象 + `IMs(InstancesManager)`           | ❌ 未实现                               | Harmony 模板仅有 ArkTS 宿主逻辑，缺少 JS 层实例管理          |
| 原生事件 ↔ JS 事件链路     | ✅ `window.postMessage` + `IMs.dispatchEvent` + `EventTarget`   | ❌ 尚未打通                             | Harmony 仅有 ArkTS 事件 → 模板，未接入 JS 事件体系           |
| 调试与可视化               | ✅ 结合 Studio，有 JS 侧日志与埋点链路                         | ❌ 未接入                               | Harmony 端缺少基于 JSVM 的调试/日志/埋点辅助                 |
| 测试与验证                 | ✅ JS Runtime 行为由 Demo + 业务实践充分验证                   | ❌ 需新建                               | Harmony 需补齐 JSVM 版 Runtime 的单测/集成测试               |

#### 3.3.2 缺失/待补齐能力清单

结合上表，可以将当前 **Harmony JS Runtime 相比 Android 的主要缺口**归纳为：

1. **引擎与 Host 抽象层缺失**
   - 缺少面向 GaiaX 的 JSVM 引擎适配层和 `GaiaXJSRuntimeManager`，无法像 Android 一样以统一方式管理 VM/Env/Context、脚本执行与上下文多实例。

2. **`bootstrap.js` 运行时环境尚未接入**
   - Harmony 端尚未执行 `bootstrap.js`，没有全局的 `Page/Component/Event/EventTarget/IMs/window/timeline/sticky/Bridge/gaiax` 等 JS 抽象。
   - 模板 JS 逻辑无法复用 Android 侧的 Page/Component 语义与生命周期模型。

3. **JS ↔ Native Bridge 能力空缺**
   - 未实现 JSVM 版 `GaiaXJSBridge` 模块，导致 `Bridge.callSync/Async/Promise`、`gaiax.xxx()` 等 JS API 在 Harmony 上无落点。
   - 缺少统一的模块调用协议(`module/method/args/contextId`)，难以在 JS 层以一致方式访问 Harmony 原生能力。

4. **Timer/微任务与事件驱动链路缺失**
   - 未建立 JSVM 内部的 `setTimeout/setInterval` 驱动与微任务队列执行逻辑，Promise 链与异步任务调度需要从零设计。
   - 原生事件只停留在 ArkTS UI 维度，尚未通过 `window.postMessage` 等通道路由到 JSVM 中的 Page/Component 实例。

5. **GaiaX JS API 模块与业务能力未暴露**
   - Android 侧现有的 `gaiax.showAlert/getStorage/request/track/animation` 等模块未在 Harmony JS 运行时复用。
   - Harmony 端需要结合自身 ArkUI/存储/网络/埋点能力，实现在 JSVM 中对等的 Tips/Storage/Network/Track/Animation 模块。

6. **调试、监控与测试体系待建立**
   - 缺少基于 JSVM 的 JS 调试与性能分析能力(可结合 JSVM 的 Debugger/CPU Profiler/Heap Snapshot 等组件)。
   - 未有针对 `gaiax-js-harmony` 的系统化测试用例，需要参考 Android 端 Demo/业务场景补齐集成测试，确保 JS 行为与 Android 对齐。

从实现路径上看，**只要按 3.2 小节分层补齐上述模块，Harmony 侧就可以在 JSVM 上重建一套与 Android 近似的 GaiaX JS Runtime**，使模板 JS 逻辑和生态在多端之间尽量保持一致。




