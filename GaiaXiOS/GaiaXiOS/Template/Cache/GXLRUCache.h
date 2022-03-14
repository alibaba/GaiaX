//
//  GXLRUCache.h
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

@interface GXLRUNode : NSObject

@property (nonatomic, strong) id value;
@property (nonatomic, copy) NSString *key;
@property (nonatomic, weak, nullable) GXLRUNode *prev;
@property (nonatomic, strong, nullable) GXLRUNode *next;

- (instancetype)initWithKey:(NSString *)key Value:(id)value;

@end


@interface GXLRUCache : NSObject

//最大容量，默认100
@property (nonatomic, readonly) NSInteger maxCount;
//当前缓存的数量
@property (nonatomic, readonly) NSUInteger count;

//初始化方法
- (instancetype)initWithMaxCount:(NSInteger)maxCount;

//添加缓存数据
- (void)setObject:(id)value forKey:(NSString *)key;

//移除缓存数据
- (void)removeObjectForKey:(NSString *)key;

//获取缓存数据
- (id)objectForKey:(NSString *)key;

//移除所有数据
- (void)removeAllObjects;

//获取所有的key
- (NSArray *)allKeys;

@end

NS_ASSUME_NONNULL_END
