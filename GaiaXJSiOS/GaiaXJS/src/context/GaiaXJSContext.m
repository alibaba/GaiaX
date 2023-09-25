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


#import "GaiaXJSContext.h"
#import "GaiaXJSComponent.h"
#import "GaiaXJSRuntime.h"

@interface GaiaXJSContext ()

@property(nonatomic, strong) NSMapTable<NSString *, NSMapTable <NSNumber *, GaiaXJSComponent *> *> *componentsMap;

@end

@implementation GaiaXJSContext


- (instancetype)initWithRuntime:(GaiaXJSRuntime *)runtime {
    if (self = [super init]) {
        runtime.context = self;
        self.componentsMap = [NSMapTable strongToStrongObjectsMapTable];
        self.bridge = [[GaiaXJSBridge alloc] initWidthJSContext:self];
    }
    return self;
}

- (GaiaXJSComponent *)newComponentWithBizId:(NSString *)bizId
                                 templateId:(NSString *)templateId
                            templateVersion:(NSString *)templateVersion
                                 instanceId:(NSInteger)instanceId
                                   jsString:(nullable NSString *)jsString {
    GaiaXJSComponent *jsComponent = [[GaiaXJSComponent alloc] initWithContext:self
                                                                        bizId:bizId
                                                                   templateId:templateId
                                                              templateVersion:templateVersion
                                                                   instanceId:instanceId
                                                                     jsString:jsString];
    jsComponent.delegate = self;
    NSMapTable *mapTable = nil;
    if ([self.componentsMap objectForKey:bizId] == nil) {
        mapTable = [NSMapTable strongToStrongObjectsMapTable];
        [self.componentsMap setObject:mapTable forKey:bizId];
    } else {
        mapTable = [self.componentsMap objectForKey:bizId];
    }
    [mapTable setObject:jsComponent forKey:@(instanceId)];
    return jsComponent;
}

- (GaiaXJSComponent *)newComponentWithBizId:(NSString *)bizId
                                 templateId:(NSString *)templateId
                            templateVersion:(NSString *)templateVersion
                                 instanceId:(NSInteger)instanceId {
    return [self newComponentWithBizId:bizId
                            templateId:templateId
                       templateVersion:templateVersion
                            instanceId:instanceId
                              jsString:nil];
}

- (NSUInteger)getComponentsCountByBizId:(NSString *)bizId {
    NSUInteger count = 0;
    NSMapTable *mapTable = [self.componentsMap objectForKey:bizId];
    if (mapTable != nil) {
        count = [mapTable count];
    }
    return count;
}

- (NSUInteger)getAllComponentsCount {
    NSEnumerator *itemEnumerator = [self.componentsMap objectEnumerator];
    NSMapTable *mapTable = nil;
    NSUInteger count = 0;
    while (mapTable = [itemEnumerator nextObject]) {
        if (mapTable != nil) {
            count += [mapTable count];
        }
    }
    return count;
}

- (void)executeIndexJS:(NSString *)jsString fileName:(NSString *)fileName args:(NSDictionary *)args {
}

- (void)evalScript:(NSString *)jsString fileName:(NSString *)fileName {
}

- (void)onDestroy {
    [self removeAllComponnets];
}

- (void)removeAllComponnets {
    NSEnumerator *itemEnumerator = [self.componentsMap objectEnumerator];
    NSMapTable *mapTable = nil;
    while (mapTable = [itemEnumerator nextObject]) {
        if (mapTable != nil) {
            GaiaXJSComponent *component = nil;
            NSEnumerator *componentEnumerator = [mapTable objectEnumerator];
            while (component = [componentEnumerator nextObject]) {
                [component onDestroy];
            }
        }
    }
}

- (void)componentDidRemoved:(GaiaXJSComponent *)component {
    [self removeComponentByBizId:component.bizId instanceId:component.instanceId];
}

- (void)removeComponentByBizId:(NSString *)bizId instanceId:(NSInteger)instanceId {
    NSMapTable *mapTable = [self.componentsMap objectForKey:bizId];
    if (mapTable != nil) {
        [mapTable removeObjectForKey:@(instanceId)];
    }
}

@end
