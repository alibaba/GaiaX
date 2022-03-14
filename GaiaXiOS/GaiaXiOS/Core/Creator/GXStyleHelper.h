//
//  GXStyleHelper.h
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
#import "GXConstants.h"
#import "GXStyle.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, GXValueType) {
    GXValueTypeDefault = 0,
    GXValueTypeToken
};


@interface GXStyleHelper : NSObject

//赋值
+ (void)configStyleModel:(GXStyleModel *)styleModel style:(NSDictionary *)styleJSON;

//获取Margin信息
+ (StretchStyleRect)convertMargin:(NSDictionary *)styleJSON;

//获取Padding信息
+ (StretchStyleRect)convertPadding:(NSDictionary *)styleJSON;

//获取Size信息
+ (StretchStyleSize)convertSize:(NSDictionary *)styleJSON;

//获取绝对布局信息
+ (StretchStyleRect)convertPosition:(NSDictionary *)styleJSON;

//获取布局类型
+ (PositionType)convertPositionType:(NSString *)positionType;

//获取FlexDirection
+ (FlexDirection)convertFlexDirection:(NSString *)direction;

//绝对布局方向
+ (Direction)convertDirection:(NSString *)direction;

//设置属性
+ (Display)convertDisplay:(NSString *)display;

//获取FlexWrap
+ (FlexWrap)convertFlexWrap:(NSString *)flexWrap;

//获取AlignSelf
+ (AlignSelf)convertAlignSelf:(NSString *)alignSelf;

//获取AlignItem
+ (AlignItems)convertAlignItems:(NSString *)alignItems;

//获取AlignContent
+ (AlignContent)convertAlignContent:(NSString *)alignContent;

//获取JustifyContent
+ (JustifyContent)convertJustifyContent:(NSString *)justifyContent;


@end



@interface GXStyleHelper (Value)

//获取value, 默认值为0
//支持范围：px / pt / DesignToken / 纯数字
+ (CGFloat)converSimpletValue:(NSString *)pxValue;

//获取value，默认值为undefined
//支持范围：px / pt / % / auto / DesignToken,
+ (StretchStyleDimension)convertValue:(NSString *)pxValue;

//获取value，默认值为auto
//支持范围：px / pt / % / auto / DesignToken
+ (StretchStyleDimension)convertAutoValue:(NSString *)pxValue;


@end

NS_ASSUME_NONNULL_END
