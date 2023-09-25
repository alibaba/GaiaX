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

NS_ASSUME_NONNULL_BEGIN

@class GaiaXJSContext;

typedef void(^GaiaXJSSetupContextBlock)(void);

typedef void(^GaiaXJSExecutedBlock)(void);

@interface GaiaXJSBridge : NSObject

@property(nonatomic, weak) GaiaXJSContext *jsContext;

@property(nonatomic, strong) dispatch_queue_t gaiaxJSQueue;

- (instancetype)initWidthJSContext:(GaiaXJSContext *)jsContext;

- (void)evalScript:(NSString *)jsString fileName:(NSString *)fileName;

- (void)executeIndexJS:(NSString *)jsString
              fileName:(NSString *)fileName
                  args:(NSDictionary *)args
              callback:(GaiaXJSExecutedBlock)callback;

- (void)executeJSLibrary:(NSString *)jsString fileName:(NSString *)fileName;

- (void)setupContext:(GaiaXJSSetupContextBlock)block;

@end

NS_ASSUME_NONNULL_END
