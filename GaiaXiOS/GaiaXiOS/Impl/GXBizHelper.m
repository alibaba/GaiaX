//
//  GXBizHelper.m
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

#import "GXBizHelper.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "GXRegisterCenter.h"

#define GX_INVALID_DIM -10086

@implementation GXBizHelper

//加载iconfont
+ (void)loadIconFont{
    static BOOL _hasRegister = NO;
    if (!_hasRegister) {
        _hasRegister = YES;
        Class bizServiceClass = TheGXRegisterCenter.bizServiceImpl;
        if (bizServiceClass && [bizServiceClass respondsToSelector:@selector(loadIconFont)]) {
            [bizServiceClass loadIconFont];
        }
    }
}

@end


@implementation GXBizHelper(DesignToken)

//用于加载间距DesignToken
+ (CGFloat)dimFromDesignToken:(NSString *)token{
    CGFloat dimValue = GX_INVALID_DIM;
    Class bizServiceClass = TheGXRegisterCenter.bizServiceImpl;
    if (bizServiceClass && [bizServiceClass respondsToSelector:@selector(dimFromDesignToken:)]) {
        dimValue = [bizServiceClass dimFromDesignToken:token];
    }
    return dimValue;
}

//用于加载颜色DesignToken
+ (UIColor *)colorFromDesignToken:(NSString *)token{
    UIColor *color = nil;
    Class bizServiceClass = TheGXRegisterCenter.bizServiceImpl;
    if (bizServiceClass && [bizServiceClass respondsToSelector:@selector(colorFromDesignToken:)]) {
        color = [bizServiceClass colorFromDesignToken:token];
    }
    return color;
}

//用于加载字体DesignToken
+ (UIFont *)fontFromDesignToken:(NSString *)token{
    UIFont *font = nil;
    Class bizServiceClass = TheGXRegisterCenter.bizServiceImpl;
    if (bizServiceClass && [bizServiceClass respondsToSelector:@selector(fontFromDesignToken:)]) {
        font = [bizServiceClass fontFromDesignToken:token];
    }
    return font;
}

+ (UIFont *)fontFromDesignToken:(NSString *)token fontWeight:(UIFontWeight)fontWeight{
    UIFont *font = nil;
    Class bizServiceClass = TheGXRegisterCenter.bizServiceImpl;
    if (bizServiceClass && [bizServiceClass respondsToSelector:@selector(fontFromDesignToken:fontWeight:)]) {
        font = [bizServiceClass fontFromDesignToken:token fontWeight:fontWeight];
    }
    return font;
}

@end


@implementation GXBizHelper(ResponsiveLayout)

//响应式屏幕size
+ (CGSize)screenSize{
    CGSize size = CGSizeZero;
    Class bizServiceClass = TheGXRegisterCenter.bizServiceImpl;
    if (bizServiceClass && [bizServiceClass respondsToSelector:@selector(screenSize)]) {
        size = [bizServiceClass screenSize];
    }
    return size;
}

//获取响应式视图的宽度
+ (CGFloat)valueForRule:(NSString *)rule withContainerWidth:(CGFloat)containerWidth gap:(CGFloat)gap margin:(CGFloat)margin{
    CGFloat value = 0.f;
    Class bizServiceClass = TheGXRegisterCenter.bizServiceImpl;
    if (bizServiceClass && [bizServiceClass respondsToSelector:@selector(valueForRule:withContainerWidth:gap:margin:)]) {
        value = [bizServiceClass valueForRule:rule withContainerWidth:containerWidth gap:gap margin:margin];
    }
    return value;

}

@end
