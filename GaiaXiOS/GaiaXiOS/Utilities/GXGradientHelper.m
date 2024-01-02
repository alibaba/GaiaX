//
//  GXGradientHelper.m
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

#import "GXGradientHelper.h"
#import "NSDictionary+GX.h"
#import "GXCacheCenter.h"
#import "NSArray+GX.h"
#import "UIColor+GX.h"
#import "GXCache.h"
#import "GXUtils.h"

@interface GXGradientView () {
    BOOL _isDynamic;
}

@property (nonatomic, strong) NSArray *colors;

@end

@implementation GXGradientView

+ (Class)layerClass {
    return [CAGradientLayer class];
}

- (void)traitCollectionDidChange:(UITraitCollection *)previousTraitCollection {
    [super traitCollectionDidChange:previousTraitCollection];
    if (@available(iOS 13.0, *)) {
        // trait发生了改变
        if ([self.traitCollection hasDifferentColorAppearanceComparedToTraitCollection:previousTraitCollection]) {
//            BOOL isDarkMode = self.traitCollection.userInterfaceStyle == UIUserInterfaceStyleDark;
            if (self.colors.count > 0) {
                CAGradientLayer *layer = (CAGradientLayer *)self.layer;
                layer.colors = [self colorRefs];
            }
        }
    }
}

- (void)setupGradientWithStartPoint:(CGPoint)startPoint
                           endPoint:(CGPoint)endPoint
                          locations:(NSArray *)locations
                             colors:(NSArray<UIColor *> *)colors {
    //设置colors
    self.colors = colors;
    
    // 修复添加了渐变色view 手势被拦截的问题
    self.userInteractionEnabled = NO;
    
    //生成渐变
    CAGradientLayer *layer = (CAGradientLayer *)self.layer;
    layer.colors = [self colorRefs];
    layer.startPoint = startPoint;
    layer.endPoint = endPoint;
    
    //locations
    if (locations) {
        layer.locations = locations;
    }
}

- (NSArray *)colorRefs{
    NSMutableArray *colorRefArray = [NSMutableArray array];
    for (int i = 0; i < self.colors.count; i++) {
        NSString *colorStr = [self.colors gx_objectAtIndex:i];
        UIColor *tmpColor = [UIColor gx_colorWithString:colorStr];
        [colorRefArray gx_addObject:(id)tmpColor.CGColor];
    }
    return  colorRefArray;
}

- (void)dealloc{
    
}


@end


@implementation GXGradientHelper

#pragma mark - 渐变图片

+ (UIView *)creatGradientViewWithParams:(NSDictionary *)params bounds:(CGRect)bounds{
    if (![GXUtils isValidDictionary:params]) {
        return nil;
    }
    
    //direction
    NSString *direction = [params gx_stringForKey:@"direction"];
    CGPoint startPoint = CGPointZero;
    CGPoint endPoint = CGPointZero;
    if ([direction isEqualToString:@"toright"]) {
        startPoint = CGPointMake(0.0, 0.0);
        endPoint = CGPointMake(1.0, 0.0);
    } else if ([direction isEqualToString:@"toleft"]) {
        startPoint = CGPointMake(1.0, 0.0);
        endPoint = CGPointMake(0.0, 0.0);
    } else if ([direction isEqualToString:@"tobottom"]) {
        startPoint = CGPointMake(0.0, 0.0);
        endPoint = CGPointMake(0.0, 1.0);
    } else if ([direction isEqualToString:@"totop"]) {
        startPoint = CGPointMake(0.0, 1.0);
        endPoint = CGPointMake(0.0, 0.0);
    } else if ([direction isEqualToString:@"tobottomright"]) {
        startPoint = CGPointMake(0, 0);
        endPoint = CGPointMake(1, 1);
    } else if ([direction isEqualToString:@"tobottomleft"]) {
        startPoint = CGPointMake(1.0, 0.0);
        endPoint = CGPointMake(0.0, 1.0);
    } else if ([direction isEqualToString:@"totopright"]) {
        startPoint = CGPointMake(0.0, 1.0);
        endPoint = CGPointMake(1.0, 0.0);
    } else if ([direction isEqualToString:@"totopleft"]) {
        startPoint = CGPointMake(1.0, 1.0);
        endPoint = CGPointMake(0, 0);
    } else {
        startPoint = CGPointMake(0, 0.0);
        endPoint = CGPointMake(1.0, 0.0);
    }
    
    //colors
    NSArray *colors = [params gx_arrayForKey:@"colors"];
    if (colors == nil || ![colors isKindOfClass:NSArray.class]) {
        return nil;
    }
     
    //locations
    NSArray *locations = [params gx_arrayForKey:@"locations"];

    //创建layer
    GXGradientView *gradientView = [[GXGradientView alloc] initWithFrame:bounds];
    [gradientView setupGradientWithStartPoint:startPoint endPoint:endPoint locations:locations colors:colors];
        
    return gradientView;

}

