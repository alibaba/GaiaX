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


#import "GaiaXJSNativeLoggerModule.h"
#import <GaiaXSocket/GaiaXSocket.h>
#import "GaiaXJSHandler.h"

@implementation GaiaXJSNativeLoggerModule

GAIAXJS_EXPORT_MODULE(NativeLogger)

GAIAXJS_EXPORT_SYNC_METHOD(void, log:(NSDictionary *) data) {
    GaiaXSocketModel *model = [GaiaXSocketModel notificationWithMethod:@"js/console" params:@{@"level": @"log", @"data": data != nil && data[@"data"] != nil ? data[@"data"] : @""}];
    [[[GaiaXSocketManager sharedInstance] socketClient] sendeNotification:model];
}

GAIAXJS_EXPORT_SYNC_METHOD(void, info:(NSDictionary *) data) {
    GaiaXSocketModel *model = [GaiaXSocketModel notificationWithMethod:@"js/console" params:@{@"level": @"info", @"data": data != nil && data[@"data"] != nil ? data[@"data"] : @""}];
    [[[GaiaXSocketManager sharedInstance] socketClient] sendeNotification:model];
}

GAIAXJS_EXPORT_SYNC_METHOD(void, warn:(NSDictionary *) data) {
    GaiaXSocketModel *model = [GaiaXSocketModel notificationWithMethod:@"js/console" params:@{@"level": @"warn", @"data": data != nil && data[@"data"] != nil ? data[@"data"] : @""}];
    [[[GaiaXSocketManager sharedInstance] socketClient] sendeNotification:model];
}

GAIAXJS_EXPORT_SYNC_METHOD(void, error:(NSDictionary *) data) {
    GaiaXSocketModel *model = [GaiaXSocketModel notificationWithMethod:@"js/console" params:@{@"level": @"error", @"data": data != nil && data[@"data"] != nil ? data[@"data"] : @""}];
    [[[GaiaXSocketManager sharedInstance] socketClient] sendeNotification:model];
    if (data && data[@"data"] != nil) {
        NSDictionary *errorDictionary = data[@"data"];
        if ([errorDictionary isKindOfClass:[NSDictionary class]]) {
            NSDictionary *errorInfo = @{@"errorCode": [NSString stringWithFormat:@"%@##%@@%@##%@", GaiaXJSSafeString(errorDictionary[@"bizId"]), GaiaXJSSafeString(errorDictionary[@"templateId"]), GaiaXJSSafeString(errorDictionary[@"templateVersion"]), GaiaXJSSafeString(errorDictionary[@"message"])], @"errorMessage": GaiaXJSSafeString(errorDictionary[@"message"]), @"errorStack": GaiaXJSSafeString(errorDictionary[@"stack"])};
            [GaiaXJSHandler throwJSError:errorInfo];
        } else {
            NSDictionary *errorInfo = @{@"errorCode": GaiaXJSSafeString(data[@"data"]), @"errorMessage": GaiaXJSSafeString(data[@"data"])};
            [GaiaXJSHandler throwJSError:errorInfo];
        }

    }
}

@end
