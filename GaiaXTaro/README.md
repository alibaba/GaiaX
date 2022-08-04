## build project
```
yarn build
```

## bootstrap project
```
yarn bootstrap
```

## run demo
```
cd packages/gaiax-taro-demo
yarn dev:h5
```

## 如何监听GaiaStudio

获取本机ip地址，赋值给GXFastPreview.studioIpAddress变量

## Studio

https://dl-oss-wanju.youku.com/gaia-opensource/gaia-studio/mac/Gaia%20Studio-0.1.8.dmg

## 进度

- 8月4日：
    - 总体进度：30%左右
    - 分支：https://github.com/alibaba/GaiaX/tree/features/taro
    - 已完成内容：
        - 工程架构改造，使用lerna改造项目工程，拆分GaiaXTaroSDK和GaiaXTaroDemo。
        - 代码逻辑改造，仿照Android版本构建模板的解析和构建流程。
        - 完成节点树与视图树的创建流程；
        - 完成基础样式的赋值；
        - 完成GXNode的改造；
        - 完成GXTemplateNode改造；
        - 完成GXTemplateEngine改造；
        - 完成GXTemplateContext改造；
        - 完成view、image、text的赋值改造；
    - 待推进内容：
        - 组件开发：RichText组件开发、IconFont组件开发、Scroll组件开发、Gird组件开发
        - 样式优化与Native一致性校对。
        - 事件处理。
        - 动画处理。
        - 单测用例。