+ (UIImage *)creatGradientImageWithParams:(NSDictionary *)params bounds:(CGRect)bounds{
    //生成layer
    CAGradientLayer *gradientLayer = [self creatGradientLayerWithParams:params bounds:bounds];
    //绘制图片
    UIImage *gradientImage = [self renderImageFromLayer:gradientLayer];
    return gradientImage;
}


//渐变layer
+ (CAGradientLayer *)creatGradientLayerWithParams:(NSDictionary *)params bounds:(CGRect)bounds{
    if (![GXUtils isValidDictionary:params]) {
        return nil;
    }
    
    //direction
    NSString *direction = [params gx_stringForKey:@"direction"];
    CGPoint startPoint = CGPointZero;
    CGPoint endPoint = CGPointZero;
    if ([direction isEqualToString:@"toright"]) {
        startPoint = CGPointMake(0.0, 0.0);
        endPoint = CGPointMake(1.0, 0.0);
    } else if ([direction isEqualToString:@"toleft"]) {
        startPoint = CGPointMake(1.0, 0.0);
        endPoint = CGPointMake(0.0, 0.0);
    } else if ([direction isEqualToString:@"tobottom"]) {
        startPoint = CGPointMake(0.0, 0.0);
        endPoint = CGPointMake(0.0, 1.0);
    } else if ([direction isEqualToString:@"totop"]) {
        startPoint = CGPointMake(0.0, 1.0);
        endPoint = CGPointMake(0.0, 0.0);
    } else if ([direction isEqualToString:@"tobottomright"]) {
        startPoint = CGPointMake(0, 0);
        endPoint = CGPointMake(1, 1);
    } else if ([direction isEqualToString:@"tobottomleft"]) {
        startPoint = CGPointMake(1.0, 0.0);
        endPoint = CGPointMake(0.0, 1.0);
    } else if ([direction isEqualToString:@"totopright"]) {
        startPoint = CGPointMake(0.0, 1.0);
        endPoint = CGPointMake(1.0, 0.0);
    } else if ([direction isEqualToString:@"totopleft"]) {
        startPoint = CGPointMake(1.0, 1.0);
        endPoint = CGPointMake(0, 0);
    } else {
        startPoint = CGPointMake(0, 0.0);
        endPoint = CGPointMake(1.0, 0.0);
    }
    
    //colors
    NSArray *colors = [params gx_arrayForKey:@"colors"];
    if (colors == nil || ![colors isKindOfClass:NSArray.class]) {
        return nil;
    }
    
    //颜色
    NSMutableArray *colorRefArray = [NSMutableArray array];
    for (int i = 0; i < colors.count; i++) {
        NSString *colorStr = [colors gx_objectAtIndex:i];
        UIColor *tmpColor = [UIColor gx_colorWithString:colorStr];
        [colorRefArray gx_addObject:(id)tmpColor.CGColor];
    }
    
    //创建layer
    CAGradientLayer * gradientLayer = [CAGradientLayer layer];
    gradientLayer.colors = colorRefArray;
    gradientLayer.startPoint = startPoint;
    gradientLayer.endPoint = endPoint;
    gradientLayer.frame = bounds;

    //locations
    NSArray *locations = [params gx_arrayForKey:@"locations"];
    if (locations) {
        gradientLayer.locations = locations;
    }
    
    //异步绘制
    //gradientLayer.drawsAsynchronously = YES;
    
    return gradientLayer;
}

