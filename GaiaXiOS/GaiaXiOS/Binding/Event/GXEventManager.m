//
//  GXEventManager.m
//  GaiaXiOS
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.


#import "GXEventManager.h"
#import "GXNode.h"
#import "GXEvent.h"
#import "GXUtils.h"
#import "GXRootView.h"
#import "UIView+GX.h"
#import "NSArray+GX.h"
#import "GXEvent_Private.h"
#import "NSDictionary+GX.h"
#import "GXRegisterCenter.h"
#import "GXTemplateContext.h"

@interface GXEventManager()

//模板实例
@property (nonatomic, strong) NSMapTable *map;
//实例id，进行自增
@property (nonatomic, assign) NSInteger instanceId;
//信号量
@property (nonatomic, strong) dispatch_semaphore_t semaphore;

@end


@implementation GXEventManager

+ (instancetype)defaultManager{
    static GXEventManager *manager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if (nil == manager) {
            manager = [[GXEventManager alloc] init];
        }
    });
    return manager;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        self.instanceId = 0;
        //创建信号量
        self.semaphore = dispatch_semaphore_create(1);
    }
    return self;
}

- (void)dealloc{
    GXLog(@"[GaiaX] 事件管理eventManager释放 - %@", self);
}


#pragma mark - 事件分发

//注册事件
- (void)registerEvent:(GXEvent *)event forNode:(GXNode *)node{
    //类型判断
    if (event == nil || node == nil) {
        return;
    }
    
    //给节点添加手势/事件
    [node bindEvent:event];
}

//发送事件（Native）
- (void)fireEvent:(GXEvent *)event toNode:(GXNode *)node{
    //类型判断
    if (event == nil || node == nil) {
        return;
    }
    
    //事件分发
    GXTemplateContext *ctx = node.rootNode.templateContext;
    [self handleEvent:event context:ctx];
}


#pragma mark - 事件处理

- (void)handleEvent:(GXEvent *)event context:(GXTemplateContext *)ctx{
    GXJsEvent *jsEvent = event.jsEvent;
    //js和native兼容处理Œ
    if (jsEvent) {
        switch (jsEvent.eventLevel) {
            case GXEventLevelCover:{
                //js事件处理
                [self handleJSEvent:jsEvent context:ctx];
            }
                break;
            case GXEventLevelJS:{
                //js事件处理
                [self handleJSEvent:jsEvent context:ctx];
                //native事件处理
                [self handleNativeEvent:event context:ctx];
            }
                break;
            default:{
                //native事件处理
                [self handleNativeEvent:event context:ctx];
                //js事件处理
                [self handleJSEvent:jsEvent context:ctx];
            }
                break;
        }
        
    } else {
        //native事件处理
        [self handleNativeEvent:event context:ctx];
    }
}

// JS Event
- (void)handleJSEvent:(GXJsEvent *)event context:(GXTemplateContext *)ctx{
    //处理js事件
    GaiaXJSComponent *jsComponent = event.gxEvent.jsComponent;
    if (jsComponent) {
        //生成type
        GaiaXJSEventType type = (GaiaXJSEventType)event.eventType;
        //生成data
        NSMutableDictionary *data = [NSMutableDictionary dictionary];
        GXNode *targetNode = event.gxEvent.view.gxNode;
        [data gx_setObject:targetNode.type forKey:@"targetType"];
        [data gx_setObject:targetNode.nodeId forKey:@"targetId"];
        [data gx_setObject:targetNode.subType forKey:@"targetSubType"];
        [data gx_setObject:@([[NSDate date] timeIntervalSince1970]) forKey:@"timeStamp"];
        //发送js事件
        [jsComponent emmitEvent:type data:data];
    }
}

// Native Event
- (void)handleNativeEvent:(GXEvent *)event context:(GXTemplateContext *)ctx{
    //获取eventListener & 响应方法
    id <GXEventProtocal> eventListener = ctx.templateData.eventListener;
    if (eventListener && [eventListener respondsToSelector:@selector(gx_onGestureEvent:)]) {
        [eventListener gx_onGestureEvent:event];
    }
}


#pragma mark - 添加模板实例

- (void)addTemplate:(GXNode *)node{
    if (nil == node || ![node isKindOfClass:[GXNode class]]) {
        return;
    }
    
    //等待降低信号量
    dispatch_semaphore_wait(self.semaphore, DISPATCH_TIME_FOREVER);
    //添加实例
    NSString *key = [NSString stringWithFormat:@"%ld", (long)self.instanceId];
    [self.map setObject:node forKey:key];
    self.instanceId++;
    //提高型号量
    dispatch_semaphore_signal(self.semaphore);
}

- (GXNode *)templateForkey:(NSString *)key{
    if (nil == key) {
        return nil;
    }
    
    GXNode *node = nil;
    
    //等待降低信号量
    dispatch_semaphore_wait(self.semaphore, DISPATCH_TIME_FOREVER);
    //读取value
    node = [self.map objectForKey:key];
    //提高型号量
    dispatch_semaphore_signal(self.semaphore);
    
    return node;
}

- (void)removeTemplateForkey:(NSString *)key{
    if (nil == key) {
        return;
    }
    
    //等待降低信号量
    dispatch_semaphore_wait(self.semaphore, DISPATCH_TIME_FOREVER);
    //移除实例
    [self.map removeObjectForKey:key];
    //提高型号量
    dispatch_semaphore_signal(self.semaphore);
}


#pragma mark - lazy load

- (NSMapTable *)map{
    if (!_map) {
        _map = [NSMapTable strongToWeakObjectsMapTable];
    }
    return _map;
}


@end
