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


#import "GaiaXJSHandler.h"

@implementation GaiaXJSHandler

+ (instancetype)defaultHandler {
    static dispatch_once_t onceToken;
    static GaiaXJSHandler *handler = nil;
    dispatch_once(&onceToken, ^{
        handler = [[GaiaXJSHandler alloc] init];
    });
    return handler;
}

+ (void)dispatchExecPhase:(GaiaXJSExecPhase)execPhase extendInfo:(nullable NSDictionary *)extendInfo {
    GaiaXJSHandler *handler = [GaiaXJSHandler defaultHandler];
    if ([handler respondsToSelector:@selector(gaiaxjsEnterExecutePhase:extendInfo:)]) {
        [handler gaiaxjsEnterExecutePhase:execPhase extendInfo:extendInfo];
    }
};

+ (void)throwJSError:(NSDictionary *)errorInfo {
    GaiaXJSHandler *handler = [GaiaXJSHandler defaultHandler];
    if ([handler respondsToSelector:@selector(gaiaxjsCatchJSError:)]) {
        [handler gaiaxjsCatchJSError:errorInfo];
    }
}

@end
