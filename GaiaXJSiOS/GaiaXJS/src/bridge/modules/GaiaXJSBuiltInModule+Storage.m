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


#import "GaiaXJSBuiltInModule+Storage.h"

@implementation GaiaXJSBuiltInModule (Storage)


GAIAXJS_EXPORT_ASYNC_METHOD(getStorage:(NSString *) key resolver:(GaiaXJSPromiseResolveBlock) resolve rejecter:(GaiaXJSPromiseRejectBlock) reject) {
    NSUserDefaults *defaults = [GaiaXJSHelper getUserDefaults];
    id result = nil;
    if (key != nil) {
        result = [defaults objectForKey:key];
        resolve(result);
        return;
    }
    reject(@"-1", @"获取失败");
}

GAIAXJS_EXPORT_ASYNC_METHOD(setStorage:(NSString *) key value:(id) value resolver:(GaiaXJSPromiseResolveBlock) resolve rejecter:(GaiaXJSPromiseRejectBlock) reject) {
    NSUserDefaults *defaults = [GaiaXJSHelper getUserDefaults];
    if (key != nil && value != nil) {
        [defaults setObject:value forKey:key];
        resolve(nil);
        return;
    }
    reject(@"-1", @"存储失败");
}

GAIAXJS_EXPORT_ASYNC_METHOD(removeStorage:(NSString *) key resolver:(GaiaXJSPromiseResolveBlock) resolve rejecter:(GaiaXJSPromiseRejectBlock) reject) {
    NSUserDefaults *defaults = [GaiaXJSHelper getUserDefaults];
    if (key != nil) {
        [defaults removeObjectForKey:key];
        resolve(nil);
        return;
    }
    reject(@"-1", @"删除失败");
}

@end
