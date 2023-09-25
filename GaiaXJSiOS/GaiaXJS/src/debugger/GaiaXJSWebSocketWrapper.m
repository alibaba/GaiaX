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


#import "GaiaXJSWebSocketWrapper.h"
#import "GaiaXJSDefines.h"
#import "GaiaXJSModuleManager.h"
#import <GaiaXSocket/GaiaXSocket.h>

@interface GaiaXJSWebSocketWrapper () <GaiaXSocketProtocol>

@property(nonatomic, strong) dispatch_queue_t gaiaxJSDebugQueue;
@property(nonatomic, strong) dispatch_semaphore_t semaphore;
@property(nonatomic, strong) NSString *bootstrapJS;

@end

@implementation GaiaXJSWebSocketWrapper

- (instancetype)init {
    if (self = [super init]) {
        NSString *labelString = [NSString stringWithFormat:@"%@", GaiaXJSDebugQueuePrefix];
        self.gaiaxJSDebugQueue = dispatch_queue_create(labelString.UTF8String, NULL);
        self.semaphore = dispatch_semaphore_create(0);
        [[GaiaXSocketManager sharedInstance] registerListener:self];
    }
    return self;
}

- (GaiaXSocketClient *)socketClient {
    return [[GaiaXSocketManager sharedInstance] socketClient];
}

- (NSString *)gxMessageId {
    return @"GAIAX_JS_DEBUGGER";
}

- (void)gxSocketClient:(GaiaXSocketClient *)client didReceiveMessage:(GaiaXSocketModel *)model {
    if (model.messageId == nil) {
        return;
    }
    if ([model.method isEqualToString:@"js/callSync"]) {
        dispatch_async(self.jsBridge.gaiaxJSQueue, ^{
            NSDictionary *dictionary = model.params;
            id retValue = [[GaiaXJSModuleManager debuggerManager]
                    invokeMethodWithContextId:[dictionary[@"contextId"] integerValue]
                                     moduleId:[dictionary[@"moduleId"] integerValue]
                                     methodId:[dictionary[@"methodId"] integerValue]
                                         args:dictionary[@"args"]];
            dispatch_async(self.gaiaxJSDebugQueue, ^{
                NSMutableDictionary *result = [NSMutableDictionary dictionary];
                if (retValue != nil) {
                    result[@"value"] = retValue;
                }
                GaiaXSocketModel *response = [GaiaXSocketModel responseWithMessageId:[model.messageId integerValue]
                                                                              result:result];
                [client sendResponse:response];
            });

        });
    } else if ([model.method isEqualToString:@"js/callAsync"]) {
        dispatch_async(self.jsBridge.gaiaxJSQueue, ^{
            NSDictionary *dictionary = model.params;
            [[GaiaXJSModuleManager debuggerManager]
                    invokeMethodWithContextId:[dictionary[@"contextId"] integerValue]
                                     moduleId:[dictionary[@"moduleId"] integerValue]
                                     methodId:[dictionary[@"methodId"] integerValue]
                                         args:dictionary[@"args"]
                                     callback:^(id result) {
                                         dispatch_async(self.gaiaxJSDebugQueue, ^{
                                             NSString *script = nil;
                                             if (result != nil) {
                                                 script = [NSString stringWithFormat:@"Bridge.invokeCallback(%@, %@)", dictionary[@"callbackId"], [self stringifyGaiaXJSValue:result]];
                                             } else {
                                                 script = [NSString stringWithFormat:@"Bridge.invokeCallback(%@)", dictionary[@"callbackId"]];
                                             }
                                             GaiaXSocketModel *response = [GaiaXSocketModel responseWithMessageId:[model.messageId integerValue]
                                                                                                           result:@{@"script": script}];
                                             [client sendResponse:response];
                                         });

                                     }];

        });
    } else if ([model.method isEqualToString:@"js/callPromise"]) {
        dispatch_async(self.jsBridge.gaiaxJSQueue, ^{
            NSDictionary *dictionary = model.params;
            [[GaiaXJSModuleManager debuggerManager]
                    invokeMethodWithContextId:[dictionary[@"contextId"] integerValue]
                                     moduleId:[dictionary[@"moduleId"] integerValue]
                                     methodId:[dictionary[@"methodId"] integerValue]
                                         args:dictionary[@"args"]
                                     resolver:^(id result) {
                                         dispatch_async(self.gaiaxJSDebugQueue, ^{
                                             NSString *script = nil;
                                             if (result != nil) {
                                                 script = [NSString stringWithFormat:@"Bridge.invokePromiseSuccess(%@, %@)", dictionary[@"callbackId"], [self stringifyGaiaXJSValue:result]];
                                             } else {
                                                 script = [NSString stringWithFormat:@"Bridge.invokePromiseSuccess(%@)", dictionary[@"callbackId"]];
                                             }
                                             GaiaXSocketModel *response = [GaiaXSocketModel responseWithMessageId:[model.messageId integerValue]
                                                                                                           result:@{@"script": script}];
                                             [client sendResponse:response];
                                         });

                                     }
                                     rejecter:^(NSString *code, NSString *message) {
                                         dispatch_async(self.gaiaxJSDebugQueue, ^{
                                             NSString *script = nil;
                                             if (code != nil && message != nil) {
                                                 script = [NSString stringWithFormat:@"Bridge.invokePromiseFailure(%@, %@)", dictionary[@"callbackId"], @{@"code": code, @"message": message}];
                                             } else {
                                                 script = [NSString stringWithFormat:@"Bridge.invokePromiseFailure(%@)", dictionary[@"callbackId"]];
                                             }
                                             GaiaXSocketModel *response = [GaiaXSocketModel responseWithMessageId:[model.messageId integerValue] result:@{@"script": script}];
                                             [client sendResponse:response];
                                         });

                                     }];
        });
    } else if ([model.method isEqualToString:@"js/getLibrary"]) {
        dispatch_async(self.gaiaxJSDebugQueue, ^{
            GaiaXSocketModel *response = [GaiaXSocketModel responseWithMessageId:[model.messageId integerValue] result:@{@"script": self.bootstrapJS}];
            [client sendResponse:response];
        });
    }
}

