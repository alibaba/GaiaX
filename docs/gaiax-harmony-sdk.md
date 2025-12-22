

## GaiaX Harmony 对齐方案领导汇报稿（摘要版）

### 一、整体结论

- **前提依赖**：
  - 本方案实施建议配合使用具备代码理解与重构能力的**付费 AI Coding 工具**（用于代码检索、差异对齐与重构辅助）。
- **核心能力已基本对齐**：  
  - 布局能力由 **Stretch(Rust)** 提供，Android 与 Harmony 复用同一套 `.so`，FlexBox 算法与布局结果已对齐。  
  - 表达式引擎 **GXAnalyze(C++)** 同样以 `.so` 形式复用，Android 与 Harmony 使用相同核心实现。
- **差异主要集中在 SDK 外围能力与 JS Runtime 链路**：  
  - GaiaX SDK：模板管理、组件/样式细节、事件系统、适配器与测试体系等需要补齐。  
  - GaiaX JS Runtime：JS 引擎封装、`bootstrap.js` 运行时、Bridge、Timer 与原生模块等尚未在 Harmony 上落地。
- **整体工作量评估** （需完整的AllIn人力投入）：  
  - **GaiaX SDK Harmony 对齐：约 1 周**（5 个工作日左右，净投入）。  
  - **GaiaX JS Harmony Runtime：约 1.5 周**（7–8 个工作日左右，净投入）。  
  - 风险可控，更多是工程性补齐与测试完善。

---

### 二、GaiaX SDK：Android vs Harmony 主要差异与工作量

**模板引擎：**
| 功能项         | GaiaX Android          | GaiaX Harmony       | 差异说明                                                       |
| -------------- | ---------------------- | ------------------- | -------------------------------------------------------------- |
| 模板加载与解析 | ✅ 完整支持             | ✅ 基础支持          | 都支持从 assets 加载模板,Harmony 支持从 rawfile 加载           |
| 模板缓存机制   | ✅ 多级缓存             | ⚠️ LRU 缓存          | Android 采用 ConcurrentHashMap,Harmony 采用 GXTemplateLRUCache |
| 模板预编译     | ✅ 支持                 | ⚠️ 部分支持          | Android 与 Harmony 均支持二进制模板加                          |
| 模板热更新     | ✅ 支持                 | ❌ 不支持            | 需要支持运行时模板动态更新                                     |
| 多版本管理     | ✅ 支持                 | ❌ 不支持            | 模板版本控制能力缺失                                           |
| 模板信息源     | ✅ GXTemplateInfoSource | ✅ GXTemplateManager | 两者类似,但 Android 功能更完善                                 |
| 模板锁机制     | ✅ 支持                 | ⚠️ 部分支持          | 防止多线程同时加载同一模板                                     |

**注册中心：**
| 功能项         | GaiaX Android                     | GaiaX Harmony                     | 差异说明                                     |
| -------------- | --------------------------------- | --------------------------------- | -------------------------------------------- |
| 组件注册       | ✅ GXRegisterCenter                | ✅ GXRegisterCenter                | 基本功能已实现                               |
| 扩展点管理     | ✅ 多种扩展接口                    | ⚠️ 基础扩展接口                    | Android 支持表达式、数据绑定、颜色等多种扩展 |
| 自定义视图注册 | ✅ registerExtensionViewSupport    | ❌ 不支持                          | 缺少自定义组件注册能力                       |
| 适配器注册     | ✅ registerExtensionXXX            | ❌ 不支持                          | 无适配器扩展机制                             |
| 模板源注册     | ✅ registerExtensionTemplateSource | ✅ registerExtensionTemplateSource | 都支持,但 Android 支持优先级配置             |
| 全局配置管理   | ✅ 完整支持                        | ⚠️ 部分支持                        | 配置能力有限                                 |

**基础组件：**
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

容器组件：
| 组件类型          | GaiaX Android | GaiaX Harmony | 差异说明                |
| ----------------- | ------------- | ------------- | ----------------------- |
| Scroll (滚动容器) | ✅ 完整支持    | ✅ 基础支持    | Harmony 功能较简单      |
| Grid (网格容器)   | ✅ 完整支持    | ✅ 基础支持    | Harmony 功能较简单      |
| Slider (轮播容器) | ✅ 完整支持    | ✅ 基础支持    | Harmony 功能较简单      |
| Footer 支持       | ✅ 完整支持    | ❌ 不支持      | Grid/Scroll 缺少 Footer |
| Header 支持       | ✅ 完整支持    | ❌ 不支持      | Grid/Scroll 缺少 Header |

