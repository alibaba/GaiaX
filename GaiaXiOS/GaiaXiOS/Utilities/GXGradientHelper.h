//
//  GXGradientHelper.h
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

NS_ASSUME_NONNULL_BEGIN

@interface GXGradientView : UIView

- (void)setupGradientWithStartPoint:(CGPoint)startPoint
                           endPoint:(CGPoint)endPoint
                          locations:(NSArray *)locations
                             colors:(NSArray<UIColor *> *)colors;

@end


@interface GXGradientHelper : NSObject

/// 生成渐变View
/// @param params 渐变参数
/// @param bounds 对应的bounds
+ (UIView *)creatGradientViewWithParams:(NSDictionary *)params bounds:(CGRect)bounds;

/// 生成渐变layer
/// @param params 渐变参数
/// @param bounds 对应的bounds
+ (CAGradientLayer *)creatGradientLayerWithParams:(NSDictionary *)params bounds:(CGRect)bounds;

/// 生成渐变image
/// @param params 渐变参数
/// @param bounds 对应的bounds
+ (UIImage *)creatGradientImageWithParams:(NSDictionary *)params bounds:(CGRect)bounds;

/// 通过 linear-gradient() 生成params
/// @param linearGradient 渐变的字符串
+ (NSDictionary *)parserLinearGradient:(NSString *)linearGradient;

@end






NS_ASSUME_NONNULL_END
