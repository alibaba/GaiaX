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

+ (instancetype)sharedInstance{
    static GXNativeAnalyze *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if (nil == instance) {
            instance = [[GXNativeAnalyze alloc] init];
        }
    });
    return instance;
}

- (instancetype)init{
    if (self = [super init]) {
         _analyze.setup();
    }
    return self;
}

- (id)valueWithExpression:(id)expression Source:(NSDictionary *)source{
    //默认值
    id result = nil;
    
    //表达式类型拦截
    if ([expression isKindOfClass:[NSString class]]) {
        //调用C++，获取对应value
        NSString *realExp = (NSString *)expression;
        long value = _analyze.getValue(realExp.UTF8String, (__bridge void *)source);
        //解析结果
        result = [GXContext getValueWithAdress:value];
        
    } else if ([expression isKindOfClass:[NSNumber class]]){
        //number类型直接返回
        result = expression;
    }

    //返回值
    return result;
}

@end

