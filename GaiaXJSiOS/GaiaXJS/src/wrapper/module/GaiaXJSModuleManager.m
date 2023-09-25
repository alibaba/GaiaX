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

#import "GaiaXJSModuleManager.h"
#import <mach-o/getsect.h>
#import <dlfcn.h>
#import <objc/runtime.h>
#import "GaiaXJSHelper.h"
#import "GaiaXJSFactory.h"
#import "GaiaXJSContext.h"
#import "GaiaXJSHandler.h"

static NSUInteger moduleIndex = 0;


@interface GaiaXJSModuleManager ()
@property (nonatomic, assign)GaiaXJSEngineType engineType;
@end


@implementation GaiaXJSModuleManager

+ (instancetype)defaultManager {
    static dispatch_once_t onceToken;
    static GaiaXJSModuleManager *defaultM;
    dispatch_once(&onceToken, ^{
        defaultM = [[GaiaXJSModuleManager alloc] init];
        defaultM.engineType = GaiaXJSEngineTypeJSC;
        [defaultM initMaps];
        [defaultM registerModules];
    });
    return defaultM;
}

+ (instancetype)debuggerManager {
    static dispatch_once_t onceDebuggerToken;
    static GaiaXJSModuleManager *debuggerM;
    dispatch_once(&onceDebuggerToken, ^{
        debuggerM = [[GaiaXJSModuleManager alloc] init];
        debuggerM.engineType = GaiaXJSEngineTypeDebugger;
        [debuggerM initMaps];
        [debuggerM registerModulesWithEngineType:GaiaXJSEngineTypeDebugger];
    });
    return debuggerM;
}

- (void)initMaps {
    self.modulesMap = [[NSMapTable alloc] initWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory capacity:20];
    self.idsMap = [[NSMapTable alloc] initWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory capacity:20];
    self.injectConfigs = [NSMutableString string];
    self.rdInjectConfigs = [NSMutableString string];
}

- (void)registerModules {
    NSTimeInterval startTime = [[NSDate date] timeIntervalSince1970] * 1000;
    [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartLoadNativeModules extendInfo:@{@"timestamp": @(startTime)}];
    [self registerModulesWithEngineType:GaiaXJSEngineTypeJSC];
    NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
    [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndLoadNativeModules extendInfo:@{@"timestamp": @(endTime), @"cost": @(endTime - startTime)}];
}

- (void)registerModulesWithEngineType:(GaiaXJSEngineType)engineType {
    static NSString *configuration = @"";
    Dl_info info;
    dladdr((__bridge const void *) (configuration), &info);
#ifndef __LP64__
    const struct mach_header *mhp = (struct mach_header*)info.dli_fbase;
    unsigned long size = 0;
    uint32_t *data = (uint32_t*)getsectiondata(mhp, "__DATA", "__gaiaxjs", &size);
#else
    const struct mach_header_64 *mhp = (struct mach_header_64 *) info.dli_fbase;
    unsigned long size = 0;
    uint64_t *data = (uint64_t *) getsectiondata(mhp, "__DATA", "__gaiaxjs", &size);
#endif
    NSUInteger counter = size / sizeof(void *);
    for (NSUInteger idx = 0; idx < counter; ++idx) {
        char *string = (char *) data[idx];
        NSString *str = [NSString stringWithFormat:@"%s", string];
        [self buildMapsWithJSName:str index:moduleIndex++ engineType:engineType];
    }
}

