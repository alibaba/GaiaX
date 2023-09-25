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
#import <GaiaXJS/GaiaXJSBridge.h>

NS_ASSUME_NONNULL_BEGIN

@class GaiaXJSRuntime;
@class GaiaXJSComponent;

@protocol GaiaXJSContextProtocol <NSObject>

- (void)componentDidRemoved:(GaiaXJSComponent *)component;

@end

@interface GaiaXJSContext : NSObject <GaiaXJSContextProtocol>

@property(nonatomic, strong) GaiaXJSBridge *bridge;
@property(nonatomic, assign) NSInteger contextId;

- (instancetype)initWithRuntime:(GaiaXJSRuntime *)runtime;

- (GaiaXJSComponent *)newComponentWithBizId:(NSString *)bizId
                                 templateId:(NSString *)templateId
                            templateVersion:(NSString *)templateVersion
                                 instanceId:(NSInteger)instanceId;


- (GaiaXJSComponent *)newComponentWithBizId:(NSString *)bizId
                                 templateId:(NSString *)templateId
                            templateVersion:(NSString *)templateVersion
                                 instanceId:(NSInteger)instanceId
                                   jsString:(nullable NSString *)jsString;

- (NSUInteger)getComponentsCountByBizId:(NSString *)bizId;

- (NSUInteger)getAllComponentsCount;

- (void)executeIndexJS:(NSString *)jsString fileName:(NSString *)fileName args:(NSDictionary *)args;

- (void)evalScript:(NSString *)jsString fileName:(NSString *)fileName;


/**
 销毁
 */
- (void)onDestroy;

@end

NS_ASSUME_NONNULL_END
