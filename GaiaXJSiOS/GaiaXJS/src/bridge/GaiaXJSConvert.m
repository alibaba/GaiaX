/*
 * Copyright (c) 2022, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

#import "GaiaXJSConvert.h"
#import "GaiaXJSDefines.h"


@implementation GaiaXJSConvert

#define GAIAXJS_JSON_ARRAY_CONVERTER(name)       \
+(NSArray *)name##Array : (id)json               \
{                                                \
  return json;                                   \
}

GAIAXJS_CONVERTER(id, id, self)

GAIAXJS_NUMBER_CONVERTER(double, doubleValue)

GAIAXJS_NUMBER_CONVERTER(float, floatValue)

GAIAXJS_NUMBER_CONVERTER(int, intValue)

GAIAXJS_CUSTOM_CONVERTER(CGFloat, CGFloat, [self double:json])

#define GAIAXJS_JSON_CONVERTER(type) \
  +(type *)type : (id)json       \
  {                              \
    return json;                 \
  }

GAIAXJS_JSON_CONVERTER(NSArray)

GAIAXJS_JSON_CONVERTER(NSDictionary)

GAIAXJS_JSON_CONVERTER(NSString)

GAIAXJS_JSON_CONVERTER(NSNumber)

GAIAXJS_NUMBER_CONVERTER(NSInteger, integerValue)

GAIAXJS_NUMBER_CONVERTER(NSUInteger, unsignedIntegerValue)

GAIAXJS_JSON_ARRAY_CONVERTER(NSNumber)

+ (UIColor *)UIColor:(id)json {
    if (!json) {
        return nil;
    }
    if ([json isKindOfClass:[NSArray class]]) {
        NSArray *components = [self NSNumberArray:json];
        CGFloat alpha = components.count > 3 ? [self CGFloat:components[3]] : 1.0;
        return [UIColor colorWithRed:[self CGFloat:components[0]]
                               green:[self CGFloat:components[1]]
                                blue:[self CGFloat:components[2]]
                               alpha:alpha];
    } else if ([json isKindOfClass:[NSNumber class]]) {
        NSUInteger argb = [self NSUInteger:json];
        CGFloat a = ((argb >> 24) & 0xFF) / 255.0;
        CGFloat r = ((argb >> 16) & 0xFF) / 255.0;
        CGFloat g = ((argb >> 8) & 0xFF) / 255.0;
        CGFloat b = (argb & 0xFF) / 255.0;
        return [UIColor colorWithRed:r green:g blue:b alpha:a];
    } else {
        return nil;
    }
}


@end
