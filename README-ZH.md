<h1 align="center">
    <img src="https://img.alicdn.com/imgextra/i2/O1CN01DvZYVD1hLaOVqNlkK_!!6000000004261-2-tps-1024-1024.png" width="250" alt="GaiaX-logo">
</h1>
<p align="center">
    动态模板引擎是阿里巴巴优酷技术团队研发的一套轻量级的纯原生动态化卡片跨端解决方案
</p>

<p align="center">

[![README-en](https://shields.io/badge/README-ENGLISH-blue)](README.md)
[![README-zh](https://shields.io/badge/README-%E4%B8%AD%E6%96%87-blue)](README-ZH.md)
[![Docs-zh](https://shields.io/badge/Docs-%E4%B8%AD%E6%96%87-blue?logo=Read%20The%20Docs)](www.yuque.com/biezhihua/gaiax)
[![GitHub release](https://img.shields.io/github/release/alibaba/GaiaX)](https://github.com/alibaba/GaiaX/releases)
[![License](https://img.shields.io/github/license/alibaba/GaiaX?color=4D7A97&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![GitHub Stars](https://img.shields.io/github/stars/alibaba/GaiaX)](https://github.com/alibaba/GaiaX/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/alibaba/GaiaX)](https://github.com/alibaba/GaiaX/fork)
[![user repos](https://badgen.net/github/dependents-repo/alibaba/GaiaX?label=user%20repos)](https://github.com/alibaba/GaiaX/network/dependents)
[![GitHub Contributors](https://img.shields.io/github/contributors/alibaba/GaiaX)](https://github.com/alibaba/GaiaX/graphs/contributors)

</p>

# 动态模板引擎

动态模板引擎是阿里巴巴优酷技术团队研发的一套轻量级的纯原生动态化卡片跨端解决方案。

除了客户端SDK外，还提供了模板可视化搭建工具Studio，以及Demo工程（包含模板示例，以及扫码预览），支持从模板创建、编辑、真机调试、预览等研发全链路技术。

动态模板引擎的目标是在保证原生体验与性能的同时，帮助客户端开发实现低代码。

## 目标

以下这些目标是我们项目前进的方向：

- 高性能
- 跨端技术
- 可视化搭建
- 纯Native渲染

## 支持平台

- Android
- iOS

## 核心概念

<p align="center">
    <img src="https://gw.alicdn.com/imgextra/i3/O1CN01Y4sMkn1Nmnc7thyzR_!!6000000001613-2-tps-3423-886.png" width="1000" alt="GaiaX-arch">
</p>

## 使用到的技术

Rust/Android/Kotlin/iOS/OC/C++/JNI/CSS/FlexBox

## 使用方法

### Android

#### 依赖 

增加jitpack源:
```
// 方式1:在setting.gradle中增加
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        maven { url 'https://jitpack.io' }
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { url 'https://jitpack.io' }
        mavenCentral()
    }
}

// 方式2:在根build.gradle中增加
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

```


Android-Support version:
```
implementation 'com.github.alibaba.GaiaX:GaiaX-Adapter:0.2.3-support'
implementation 'com.github.alibaba.GaiaX:GaiaX:0.2.3-support'
implementation 'com.alibaba:fastjson:1.2.76'
```

AndroidX version:
```
implementation 'com.github.alibaba.GaiaX:GaiaX-Adapter:0.2.3'
implementation 'com.github.alibaba.GaiaX:GaiaX:0.2.3'
implementation 'com.alibaba:fastjson:1.2.76'
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

//注册模板服务
[TheGXRegisterCenter registerTemplateServiceWithBizId:bizId templateBundle:@"xxx.bundle"];

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


## 联系我们

钉钉：

<img src="https://gw.alicdn.com/imgextra/i3/O1CN01mmrIDM1ofvIASkhPI_!!6000000005253-2-tps-474-469.png" width="200" height="200" />

微信：

<img src="https://gw.alicdn.com/imgextra/i1/O1CN01LlZEeh1dLLjGAVpFn_!!6000000003719-2-tps-746-746.png" width="200" height="200" />

Email:

biezhihua@gmail.com

## 支持者

[![Forkers repo roster for @alibaba/GaiaX](https://reporoster.com/forks/alibaba/GaiaX)](https://github.com/alibaba/GaiaX/network/members)

[![Stargazers repo roster for @alibaba/GaiaX](https://reporoster.com/stars/alibaba/GaiaX)](https://github.com/alibaba/GaiaX/stargazers)

# 许可证
```
Ali-GaiaX-Project is a template dynamic develop solutions developed by Alibaba and licensed under the Apache License (Version 2.0)
This product contains various third-party components under other open source licenses. 
See the NOTICE file for more information.
```
