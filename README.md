<h1 align="center">
    <img src="https://img.alicdn.com/imgextra/i2/O1CN01DvZYVD1hLaOVqNlkK_!!6000000004261-2-tps-1024-1024.png" width="250" alt="GaiaX-logo">
</h1>
<p align="center">
    GaiaX dynamic template engine is a lightweight cross-platform solution for pure native dynamic card, developed by Alibaba YouKu technology team
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

# GaiaX dynamic template engine

GaiaX dynamic template engine is a lightweight cross-platform solution for pure native dynamic card, developed by Alibaba YouKu technology team. 

Besides client SDK, we provide the template visual build tool - GaiaStudio, and Demo Project - template sample and real-time preview, which supports creating templates, editing templates, real machine debugging, and real-time preview.

GaiaX dynamic template engine aims to ensure that the native experience and performance at the same time, help the client achieve low code.

## Goals

The following goals are the way forward for our project:

- High performance
- Cross-platform technology
- Visual construction
- Pure native rendering

## Supported Platforms

- Android
- iOS

## Core Concept

<p align="center">
    <img src="https://gw.alicdn.com/imgextra/i3/O1CN01Y4sMkn1Nmnc7thyzR_!!6000000001613-2-tps-3423-886.png" width="1000" alt="GaiaX-arch">
</p>


## The technology used

Rust/Android/Kotlin/iOS/OC/C++/JNI/CSS/FlexBox

## Usage

### Android

#### Dependency

add jitpack source:
```
// with setting.gradle
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

// with build.gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

```


Android-Support version:
```
implementation 'com.github.alibaba.GaiaX:GaiaX-Adapter:$version-support'
implementation 'com.github.alibaba.GaiaX:GaiaX:$version-support'
implementation 'com.alibaba:fastjson:1.2.76'
```

AndroidX version:
```
implementation 'com.github.alibaba.GaiaX:GaiaX-Adapter:$version'
implementation 'com.github.alibaba.GaiaX:GaiaX:$version'
implementation 'com.alibaba:fastjson:1.2.76'
```
#### Template File
```
// Path used to store template resources
/assets/${templateBiz}/${templateId}
```

#### Methods
```
// SDK usages

// Initialization - Initializes the SDK
GXTemplateEngine.instance.init(activity)

// Build template parameters - Template information
// activity       - context
// templateBiz    - template biz id
// templateId     - template id
val item = GXTemplateEngine.GXTemplateItem(activity, "templateBiz", "templateId")

// Build template parameters - Viewport size (template draw size, similar to the concept of canvas in Android)
val size = GXTemplateEngine.GXMeasureSize(100F.dpToPx(), null)

// Build template parameters - Template data
val dataJson = AssetsUtils.parseAssets(activity, "template-data.json")
val data = GXTemplateEngine.GXTemplateData(dataJson)

// Create template View - Creates a native View based on template parameters
val view = GXTemplateEngine.instance.createView(item, size)

// Bind the view data
GXTemplateEngine.instance.bindData(view, data)

// Insert the template into the container for rendering
findViewById<ViewGroup>(R.id.template_container).addView(view, 0)
```

### iOS

#### CocoaPods
Add a dependency to your Podfile
```
// Dependency
pod 'GaiaXiOS'
```

#### Template File
Add template files to App or FrameWork
```
// Path used to store template resources
xxx.bundle/templateId
```

#### Methods
```
// SDK Usages

// Introduced header files
#import <GaiaXiOS/GaiaXiOS.h>

//register template service
[TheGXRegisterCenter registerTemplateServiceWithBizId:bizId templateBundle:@"xxx.bundle"];

// Build template parameters - Template information
// activity       - context
// templateBiz    - template biz id
// templateId     - template id
GXTemplateItem *item = [[GXTemplateItem alloc] init];
item.templateId = templateId;
item.bizId = templateBiz;

// Build template parameters - Viewport size (template draw size, similar to the concept of canvas in Android)
CGSize size = CGSizeMake(1080, NAN);

// Build template parameters - Template data
GXTemplateData *data = [[GXTemplateData alloc] init];
data.data = @{@"xxx": @"xxx"};

// Create template View - Creates a native View based on template parameters
UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];

// Bind the view data
[TheGXTemplateEngine bindData:data onView:view];

// Insert the template into the container for rendering
[self.view addSubview:view];
```

## Contributing

We very welcome your to contribute code for the project. In you before writing any code, start by creating a issue or pull request in order for us to be able to discuss details of the proposal and the rationality of the scheme. You can in the following areas contribute code:

- Packet size
- The run-time performance
- Across-side consistency
- Unit test cases
- Document or use cases
- And so on


## Tool

- [Template visual build Studio](https://dl-oss-wanju.youku.com/gaia-opensource/gaia-studio/mac/Gaia%20Studio-0.1.8.dmg)

## Contact us

DingTalk：

<img src="https://gw.alicdn.com/imgextra/i3/O1CN01mmrIDM1ofvIASkhPI_!!6000000005253-2-tps-474-469.png" width="200" height="200" />

WeChat：

<img src="https://gw.alicdn.com/imgextra/i1/O1CN01LlZEeh1dLLjGAVpFn_!!6000000003719-2-tps-746-746.png" width="200" height="200" />

Email:
biezhihua@gmail.com

## Supporters

[![Forkers repo roster for @alibaba/GaiaX](https://reporoster.com/forks/alibaba/GaiaX)](https://github.com/alibaba/GaiaX/network/members)

[![Stargazers repo roster for @alibaba/GaiaX](https://reporoster.com/stars/alibaba/GaiaX)](https://github.com/alibaba/GaiaX/stargazers)

# LICENSE
```
Ali-GaiaX-Project is a template dynamic develop solutions developed by Alibaba and licensed under the Apache License (Version 2.0)
This product contains various third-party components under other open source licenses. 
See the NOTICE file for more information.
```
