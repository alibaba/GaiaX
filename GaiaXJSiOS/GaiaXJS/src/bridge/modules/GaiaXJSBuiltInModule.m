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


#import "GaiaXJSBuiltInModule.h"
#import "GaiaXJSDefines.h"
#import <UIKit/UIKit.h>
#import "GaiaXJSFactory.h"

@implementation GaiaXJSBuiltInModule

GAIAXJS_EXPORT_MODULE(BuiltIn)

GAIAXJS_EXPORT_SYNC_METHOD(void, setStyle:(NSString *) key value:(id) value extendInfo:(NSDictionary *) extendInfo) {
    NSLog(@"setStyle");
}

GAIAXJS_EXPORT_SYNC_METHOD(void, setProps:(NSString *) key value:(id) value extendInfo:(NSDictionary *) extendInfo) {
    NSLog(@"setProps");
}

GAIAXJS_EXPORT_SYNC_METHOD(NSDictionary *, getData:(NSDictionary *) extendInfo) {
    NSLog(@"getData extendInfo = %@", extendInfo);
    NSDictionary *data = nil;
    if ([[GaiaXJSFactory defaultFactory].modulesImplDelegate respondsToSelector:@selector(getBindingData:)]) {
        data = [[GaiaXJSFactory defaultFactory].modulesImplDelegate performSelector:@selector(getBindingData:) withObject:extendInfo];
    }
    return data;
}

GAIAXJS_EXPORT_ASYNC_METHOD(setData:(NSDictionary *) data extendInfo:(NSDictionary *) extendInfo callback:(GaiaXJSCallbackBlock) callback) {
    NSLog(@"setData %@, extendInfo = %@", data, extendInfo);
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([[GaiaXJSFactory defaultFactory].modulesImplDelegate respondsToSelector:@selector(setBindingData:extendInfo:)]) {
            [[GaiaXJSFactory defaultFactory].modulesImplDelegate performSelector:@selector(setBindingData:extendInfo:) withObject:data withObject:extendInfo];
        }
        if (callback != nil) {
            callback(nil);
        }
    });
}

GAIAXJS_EXPORT_SYNC_METHOD(NSNumber *, getComponentIndex:(NSDictionary *) extendInfo) {
    NSNumber *index = nil;
    if ([[GaiaXJSFactory defaultFactory].modulesImplDelegate respondsToSelector:@selector(getIndex:)]) {
        index = [[GaiaXJSFactory defaultFactory].modulesImplDelegate performSelector:@selector(getIndex:) withObject:extendInfo];
    } else {
        index = @(-1);
    }
    return index;
}

GAIAXJS_EXPORT_ASYNC_METHOD(showAlert:(NSDictionary *) data callback:(GaiaXJSCallbackBlock) callback) {
    dispatch_async(dispatch_get_main_queue(), ^{
        UIAlertController *alertController = [UIAlertController alertControllerWithTitle:data[@"title"] != nil ? data[@"title"] : @""
                                                                                 message:data[@"message"] != nil ? data[@"message"] : @""
                                                                          preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction *_Nonnull action) {
            if (callback != nil) {
                callback(@{@"canceled": @(YES)});
            }
        }];
        UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"好的" style:UIAlertActionStyleDefault handler:^(UIAlertAction *_Nonnull action) {
            if (callback != nil) {
                callback(@{@"canceled": @(NO)});
            }
        }];
        [alertController addAction:cancelAction];
        [alertController addAction:okAction];
        [[[UIApplication sharedApplication] windows][0].rootViewController presentViewController:alertController animated:YES completion:^{

        }];
    });
}


@end
