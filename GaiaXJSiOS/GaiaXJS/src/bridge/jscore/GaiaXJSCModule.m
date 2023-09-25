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


#import "GaiaXJSCModule.h"
#import "GaiaXJSCContext.h"
#import "GaiaXJSModuleManager.h"

@implementation GaiaXJSCModule

static NSMutableDictionary *timerMaps = nil;
static int timerId = 1;

void init_jsc_module(GaiaXJSCContext *context) {
    timerMaps = [NSMutableDictionary dictionary];
    __weak GaiaXJSCContext *weakContext = context;
    context.ctx[@"GaiaXJSBridge"] = [GaiaXJSCModule class];
    context.ctx[@"setTimeout"] = ^(JSValue *callback, JSValue *timeoutValue) {
        int timeout = -1;
        if (timeoutValue != nil) {
            timeout = [[timeoutValue toNumber] intValue];
        }
        if (timeout >= 0) {
            int tempTimerId = timerId;
            dispatch_block_t block = dispatch_block_create(DISPATCH_BLOCK_INHERIT_QOS_CLASS, ^{
                [callback callWithArguments:@[]];
                @synchronized (timerMaps) {
                    [timerMaps removeObjectForKey:@(tempTimerId)];
                }
            });
            @synchronized (timerMaps) {
                timerMaps[@(timerId)] = block;
            }
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t) (timeout * NSEC_PER_MSEC)), weakContext.bridge.gaiaxJSQueue, block);
            return [JSValue valueWithInt32:timerId++ inContext:weakContext.ctx];
        }
        return [JSValue valueWithUndefinedInContext:weakContext.ctx];
    };
    context.ctx[@"clearTimeout"] = ^(JSValue *uuid) {
        if (uuid == nil) {
            return;
        }
        dispatch_block_t block = timerMaps[@([uuid toInt32])];
        if (block != NULL) {
            dispatch_block_cancel(block);
        }
        @synchronized (timerMaps) {
            [timerMaps removeObjectForKey:@([uuid toInt32])];
        }
    };


    context.ctx[@"setInterval"] = ^(JSValue *callback, JSValue *intervalValue) {
        int interval = -1;
        if (intervalValue != nil) {
            interval = [[intervalValue toNumber] intValue];
        }
        if (interval >= 0) {
            dispatch_source_t _timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, weakContext.bridge.gaiaxJSQueue);
            dispatch_source_set_timer(_timer, dispatch_time(DISPATCH_TIME_NOW, (int64_t) (interval * NSEC_PER_MSEC)), interval * NSEC_PER_MSEC, 0);
            dispatch_source_set_event_handler(_timer, ^{
                [callback callWithArguments:@[]];
            });
            @synchronized (timerMaps) {
                timerMaps[@(timerId)] = _timer;
            }
            dispatch_resume(_timer);
            return [JSValue valueWithInt32:timerId++ inContext:weakContext.ctx];
        }
        return [JSValue valueWithUndefinedInContext:weakContext.ctx];
    };

    context.ctx[@"clearInterval"] = ^(JSValue *uuid) {
        if (uuid == nil) {
            return;
        }
        dispatch_source_t _timer = timerMaps[@([uuid toInt32])];
        if (_timer != NULL) {
            dispatch_source_cancel(_timer);
        }
        @synchronized (timerMaps) {
            [timerMaps removeObjectForKey:@([uuid toInt32])];
        }
    };
}

+ (JSValue *)callSync:(NSDictionary *)dictionary {
    JSContext *context = [JSContext currentContext];
    if (dictionary == nil) {
        return [JSValue valueWithUndefinedInContext:context];
    }

    id retValue = [[GaiaXJSModuleManager defaultManager]
            invokeMethodWithContextId:[dictionary[@"contextId"] integerValue]
                             moduleId:[dictionary[@"moduleId"] integerValue]
                             methodId:[dictionary[@"methodId"] integerValue]
                            timestamp:[dictionary[@"timestamp"] doubleValue]
                                 args:dictionary[@"args"]];
    if (retValue != nil) {
        return [GaiaXJSCModule valueFromObject:retValue context:context];
    }

    return [JSValue valueWithUndefinedInContext:context];
}

+ (void)callAsync:(NSDictionary *)dictionary {
    if (dictionary == nil) {
        return;
    }
    JSContext *context = [JSContext currentContext];
    [[GaiaXJSModuleManager defaultManager]
            invokeMethodWithContextId:[dictionary[@"contextId"] integerValue]
                             moduleId:[dictionary[@"moduleId"] integerValue]
                             methodId:[dictionary[@"methodId"] integerValue]
                            timestamp:[dictionary[@"timestamp"] doubleValue]
                                 args:dictionary[@"args"]
                             callback:^(id result) {
                                 [[[context globalObject] valueForProperty:@"Bridge"]
                                         invokeMethod:@"invokeCallback"
                                        withArguments:@[
                                                dictionary[@"callbackId"], result == nil ? [JSValue valueWithUndefinedInContext:context] : result
                                        ]];
                             }];
}

+ (void)callPromise:(NSDictionary *)dictionary {
    if (dictionary == nil) {
        return;
    }
    JSContext *context = [JSContext currentContext];
    [[GaiaXJSModuleManager defaultManager]
            invokeMethodWithContextId:[dictionary[@"contextId"] integerValue]
                             moduleId:[dictionary[@"moduleId"] integerValue]
                             methodId:[dictionary[@"methodId"] integerValue]
                            timestamp:[dictionary[@"timestamp"] doubleValue]
                                 args:dictionary[@"args"]
                             resolver:^(id result) {
                                 [[[context globalObject] valueForProperty:@"Bridge"]
                                         invokeMethod:@"invokePromiseSuccess"
                                        withArguments:@[dictionary[@"callbackId"], result == nil ? [JSValue valueWithUndefinedInContext:context] : result]];
                             }
                             rejecter:^(NSString *code, NSString *message) {
                                 [[[context globalObject] valueForProperty:@"Bridge"]
                                         invokeMethod:@"invokePromiseFailure"
                                        withArguments:@[
                                                dictionary[@"callbackId"], @{@"code": code, @"message": message}
                                        ]];
                             }];
}

+ (JSValue *)valueFromObject:(id)object context:(JSContext *)ctx {
    JSValue *objectVal = nil;
    if ([object isKindOfClass:[NSNumber class]]) {
        NSNumber *numberObject = (NSNumber *) object;
        if (numberObject.class == [@YES class]) {
            objectVal = [JSValue valueWithBool:[numberObject boolValue]
                                     inContext:ctx];
        } else {
            CFNumberType numberType = CFNumberGetType((CFNumberRef) numberObject);
            switch (numberType) {
                case kCFNumberFloatType:
                case kCFNumberFloat64Type:
                case kCFNumberCGFloatType:
                case kCFNumberDoubleType:
                    objectVal = [JSValue valueWithDouble:[numberObject doubleValue]
                                               inContext:ctx];
                    break;
                default:
                    objectVal = [JSValue valueWithInt32:[numberObject intValue]
                                              inContext:ctx];
                    break;
            }
        }
    } else {
        objectVal = [JSValue valueWithObject:object inContext:ctx];
    }

    return objectVal;
}

@end