- (void)buildMapsWithJSName:(NSString *)jsName index:(NSUInteger)index engineType:(GaiaXJSEngineType)engineType {
    BOOL isDebugging = engineType == GaiaXJSEngineTypeDebugger;
    NSString *className = [NSString stringWithFormat:@"GaiaXJS%@Module", jsName];
    Class cls = NSClassFromString(className);
    if (cls == nil) {
        className = [NSString stringWithFormat:@"GaiaX%@Module", jsName];
        cls = NSClassFromString(className);
    }
    if (cls != nil) {
        GaiaXJSModuleInfo *moduleInfo = [[GaiaXJSModuleInfo alloc] init];
        moduleInfo.moduleName = className;
        [self.modulesMap setObject:moduleInfo forKey:@(index)];
        [self.idsMap setObject:@(index) forKey:moduleInfo.moduleName];
        [self.injectConfigs appendFormat:@"var %@ = /** @class */ (function (_super) {\
            __extends(%@, _super);\
            function %@() {\
                return _super.call(this) || this;\
            }\
            return %@;\
         }(Bridge));", className, className, className, className];
        if (isDebugging) {
            [self.rdInjectConfigs appendFormat:@"class %@ extends Bridge {}; ", className];
        }
        if (![jsName isEqualToString:@"BuiltIn"]) {
            [self.injectConfigs appendFormat:@"__globalThis.%@ = new %@(); ", jsName, className];
            if (isDebugging) {
                [self.rdInjectConfigs appendFormat:@"__globalThis.%@ = new %@(); ", jsName, className];
            }
        } else {
            [self.injectConfigs appendFormat:@"__globalThis.gaiax = new %@(); ", className];
            if (isDebugging) {
                [self.rdInjectConfigs appendFormat:@"__globalThis.gaiax = new %@(); ", className];
            }
        }
        unsigned int methodCount;
        Method *methods = class_copyMethodList(object_getClass(cls), &methodCount);
        moduleInfo.methodsMap = [[NSMapTable alloc] initWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory capacity:20];
        moduleInfo.idsMap = [[NSMapTable alloc] initWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory capacity:20];
        for (NSUInteger i = 0; i < methodCount; i++) {
            Method method = methods[i];
            SEL selector = method_getName(method);
            NSString *selectorString = NSStringFromSelector(selector);
            if ([selectorString hasPrefix:@"__gaiaxjs_export__"]) {
                IMP imp = method_getImplementation(method);
                GaiaXJSExportedMethod *exportedMethod = ((GaiaXJSExportedMethod *(*)(id, SEL)) imp)(cls, selector);
                GaiaXJSMethodInfo *methodInfo = [[GaiaXJSMethodInfo alloc] initWithExportedMethod:exportedMethod
                                                                                       moduleInfo:moduleInfo];
                [moduleInfo.methodsMap setObject:methodInfo forKey:@(i)];
                [moduleInfo.idsMap setObject:@(i) forKey:methodInfo.methodName];
                [self.injectConfigs appendFormat:@"%@\r\n", [GaiaXJSHelper generateJSMethodString:methodInfo moduleIndex:index methodIndex:i]];
                if (isDebugging) {
                    [self.rdInjectConfigs appendFormat:@"%@\r\n", [GaiaXJSHelper generateJSMethodString:methodInfo moduleIndex:index methodIndex:i]];
                }
            }
        }
        free(methods);
    }
}

- (GaiaXJSMethodInfo *)getMethodInfoByModuleId:(NSUInteger)moduleId methodId:(NSUInteger)methodId {
    GaiaXJSModuleInfo *moduleInfo = [self.modulesMap objectForKey:@(moduleId)];
    if (moduleInfo == nil) {
        return nil;
    }
    GaiaXJSMethodInfo *methodInfo = [moduleInfo.methodsMap objectForKey:@(methodId)];
    if (methodInfo == nil) {
        return nil;
    }
    return methodInfo;
}

