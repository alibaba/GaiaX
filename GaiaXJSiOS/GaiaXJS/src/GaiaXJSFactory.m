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

#import "GaiaXJSFactory.h"
#import "GaiaXJSModuleManager.h"
#import "GaiaXJSContext.h"
#import "GaiaXJSCContext.h"
#import "GaiaXJSDebuggerContext.h"
#import "GaiaXJSCRuntime.h"
#import "GaiaXJSUIManager.h"
#import "GaiaXJSConfig.h"
#import <QuartzCore/QuartzCore.h>
#import "GaiaXJSDRuntime.h"

@interface GaiaXJSFactory ()

@property(nonatomic, assign) GaiaXJSEngineType engineType;
@property(nonatomic, strong) NSMapTable<NSNumber *, GaiaXJSRuntime *> *quickJSVMSMap;
@property(nonatomic, strong) NSMapTable<NSNumber *, GaiaXJSRuntime *> *jscVMSMap;
@property(nonatomic, strong) NSMapTable<NSNumber *, GaiaXJSRuntime *> *debuggerVMSMap;
@property(nonatomic, assign) NSInteger runtimeCount;


@end

@implementation GaiaXJSFactory

+ (instancetype)defaultFactory {
    static dispatch_once_t onceToken;
    static GaiaXJSFactory *jsFactory;
    dispatch_once(&onceToken, ^{
        jsFactory = [[GaiaXJSFactory alloc] init];
        jsFactory.runtimeCount = 0;
        [jsFactory initMaps];
        [[NSNotificationCenter defaultCenter] addObserver:jsFactory selector:@selector(jsEngineTypeChanged:) name:@"GAIAX_JS_ENGINE_TYPE_CHANGED" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:jsFactory selector:@selector(devToolsDidClosed:) name:@"GAIAX_DEVTOOLS_DID_CLOSED" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:jsFactory selector:@selector(devToolsDidOpened:) name:@"GAIAX_DEVTOOLS_DID_OPENED" object:nil];
    });
    return jsFactory;
}

- (void)initMaps {
    GaiaXJSEngineType type = GaiaXJSEngineTypeJSC;
    if ([GaiaXJSConfig isBreakPointDebugging]) {
        type = GaiaXJSEngineTypeDebugger;
    }
    [self initMapsWithEngineType:type];
}

- (void)initMapsWithEngineType:(GaiaXJSEngineType)engineType {
    self.engineType = engineType;
    if (self.engineType == GaiaXJSEngineTypeJSC && self.jscVMSMap == nil) {
        self.jscVMSMap = [NSMapTable strongToStrongObjectsMapTable];
    } else if (self.engineType == GaiaXJSEngineTypeQuickJS && self.quickJSVMSMap == nil) {
        self.quickJSVMSMap = [NSMapTable strongToStrongObjectsMapTable];
    } else if (self.engineType == GaiaXJSEngineTypeDebugger && self.debuggerVMSMap == nil) {
        self.debuggerVMSMap = [NSMapTable strongToStrongObjectsMapTable];
    }
}

- (void)devToolsDidClosed:(NSNotification *)notify {
    if ([GaiaXJSConfig isBreakPointDebugging]) {
        [GaiaXJSConfig setBreakPointDebuggingEnabled:NO];
        [self initMapsWithEngineType:GaiaXJSEngineTypeJSC];
    }
}

- (void)devToolsDidOpened:(NSNotification *)notify {
    GaiaXJSEngineType type = GaiaXJSEngineTypeJSC;
    if ([GaiaXJSConfig isBreakPointDebugging]) {
        type = GaiaXJSEngineTypeDebugger;
    }
    [self initMapsWithEngineType:type];
}

- (void)jsEngineTypeChanged:(NSNotification *)notify {
    NSDictionary *userInfo = notify.userInfo;
    GaiaXJSEngineType type = GaiaXJSEngineTypeJSC;
    if ([userInfo[@"engineType"] isEqualToString:@"breakpoint"]) {
        type = GaiaXJSEngineTypeDebugger;
    } else if ([userInfo[@"engineType"] isEqualToString:@"quickjs"]) {
        type = GaiaXJSEngineTypeQuickJS;
    }
    [self initMapsWithEngineType:type];
}

+ (GaiaXJSContext *)getContextByContextId:(NSInteger)contextId {
    GaiaXJSFactory *factory = [GaiaXJSFactory defaultFactory];
    NSMapTable <NSNumber *, GaiaXJSRuntime *> *map = nil;
    if (factory.engineType == GaiaXJSEngineTypeJSC) {
        map = factory.jscVMSMap;
    } else if (factory.engineType == GaiaXJSEngineTypeQuickJS) {
        map = factory.quickJSVMSMap;
    } else if (factory.engineType == GaiaXJSEngineTypeDebugger) {
        map = factory.debuggerVMSMap;
    }
    return [[map objectForKey:@(contextId)] context];
}

