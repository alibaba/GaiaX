//
//  GXNativeAnalyze.m
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/18.
//

#import "GXNativeAnalyze.h"
#include "GXAnalyzeImpl.hpp"
#import "GXContext.h"


@interface GXNativeAnalyze ()

@property (nonatomic, assign) GXAnalyzeImpl analyze;

@end


@implementation GXNativeAnalyze

- (instancetype)init{
    if (self = [super init]) {
         _analyze.setup();
    }
    return self;
}

- (id)valueWithExpression:(NSString *)expression Source:(NSDictionary *)source{
    //调用C++，获取对应value
    long value = _analyze.getValue(expression.UTF8String, (__bridge void *)source);
        
    //解析结果
    id result = [GXContext getValueWithAdress:value];

    //返回值
    return result;
}

@end