- (id)invokeMethodWithContextId:(NSInteger)contextId
                       moduleId:(NSInteger)moduleId
                       methodId:(NSInteger)methodId
                      timestamp:(NSTimeInterval)timestamp
                           args:(NSArray *)args {
    GaiaXJSMethodInfo *methodInfo = [self getMethodInfoByModuleId:moduleId methodId:methodId];
    if (methodInfo != nil) {
        if (timestamp > 0) {
            [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartInvokeSyncMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseJSToContext),
                    @"timestamp": @(timestamp),
                    @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                    @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                    @"apiType": @(GaiaXJSMethodTypeSync)
            }];
            NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
            [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndInvokeSyncMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseJSToContext),
                    @"timestamp": @(endTime),
                    @"cost": @(endTime - timestamp),
                    @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                    @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                    @"apiType": @(GaiaXJSMethodTypeSync)
            }];
        }
        NSTimeInterval startTime = [[NSDate date] timeIntervalSince1970] * 1000;
        [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartInvokeSyncMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseContextToReturn),
                @"timestamp": @(startTime),
                @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                @"apiType": @(GaiaXJSMethodTypeSync)
        }];
        id result = [methodInfo invokeWithArguments:args];
        NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
        [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartInvokeSyncMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseContextToReturn),
                @"timestamp": @(endTime),
                @"cost": @(endTime - timestamp),
                @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                @"apiType": @(GaiaXJSMethodTypeSync)
        }];

        return result;
    }
    return nil;
}


- (id)invokeMethodWithContextId:(NSInteger)contextId
                       moduleId:(NSInteger)moduleId
                       methodId:(NSInteger)methodId
                           args:(NSArray *)args {
    NSTimeInterval startTime = 0;
    return [self invokeMethodWithContextId:contextId
                                                  moduleId:moduleId
                                                  methodId:methodId
                                                 timestamp:startTime
                                                      args:args];
}

- (void)invokeMethodWithContextId:(NSInteger)contextId
                         moduleId:(NSInteger)moduleId
                         methodId:(NSInteger)methodId
                        timestamp:(NSTimeInterval)timestamp
                             args:(NSArray *)args
                         callback:(GaiaXJSCallbackBlock)callback {
    GaiaXJSMethodInfo *methodInfo = [self getMethodInfoByModuleId:moduleId methodId:methodId];
    if (methodInfo != nil) {
        if (timestamp > 0) {
            [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartInvokeAsyncMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseJSToContext),
                    @"timestamp": @(timestamp),
                    @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                    @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                    @"apiType": @(GaiaXJSMethodTypeAsync)}];
            NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
            [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndInvokeAsyncMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseJSToContext),
                    @"timestamp": @(endTime),
                    @"cost": @(endTime - timestamp),
                    @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                    @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                    @"apiType": @(GaiaXJSMethodTypeAsync)
            }];
        }

        const char *currentQueue = dispatch_queue_get_label(DISPATCH_CURRENT_QUEUE_LABEL);

        __block NSTimeInterval startTime = [[NSDate date] timeIntervalSince1970] * 1000;
        [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartInvokeAsyncMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseContextToReturn),
                @"timestamp": @(startTime),
                @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                @"apiType": @(GaiaXJSMethodTypeAsync)}];
        [methodInfo invokeWithArguments:args callback:^(id result) {
            NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
            [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndInvokeAsyncMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseContextToReturn),
                    @"timestamp": @(endTime),
                    @"cost": @(endTime - startTime),
                    @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                    @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                    @"apiType": @(GaiaXJSMethodTypeAsync)
            }];
            const char *nextQueue = dispatch_queue_get_label(DISPATCH_CURRENT_QUEUE_LABEL);
            startTime = [[NSDate date] timeIntervalSince1970] * 1000;
            [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartInvokeAsyncMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseReturnToContext),
                    @"timestamp": @(startTime),
                    @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                    @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                    @"apiType": @(GaiaXJSMethodTypeAsync)}];
            if (strcmp(currentQueue, nextQueue) != 0) {
                if (contextId > 0) {
                    GaiaXJSContext *context = [GaiaXJSFactory getContextByContextId:contextId];
                    if (context != nil) {
                        dispatch_async(context.bridge.gaiaxJSQueue, ^{
                            if (callback != nil) {
                                NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
                                [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndInvokeAsyncMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseReturnToContext),
                                        @"timestamp": @(endTime),
                                        @"cost": @(endTime - startTime),
                                        @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                                        @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                                        @"apiType": @(GaiaXJSMethodTypeAsync)
                                }];
                                callback(result);
                            }
                        });
                    }
                } else {
                    if (callback != nil) {
                        callback(result);
                    }
                }
            } else {
                if (contextId > 0) {
                    GaiaXJSContext *context = [GaiaXJSFactory getContextByContextId:contextId];
                    if (context != nil) {
                        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t) (0 * NSEC_PER_SEC)), context.bridge.gaiaxJSQueue, ^{
                            if (callback != nil) {
                                NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
                                [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndInvokeAsyncMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseReturnToContext),
                                        @"timestamp": @(endTime),
                                        @"cost": @(endTime - startTime),
                                        @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                                        @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                                        @"apiType": @(GaiaXJSMethodTypeAsync)}];
                                callback(result);
                            }
                        });
                    }
                } else {
                    if (callback != nil) {
                        callback(result);
                    }
                }
            }
        }];
    } else {
        if (callback != nil) {
            callback(nil);
        }
    }
}

