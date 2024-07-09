//
//  GXEvent.m
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

#import "GXEvent.h"
#import "GXUtils.h"
#import "NSDictionary+GX.h"
#import "GXEvent_Private.h"

@interface GXEvent ()

//scroll容器的偏移量
@property (nonatomic) CGPoint contentOffset;

@end

@implementation GXEvent

- (instancetype)init{
    self = [super init];
    if (self) {
        self.index = -1;
    }
    return self;
}

- (void)setupEventInfo:(NSDictionary *)eventInfo {
    //事件类型
    self.eventType = [GXEvent eventTypeWithEventInfo:eventInfo];
    //事件信息
    self.eventParams = eventInfo;
}

+ (GXEventType)eventTypeWithEventInfo:(NSDictionary *)eventInfo{
    NSString *eventType = [eventInfo gx_stringForKey:@"type"]; //tap、longpress
    if ([@"longpress" isEqualToString:eventType]) {
        return GXEventTypeLongPress;
    } else {
        return GXEventTypeTap;
    }
}


#pragma mark - JS

//创建jsEvent
- (void)creatJsEvent:(NSDictionary *)eventInfo{
    //js事件类型处理
    GXEventType eventType = [GXEvent eventTypeWithEventInfo:eventInfo];
    
    GXJsEvent *jsEvent = self.jsEvent;
    if (jsEvent == nil) {
        jsEvent = [[GXJsEvent alloc] init];
        jsEvent.gxEvent = self;
        self.jsEvent = jsEvent;
    }
    
    jsEvent.eventType = eventType;
    jsEvent.eventLevel = [self eventLevelFrom:eventInfo];
}

//获取js和native事件优先级
- (GXEventLevel)eventLevelFrom:(NSDictionary *)eventInfo{
    GXEventLevel level = GXEventLevelNative;
    
    //事件option配置
    NSDictionary *option = [eventInfo gx_dictionaryForKey:@"option"];
    if (option) {
        BOOL cover = [option gx_boolForKey:@"cover"];
        if (cover) {
            level = GXEventLevelCover;
        } else {
            NSInteger tmpLevel = [option gx_integerForKey:@"level"];
            if (tmpLevel >= -1 && tmpLevel <= 1) {
                level = (GXEventLevel)tmpLevel;
            }
        }
    }
    
    return level;
}

@end


@implementation GXEvent(Scroll)

@end

@implementation GXJsEvent

@end
