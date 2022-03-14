//
//  GXTemplateLoader.h
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
#import "GXFunctionDef.h"
#import "GXCommonDef.h"

NS_ASSUME_NONNULL_BEGIN

@interface GXTemplateLoader : NSObject

/// 单例
+ (instancetype)defaultLoader;


/// 删除缓存中的模板数据
- (void)removeCacheTemplateForKey:(NSString *)key;

/// 加载缓存中的模板数据
- (NSDictionary *)loadCacheTemplateForKey:(NSString *)key;

/// 缓存模板信息
/// @param templateInfo 模板信息
/// @param key 索引
- (void)cacheTemplate:(NSDictionary *)templateInfo forKey:(NSString *)key;


@end


@interface GXTemplateLoader (Local)

/// 同步加载业务bundle模板
/// @param bizId 业务id
/// @param templateId 模板id
- (NSDictionary * _Nullable)loadTemplateInfoWithBizId:(NSString *)bizId
                                           templateId:(NSString *)templateId;

/// 异步加载业务bundle本地模板
/// @param bizId 业务id
/// @param templateId 模板id
/// @param completion 回调
- (void)loadTemplateInfoWithBizId:(NSString *)bizId
                       templateId:(NSString *)templateId
                       completion:(GXTemplateLoadCompletion)completion;

@end


@interface GXTemplateLoader (Cloud)

/// 根据模板的version倒序获取最新末班
/// @param templateId 模板id
- (NSDictionary *)loadTemplateInfoOrderByTemplateId:(NSString *)templateId;


/// 同步加载沙盒模板（远端）
/// @param templateId 模板Id
/// @param templateVersion 模板Version
- (NSDictionary *_Nullable)loadTemplateInfoWithTemplateId:(NSString *)templateId
                                          templateVersion:(NSString *)templateVersion;


/// 异步加载沙盒模板（远端）
/// @param templateId 模板Id
/// @param templateVersion 模板Version
/// @param completion 回调
- (void)loadTemplateInfoWithTemplateId:(NSString *)templateId
                       templateVersion:(NSString *)templateVersion
                            completion:(GXTemplateLoadCompletion)completion;


@end



NS_ASSUME_NONNULL_END