**样式属性：**
| 功能项       | GaiaX Android | GaiaX Harmony | 差异说明                                                                                                                              |
| ------------ | ------------- | ------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| FlexBox 布局 | ✅ 完整支持    | ✅ 完整支持    |                                                                                                                                       |
| 基础样式属性 | ✅ 完整支持    | ✅ 基础支持    | 已支持宽高、margin/padding、颜色、字体、透明度、行高、行数等常用样式，但 hidden、部分 display 取值、mode/mode-type 等高级样式尚未对齐 |
| 渐变背景     | ✅ 完整支持    | ✅ 完整支持    |                                                                                                                                       |
| 阴影效果     | ✅ 完整支持    | ✅ 完整支持    |                                                                                                                                       |
| 圆角处理     | ✅ 完整支持    | ✅ 完整支持    |                                                                                                                                       |
| 边框样式     | ✅ 完整支持    | ✅ 基础支持    | 支持统一 border-width/border-color/border-radius，暂不支持 border-style 以及单边边框等复杂样式                                        |

#### 2.1 模板引擎 & 注册中心

- **已对齐/基础情况**  
  - 模板加载与解析：Harmony 已支持从 `assets`/`rawfile` 加载模板。  
  - 模板缓存：已有 `GXTemplateLRUCache` 基础缓存。  
  - 表达式引擎 & 布局引擎：均复用 C++/Rust 核心 `.so`，核心计算逻辑一致。  
  - 注册中心 `GXRegisterCenter`：已具备模板源注册等基础能力。

- **主要差异/缺失项**  
  - **模板引擎**  
    - 缺少：模板热更新、多版本模板管理。  
    - 缓存策略：Android 有“模板信息 + 布局结果 + 视图节点”多级缓存，Harmony 仅有基础 LRU 模板缓存。  
    - 模板锁机制：Android 已有完整并发加载锁，Harmony 仅为部分支持。
  - **注册中心与扩展能力**  
    - Android 提供 `GXIExtensionExpression/DataBinding/Color/Size/DynamicProperty` 等多类扩展接口；Harmony 目前只有基础模板源注册。  
    - 缺少：自定义视图注册 (`registerExtensionViewSupport`)、Lottie 等适配器注册、统一扩展点管理与优先级配置。  

- **工作量粗评（纳入 SDK 1 周内）**  
  - 对齐模板多级缓存与模板锁机制。  
  - 扩展 `GXRegisterCenter`，补齐常用扩展接口与自定义视图/适配器注册能力。  
  - 完成必要的回归测试，验证并发加载、缓存命中与扩展注入链路。  

#### 2.2 组件系统与容器能力

- **已对齐/基础情况**  
  - 基础组件：`View/Text/Image/RichText` 在 Harmony 上已对齐主流程。  
  - 容器组件：`Scroll/Grid/Slider` 已具备基础滚动与网格能力。

- **主要差异/缺失项**  
  - **基础组件**  
    - 缺少：`Lottie` 动画组件、`Progress` 进度条、`IconFont` 图标字体、自定义组件扩展能力。  
    - Harmony 可复用官方 Lottie 能力（如 `@ohos/lottie-turbo`），需打通到 GaiaX 组件层。  
  - **容器组件（Scroll/Grid/Slider）**  
    - Android 支持丰富的 Header/Footer 配置，包含分页加载 `item-footer-type` + `hasMore`。  
    - Harmony 当前缺少 Header/Footer 以及基于 Footer 的“加载更多”能力。  

- **工作量粗评（纳入 SDK 1 周内）**  
  - 实现 Lottie/Progress/IconFont 对应的组件封装及模板绑定。  
  - 在 Grid/Scroll/Slider 上补齐 Header/Footer 能力及分页加载流程。  
  - 通过少量 Demo/测试模板验证复杂场景（长列表、分页、动画混合场景）。

#### 2.3 样式系统

