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

#import "GaiaXJSNativeEventModule.h"
#import "GaiaXJSDefines.h"
#import "GaiaXJSNativeEventManager.h"
#import "GaiaXJSFactory.h"

@implementation GaiaXJSNativeEventModule

GAIAXJS_EXPORT_MODULE(NativeEvent)

/*
 data 内容 @{}
 templateId,
 targetId,
 instanceId,
 eventType,
 option
 */

//添加监听
GAIAXJS_EXPORT_ASYNC_METHOD(addEventListener:(NSDictionary *) extendInfo resolver:(GaiaXJSPromiseResolveBlock) resolve rejecter:(GaiaXJSPromiseRejectBlock) reject) {
    NSLog(@"native extendInfo = %@", extendInfo);
    if ([[GaiaXJSFactory defaultFactory].modulesImplDelegate respondsToSelector:@selector(addEventListener:)]) {
        BOOL success = [[GaiaXJSFactory defaultFactory].modulesImplDelegate performSelector:@selector(addEventListener:) withObject:extendInfo];
        if (success) {
            if (resolve != nil) {
                resolve(nil);
            }
        } else {
            if (reject != nil) {
                reject(@"-1", @"无法添加事件");
            }
        }
    } else {
        if (reject != nil) {
            reject(@"-1", @"无法添加事件");
        }
    }
}

//移除监听
GAIAXJS_EXPORT_ASYNC_METHOD(removeEventListener:(NSDictionary *) extendInfo resolver:(GaiaXJSPromiseResolveBlock) resolve rejecter:(GaiaXJSPromiseRejectBlock) reject) {
    NSLog(@"native extendInfo = %@", extendInfo);
    if ([[GaiaXJSFactory defaultFactory].modulesImplDelegate respondsToSelector:@selector(removeEventListener:)]) {
        BOOL success = [[GaiaXJSFactory defaultFactory].modulesImplDelegate performSelector:@selector(removeEventListener:) withObject:extendInfo];
        if (success) {
            if (resolve != nil) {
                resolve(nil);
            }
        } else {
            if (reject != nil) {
                reject(@"-1", @"无法移除事件");
            }
        }
    } else {
        if (reject != nil) {
            reject(@"-1", @"无法移除事件");
        }
    }
}

GAIAXJS_EXPORT_SYNC_METHOD(NSNumber *, addNativeEventListener:(NSDictionary *) extendInfo) {
    NSLog(@"native extendInfo = %@", extendInfo);
    BOOL success = [GaiaXJSNativeEventManager registerMessage:extendInfo];
    return @(success);
}

GAIAXJS_EXPORT_SYNC_METHOD(NSNumber *, removeNativeEventListener:(NSDictionary *) extendInfo) {
    NSLog(@"native extendInfo = %@", extendInfo);
    BOOL success = [GaiaXJSNativeEventManager unRegisterMessage:extendInfo];
    return @(success);
}

@end
