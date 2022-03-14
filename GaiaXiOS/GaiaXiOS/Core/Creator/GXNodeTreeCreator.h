//
//  GXNodeTreeCreator.h
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
@class GXTemplateContext;
@class GXTemplateItem;
@class GXNode;

NS_ASSUME_NONNULL_BEGIN

@interface GXNodeTreeCreator : NSObject

/// 创建节点树，并获取根节点
/// @param templateItem 模板信息
/// @param ctx 模板上下文信息
- (GXNode *)creatNodeTreeWithTemplateItem:(GXTemplateItem *)templateItem
                                  context:(GXTemplateContext *)ctx;


/// 创建节点树，并获取根节点
/// @param templateItem 模板信息
/// @param templateInfo 模板内容信息
/// @param ctx 模板上线文信息
- (GXNode *)creatNodeTreeWithTemplateItem:(GXTemplateItem *)templateItem
                             templateInfo:(NSDictionary *)templateInfo
                                  context:(GXTemplateContext *)ctx;

@end

NS_ASSUME_NONNULL_END
