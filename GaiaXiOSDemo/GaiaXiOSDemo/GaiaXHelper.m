//
//  GaiaXHelper.m
//  GaiaXiOSDemo
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#import "GaiaXHelper.h"

@implementation GaiaXHelper

+ (NSString *)bizId{
    return @"GaiaXDemo";
}

//功能列表
+ (NSArray *)loadGaiaXFounctionList{
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"FunctionList" ofType:@"plist"];
    NSArray *array = [NSArray arrayWithContentsOfFile:filePath];
    return array;
}

//读取json文件信息
+ (NSDictionary *)jsonWithFileName:(NSString *)name{
    NSDictionary *dataDict = nil;
    NSString *filePath = [[NSBundle mainBundle] pathForResource:name ofType:@"json"];
    NSData *jsonData = [NSData dataWithContentsOfFile:filePath];
    if (jsonData) {
        dataDict = [NSJSONSerialization JSONObjectWithData:jsonData
                                                   options:NSJSONReadingMutableContainers
                                                     error:nil];
    }
    return dataDict;
}

+ (BOOL)isNetworkReachable{
    return YES;
}

+ (BOOL)isValidDictionary:(NSDictionary *)dict{
    if (dict && [dict isKindOfClass:[NSDictionary class]] && dict.count > 0){
        return YES;
    } else {
        return NO;
    }
}

- (NSDictionary *)dictionaryFromJSONString:(NSString *)jsonString {
    if (jsonString == nil || ![jsonString isKindOfClass:[NSString class]] || jsonString.length == 0) {
        return nil;
    }
    
    NSError *error = nil;
    NSData *data = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
    if (error) {
        return nil;
    }
    return dict;
}

+ (NSString *)stringFromDictionary:(NSDictionary *)dict{
    NSString *jsonString = @"";
    if (!dict || ![dict isKindOfClass:[NSDictionary class]] || [dict count] == 0) {
        return jsonString;
    }
    
    //转化
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&error];
    if (jsonData) {
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    return jsonString;
}

+ (NSDictionary * _Nullable)dictionaryFromJSONString:(NSString *)jsonString{
    NSDictionary *dic = nil;
    if (jsonString && [jsonString isKindOfClass:[NSString class]] && jsonString.length) {
        //获取data
        NSError *error;
        NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
        if (jsonData && [jsonData isKindOfClass:[NSData class]]) {
            NSDictionary *dictionary = [NSJSONSerialization JSONObjectWithData:jsonData
                                                                       options:NSJSONReadingMutableContainers
                                                                         error:&error];
            //数据转化成功
            if (dictionary && [dictionary isKindOfClass:[NSDictionary class]]) {
                dic = dictionary;
            }
        }
    }
    return dic;
}

+ (NSString *)URLEncodedString:(NSString *)str{
    return  [str stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
}

+ (NSString *)URLDecodedString:(NSString *)str{
    return [str stringByRemovingPercentEncoding];
}

+ (NSDictionary *)parameterFromUrl:(NSString *)url {
    if (url && [url isKindOfClass:[NSString class]]) {
        //传入url创建url组件类
        NSURLComponents *urlComponents = [[NSURLComponents alloc] initWithString:url];
        //回调遍历所有参数，添加入字典
        NSMutableDictionary *params = nil;
        if (urlComponents.queryItems.count) {
            //创建dictionary
            params = [NSMutableDictionary dictionaryWithCapacity:2];
            //遍历模板信息
            [urlComponents.queryItems enumerateObjectsUsingBlock:^(NSURLQueryItem * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                [params setObject:obj.value forKey:obj.name];
            }];
        }
        return params;
    }
    return nil;
}

@end
