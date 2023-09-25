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


#import "GaiaXJSNativeEventManager.h"
#import "GaiaXJSDefines.h"
#import "GaiaXJSFactory.h"
#import "GaiaxJSContext.h"

@interface GaiaXJSNativeEventManager ()

@property(nonatomic, strong) NSMutableArray *callbacks;

@end

@implementation GaiaXJSNativeEventManager

+ (instancetype)defaultManager {
    static dispatch_once_t onceToken;
    static GaiaXJSNativeEventManager *manager;
    dispatch_once(&onceToken, ^{
        manager = [[GaiaXJSNativeEventManager alloc] init];
        manager.callbacks = [[NSMutableArray alloc] init];
    });
    return manager;
}


+ (BOOL)registerMessage:(NSDictionary *)data {
    if (data && data[@"type"] != nil && data[@"contextId"] != nil && data[@"instanceId"] != nil) {
        GaiaXJSNativeEventManager *manager = [GaiaXJSNativeEventManager defaultManager];
        @synchronized (manager) {
            BOOL alreadyRegisterMessage = NO;
            for (NSUInteger i = 0; i < [manager.callbacks count]; i++) {
                NSDictionary *callback = manager.callbacks[i];
                if ([callback[@"type"] isEqualToString:data[@"type"]] &&
                        [data[@"contextId"] integerValue] == [callback[@"contextId"] integerValue] &&
                        [data[@"instanceId"] integerValue] == [callback[@"instanceId"] integerValue]) {
                    alreadyRegisterMessage = YES;
                    break;;
                }
            }
            if (!alreadyRegisterMessage) {
                [manager.callbacks addObject:data];

            }
            [[NSNotificationCenter defaultCenter] removeObserver:manager name:data[@"type"] object:nil];
            [[NSNotificationCenter defaultCenter] addObserver:manager selector:@selector(receivedMessage:) name:data[@"type"] object:nil];
        }
        return YES;
    }
    return NO;
}

- (void)receivedMessage:(NSNotification *)notify {
    @synchronized (self) {
        for (NSUInteger i = 0; i < [self.callbacks count]; i++) {
            NSDictionary *callback = self.callbacks[i];
            if ([callback[@"type"] isEqualToString:notify.name]) {
                NSMutableDictionary *injectEventData = [NSMutableDictionary dictionaryWithDictionary:callback];
                if (notify.userInfo != nil) {
                    [injectEventData setObject:notify.userInfo forKey:@"userData"];
                }
                [injectEventData setObject:GaiaXJSSafeString(notify.name) forKey:@"type"];
                [injectEventData setObject:@([@([[NSDate date] timeIntervalSince1970] * 1000) integerValue]) forKey:@"timestamp"];
                GaiaXJSContext *context = [GaiaXJSFactory getContextByContextId:[callback[@"contextId"] integerValue]];
                NSData *jsonData = nil;
                @try {
                    jsonData = [NSJSONSerialization dataWithJSONObject:injectEventData
                                                               options:0
                                                                 error:NULL];
                } @catch (NSException *exception) {

                } @finally {
                    NSString *jsonString = nil;
                    if (jsonData != nil) {
                        jsonString = [[NSString alloc] initWithData:jsonData
                                                           encoding:NSUTF8StringEncoding];
                    }
                    NSString *injectString =
                            [NSString stringWithFormat:@"window.postNativeMessage(%@)", jsonString];
                    [context.bridge evalScript:injectString fileName:@"index.js"];
                }
            }
        }
    }
}

+ (BOOL)unRegisterMessage:(NSDictionary *)data {
    if (data && data[@"type"] != nil && data[@"contextId"] != nil && data[@"instanceId"] != nil) {
        GaiaXJSNativeEventManager *manager = [GaiaXJSNativeEventManager defaultManager];
        NSMutableIndexSet *idxSet = [[NSMutableIndexSet alloc] init];
        @synchronized (manager) {
            for (NSUInteger i = 0; i < [manager.callbacks count]; i++) {
                NSDictionary *callback = manager.callbacks[i];
                if ([callback[@"type"] isEqualToString:data[@"type"]] &&
                        [data[@"contextId"] integerValue] == [callback[@"contextId"] integerValue] &&
                        [data[@"instanceId"] integerValue] == [callback[@"instanceId"] integerValue]) {
                    [idxSet addIndex:i];
                }
            }
            [manager.callbacks removeObjectsAtIndexes:idxSet];
        }
        return YES;
    }
    return NO;
}

@end
