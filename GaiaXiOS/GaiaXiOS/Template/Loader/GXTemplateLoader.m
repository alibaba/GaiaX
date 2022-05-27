//
//  GXTemplateLoader.m
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

#import "GXTemplateLoader.h"
#import "GXTemplatePathHelper.h"
#import "GXRegisterCenter.h"
#import "GXTemplateReader.h"
#import "NSDictionary+GX.h"
#import "GXCacheCenter.h"
#import "GXUtils.h"

#define kGXTemplateLoaderAsyncQueue "com.gaiax.loader.queue"

@interface GXTemplateLoader (){
    //线程队列
    dispatch_queue_t _loaderAsyncQueue;
}

//模板缓存
@property (nonatomic, strong) GXCache *templateCache;
//模板读取器
@property (nonatomic, strong) GXTemplateReader *templateReader;;

@end

@implementation GXTemplateLoader

+ (instancetype)defaultLoader{
    static dispatch_once_t onceToken;
    static GXTemplateLoader *loader = nil;
    dispatch_once(&onceToken, ^{
        loader = [[GXTemplateLoader alloc] init];
    });
    return loader;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        _loaderAsyncQueue = dispatch_queue_create(kGXTemplateLoaderAsyncQueue, DISPATCH_QUEUE_CONCURRENT);
    }
    return self;
}


#pragma mark - lazy load
//模板缓存
- (GXCache *)templateCache{
    if (!_templateCache) {
        _templateCache = [GXCacheCenter defaulCenter].templateCahche;
    }
    return _templateCache;
}

//模板读取器
- (GXTemplateReader *)templateReader{
    if (!_templateReader) {
        _templateReader = [[GXTemplateReader alloc] init];
    }
    return _templateReader;
}


#pragma mark - cache
// 移除模板
- (void)removeCacheTemplateForKey:(NSString *)key{
    if ([GXUtils isValidString:key]) {
        [self.templateCache removeObjectForKey:key];
    }
}

// 读取缓存模板
- (NSDictionary *)loadCacheTemplateForKey:(NSString *)key{
    if ([GXUtils isValidString:key]) {
        NSDictionary *templateDict = [self.templateCache dictionaryForKey:key];
        return templateDict;
    }
    return nil;
}

// 写缓存模板
- (void)cacheTemplate:(NSDictionary *)templateDict forKey:(NSString *)key{
    if ([GXUtils isValidString:key] && [GXUtils isDictionary:templateDict]) {
        [self.templateCache setObject:templateDict forKey:key];
    }
}

@end


@implementation GXTemplateLoader (Local)

/// 同步加载业务bundle模板
- (NSDictionary * _Nullable)loadTemplateInfoWithBizId:(NSString *)bizId
                                           templateId:(NSString *)templateId{
    //有效性判断
    if (![GXUtils isValidString:bizId] || ![GXUtils isValidString:templateId]) {
        return nil;
    }
        
    //优先读取缓存，返回模板信息
    NSString *cacheKey = [NSString stringWithFormat:@"%@#%@", bizId, templateId];
    NSDictionary *cacheResult = [self loadCacheTemplateForKey:cacheKey];
    if (cacheResult) {
        return cacheResult;
    }
    
    //通过bizId获取业务的bundle名称
    NSString *bizBundlePath = [TheGXRegisterCenter loadTemplateBundlePathForBizId:bizId];
    if (![GXUtils isValidString:bizBundlePath]) {
        [self cacheTemplate:@{} forKey:cacheKey];
        return nil;
    }
    
    //读取路径 & 加载模板
    NSDictionary *resultDict = [self.templateReader readTemplateContenttWithFolderPath:bizBundlePath templateId:templateId templateVersion:nil];
    
    //写入缓存
    [self cacheTemplate:(resultDict ?: @{}) forKey:cacheKey];
        
    return resultDict;
}

/// 异步加载业务bundle本地模板
- (void)loadTemplateInfoWithBizId:(NSString * _Nonnull)bizId
                       templateId:(NSString *_Nonnull)templateId
                       completion:(GXTemplateLoadCompletion)completion{
    //判断
    if (completion == nil) {
        return;
    }
    
    //获取模板信息
    GXWeakSelf(self)
    dispatch_async(_loaderAsyncQueue, ^{
        GXStrongSelf(self)
        if (self && completion) {
            NSDictionary *templateDict = [self loadTemplateInfoWithBizId:bizId templateId:templateId];
            completion(templateDict);
        }
    });

}

@end
