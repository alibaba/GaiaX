//
//  GXUIHelper.h
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

@interface GXUIHelper : NSObject

//字体设置，完整流程
+ (UIFont *)fontFromStyle:(NSDictionary *)styleJson;

//字体token
+ (UIFont *)convertFont:(NSString *)fontSizeStr;
+ (UIFont *)convertFont:(NSString *)fontSizeStr fontWeight:(NSString * _Nullable)fontWeight;

//UIFontWeight设置
+ (UIFontWeight)convertFontWeight:(NSString *)fontWeight;

//UIViewContentMode设置
+ (UIViewContentMode)convertContentMode:(NSString *)mode;

//NSTextAlignment设置
+ (NSTextAlignment)convertTextAlignment:(NSString *)textAlign;


//屏幕宽度 / 375.f (最大值1.3)
+ (CGFloat)deviceRatio;

//竖屏尺寸（不受当前屏幕转向影响）
+ (CGRect)portraitBounds;

@end

NS_ASSUME_NONNULL_END
