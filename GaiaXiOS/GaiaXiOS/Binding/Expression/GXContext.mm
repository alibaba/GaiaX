//
//  GXContext.m
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/15.
//

#import "GXContext.h"
#include "GXObject.h"
#include "GXValue.h"
#include "GXAnalyzeCore/GXAnalyze.h"

@implementation GXContext

+ (id)getValueWithAdress:(long)adress{
    id result = nil;
    
    if (adress > 0) {
        //获取value对应的地址
        NSString *hexAdress = [NSString stringWithFormat:@"%lx",adress];
        
        //通过地址获取内容
        GXValue* value;
        sscanf([hexAdress cStringUsingEncoding:NSUTF8StringEncoding], "%p", &value);
        
        //解析结果
        result = [self parserValue:(*value)];

    }
    
    return result;
}

+ (id)parserValue:(GXValue)value{
    //结果
    id result = nil;
    
    int64_t tag = value.tag;
    switch (tag) {
        case GX_TAG_BOOL:{
            BOOL boolValue = GX_VALUE_GET_BOOL(value);
            result = [GXBool getResultByValue:boolValue];
        }
            break;
        case GX_TAG_FLOAT:{
            Float64 floatValue = GX_VALUE_GET_FLOAT64(value);
            result = [GXFloat getResultByValue:floatValue];
        }
            break;
        case GX_TAG_STRING:{
//            const char *cString = GX_ToCString(value);
//            //value log
            NSString *stringValue = [NSString stringWithUTF8String:value.u.str.c_str()];
//            NSString *stringValue= [NSString stringWithCString:value.u.str.c_str() encoding:[NSString defaultCStringEncoding]];
            result = [GXStr getResultByValue:stringValue];
        }
            break;
        case GX_TAG_ARRAY:{
            void *array = GX_VALUE_GET_OBJECT(value);
            result = [GXArray getResultByValue:array];
            value.u.ptr = nullptr;
        }
            break;
        case GX_TAG_MAP:{
            void *map = GX_VALUE_GET_OBJECT(value);
            result = [GXMap getResultByValue:map];
            value.u.ptr = nullptr;
        }
            break;
        default:
            break;
    }
    if(value.hasChanged && value.count != -2){
        GXAnalyze::eraseGXMap(value.count);
    }
    return result;
}


#pragma mark -

+ (long)getAdressWithValue:(id)value{
    
    GXValue val;
    
    if ([value isKindOfClass:[NSNumber class]]) {
        //number类型
        NSNumber *number = (NSNumber *)value;
        if (strcmp([number objCType], @encode(BOOL)) == 0 || strcmp([number objCType], @encode(char)) == 0) {
            //bool类型
            val = GX_NewBool([number boolValue]);
        } else {
            //float类型
            val = GX_NewFloat64([number floatValue]);
        }
        
    } else if ([value isKindOfClass:[NSString class]]){
        //string类型
        NSString *string = (NSString *)value;
        val = GX_NewGXString([string UTF8String]);
        
    } else if ([value isKindOfClass:[NSArray class]]){
        //array类型
        val = GX_NewArray((__bridge void*)value);
        
    } else if ([value isKindOfClass:[NSDictionary class]]){
        //dictionary类型
        val = GX_NewMap((__bridge void *)value);
        
    } else {
        //未知类型，返回null
        val = GX_NewNull(0);
    }
    
    return GXAnalyze::addGXMap(val);
    
    //生成安全返回值
//    GXValue *result  = (GXValue *) malloc(sizeof(val));
//    memcpy(result, &val, sizeof(val));
//
//    return (long)(result);
}

@end
