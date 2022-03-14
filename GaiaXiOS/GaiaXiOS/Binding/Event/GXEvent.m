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

- (void)setupEventInfo:(NSDictionary *)eventInfo{
    //默认手势
    self.gestureType = GXEventGestureTypeTap;
    
    //解析数据信息
    NSDictionary *eventParams = nil;
    if ([GXUtils isValidDictionary:eventInfo]) {
        eventParams = eventInfo;
        //手势类型
        NSString *type = [eventParams gx_stringForKey:@"type"];
        self.gestureType = (![type isEqualToString:@"longpress"]) ? GXEventGestureTypeTap : GXEventGestureTypeLongPress;
    }
    
    //事件信息
    self.eventParams = eventParams;
}

@end


@implementation GXEvent(Scroll)

@end
