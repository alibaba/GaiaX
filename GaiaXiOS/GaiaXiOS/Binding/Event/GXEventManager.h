//
//  GXEventManager.h
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
@class GXEvent;
@class GXNode;

NS_ASSUME_NONNULL_BEGIN

#define TheGXEventManager [GXEventManager defaultManager]

@interface GXEventManager : NSObject

//模板实例id
@property (nonatomic, assign, readonly) NSInteger instanceId;
//模板实例
@property (nonatomic, strong, readonly) NSMapTable *map;

/// 单例
+ (instancetype)defaultManager;

/// 注册事件
/// @param event 事件
/// @param node 节点
- (void)registerEvent:(GXEvent *)event forNode:(GXNode *)node;

/// 发送事件
/// @param event 事件
/// @param node 节点
- (void)fireEvent:(GXEvent *)event toNode:(GXNode *)node;


//存储 & 读取模板的实例
- (void)addTemplate:(GXNode *)node;
- (GXNode *)templateForkey:(NSString *)key;
- (void)removeTemplateForkey:(NSString *)key;

@end

NS_ASSUME_NONNULL_END
