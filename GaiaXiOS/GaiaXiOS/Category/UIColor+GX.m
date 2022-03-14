//
//  UIColor+GX.m
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

#import "UIColor+GX.h"
#import "NSArray+GX.h"
#import "GXFunctionDef.h"

@implementation UIColor (GX)

+ (UIColor *)gx_colorWithString:(NSString *)string {
    if (!string || ![string isKindOfClass:[NSString class]] || !string.length) {
        return [UIColor clearColor];
    }
    
    //颜色类型处理
    UIColor *color = nil;
    string = [string stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    if ([string hasPrefix:@"#"]) {
        //16进制颜色
        color = [UIColor gx_colorWithHexString:string];
        
    } else if ([string hasPrefix:@"rgba"]) {
        //处理原始字符串
        string = [string stringByReplacingOccurrencesOfString:@"rgba(" withString:@""];
        string = [string stringByReplacingOccurrencesOfString:@")" withString:@""];
        
        //处理颜色
        NSArray *colors = [string componentsSeparatedByString:@","];
        CGFloat r = [[colors gx_objectAtIndex:0] floatValue];
        CGFloat g = [[colors gx_objectAtIndex:1] floatValue];
        CGFloat b = [[colors gx_objectAtIndex:2] floatValue];
        CGFloat a = [[colors gx_objectAtIndex:3] floatValue];
        //颜色
        color = GX_RGBA(r, g, b, a);
        
    } else if ([string hasPrefix:@"rgb"]) {
       //处理原始字符串
        string = [string stringByReplacingOccurrencesOfString:@"rgb(" withString:@""];
        string = [string stringByReplacingOccurrencesOfString:@")" withString:@""];
        
        //处理颜色
        NSArray *colors = [string componentsSeparatedByString:@","];
        CGFloat r = [[colors gx_objectAtIndex:0] floatValue];
        CGFloat g = [[colors gx_objectAtIndex:1] floatValue];
        CGFloat b = [[colors gx_objectAtIndex:2] floatValue];
        //颜色
        color = GX_RGB(r, g, b);
        
    } else {
        //系统颜色
        if ([string isEqualToString:@"white"]) {
            color = [UIColor whiteColor];
        } else if ([string isEqualToString:@"black"]) {
            color = [UIColor blackColor];
        } else if ([string isEqualToString:@"gray"]) {
            color = [UIColor grayColor];
        } else if ([string isEqualToString:@"red"]) {
            color = [UIColor redColor];
        } else if ([string isEqualToString:@"green"]) {
            color = [UIColor greenColor];
        } else if ([string isEqualToString:@"yellow"]) {
            color = [UIColor yellowColor];
        } else if ([string isEqualToString:@"blue"]) {
            color = [UIColor blueColor];
        } else if ([string isEqualToString:@"cyan"]) {
            color = [UIColor cyanColor];
        } else if ([string isEqualToString:@"transparent"]) {
            color = [UIColor clearColor];//透明
        } else {
            color = [UIColor clearColor];
        }
        
    }
    
    return color;
}


#pragma mark - 16进制颜色

/// 通过16进制字符串生成颜色, 例如 @"ff0000",  @"ff00ffcc"
+ (UIColor *)gx_colorWithHexString:(NSString *)hexString {
    if (![hexString isKindOfClass:[NSString class]] || [hexString length] == 0) {
        return [UIColor colorWithRed:0.0f green:0.0f blue:0.0f alpha:1.0f];
    }
    
    float red = 0.f;
    float green = 0.f;
    float blue = 0.f;
    float alpha = 0.f;
    
    const char *string = [hexString cStringUsingEncoding:NSASCIIStringEncoding];
    if (*string == '#')  ++string;
    unsigned long long value = strtoll(string, nil, 16);
        
    switch (strlen(string)) {
        case 6:{
            // RRGGBB
            red = (value & 0xff0000) >> 16;
            green = (value & 0x00ff00) >>  8;
            blue = (value & 0x0000ff) >>  0;
            alpha = 255;
        }
            break;
        case 8:{
            // RRGGBBAA
            red = (value & 0xff000000) >> 24;
            green = (value & 0x00ff0000) >> 16;
            blue = (value & 0x0000ff00) >>  8;
            alpha = (value & 0x000000ff) >>  0;
        }
            break;
        default:
            red = 0.f;
            green = 0.f;
            blue = 0.f;
            alpha = 1.f;
            break;
    }
    
    return [UIColor colorWithRed:red/255.0f
                           green:green/255.0f
                            blue:blue/255.0f
                           alpha:alpha/255.0f];
}


@end