//layer绘制成图片
+ (UIImage *)renderImageFromLayer:(CALayer *)layer{
    if (nil == layer) {
        return nil;
    }
    
    //创建视图
    CGSize size = layer.bounds.size;
    UIGraphicsImageRenderer *render = [[UIGraphicsImageRenderer alloc] initWithSize:size];
    UIImage *image = [render imageWithActions:^(UIGraphicsImageRendererContext * _Nonnull rendererContext) {
        [layer renderInContext:rendererContext.CGContext];
    }];
    
    return image;

}


#pragma mark - 解析渐变色

+ (NSDictionary *)parserLinearGradient:(NSString *)linearGradient{
    NSMutableDictionary *dict = nil;
    // 掐头去尾 
    NSString *linearString = [linearGradient substringWithRange:NSMakeRange(16, [linearGradient length]-17)];
    // 解析内容
    NSRegularExpression *regex = [self linearGradientRegular];
    if (regex) {
        NSArray *matches = [regex matchesInString:linearString options:0 range:NSMakeRange(0, [linearString length])];
        if (matches.count > 0) {
            dict = [NSMutableDictionary dictionaryWithCapacity:2];
            NSMutableArray *locations = [NSMutableArray array];
            NSMutableArray *colors = [NSMutableArray array];
            NSString *direction = @"";

            for (NSTextCheckingResult *match in matches) {
                // direction
                NSRange directionRange = [match rangeAtIndex:1];
                if (directionRange.location != NSNotFound) {
                    direction = [linearString substringWithRange:directionRange];
                    direction = [direction stringByReplacingOccurrencesOfString:@" " withString:@""];
                    continue;
                }

                // colors
                NSRange colorRange = [match rangeAtIndex:2];
                if (colorRange.location != NSNotFound) {
                    NSString *color = [linearString substringWithRange:colorRange];
                    [colors addObject:color];
                }

                // locations
                NSRange locationRange = [match rangeAtIndex:3];
                if (locationRange.location != NSNotFound) {
                    NSString *locationString = [linearString substringWithRange:locationRange];
                    locationString = [locationString stringByReplacingOccurrencesOfString:@"" withString:@"%"];
                    NSNumber *location = @([locationString floatValue] / 100.0f);
                    [locations addObject:location];
                }
            }
            
            // direction
            [dict gx_setValue:direction forKey:@"direction"];

            // location和color一致时才会设置
            if (locations.count && (locations.count == colors.count)) {
                [dict gx_setObject:locations forKey:@"locations"];
            }
            
            // 设置color
            [dict gx_setObject:colors forKey:@"colors"];
        }
        
    }

    return dict;
}

+ (NSRegularExpression *)linearGradientRegular{
    NSString *key = @"linear-gradient";
    GXCache *cahche = [GXCacheCenter defaulCenter].regularCahche;
    NSRegularExpression *regular = [cahche objectForKey:key];
    // 缓存为空创建表达式
    if (regular == nil) {
        // 获取正则的string
        NSError *error = NULL;
        NSString *regexStr = @"(to\\s+\\w+)|(rgba?\\([^)]+\\)|#\\w{3,8})\\s*(\\d+%)*";
        regular = [NSRegularExpression regularExpressionWithPattern:regexStr
                                                            options:NSRegularExpressionCaseInsensitive
                                                              error:&error];
        if (regular) {
            [cahche setObject:regular forKey:key];
        }

    }
    // 返回
    return regular;
}



@end

