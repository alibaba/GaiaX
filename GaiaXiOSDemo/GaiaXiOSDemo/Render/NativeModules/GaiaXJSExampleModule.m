//  Copyright (c) 2023, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#import "GaiaXJSExampleModule.h"
#import <GaiaXJS/GaiaXJS.h>

@implementation GaiaXJSExampleModule

GAIAXJS_EXPORT_MODULE(Example)

// Sync Method
GAIAXJS_EXPORT_SYNC_METHOD(void, log:(NSString *)data) {
    NSLog(@"GaiaXJS log %@", data);
}

// Async Method with Callback
GAIAXJS_EXPORT_ASYNC_METHOD(log2:(NSString *)data
                        callback:(GaiaXJSCallbackBlock)callback) {
    // some code here ...
    if (callback != nil) {
        callback(@{});
    }
}

// Async Method with Promise
GAIAXJS_EXPORT_SYNC_METHOD(void, log3:(NSString *)data
                             resolver:(GaiaXJSPromiseResolveBlock)resolve
                           rejecter:(GaiaXJSPromiseRejectBlock)reject) {
    // some code here ...
    if (true) {
        if (resolve != nil) {
            resolve(@{});
        }
        return;
    }
    if (reject != nil) {
        reject(@"-1", @"error occured");
    }
}
@end