+ (GaiaXJSContext *)newContextByBizIdIfNeeded:(NSString *)bizId {
    GaiaXJSFactory *factory = [GaiaXJSFactory defaultFactory];
    if (factory == nil) {
        return nil;
    }
    return [GaiaXJSFactory newContextByTypeIfNeeded:factory.engineType bizId:bizId];
}

+ (GaiaXJSContext *)newContextByTypeIfNeeded:(GaiaXJSEngineType)type bizId:(NSString *)bizId {
    GaiaXJSContext *context = nil;
    GaiaXJSRuntime *runtime = nil;
    GaiaXJSFactory *factory = [GaiaXJSFactory defaultFactory];
    [self getCurrentModuleManagerByEngineType:type];
    [self getCurrentUIManagerByEngineType:type];
    if (type == GaiaXJSEngineTypeJSC) {
        GaiaXJSRuntime *runtimeInPool = [factory getRuntimeFromPoolByType:type bizId:bizId];
        if (runtimeInPool != nil) {
            context = runtimeInPool.context;
        } else {
            NSTimeInterval startTime = [[NSDate date] timeIntervalSince1970] * 1000;
            [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartCreateContext extendInfo:@{@"timestamp": @(startTime)}];
            runtime = [[GaiaXJSCRuntime alloc] init];
            factory.runtimeCount++;
            [factory.jscVMSMap setObject:runtime forKey:@(factory.runtimeCount)];
            context = [[GaiaXJSCContext alloc] initWithRuntime:runtime];
            context.contextId = factory.runtimeCount;
            NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
            [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndCreateContext extendInfo:@{@"timestamp": @(endTime),
                    @"cost": @(endTime - startTime)}];
        }
    } else if (type == GaiaXJSEngineTypeDebugger) {
        GaiaXJSRuntime *runtimeInPool = [factory getRuntimeFromPoolByType:type bizId:bizId];
        if (runtimeInPool != nil) {
            context = runtimeInPool.context;
        } else {
            runtime = [[GaiaXJSDRuntime alloc] init];
            factory.runtimeCount++;
            [factory.debuggerVMSMap setObject:runtime forKey:@(factory.runtimeCount)];
            context = [[GaiaXJSDebuggerContext alloc] initWithRuntime:runtime];
            context.contextId = factory.runtimeCount;
        }
    }
    return context;
}


- (GaiaXJSRuntime *)getRuntimeFromPoolByType:(GaiaXJSEngineType)type bizId:(NSString *)bizId {
    GaiaXJSRuntime *runtime = nil;
    if (type == GaiaXJSEngineTypeJSC) {
        NSEnumerator *enumerator = [self.jscVMSMap objectEnumerator];
        while (runtime = [enumerator nextObject]) {
            if ([runtime isKindOfClass:[GaiaXJSCRuntime class]]) {
                GaiaXJSCRuntime *jsc = (GaiaXJSCRuntime *) runtime;
                if ([jsc.context getComponentsCountByBizId:bizId] > 0) {
                    runtime = jsc;
                    break;
                } else {
                    if ([jsc.context getAllComponentsCount] <= [self maxComponent]) {
                        runtime = jsc;
                        break;
                    }
                }
            }
        }
    } else if (type == GaiaXJSEngineTypeDebugger) {
        NSEnumerator *enumerator = [self.debuggerVMSMap objectEnumerator];
        while (runtime = [enumerator nextObject]) {
            if ([runtime isKindOfClass:[GaiaXJSDRuntime class]]) {
                GaiaXJSDRuntime *jsc = (GaiaXJSDRuntime *) runtime;
                runtime = jsc;
                break;
            }
        }
    }
    return runtime;
}


+ (GaiaXJSModuleManager *)getCurrentModuleManagerByEngineType:(GaiaXJSEngineType)engineType {
    GaiaXJSModuleManager *manager = nil;
    if (engineType == GaiaXJSEngineTypeDebugger) {
        manager = [GaiaXJSModuleManager debuggerManager];
    } else {
        manager = [GaiaXJSModuleManager defaultManager];
    }
    return manager;
}

+ (GaiaXJSUIManager *)getCurrentUIManagerByEngineType:(GaiaXJSEngineType)engineType {
    GaiaXJSUIManager *manager = nil;
    if (engineType == GaiaXJSEngineTypeDebugger) {
        manager = [GaiaXJSUIManager debuggerManager];
    } else {
        manager = [GaiaXJSUIManager defaultManager];
    }
    return manager;
}

- (NSInteger)maxComponent {
    return 1000000;
}

@end
