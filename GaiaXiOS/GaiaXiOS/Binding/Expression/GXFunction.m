//
//  GaiaFunctionParser.m
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/16.
//

#import "GXFunction.h"
#import "GXRegisterCenter.h"

@implementation GXFunction

+ (id)function:(NSString *)func params:(NSArray *)params{
    
    //方法处理
    id value = nil;
    
    // 处理函数扩展逻辑
    id functionExpression= TheGXRegisterCenter.functionExpression;
    if (functionExpression) {
        value = [functionExpression execute:func params:params];
        
        if (value != nil) {
            return value;
        }
    }
    
    if ([func isEqualToString:@"size"]) {
        value = [self size:params];
        
    } else if ([func isEqualToString:@"env"]){
        value = [self env:params];
    }
    
    return value;
}


+ (NSNumber *)size:(NSArray *)value{
    NSInteger count = 0;
    
    //解析内容
    if (value.count > 0) {
        id result = value[0];
        if (result) {
            if ([result isKindOfClass:[NSString class]]) {
                //string
                count = [(NSString *)result length];
                
            } else if ([result isKindOfClass:[NSArray class]]){
                //array
                count = [(NSArray *)result count];
                
            } else if ([result isKindOfClass:[NSDictionary class]]){
                //dictionary
                count = [(NSDictionary *)result count];
            }
        }
    }
    
    return @(count);
}


+ (id)env:(NSArray *)value{
    //解析内容
    if (value.count > 0) {
        id result = value[0];
        if (result) {
            if ([result isKindOfClass:[NSString class]] && [[result lowercaseString] isEqualToString:@"isios"] ) {
                //string
                return @(YES);
            }
        }
    }
    return @(NO);
}

@end
