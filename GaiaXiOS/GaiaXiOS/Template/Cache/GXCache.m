//
//  GXCache.m
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

#import "GXCache.h"
#import "GXLRUCache.h"
#import <pthread/pthread.h>


@interface GXCache (){
    //互斥锁
    pthread_mutex_t _mutex;
}

@property (nonatomic, strong) GXLRUCache *gxCache;

@end

@implementation GXCache

- (instancetype)init{
    if (self = [super init]) {
        //初始化互斥锁
        pthread_mutex_init(&_mutex, NULL);
        _gxCache = [[GXLRUCache alloc] initWithMaxCount:0];
    }
    return self;
}

- (id)initWithCacheCount:(NSUInteger)cacheCount{
    if (self = [super init]) {
        //初始化互斥锁
        pthread_mutex_init(&_mutex, NULL);
        _gxCache = [[GXLRUCache alloc] initWithMaxCount:cacheCount];
    }
    return self;
}


#pragma mark - 基础方法

//缓存数据
- (void)setObject:(id)cacheObject forKey:(NSString *)cacheKey{
    if (cacheObject && cacheKey) {
        pthread_mutex_lock(&_mutex);
        [self.gxCache setObject:cacheObject forKey:cacheKey];
        pthread_mutex_unlock(&_mutex);
    }
}

//移除缓存
- (void)removeObjectForKey:(NSString *)cacheKey{
    pthread_mutex_lock(&_mutex);
    [self.gxCache removeObjectForKey:cacheKey];
    pthread_mutex_unlock(&_mutex);
}

//获取缓存信息
- (id)objectForKey:(NSString *)cacheKey{
    pthread_mutex_lock(&_mutex);
    id object = [self.gxCache objectForKey:cacheKey];
    pthread_mutex_unlock(&_mutex);
    return object;
}

//移除所有缓存
- (void)removeAllObjects{
    pthread_mutex_lock(&_mutex);
    [self.gxCache removeAllObjects];
    pthread_mutex_unlock(&_mutex);
}

//获取所有的key
- (NSArray *)allKeys{
    pthread_mutex_lock(&_mutex);
    NSArray *allkey = [self.gxCache allKeys];
    pthread_mutex_unlock(&_mutex);
    return allkey;
}


#pragma mark - 获取指定类型

//获取数组
- (NSArray *)arrayForKey:(NSString *_Nonnull)cacheKey{
   id object = [self objectForKey:cacheKey];
   if (object && [object isKindOfClass:[NSArray class]]) {
       return (NSArray *)object;
   }
   return nil;
}

//获取字典
- (NSDictionary *)dictionaryForKey:(NSString *_Nonnull)cacheKey{
    id object = [self objectForKey:cacheKey];
    if (object && [object isKindOfClass:[NSDictionary class]]) {
        return (NSDictionary *)object;
    }
    return nil;
}


- (void)dealloc {
    pthread_mutex_destroy(&_mutex);
}

@end