- **已对齐/基础情况**  
  - FlexBox 布局：依赖 Stretch 核心，Android/Harmony 布局结果一致。  
  - 已支持的属性：宽高、`margin/padding`、颜色、字体、透明度、行高、行数、渐变、阴影、圆角等。

- **主要差异/缺失项**  
  - `hidden` 属性未在 Harmony 样式解析中生效，当前主要依赖 `display/overflow`。  
  - `display` 取值在 Harmony 侧更简化，对 `absolute` 等场景处理差异较大。  
  - Border：Harmony 只支持统一 `border-width/color/radius`，暂不支持 `border-style`、单边边框等复杂样式。  
  - 图片裁剪模式：Android 支持 `mode + mode-type` 多种模式，Harmony 仅简单映射 `cover/contain/其它 → fill`。  
  - `backdrop-filter` 在 Harmony 侧尚未完全消费，可对齐到 ArkUI `backgroundBlurStyle`。  

- **工作量粗评（纳入 SDK 1 周内）**  
  - 扩展 ArkTS 样式解析逻辑，使 `hidden/display/mode/border-style` 等与 Android 行为趋同。  
  - 在必要的 Node 上接入 `backgroundBlurStyle` 等 ArkUI 属性，补齐滤镜效果。  
  - 重点场景（复杂边框、图片裁剪、隐藏/显示）通过模板 + 自动化测试校验结果。

#### 2.4 事件系统、适配器与测试体系

- **事件系统差异**  
  - 已支持：点击事件。  
  - 缺少：长按、自定义事件、事件冒泡与拦截、曝光埋点等。  
  - Android 已通过 `GXEventType` + 事件冒泡链实现复杂事件传播与覆盖逻辑。

- **适配器系统差异**  
  - Android 拥有统一适配器框架（图片、动画、自定义适配器等）。  
  - Harmony 目前只有基于 `GXInjector` 的图片注入能力，整体适配器架构尚未建立。

- **测试体系差异**  
  - Android：组件测试 11 类、核心功能测试 8 类、样式测试 6 类，总计 25+ 测试类、378+ 用例。  
  - Harmony：仅有零散基础用例，尚未形成系统化测试。  

- **工作量粗评（纳入 SDK 1 周内）**  
  - 事件：补齐长按、滚动、自定义事件、冒泡/拦截与曝光回调的基本链路。  
  - 适配器：设计简化版适配器接口，优先支持图片 & Lottie 等高优先级能力。  
  - 测试：参考 Android 测试集，先选取 **核心组件 + 核心功能** 建立首批 Harmony 自动化测试（不追求一次性全量覆盖）。  

> **总结（SDK 部分）**：  
> 核心计算引擎已共用，Harmony 端主要是 ArkTS 层的“能力补齐 + 架构对齐 + 基础测试建设”。整体工作量评估 **约 1 周**，风险较小，适合快速冲刺一轮对齐版本。

---

### 三、GaiaX JS Runtime：Android vs Harmony 主要差异与工作量

#### 3.1 当前状态与目标

- **Android 当前形态**  
  - JS 引擎：基于 **QuickJS**，有较成熟封装。  
  - Host 层：`GXJSEngine/GXHostEngine/GXHostRuntime/GXHostContext` 完整，统一管理 JS 引擎、运行时与上下文。  
  - 运行时：`bootstrap.js` + 模块体系，提供 `Page/Component/IMs/Event/EventTarget/window/timeline/sticky/Bridge/gaiax` 等完整 JS 世界。  
  - Bridge：`QuickJSBridgeModule` + `Bridge.callSync/Async/Promise`，支持 JS ↔ Native 双向调用与回调管理。  
  - Timer/微任务：`timer` 模块 + `QuickJSTimer` + `executePendingJob` 完整闭环。  

- **Harmony 当前状态**  
  - 仅规划基于 **JSVM-API** 的实现方案，尚未形成面向 GaiaX 的统一 JS 引擎封装层。  
  - `bootstrap.js` 未接入，Page/Component 抽象、实例管理与事件系统尚未建立。  
  - 缺少 JSVM 版 `GaiaXJSBridge`、Timer/微任务驱动以及与 GaiaX 模板/事件体系的联动。  

- **目标**  
  - 在 **JSVM** 上重建一套与 Android 逻辑等价的 GaiaX JS Runtime，使 GaiaX 模板 JS 逻辑在 Android/Harmony 基本保持一致。

