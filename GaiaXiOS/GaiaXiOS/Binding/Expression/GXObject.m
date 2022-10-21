//
//  GaiaXValue.m
//  TestExpresssion
//
//  Created by 张敬成 on 2022/3/11.
//

#import "GXObject.h"

@implementation GXObject

@end

@implementation GXBool

+ (NSNumber *)getResultByValue:(BOOL)value{
    return @(value);
}

@end

@implementation GXFloat

+ (NSNumber *)getResultByValue:(CGFloat)value{
    NSDecimalNumber *number = [NSDecimalNumber decimalNumberWithString:[NSString stringWithFormat:@"%f",value]];
    return number;
}

@end
@implementation GXLong

+ (NSNumber *)getResultByValue:(int64_t)value{
    return @(value);
}

@end
@implementation GXStr

+ (NSString *)getResultByValue:(NSString *)value{
    return value;
}

@end

@implementation GXArray

+ (NSArray *)getResultByValue:(void *)value{
    NSArray *array = (__bridge id)value;
    return array;
}

@end

@implementation GXMap

+ (NSDictionary *)getResultByValue:(void *)value{
    NSDictionary *dictionary = (__bridge id)value;
    return dictionary;
}

@end
