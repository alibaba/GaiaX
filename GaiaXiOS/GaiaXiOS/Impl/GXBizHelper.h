//
//  GXBizHelper.h
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

@interface GXBizHelper : NSObject

//加载iconfont
+ (void)loadIconFont;

@end


@interface GXBizHelper (DesignToken)

//用于加载间距DesignToken
+ (CGFloat)dimFromDesignToken:(NSString *)token;

//用于加载颜色DesignToken
+ (UIColor *)colorFromDesignToken:(NSString *)token;

//用于加载字体DesignToken
+ (UIFont *)fontFromDesignToken:(NSString *)token;
+ (UIFont *)fontFromDesignToken:(NSString *)token fontWeight:(UIFontWeight)fontWeight;

@end


@interface GXBizHelper (ResponsiveLayout)

//响应式屏幕size
+ (CGSize)screenSize;

//获取响应式视图的宽度
+ (CGFloat)valueForRule:(NSString *)rule withContainerWidth:(CGFloat)containerWidth gap:(CGFloat)gap margin:(CGFloat)margin;

@end


NS_ASSUME_NONNULL_END
