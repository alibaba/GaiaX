# 动态模板引擎

动态模板引擎是阿里巴巴优酷技术团队研发的一套轻量级的纯原生动态化卡片跨端解决方案。除了客户端渲染SDK之外，同时提供了配套的模板可视化搭建工具Studio和Demo（模板示例，以及扫码预览），支持从模板搭建/编辑、真机调试/预览等研发全链路技术支撑，动态模板引擎的目标是在保证原生体验与性能的同时，帮助客户端开发领域实现低代码。

## 目标

以下这些目标是我们项目前进的方向：

- 高性能
- 跨端技术
- 可视化搭建
- 纯Native渲染

## 支持平台

- Android
- iOS

## 使用方法

### Android

#### 依赖 [![](https://jitpack.io/v/alibaba/GaiaX.svg)](https://jitpack.io/#alibaba/GaiaX)
```
// 依赖
dependencies {
    implementation 'com.github.alibaba:GaiaX:0.1.0'
}
```

#### 模板文件
```
// 用于存放模板资源的路径
/assets/${templateBiz}/${templateId}
```

#### 调用方式
```
// SDK使用方式

// 初始化          - 初始化SDK
GXTemplateEngine.instance.init(activity)

// 构建模板参数     - 模板信息
// activity       - 上下文
// templateBiz    - 业务ID
// templateId     - 模板ID
val item = GXTemplateEngine.GXTemplateItem(activity, "templateBiz", "templateId")

// 构建模板参数     - 视口大小(模板绘制尺寸，类似于Android中画布的概念)
val size = GXTemplateEngine.GXMeasureSize(100F.dpToPx(), null)

// 构建模板参数     - 模板数据
val dataJson = AssetsUtils.parseAssets(activity, "template-data.json")
val data = GXTemplateEngine.GXTemplateData(dataJson)

// 创建模板视图     - 根据模板参数创建出一个原生View
val view = GXTemplateEngine.instance.createView(item, size)

// 视图绑定数据
GXTemplateEngine.instance.bindData(view, data)

// 将插入模板插入到容器中进行渲染
findViewById<ViewGroup>(R.id.template_container).addView(view, 0)
```

### iOS

#### CocoaPods
在Podfile中添加依赖
```
// 依赖
pod 'GaiaXiOS'
```

#### 模板文件
在App或者FrameWork中添加模板文件
```
// 用于存放模板资源的路径
xxx.bundle/templateId
```

#### 调用方式
```
// SDK使用方式

// 引入头文件
#import <GaiaXiOS/GaiaXiOS.h>

// 构建模板参数     - 模板信息
// templateBiz    - 业务ID
// templateId     - 模板ID
GXTemplateItem *item = [[GXTemplateItem alloc] init];
item.templateId = templateId;
item.bizId = templateBiz;

// 构建模板参数     - 视口大小(模板绘制尺寸)
CGSize size = CGSizeMake(1080, NAN);

// 构建模板参数     - 模板数据
GXTemplateData *data = [[GXTemplateData alloc] init];
data.data = @{@"xxx": @"xxx"};

// 创建模板视图     - 根据模板参数创建出一个原生View
UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];

// 视图绑定数据
[TheGXTemplateEngine bindData:data onView:view];

// 将插入模板插入到容器中进行渲染
[self.view addSubview:view];
```

## 如何贡献代码

我们非常欢迎您为项目贡献代码。在您编写代码之前，请先创建一个issue或者pull request以便我们能够讨论方案的细节与方案的合理性。您可以针对以下领域贡献代码：

- 包大小
- 运行时性能
- 跨端一致性
- 单元测试用例
- 文档或者使用案例
- 等等

## 工具

- [模板可视化搭建工具Studio](https://dl-oss-wanju.youku.com/gaia-opensource/gaia-studio/mac/Gaia%20Studio-0.1.8.dmg)

## 文档

- [SDK文档](www.yuque.com/biezhihua/gaiax)

# 许可证
```
Ali-GaiaX-Project is a template dynamic develop solutions developed by Alibaba and licensed under the Apache License (Version 2.0)
This product contains various third-party components under other open source licenses. 
See the NOTICE file for more information.
```
