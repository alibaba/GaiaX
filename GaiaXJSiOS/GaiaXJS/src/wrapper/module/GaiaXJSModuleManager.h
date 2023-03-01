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
#import "GaiaXJSModuleInfo.h"
#import "GaiaXJSMethodInfo.h"

NS_ASSUME_NONNULL_BEGIN

@interface GaiaXJSModuleManager : NSObject

+ (instancetype)defaultManager;
+ (instancetype)debuggerManager;

@property(nonatomic, strong) NSMapTable<NSNumber *, GaiaXJSModuleInfo *> *modulesMap;
@property(nonatomic, strong) NSMapTable<NSString *, NSNumber *> *idsMap;
@property(nonatomic, strong) NSMutableString *injectConfigs;
@property(nonatomic, strong) NSMutableString *rdInjectConfigs;

/**
 同步方法
 */
- (id)invokeMethodWithContextId:(NSInteger)contextId
                       moduleId:(NSInteger)moduleId
                       methodId:(NSInteger)methodId
                           args:(NSArray *)args;

- (id)invokeMethodWithContextId:(NSInteger)contextId
                       moduleId:(NSInteger)moduleId
                       methodId:(NSInteger)methodId
                      timestamp:(NSTimeInterval)timestamp
                           args:(NSArray *)args;

/**
 异步方法，callback
 */
- (void)invokeMethodWithContextId:(NSInteger)contextId
                         moduleId:(NSInteger)moduleId
                         methodId:(NSInteger)methodId
                             args:(NSArray *)args
                         callback:(GaiaXJSCallbackBlock)callback;

- (void)invokeMethodWithContextId:(NSInteger)contextId
                         moduleId:(NSInteger)moduleId
                         methodId:(NSInteger)methodId
                        timestamp:(NSTimeInterval)timestamp
                             args:(NSArray *)args
                         callback:(GaiaXJSCallbackBlock)callback;

/*
 异步方法，Promise
 */
- (void)invokeMethodWithContextId:(NSInteger)contextId
                         moduleId:(NSInteger)moduleId
                         methodId:(NSInteger)methodId
                             args:(NSArray *)args
                         resolver:(GaiaXJSPromiseResolveBlock)resolve
                         rejecter:(GaiaXJSPromiseRejectBlock)reject;

- (void)invokeMethodWithContextId:(NSInteger)contextId
                         moduleId:(NSInteger)moduleId
                         methodId:(NSInteger)methodId
                        timestamp:(NSTimeInterval)timestamp
                             args:(NSArray *)args
                         resolver:(GaiaXJSPromiseResolveBlock)resolve
                         rejecter:(GaiaXJSPromiseRejectBlock)reject;

@end

NS_ASSUME_NONNULL_END
