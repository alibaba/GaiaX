# Dynamic template engine

Dynamic template engine is a lightweight cross-end solution of pure native dynamic card developed by Alibaba Youku technology team. In addition to render the client SDK, and at the same time provides the necessary Template visual build Studio and Demo (template sample, and sweep code preview), support from the template set/edit, real machine debugging/preview all link technical support such as r&d, dynamic template engine aims to ensure that the native experience and performance at the same time, help the client achieve low code development field.

## Goals

The following goals are the way forward for our project:

- High performance
- Cross-end technology
- Visual construction
- Pure Native rendering

## Supported Platforms

- Android
- iOS

## Usage

### Android

#### Dependency  [![](https://jitpack.io/v/alibaba/GaiaX.svg)](https://jitpack.io/#alibaba/GaiaX)
```
// Dependency
dependencies {
    implementation 'com.alibaba.gaiax:GaiaX:0.0.1'
}
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

We very much welcome your project contribute code. In you before writing any code, start by creating a issue or pull request in order for us to be able to discuss details of the proposal and the rationality of the scheme. You can in the following areas contribute code:

- Packet size
- The run-time performance
- Across-side consistency
- Unit test cases
- Document or use cases
- And so on


## 工具

- [Template visual build Studio](https://dl-oss-wanju.youku.com/gaia-opensource/gaia-studio/mac/Gaia%20Studio-0.1.8.dmg)

# LICENSE
```
Ali-GaiaX-Project is a template dynamic develop solutions developed by Alibaba and licensed under the Apache License (Version 2.0)
This product contains various third-party components under other open source licenses. 
See the NOTICE file for more information.
```
