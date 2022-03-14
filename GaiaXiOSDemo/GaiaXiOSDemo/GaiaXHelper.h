//
//  GaiaXHelper.h
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

#import <Foundation/Foundation.h>

#define GaiaXWeakSelf(type)    __weak typeof(type) weak##type = type;
#define GaiaXStrongSelf(type)  __strong typeof(type) type = weak##type;

#define dispatch_main_async_safe(block)\
if ([NSThread isMainThread]) {\
block();\
} else {\
dispatch_async(dispatch_get_main_queue(), block);\
}

NS_ASSUME_NONNULL_BEGIN

@interface GaiaXHelper : NSObject

//业务id
+ (NSString *)bizId;

//读取功能列表
+ (NSArray *)loadGaiaXFounctionList;

//读取json文件
+ (NSDictionary *)jsonWithFileName:(NSString *)name;

//判断网络是否链接
+ (BOOL)isNetworkReachable;

+ (BOOL)isValidDictionary:(NSDictionary *)dict;

+ (NSString *)stringFromDictionary:(NSDictionary *)dict;

+ (NSDictionary * _Nullable)dictionaryFromJSONString:(NSString *)jsonString;


+ (NSString *)URLEncodedString:(NSString *)str;

+ (NSString *)URLDecodedString:(NSString *)str;

+ (NSDictionary *)parameterFromUrl:(NSString *)url;

@end

NS_ASSUME_NONNULL_END
