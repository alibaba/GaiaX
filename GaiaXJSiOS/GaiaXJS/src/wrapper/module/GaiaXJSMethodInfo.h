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

@class GaiaXJSModuleInfo;

@interface GaiaXJSMethodInfo : NSObject

@property(nonatomic, weak) GaiaXJSModuleInfo *moduleInfo;
@property(nonatomic, strong) NSString *methodName;
@property(nonatomic, assign) BOOL isSync;
@property(nonatomic, assign) GaiaXJSMethodType methodType;

- (instancetype)initWithExportedMethod:(GaiaXJSExportedMethod *)exportMethod
                            moduleInfo:(GaiaXJSModuleInfo *)moduleInfo NS_DESIGNATED_INITIALIZER;


- (id)invokeWithArguments:(NSArray *)args;

- (void)invokeWithArguments:(NSArray *)args
                   callback:(GaiaXJSCallbackBlock)callback;

- (void)invokeWithArguments:(NSArray *)args
                   resolver:(GaiaXJSPromiseResolveBlock)resolve
                   rejecter:(GaiaXJSPromiseRejectBlock)reject;

@end

NS_ASSUME_NONNULL_END
