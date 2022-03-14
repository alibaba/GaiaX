//
//  GXStyle.h
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
#import <UIKit/UIKit.h>
#import "GXConstants.h"

NS_ASSUME_NONNULL_BEGIN

@class GXStyleModel;

@interface GXStyle : NSObject

//rust的布局信息
@property(nonatomic, assign) void *rustptr;
//rust的临时布局信息
@property(nonatomic, assign) void *prevRustptr;
//样式字典
@property(nonatomic, strong) NSDictionary *styleInfo;
//转化的css布局信息
@property(nonatomic, strong) GXStyleModel *styleModel;

//赋值model
- (void)setupStyleInfo:(NSDictionary *)styleInfo;

//更新当前的rust指针
- (void)updateRustPtr;

//释放上次的rust指针
- (void)freePrevRustptr;


@end


@interface GXStyleModel : NSObject

//是否展示
@property(nonatomic, assign) Display display;
//布局方向
@property(nonatomic, assign) FlexWrap flexWrap;
@property(nonatomic, assign) Overflow overflow;
@property(nonatomic, assign) AlignSelf alignSelf;
@property(nonatomic, assign) AlignItems alignItems;
@property(nonatomic, assign) StretchStyleRect border;
@property(nonatomic, assign) AlignContent alignContent;
@property(nonatomic, assign) PositionType positionType;
@property(nonatomic, assign) FlexDirection flexDirection;
@property(nonatomic, assign) JustifyContent justifyContent;
//位置信息
@property(nonatomic, assign) Direction direction;//绝对布局方向
@property(nonatomic, assign) StretchStyleRect position;
@property(nonatomic, assign) StretchStyleRect margin;
@property(nonatomic, assign) StretchStyleRect padding;
//size属性
@property(nonatomic, assign) StretchStyleSize size;
@property(nonatomic, assign) StretchStyleSize minSize;
@property(nonatomic, assign) StretchStyleSize maxSize;
@property(nonatomic, assign) StretchStyleSize defaultSize;
//flex属性
@property(nonatomic, assign) CGFloat flexGrow;
@property(nonatomic, assign) CGFloat flexShrink;
@property(nonatomic, assign) StretchStyleDimension flexBasis;
//比例属性
@property(nonatomic, assign) CGFloat aspectRatio;
//文字的fit-content属性
@property(nonatomic, assign) BOOL fitContent;

//记录原始的flexGrow
@property(nonatomic, assign) CGFloat recordFlexGrow;//方便还原的flex-grow
//记录原始的size
@property(nonatomic, assign) StretchStyleSize recordSize;//方便还原的size
//记录原始minSize
@property(nonatomic, assign) StretchStyleSize recordMinSize;//方便还原的minSize


//生成默认StyleModel
+ (GXStyleModel *)defaultStyleModel;

@end


NS_ASSUME_NONNULL_END
