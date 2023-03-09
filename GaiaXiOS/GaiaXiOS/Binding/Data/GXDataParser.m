//
//  GXDataParser.m
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

#import "GXDataParser.h"
#import "NSDictionary+GX.h"
#import "GXCacheCenter.h"
#import "GXExpression.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "NSArray+GX.h"
#import "GXUtils.h"


@implementation GXDataParser

//解析数据
+(NSMutableDictionary *)parseData:(NSDictionary *)data withSource:(NSDictionary *)sourceDict{
    if ([data isKindOfClass:[NSDictionary class]]) {
        //如果为dictionary类型
        NSDictionary *dbDict = data;
        NSMutableDictionary *resultDict = [self gx_handleDB:dbDict withData:sourceDict];
        return resultDict;
    } else {
        GXLog(@"[GaiaX] 数据转化异常：数据格式不支持 - %@",data);
    }
    
    return nil;
}

//如果databinding为字典类型
+ (NSMutableDictionary *)gx_handleDB:(NSDictionary *)dbDict withData:(NSDictionary *)dataDict{
    //生成数据
    NSMutableDictionary *resultDict = [NSMutableDictionary dictionary];
    //遍历数据
    [dbDict enumerateKeysAndObjectsUsingBlock:^(id  _Nonnull key, id  _Nonnull obj, BOOL * _Nonnull stop) {
        //处理value
        if ([obj isKindOfClass:[NSString class]]) {
            //如果object为字符串类型
            NSString *valueStr = (NSString *)obj;
            id result = [self gx_handleExp:valueStr withData:dataDict];
            [resultDict gx_setObject:result forKey:key];
            
        } else if ([obj isKindOfClass:[NSDictionary class]]){
            //如果object为字典类型
            NSDictionary *valueDict = (NSDictionary *)obj;
            NSMutableDictionary *result = [self gx_handleDB:valueDict withData:dataDict];
            [resultDict gx_setObject:result forKey:key];
            
        } else if ([obj isKindOfClass:[NSNumber class]]){
            //如果object为number类型
            NSNumber *valueNum = (NSNumber *)obj;
            [resultDict gx_setObject:valueNum forKey:key];
            
        } else if ([obj isKindOfClass:[NSArray class]]) {
            //如果object类型为数组类型
            NSArray *valueArray = (NSArray *)obj;
            if (valueArray.count) {
                NSMutableArray *resultArray = [NSMutableArray array];
                for (int i = 0; i < valueArray.count; i++) {
                    // 获取值
                    id value = [valueArray gx_objectAtIndex:i];
                    if ([value isKindOfClass:[NSString class]]) {
                        //字符串类型
                        id result = [self gx_handleExp:value withData:dataDict];
                        [resultArray gx_addObject:result];
                        
                    } else if ([value isKindOfClass:[NSDictionary class]]){
                        //字典类型
                        NSDictionary *valueDict = (NSDictionary *)value;
                        NSMutableDictionary *result = [self gx_handleDB:valueDict withData:dataDict];
                        [resultArray gx_addObject:result];
                        
                    } else if ([value isKindOfClass:[NSNumber class]]){
                        //如果object为number类型
                        [resultArray gx_addObject:value];

                    }
                    
                }
                
                [resultDict gx_setObject:resultArray forKey:key];
            }
            
        }
        
    }];
    
    return resultDict;
}


//如果databinding为string，处理表达式
+ (id)gx_handleExp:(NSString *)exp withData:(NSDictionary *)dataDict{
    id value = [GXExpression valueWithExpression:exp Source:dataDict];
    return value;
}

@end
