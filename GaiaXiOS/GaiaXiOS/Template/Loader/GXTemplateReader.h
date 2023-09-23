//
//  GXTemplateReader.h
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

@interface GXTemplateReader : NSObject


/// 读取模板信息
/// @param folderPath 模板目录
/// @param templateId 模板id
/// @param templateVersion 模板version
- (NSMutableDictionary *)readTemplateContenttWithFolderPath:(NSString * _Nonnull)folderPath
                                                 templateId:(NSString * _Nonnull)templateId
                                            templateVersion:(NSString * _Nullable)templateVersion;


/// 读取二进制模板信息
/// @param filePath  二进制文件路径
/// @param templateId 模板id
/// @param templateVersion 模板version
- (NSMutableDictionary *)loadBinaryTemplateWithFilePath:(NSString *)filePath
                                             templateId:(NSString * _Nonnull)templateId
                                        templateVersion:(NSString * _Nullable)templateVersion;

/// 读取模板信息
/// @param templateInfo 模板具体内容
- (NSMutableDictionary *)readTemplateInfo:(NSDictionary *)templateInfo;


@end

NS_ASSUME_NONNULL_END
