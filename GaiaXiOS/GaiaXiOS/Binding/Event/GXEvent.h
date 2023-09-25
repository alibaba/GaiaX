//
//  GXEvent.h
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

#import <Foundation/Foundation.h>
#import <UIKit/UIkit.h>

@class GXTemplateItem;

NS_ASSUME_NONNULL_BEGIN

//event gesture type
typedef NS_ENUM(NSUInteger, GXEventType) {
    GXEventTypeTap = 0,//tap
    GXEventTypeLongPress = 1,//long press
};

//手势类型
typedef NS_ENUM(NSInteger, GXEventLevel) {
    GXEventLevelCover = -1,//js覆盖native
    GXEventLevelNative = 0,//native高于js
    GXEventLevelJS = 1,//native低于js
};

@interface GXEvent : NSObject

//target view
@property (nonatomic, weak) UIView *view;
//node id
@property (nonatomic, copy) NSString *nodeId;
//view index
@property (nonatomic, assign) NSInteger index;
//event parameters
@property (nonatomic, strong) NSDictionary *eventParams;
//template info
@property (nonatomic, weak) GXTemplateItem *templateItem;
//gesture type：tap, longpress
@property (nonatomic, assign) GXEventType eventType;

//set event information
- (void)setupEventInfo:(NSDictionary *)eventInfo;

// get event type
+ (GXEventType)eventTypeWithEventInfo:(NSDictionary *)eventInfo;
@end


@interface GXEvent(Scroll)

//scroll offset
@property (nonatomic) CGPoint contentOffset;

@end

@interface GXJsEvent : NSObject

//对应的GXEvent实例
@property (nonatomic, weak) GXEvent *gxEvent;
//js绑定手势类型
@property (nonatomic, assign) GXEventType eventType;
//优先级
@property (nonatomic, assign) GXEventLevel eventLevel;

@end

NS_ASSUME_NONNULL_END
