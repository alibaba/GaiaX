/*
 * Copyright (c) 2022, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

#import <Foundation/Foundation.h>
#import "GaiaXJSDefines.h"


NS_ASSUME_NONNULL_BEGIN

@class GaiaXJSContext;
@protocol GaiaXJSContextProtocol;

@interface GaiaXJSComponent : NSObject

@property(nonatomic, strong) NSString *bizId;
@property(nonatomic, strong) NSString *templateId;
@property(nonatomic, strong) NSString *templateVersion;
@property(nonatomic, assign) NSInteger instanceId;
@property(nonatomic, strong) NSString *jsString;

@property(nonatomic, weak) id <GaiaXJSContextProtocol> delegate;

- (instancetype)initWithContext:(GaiaXJSContext *)context
                          bizId:(NSString *)bizId
                     templateId:(NSString *)templateId
                templateVersion:(NSString *)templateVersion
                     instanceId:(NSInteger)instanceId;

- (instancetype)initWithContext:(GaiaXJSContext *)context
                          bizId:(NSString *)bizId
                     templateId:(NSString *)templateId
                templateVersion:(NSString *)templateVersion
                     instanceId:(NSInteger)instanceId
                       jsString:(nullable NSString *)jsString;

/**
 整个生命周期内只会回调一次
 */
- (void)onReady;

/**
 组件复用时回调
 */
- (void)onReuse;

/**
 每次当组件显示时调用一次
 */
- (void)onShow;

/**
 每次当组件消失时调用一次
 */
- (void)onHide;

/**
 组件触发加载更多时调用
 */
- (void)onLoadMore:(NSDictionary *)params;

/**
 组件要销毁时调用
 */
- (void)onDestroy;

/**
 传递事件
 */
- (void)emmitEvent:(GaiaXJSEventType)eventType data:(NSDictionary *)data;

@end

NS_ASSUME_NONNULL_END
