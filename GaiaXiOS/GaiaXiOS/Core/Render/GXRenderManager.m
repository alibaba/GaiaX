//
//  GXRenderManager.m
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

#import "GXRenderManager.h"
#import "GXTemplateContext.h"
#import "GXNodeTreeCreator.h"
#import "GXLayout.h"
#import "GXNode.h"

@interface GXRenderManager ()

@property (nonatomic, strong) GXNodeTreeCreator *creator;

@end


@implementation GXRenderManager

//渲染视图
- (UIView <GXRootViewProtocal> *)renderViewByTemplateItem:(GXTemplateItem *)templateItem
                                              measureSize:(CGSize)measureSize{
    //rootView
    UIView *rootView = nil;
    
    //context
    GXTemplateContext *ctx = [self creatTemplateContext];
    ctx.templateItem = templateItem;
    ctx.measureSize = measureSize;
    
    //获取节点树
    GXNode *rootNode = [self.creator creatNodeTreeWithTemplateItem:templateItem context:ctx];
    if (rootNode) {
        rootView = [self renderView:ctx];
    }
    
    return (UIView <GXRootViewProtocal> *)rootView;
}


//根据context渲染view
- (UIView *)renderView:(GXTemplateContext *)ctx{
    UIView *view = nil;
    GXNode *node = ctx.rootNode;
    BOOL result = [self computeAndApplyLayout:ctx];
    if (result) {
        view = [node applyView];
    }

    return view;
}

//计算 & 绑定布局
- (BOOL)computeAndApplyLayout:(GXTemplateContext *)ctx{
    GXNode *node = ctx.rootNode;
    GXLayout *layout = [self computeLayout:ctx];
    if (layout) {
        [node applyLayout:layout];
        return YES;
    }
    return NO;
}


//计算layout
- (GXLayout *)computeLayout:(GXTemplateContext *)ctx{
    GXNode *node = ctx.rootNode;
    CGSize size = ctx.measureSize;
    GXLayout *layout = [node computeLayout:(StretchSize){.width = size.width, .height = size.height}];
    return layout;
}


//强制刷新
- (void)setNeedLayout:(GXTemplateContext *)ctx{
    GXNode *node = ctx.rootNode;
    if (node == nil || !ctx.isNeedLayout) {
        return;
    }
    
    //重新计算布局，渲染视图
    [self renderView:ctx];
    
    //重置布局标识
    ctx.isNeedLayout = NO;
}


//重新计算布局
- (void)relayout:(GXTemplateContext *)ctx measureSize:(CGSize)size{
    GXNode *node = ctx.rootNode;
    CGSize measureSize = ctx.measureSize;
    if (node == nil || !CGSizeEqualToSize(size, measureSize)) {
        return;
    }
    
    //重新渲染
    ctx.measureSize = size;
    
    //重新计算布局，渲染视图
    [self renderView:ctx];
}


#pragma mark - templateContext

- (GXTemplateContext *)creatTemplateContext{
    GXTemplateContext *context = [[GXTemplateContext alloc] init];
    context.renderManager = self;
    return context;
}


#pragma mark - lazy load

- (GXNodeTreeCreator *)creator{
    if (!_creator) {
        _creator = [[GXNodeTreeCreator alloc] init];
    }
    return _creator;
}

@end