- (void)invokeMethodWithContextId:(NSInteger)contextId
                         moduleId:(NSInteger)moduleId
                         methodId:(NSInteger)methodId
                             args:(NSArray *)args
                         callback:(GaiaXJSCallbackBlock)callback {
    NSTimeInterval startTime = 0;
    [self invokeMethodWithContextId:contextId
                                           moduleId:moduleId
                                           methodId:methodId
                                          timestamp:startTime
                                               args:args
                                           callback:callback];
}

- (void)invokeMethodWithContextId:(NSInteger)contextId
                         moduleId:(NSInteger)moduleId
                         methodId:(NSInteger)methodId
                        timestamp:(NSTimeInterval)timestamp
                             args:(NSArray *)args
                         resolver:(GaiaXJSPromiseResolveBlock)resolve
                         rejecter:(GaiaXJSPromiseRejectBlock)reject {
    GaiaXJSMethodInfo *methodInfo = [self getMethodInfoByModuleId:moduleId methodId:methodId];
    if (methodInfo != nil) {
        if (timestamp > 0) {
            [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartInvokePromiseMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseJSToContext),
                    @"timestamp": @(timestamp),
                    @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                    @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                    @"apiType": @(GaiaXJSMethodTypePromise)}];
            NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
            [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndInvokePromiseMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseJSToContext),
                    @"timestamp": @(endTime),
                    @"cost": @(endTime - timestamp),
                    @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                    @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                    @"apiType": @(GaiaXJSMethodTypePromise)
            }];
        }

        const char *currentQueue = dispatch_queue_get_label(DISPATCH_CURRENT_QUEUE_LABEL);
        __block NSTimeInterval startTime = [[NSDate date] timeIntervalSince1970] * 1000;
        [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartInvokePromiseMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseContextToReturn),
                @"timestamp": @(startTime),
                @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                @"apiType": @(GaiaXJSMethodTypePromise)}];
        [methodInfo invokeWithArguments:args
                               resolver:^(id result) {
                                   NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
                                   [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndInvokePromiseMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseContextToReturn),
                                           @"timestamp": @(endTime),
                                           @"cost": @(endTime - startTime),
                                           @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                                           @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                                           @"apiType": @(GaiaXJSMethodTypePromise)
                                   }];
                                   const char *nextQueue = dispatch_queue_get_label(DISPATCH_CURRENT_QUEUE_LABEL);
                                   startTime = [[NSDate date] timeIntervalSince1970] * 1000;
                                   [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartInvokePromiseMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseContextToReturn),
                                           @"timestamp": @(startTime),
                                           @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                                           @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                                           @"apiType": @(GaiaXJSMethodTypePromise)}];
                                   if (strcmp(currentQueue, nextQueue) != 0) {
                                       if (contextId > 0) {
                                           GaiaXJSContext *context = [GaiaXJSFactory getContextByContextId:contextId];
                                           if (context != nil) {
                                               dispatch_async(context.bridge.gaiaxJSQueue, ^{
                                                   if (resolve != nil) {
                                                       NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
                                                       [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndInvokePromiseMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseReturnToContext),
                                                               @"timestamp": @(endTime),
                                                               @"cost": @(endTime - startTime),
                                                               @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                                                               @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                                                               @"apiType": @(GaiaXJSMethodTypePromise)
                                                       }];
                                                       resolve(result);
                                                   }
                                               });
                                           }
                                       } else {
                                           if (resolve != nil) {
                                               resolve(result);
                                           }
                                       }
                                   } else {
                                       if (contextId > 0) {
                                           GaiaXJSContext *context = [GaiaXJSFactory getContextByContextId:contextId];
                                           if (context != nil) {
                                               dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t) (0 * NSEC_PER_SEC)), context.bridge.gaiaxJSQueue, ^{
                                                   if (resolve != nil) {
                                                       NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
                                                       [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndInvokePromiseMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseReturnToContext),
                                                               @"timestamp": @(endTime),
                                                               @"cost": @(endTime - startTime),
                                                               @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                                                               @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                                                               @"apiType": @(GaiaXJSMethodTypePromise)}];
                                                       resolve(result);
                                                   }
                                               });
                                           }
                                       } else {
                                           if (resolve != nil) {
                                               resolve(result);
                                           }
                                       }
                                   }
                               } rejecter:^(NSString *code, NSString *message) {
                    const char *nextQueue = dispatch_queue_get_label(DISPATCH_CURRENT_QUEUE_LABEL);
                    if (strcmp(currentQueue, nextQueue) != 0) {
                        if (contextId > 0) {
                            GaiaXJSContext *context = [GaiaXJSFactory getContextByContextId:contextId];
                            if (context != nil) {
                                dispatch_async(context.bridge.gaiaxJSQueue, ^{
                                    if (reject != nil) {
                                        NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
                                        [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndInvokePromiseMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseReturnToContext),
                                                @"timestamp": @(endTime),
                                                @"cost": @(endTime - startTime),
                                                @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                                                @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                                                @"apiType": @(GaiaXJSMethodTypePromise)}];
                                        reject(code, message);
                                    }
                                });
                            }
                        } else {
                            if (reject != nil) {
                                reject(code, message);
                            }
                        }
                    } else {
                        if (contextId > 0) {
                            GaiaXJSContext *context = [GaiaXJSFactory getContextByContextId:contextId];
                            if (context != nil) {
                                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t) (0 * NSEC_PER_SEC)), context.bridge.gaiaxJSQueue, ^{
                                    if (reject != nil) {
                                        NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
                                        [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndInvokePromiseMethod extendInfo:@{@"subPhase": @(GaiaXJSInvokeMethodSubPhaseReturnToContext),
                                                @"timestamp": @(endTime),
                                                @"cost": @(endTime - startTime),
                                                @"moduleName": GaiaXJSSafeString(methodInfo.moduleInfo.moduleName),
                                                @"methodName": GaiaXJSSafeString(methodInfo.methodName),
                                                @"apiType": @(GaiaXJSMethodTypePromise)}];
                                        reject(code, message);
                                    }
                                });
                            }
                        } else {
                            if (reject != nil) {
                                reject(code, message);
                            }
                        }
                    }
                }];
    } else {
        reject(@"-1", @"客户端没有对应的实现");
    }
}

- (void)invokeMethodWithContextId:(NSInteger)contextId
                         moduleId:(NSInteger)moduleId
                         methodId:(NSInteger)methodId
                             args:(NSArray *)args
                         resolver:(GaiaXJSPromiseResolveBlock)resolve
                         rejecter:(GaiaXJSPromiseRejectBlock)reject {
    NSTimeInterval startTime = 0;
    [self invokeMethodWithContextId:contextId
                                           moduleId:moduleId
                                           methodId:methodId
                                          timestamp:startTime
                                               args:args
                                           resolver:resolve rejecter:reject];
}


@end