#### 3.2 需要补齐的核心模块

- **1）JSVM 引擎适配层与 Host 抽象**  
  - 封装 JSVM 的 VM/Env 生命周期（创建/销毁、Scope 管理）。  
  - 提供统一的 `evaluate(script, filename, mode)` 能力，支持后续 CodeCache/Snapshot 优化。  
  - 在 ArkTS 侧通过 `GaiaXJSRuntimeManager` 暴露 `init/startEngine/stopEngine/registerPage/registerComponent` 等接口，统一管理多模板/多实例。

- **2）JS ↔ Native Bridge 模块 & Timer/微任务**  
  - 基于 `JSVM_CallbackStruct` 注入 `GaiaXJSBridge`，支持：  
    - `bridge.callSync`、`bridge.callAsync`、`bridge.callPromise` 等。  
  - 在 Native 侧解析 `contextId/module/method/args`，路由到 Harmony GaiaX 原生模块。  
  - 实现 JSVM 侧 `setTimeout/setInterval/clearTimeout/clearInterval` + 微任务队列执行，保证 Promise/异步链路正常工作。

- **3）ArkTS 宿主与事件适配层**  
  - 在 GaiaX ArkTS 组件/Node 内拦截点击、长按、滚动、曝光等事件，封装为 JSON，经 NAPI 发送到 JSVM。  
  - 在模板生命周期（onLoad/onReady/onShow/onHide/onDestroy）时机调用 JS 侧 Page/Component 生命周期方法。

- **4）Bootstrap & GaiaX JS 模块复用**  
  - 尽量 **直接复用 Android 侧 `bootstrap.js` 和模块 JS 源码**，在 JSVM 中构建相同的运行时世界。  
  - 调整最小量差异（如引擎类型标识、环境变量），降低维护成本。  

- **5）Harmony 原生能力模块与调试/测试**  
  - 在 ArkTS 或 C++ 侧实现与 Android 同名的原生模块（Tips/Storage/Track/Animation 等），通过 Bridge 暴露给 JS。  
  - 构建基础的日志/错误上报链路，预留与 Studio/调试工具的对接点。  
  - 创建针对 JSVM Runtime 的 Demo 与集成测试，验证页面生命周期、事件、网络请求、埋点等完整链路。

#### 3.3 工作量评估与风险

- **工作量评估：约 1.5 周（7–8 个工作日）**  
  - JSVM 引擎适配层 + Bridge + Timer 基础：约 4 个工作日。  
  - ArkTS 宿主/事件适配 + Harmony 原生模块对齐（Tips/Storage/Track 等核心能力）：约 3 个工作日。  
  - `bootstrap.js` 及模块脚本适配 + 集成测试与问题修复：约 1 个工作日。  

- **风险与控制点**  
  - 主要风险在于 **JSVM-API 的使用细节**（Scope/Ref/异常处理）及 **多线程/性能**，但已有官方规范可遵循。  
  - **存在一个未知风险：HarmonyOS 平台在复杂业务场景下的整体性能表现和资源占用情况仍需通过系统性性能验证和压测来确认。**
  - 通过优先打通“单 Page + 少量模块”的最小链路，可快速收敛问题，后续再逐步扩展更多模块与调试能力。  

> **总结（JS 部分）**：  
> GaiaX JS Harmony 侧工作属于“在 JSVM 上重建一套已经在 Android 成熟验证的运行时”，设计方案清晰，可复用大量现有 JS 代码。整体工作量评估 **约 1.5 周**，风险主要集中在 JSVM 适配与桥接实现，整体可控。

---

### 四、整体建议

- **阶段性目标**  
  - 第一阶段（约 1 周）：完成 GaiaX SDK Harmony 能力对齐（模板、组件/样式、事件/适配器 + 基础测试）。  
  - 第二阶段（约 1.5 周）：实现 GaiaX JS Harmony Runtime 主干链路（JSVM 引擎 + `bootstrap.js` + Bridge + 核心模块 + 集成测试）。
- **收益**  
  - Harmony 端在渲染能力与 JS 扩展能力上与 Android 对齐，模板与 JS 逻辑跨端复用度显著提升。  
  - 为后续接入 Studio、完善调试与监控体系奠定基础，有利于整体动态化能力的多端统一。