- (void)createJSComponent:(NSDictionary *)params {
    dispatch_async(self.gaiaxJSDebugQueue, ^{
        GaiaXSocketModel *request = [GaiaXSocketModel requestWithMethod:@"js/createComponent"
                                                                 params:params];
        [self.socketClient sendRequest:request callback:^(GaiaXSocketModel *_Nonnull model) {

        }];
    });
}

- (void)evalInitEnvJSScript:(NSString *)jsScript {
    dispatch_async(self.gaiaxJSDebugQueue, ^{
        self.bootstrapJS = jsScript;
        GaiaXSocketModel *request = [GaiaXSocketModel requestWithMethod:@"js/initJSEnv"
                                                                 params:@{@"script": jsScript}];
        [self.socketClient sendRequest:request callback:^(GaiaXSocketModel *_Nonnull model) {

        }];
    });
}

- (void)sendJSScript:(NSString *)script {
    dispatch_async(self.gaiaxJSDebugQueue, ^{
        GaiaXSocketModel *request = [GaiaXSocketModel requestWithMethod:@"js/eval"
                                                                 params:@{@"script": script}];
        [self.socketClient sendRequest:request callback:^(GaiaXSocketModel *_Nonnull model) {

        }];
    });

}


- (id)stringifyGaiaXJSValue:(id)value {
    id result = value;
    if ([value isKindOfClass:[NSDictionary class]]) {
        @try {
            NSData *data = [NSJSONSerialization dataWithJSONObject:value options:0 error:NULL];
            result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        } @catch (NSException *exception) {
            
        } @finally {
        }
    }
    return result;
}

@end
