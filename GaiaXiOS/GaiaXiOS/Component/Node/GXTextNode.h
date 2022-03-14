//
//  GXTextNode.h
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

#import "GXViewNode.h"
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXTextNode : GXViewNode

// 字体
@property(nonatomic, strong) UIFont *font;
// 文字颜色
@property(nonatomic, strong) UIColor *textColor;
// 文字行数 - 行数限制, 0代表无限制
@property(nonatomic, assign) NSInteger numberOfLines;
// underlineStyle 删除线 / 下划线
@property (nonatomic, assign) NSString *textDecoration;
// 字符截断类型
@property (nonatomic, assign) NSLineBreakMode lineBreakMode;
// textAlignment
@property (nonatomic, assign) NSTextAlignment textAlignment;

// 文字内边距 - 计算 & 布局
@property (nonatomic, assign) UIEdgeInsets gxPadding;

// 用于业务交互的属性
@property (nonatomic, strong) GXTextData *textData;
// 内容
@property(nonatomic, strong, nullable) NSString *text;
// 富文本动态属性
@property (nonatomic, strong) NSMutableDictionary *attributes;
// 富文本
@property(nonatomic, strong, nullable) NSAttributedString *attributedText;

//更新fit-content
- (void)updateFitContentLayout;

//设置文字渐变色
- (void)setupTextGradientColor:(UILabel *)view;


@end

NS_ASSUME_NONNULL_END
