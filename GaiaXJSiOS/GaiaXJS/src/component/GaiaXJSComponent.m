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


#import "GaiaXJSComponent.h"
#import "GaiaXJSContext.h"
#import <QuartzCore/QuartzCore.h>
#import "GaiaXJSHandler.h"

@interface GaiaXJSComponent ()

@property(nonatomic, weak) GaiaXJSContext *context;

@end

@implementation GaiaXJSComponent

- (instancetype)initWithContext:(GaiaXJSContext *)context
                          bizId:(NSString *)bizId
                     templateId:(NSString *)templateId
                templateVersion:(NSString *)templateVersion
                     instanceId:(NSInteger)instanceId {
    return [self initWithContext:context
                           bizId:bizId
                      templateId:templateId
                 templateVersion:templateVersion
                      instanceId:instanceId
                        jsString:nil];
}

- (instancetype)initWithContext:(GaiaXJSContext *)context
                          bizId:(NSString *)bizId
                     templateId:(NSString *)templateId
                templateVersion:(NSString *)templateVersion
                     instanceId:(NSInteger)instanceId
                       jsString:(nullable NSString *)jsString {
    if (self = [super init]) {
        self.context = context;
        if (bizId == nil || bizId.length <= 0) {
            self.bizId = @"common";
        } else {
            self.bizId = bizId;
        }
        self.templateId = templateId;
        self.templateVersion = (templateVersion.length <= 0 ? @"-1" : templateVersion);
        self.instanceId = instanceId;
        self.jsString = jsString;
    }
    return self;
}

- (void)emmitEvent:(GaiaXJSEventType)eventType data:(NSDictionary *)data {
    NSMutableDictionary *injectEventData = [[NSMutableDictionary alloc] init];
    if (data == nil || self.templateId == nil) {
        return;
    }
    [injectEventData addEntriesFromDictionary:data];
    [injectEventData setObject:GaiaXJSSafeString(self.bizId) forKey:@"bizId"];
    [injectEventData setObject:GaiaXJSSafeString(self.templateId) forKey:@"templateId"];
    [injectEventData setObject:GaiaXJSSafeString(self.templateVersion) forKey:@"templateVersion"];
    [injectEventData setObject:@(self.instanceId) forKey:@"instanceId"];
    if (eventType == GaiaXJSEventTypeClick) {
        [injectEventData setObject:@"click" forKey:@"type"];
    } else if (eventType == GaiaXJSEventTypeDoubleClick) {
        [injectEventData setObject:@"dblclick" forKey:@"type"];
    } else if (eventType == GaiaXJSEventTypeSwipe) {
        [injectEventData setObject:@"swipe" forKey:@"type"];
    } else if (eventType == GaiaXJSEventTypeLongPress) {
        [injectEventData setObject:@"longpress" forKey:@"type"];
    } else if (eventType == GaiaXJSEventTypePinch) {
        [injectEventData setObject:@"pinch" forKey:@"type"];
    }
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
                [NSString stringWithFormat:@"window.postMessage(%@)", jsonString];
        [self.context.bridge evalScript:injectString fileName:@"index.js"];
    }
}

- (void)onReady {
    if (self.jsString.length > 0) {
        NSTimeInterval startTime = [[NSDate date] timeIntervalSince1970] * 1000;
        [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartLoadIndexJS extendInfo:@{@"timestamp": @(startTime),
                @"templateId": GaiaXJSSafeString(self.templateId),
                @"templateVersion": GaiaXJSSafeString(self.templateVersion),
                @"templateBiz": GaiaXJSSafeString(self.bizId)
        }];
        [self.context.bridge executeIndexJS:self.jsString
                                   fileName:@"index.js"
                                       args:@{@"bizId": self.bizId,
                                               @"templateId": self.templateId,
                                               @"instanceId": @(self.instanceId),
                                               @"templateVersion": self.templateVersion}
                                   callback:^{
                                       NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
                                       [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndLoadIndexJS extendInfo:@{@"timestamp": @(endTime),
                                               @"cost": @(endTime - startTime),
                                               @"templateId": GaiaXJSSafeString(self.templateId),
                                               @"templateVersion": GaiaXJSSafeString(self.templateVersion),
                                               @"templateBiz": GaiaXJSSafeString(self.bizId)
                                       }];
                                   }];

        NSString *readyStr = [NSString
                stringWithFormat:@"(function () {var instance = IMs.getComponent(%ld); "
                                 @"if (instance) { instance.onShow && instance.onShow(); "
                                 @"instance.onReady && instance.onReady(); }})()",
                                 (long) self.instanceId];
        [self.context.bridge evalScript:readyStr fileName:@"index.js"];
    }
}

- (void)onShow {
    NSString *showStr =
            [NSString stringWithFormat:
                    @"(function () {var instance = IMs.getComponent(%ld); if "
                    @"(instance) { instance.onShow && instance.onShow(); }})()",
                    (long) self.instanceId];
    [self.context.bridge evalScript:showStr fileName:@"index.js"];
}

- (void)onReuse {
    NSString *reuseStr = [NSString
            stringWithFormat:
                    @"(function () {var instance = IMs.getComponent(%ld); if "
                    @"(instance) { instance.onReuse && instance.onReuse(); }})()",
                    (long) self.instanceId];
    [self.context.bridge evalScript:reuseStr fileName:@"index.js"];
}

- (void)onLoadMore:(NSDictionary *)params {
    NSData *jsonData = nil;
    @try {
        jsonData = [NSJSONSerialization dataWithJSONObject:params
                                                   options:0
                                                     error:NULL];
    } @catch (NSException *exception) {

    } @finally {
        NSString *jsonString = nil;
        if (jsonData != nil) {
            jsonString = [[NSString alloc] initWithData:jsonData
                                               encoding:NSUTF8StringEncoding];
            NSString *reuseStr = [NSString
                    stringWithFormat:
                            @"(function () {var instance = IMs.getComponent(%ld); if "
                            @"(instance) { instance.onLoadMore && instance.onLoadMore(%@); }})()",
                            (long) self.instanceId, jsonString];
            [self.context.bridge evalScript:reuseStr fileName:@"index.js"];
        }
    }
}

- (void)onHide {
    NSString *hideStr =
            [NSString stringWithFormat:
                    @"(function () {var instance = IMs.getComponent(%ld); if "
                    @"(instance) { instance.onHide && instance.onHide(); }})()",
                    (long) self.instanceId];
    [self.context.bridge evalScript:hideStr fileName:@"index.js"];
}

- (void)onDestroy {
    NSString *destroyStr =
            [NSString stringWithFormat:
                    @"(function () {var instance = IMs.getComponent(%ld); if "
                    @"(instance) { instance.onDestroy && instance.onDestroy(); "
                    @"} IMs.removeComponent(%ld); })()",
                    (long) self.instanceId, (long) self.instanceId];
    [self.context.bridge evalScript:destroyStr fileName:@"index.js"];

    if (_delegate != nil &&
            [_delegate respondsToSelector:@selector(componentDidRemoved:)]) {
        [_delegate componentDidRemoved:self];
    }
}

@end
