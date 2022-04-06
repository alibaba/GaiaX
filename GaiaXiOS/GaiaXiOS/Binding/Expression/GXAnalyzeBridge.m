//
//  GXAnalyzeBridge.m
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/18.
//

#import "GXAnalyzeBridge.h"
#import "GXValueParser.h"
#import "GXFunction.h"
#include "GXContext.h"

@implementation GXAnalyzeBridge

+ (instancetype)sharedInstance{
    static GXAnalyzeBridge *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if (nil == instance) {
            instance = [[GXAnalyzeBridge alloc] init];
        }
    });
    return instance;
}

- (long)getFunctionValue:(NSString *)funName paramPointers:(long *)paramPointers paramsSize:(int)paramsSize{
    //获取参数
    NSMutableArray *array = [NSMutableArray array];
    for (int i = 0; i < paramsSize; i++) {
        long pointer = paramPointers[i];
        id value = [GXContext getValueWithAdress:pointer];
        if (value) {
            [array addObject:value];
        }
    }
    
    //方法处理
    id value = [GXFunction function:funName params:array];
    
    //结果转GXValue
    long result = [GXContext getAdressWithValue:value];
    return result;
}

- (long)getSourceValue:(NSString *)valuePath source:(id)source{
    //object-c计算出来的结果
    id value = nil;
    if ([valuePath isEqualToString:@"$$"]) {
        value = source;
    } else {
        GXValueParser *parser = [GXValueParser parserWithExpString:valuePath];
        value = [parser valueWithObject:source];
    }
    
    //转成C++需要值
    long result = [GXContext getAdressWithValue:value];
    return result;
}

- (void)throwError:(NSString *)message{
    NSString *result = [NSString stringWithFormat:@"错误信息：%@", message];
    NSLog(@"%@", result);
    NSAssert(false, result);
}


@end
