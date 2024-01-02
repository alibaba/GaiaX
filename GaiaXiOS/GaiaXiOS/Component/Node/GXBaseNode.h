//
//  GXBaseNode.h
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

#import "GXNode.h"
#import <UIKit/UIKit.h>
#import "GXTemplateContext.h"

NS_ASSUME_NONNULL_BEGIN

@interface GXBaseNode : GXNode

// 透明度, 默认1
@property (nonatomic) CGFloat opacity;
// 边框宽度, 默认0
@property (nonatomic) CGFloat borderWidth;
// 圆角, 默认0，最大不超过50%
@property (nonatomic) CGFloat cornerRadius;
// 百分比圆角，最大不超过50%
@property (nonatomic) CGFloat percentCornerRadius;
// 超出视图截断，默认为YES
@property (nonatomic, assign) BOOL clipsToBounds;
// 边框颜色, 默认nil
@property (nonatomic, strong) UIColor *borderColor;
// 背景颜色, 默认nil
@property (nonatomic, strong) UIColor *backgroundColor;

// 是否支持渐变背景
@property (nonatomic) BOOL isSupportGradientBgColor;
// 渐变背景, 优先级高于backgroundColor
@property (nonatomic, strong, nullable) NSString *linearGradient;

// 是否阴影
@property (nonatomic) BOOL isSupportShadow;
//阴影的属性
@property (nonatomic, strong, nullable) NSString *boxShadow;

//毛玻璃效果
@property (nonatomic, strong) NSString *backdropFilter;


//更新样式属性，不影响布局
- (void)updateNormalStyle:(NSDictionary *)styleInfo isMark:(BOOL)isMark;

//更新布局属性，影响布局
- (BOOL)updateLayoutStyle:(NSDictionary *)styleInfo;


//无障碍支持
- (void)setupAccessibilityInfo:(NSDictionary *)info;

//设置背景颜色/渐变色（view，image）
- (void)setupGradientBackground:(UIView *)view;
- (void)setupNormalBackground:(UIView *)view;

//设置圆角
- (void)setupCornerRadius:(UIView *)view;

//设置阴影
- (void)setupShadow:(UIView *)view;

// 设置毛玻璃
- (void)setupBlur:(UIView *)view;


@end

NS_ASSUME_NONNULL_END
