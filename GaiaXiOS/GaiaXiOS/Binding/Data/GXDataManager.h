//
//  GXDataManager.h
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
#import <UIKit/UIKit.h>
@class GXTemplateData;
@class GXNode;

NS_ASSUME_NONNULL_BEGIN

@interface GXDataManager : NSObject

/// 绑定数据 -（当前视图和子视图的数据 -> 递归绑定数据）
/// @param data 原始数据
/// @param view 根视图
+ (void)bindData:(GXTemplateData *)data onRootView:(UIView *)view;

/// 绑定数据 -（当前节点和子节点数据 -> 递归绑定数据）
/// @param data 原始数据
/// @param node 根节点
+ (void)bindData:(GXTemplateData *)data onRootNode:(GXNode *)node;


/// 计算布局
/// @param data 原始数据
/// @param node 根节点
+ (void)calculateData:(GXTemplateData *)data onRootNode:(GXNode *)node;


@end

NS_ASSUME_NONNULL_END
