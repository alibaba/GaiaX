//
//  GXUIHelper.m
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

#import "GXUIHelper.h"
#import "NSDictionary+GX.h"
#import "GXStyleHelper.h"
#import "GXUtils.h"

@implementation GXUIHelper

#pragma mark - font

+ (UIFont *)fontFromStyle:(NSDictionary *)styleJson{
    //设置默认值
    UIFont *font = nil;
    
    //读取属性
    NSString *fontSize = [styleJson gx_stringForKey:@"font-size"] ?: @"14px";
    NSString *fontFamily = [styleJson gx_stringForKey:@"font-family"];
    
    //设置字体
    if (fontFamily.length) {
        //① 使用fontFamily + fontSize来获取字体
        CGFloat size = [GXStyleHelper converSimpletValue:fontSize] ?: 14.f;
        font = [UIFont fontWithName:fontFamily size:size];
    } else {
        //② 处理普通字体和DesignToken
        NSString *fontWeight = [styleJson gx_stringForKey:@"font-weight"];
        font = [self convertFont:fontSize fontWeight:fontWeight];
    }
    
    if (!font) {
        //③ 兜底默认字体
        font = [UIFont systemFontOfSize:14.f];
    }
    
    return font;
}

//从font-size获取字体
+ (UIFont *)convertFont:(NSString *)fontSizeStr{
    return [self convertFont:fontSizeStr fontWeight:nil];
}

//从font-size获取字体 & fontWeight
+ (UIFont *)convertFont:(NSString *)fontSizeStr fontWeight:(NSString *)fontWeight{
    //默认值
    UIFont *font = nil;
    
    //处理字体
    if ([fontSizeStr hasSuffix:@"px"] || [fontSizeStr hasSuffix:@"pt"] || [GXUtils isNumber:fontSizeStr]) {
        //系统字体
        CGFloat size = [GXStyleHelper converSimpletValue:fontSizeStr] ?: 14.f;
        if (fontWeight.length) {
            UIFontWeight weight = [self convertFontWeight:fontWeight];
            font = [UIFont systemFontOfSize:size weight:weight];
        } else {
            font = [UIFont systemFontOfSize:size];
        }
        
    } else {
        //DesignToken
        if (fontWeight.length) {
//            UIFontWeight weight = [self convertFontWeight:fontWeight];
//            font = [GXBizHelper fontFromDesignToken:fontSizeStr fontWeight:weight];
        } else {
//            font = [GXBizHelper fontFromDesignToken:fontSizeStr];
        }
    }
    
    return font;
}


//字重
+ (UIFontWeight)convertFontWeight:(NSString *)fontWeight {
    UIFontWeight fw = UIFontWeightRegular;
    if ([GXUtils isValidString:fontWeight]) {
        NSInteger weight = [fontWeight integerValue];
        switch (weight) {
            case 100:
            case 200:
            case 300:
            case 400:{
                fw = UIFontWeightRegular;
                break;
            }
            case 500:
            case 600:
            case 700:{
                fw = UIFontWeightSemibold;
                break;
            }
            default:{
                fw = UIFontWeightRegular;
                break;
            }
        }
    }
    return fw;
}


#pragma mark - Alignment

+ (NSTextAlignment)convertTextAlignment:(NSString *)textAlign{
    //默认值
    NSTextAlignment textAlignment = NSTextAlignmentLeft;

    //获取textAlgin
    if ([textAlign isEqualToString:@"right"]) {
        textAlignment = NSTextAlignmentRight;
        
    } else if ([textAlign isEqualToString:@"center"]) {
        textAlignment = NSTextAlignmentCenter;
        
    } else if ([textAlign isEqualToString:@"justify"]) {
        textAlignment = NSTextAlignmentJustified;
        
    } else {
        textAlignment = NSTextAlignmentLeft;
    }
    
    return textAlignment;
}


#pragma mark - UIViewContentMode

+ (UIViewContentMode)convertContentMode:(NSString *)mode{
    UIViewContentMode contentMode = UIViewContentModeScaleToFill;
    if (mode.length) {
        if ([mode isEqualToString:@"aspectFill"] || [mode isEqualToString:@"cover"]) {
            contentMode = UIViewContentModeScaleAspectFill;
            
        } else if ([mode isEqualToString:@"aspectFit"] || [mode isEqualToString:@"contain"]) {
            contentMode = UIViewContentModeScaleAspectFit;
            
        } else if ([mode isEqualToString:@"top"]) {
            contentMode = UIViewContentModeTop;
            
        } else if ([mode isEqualToString:@"bottom"]) {
            contentMode = UIViewContentModeBottom;
            
        } else if ([mode isEqualToString:@"center"]) {
            contentMode = UIViewContentModeCenter;
            
        } else if ([mode isEqualToString:@"left"]) {
            contentMode = UIViewContentModeLeft;
            
        } else if ([mode isEqualToString:@"right"]) {
            contentMode = UIViewContentModeRight;
            
        } else if ([mode isEqualToString:@"top left"]) {
            contentMode = UIViewContentModeTopLeft;
            
        } else if ([mode isEqualToString:@"top right"]) {
            contentMode = UIViewContentModeTopRight;
            
        } else if ([mode isEqualToString:@"bottom left"]) {
            contentMode = UIViewContentModeBottomLeft;
            
        } else if ([mode isEqualToString:@"bottom right"]) {
            contentMode = UIViewContentModeBottomRight;
            
        } else {
            //scaleToFill | stretch
            contentMode = UIViewContentModeScaleToFill;
        }
        
    } else {
        //默认属性
        contentMode = UIViewContentModeScaleToFill;
    }
    
    return contentMode;
}


#pragma mark - 屏幕相关

//与375的设备比例
+ (CGFloat)deviceRatio {
    static CGFloat ratio;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        CGFloat width = [self portraitBounds].size.width;
        ratio = MIN(1.3, (width / 375.0f));
    });
    return ratio;
}

//竖屏尺寸（不受当前屏幕转向影响）
+ (CGRect)portraitBounds
{
    static CGRect bounds;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        bounds = [[UIScreen mainScreen] bounds];
        CGFloat width = bounds.size.width;
        CGFloat height = bounds.size.height;
        if (width > height) { //当前是横屏状态
            bounds.size.width = height;
            bounds.size.height = width;
        }
    });
    return bounds;
}


@end
