//
//  GXTemplateEngine.m
//  GaiaXiOS
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#import "GXTemplateEngine.h"
#import "GXTemplateContext.h"
#import "GXTemplateManager.h"
#import "GXRenderManager.h"
#import "GXLayoutManager.h"
#import "NSDictionary+GX.h"
#import "GXDataManager.h"
#import "GXCacheCenter.h"
#import "GXEXPression.h"
#import "UIView+GX.h"

@interface GXTemplateEngine ()

@property (nonatomic, strong) GXRenderManager *renderManager;
@property (nonatomic, strong) GXLayoutManager *layoutManager;

@end


@implementation GXTemplateEngine

+ (instancetype)sharedInstance{
    static GXTemplateEngine *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if (nil == instance) {
            instance = [[GXTemplateEngine alloc] init];
        }
    });
    return instance;
}

- (instancetype)init {
    if (self = [super init]) {
        //初始化数据相关
        self.isNeedFlat = YES;
        //初始化表达式环境
        [GXExpression setup];
    }
    return self;
}

- (GXRenderManager *)renderManager{
    if (!_renderManager) {
        _renderManager = [[GXRenderManager alloc] init];
    }
    return _renderManager;
}

- (GXLayoutManager *)layoutManager{
    if (!_layoutManager) {
        _layoutManager = [[GXLayoutManager alloc] init];
    }
    return _layoutManager;
}


#pragma mark - api

//创建视图
- (UIView <GXRootViewProtocal> *)creatViewByTemplateItem:(GXTemplateItem *)templateItem measureSize:(CGSize)measureSize{
    if (![templateItem isAvailable]) {
        return nil;
    }

    //view创建
    UIView <GXRootViewProtocal> *rootView = [self.renderManager renderViewByTemplateItem:templateItem measureSize:measureSize];
    return rootView;
}

//绑定数据
- (void)bindData:(GXTemplateData *)data measureSize:(CGSize)size onRootView:(UIView *)view{
    if (![data isAvailable]) {
        return;
    }
    
    //更新context
    GXNode *node = view.gxNode;
    GXTemplateContext *ctx = node.templateContext;
    ctx.templateData = data;
    ctx.measureSize = size;
    //绑定数据
    [GXDataManager bindData:data onRootNode:node];
}

//绑定数据
- (void)bindData:(GXTemplateData *)data onView:(UIView *)view{
    //获取size
    CGSize size = view.gxNode.templateContext.measureSize;
    [self bindData:data measureSize:size onRootView:view];
}

//通过nodeId获取根视图中的某个view
- (UIView * _Nullable)queryViewByNodeId:(NSString *)nodeId rootView:(UIView *)rootView{
    GXNode *rootNode = rootView.gxNode;
    GXNode *node = [rootNode queryNodeByNodeId:nodeId];
    return node.associatedView;
}


@end


@implementation GXTemplateEngine (Calculate)

//获取模板真实Size
- (CGSize)sizeWithTemplateItem:(GXTemplateItem *)templateItem
                   measureSize:(CGSize)measureSize{
    CGSize size = [self.layoutManager sizeWithTemplateItem:templateItem measureSize:measureSize];
    return size;
}

//获取模板真实Size
- (CGSize)sizeWithTemplateItem:(GXTemplateItem *)templateItem
                   measureSize:(CGSize)measureSize
                          data:(GXTemplateData *)data{
    CGSize size = [self.layoutManager sizeWithTemplateItem:templateItem measureSize:measureSize data:data];
    return size;
}

@end


@implementation GXTemplateEngine (Template)

//读取模板信息
- (NSDictionary * _Nullable)loadTemplateContentWithTemplateItem:(GXTemplateItem *)templateItem{
    NSDictionary *templateInfo = [GXTemplateManager loadTemplateContentWithTemplateItem:templateItem];
    return templateInfo;
}

@end

