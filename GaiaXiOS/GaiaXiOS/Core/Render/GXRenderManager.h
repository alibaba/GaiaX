//
//  GXRenderManager.h
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

#import <Foundation/Foundation.h>
#import "GXRootViewProtocal.h"
#import <UIKit/UIKit.h>

@class GXTemplateContext;
@class GXTemplateItem;
@class GXLayout;
@class GXNode;

NS_ASSUME_NONNULL_BEGIN

@interface GXRenderManager : NSObject

/// 渲染视图（templateItem）
/// @param templateItem 模板信息
/// @param measureSize 测量的size
- (UIView <GXRootViewProtocal> * _Nullable)renderViewByTemplateItem:(GXTemplateItem *)templateItem
                                                        measureSize:(CGSize)measureSize;

/// 计算布局，并绑定到节点（不涉及view）
/// @param ctx 模板上下文
- (BOOL)computeAndApplyLayout:(GXTemplateContext *)ctx;


/// 计算布局信息（不涉及view）
/// @param ctx 模板上下文
- (GXLayout *)computeLayout:(GXTemplateContext *)ctx;


/// 刷新布局（涉及view）
/// @param ctx 模板上下文
- (void)setNeedLayout:(GXTemplateContext *)ctx;


/// 重新布局（涉及view）
/// @param ctx 模板上下文
/// @param size 模板的size
- (void)relayout:(GXTemplateContext *)ctx measureSize:(CGSize)size;


@end

NS_ASSUME_NONNULL_END
