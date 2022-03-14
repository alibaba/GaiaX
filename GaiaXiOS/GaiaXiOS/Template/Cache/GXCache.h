//
//  GXCache.h
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

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXCache : NSObject

//初始化
- (instancetype)initWithCacheCount:(NSUInteger)cacheCount;

//缓存数据
- (void)setObject:(id)cacheObject forKey:(NSString *)cacheKey;

//移除缓存
- (void)removeObjectForKey:(NSString *)cacheKey;

//获取缓存信息
- (id)objectForKey:(NSString *)cacheKey;

//移除所有缓存
- (void)removeAllObjects;

//获取所有的key
- (NSArray *)allKeys;


// 获取指定的缓存对象，若为数组类型，会自行类型转换为数组
- (NSArray *)arrayForKey:(NSString *)cacheKey;

//获取指定的缓存对象，若为字典类型，会自行类型转换为字典
- (NSDictionary *)dictionaryForKey:(NSString *)cacheKey;


@end

NS_ASSUME_NONNULL_END
