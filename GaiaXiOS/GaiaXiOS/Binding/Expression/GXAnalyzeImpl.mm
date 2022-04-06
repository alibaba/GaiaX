//
//  GXAnalyzeImpl.cpp
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/18.
//

#include "GXAnalyzeImpl.hpp"
#import "GXAnalyzeBridge.h"
#import "GXContext.h"

void GXAnalyzeImpl:: setup( void ){
    GXAnalyzeBridge *impl = [GXAnalyzeBridge sharedInstance];
    self = (__bridge void *)impl;
}

long GXAnalyzeImpl:: getFunctionValue(string funName, long *paramPointers, int paramsSize, string source){
    //获取func
    NSString *func = [NSString stringWithUTF8String:funName.c_str()];
    //方法响应
    long result = [(__bridge id)self getFunctionValue:func paramPointers:paramPointers paramsSize:paramsSize];
    return result;
}

long GXAnalyzeImpl:: getSourceValue(string valuePath, void* source){
    id dataSoure = (__bridge id)source;
    NSString *path = [NSString stringWithUTF8String:valuePath.c_str()];
    long result = [(__bridge id)self getSourceValue:path source:dataSoure];
    return result;
}

void GXAnalyzeImpl:: throwError(string message){
    NSString *aMessage = [NSString stringWithUTF8String:message.c_str()];
    [(__bridge id)self throwError:aMessage];
}
