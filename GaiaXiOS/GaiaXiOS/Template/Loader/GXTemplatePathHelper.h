//
//  GXTemplatePathHelper.h
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

@interface GXTemplatePathHelper : NSObject

/// 获取业务budle下指定模版文件夹路径
/// @param bundleName 业务bungle（GaiaX.bundle）
+ (NSString *)loadBizBundlePathWithBundleName:(NSString *)bundleName;


/// 根据bundle路径获取模板文件（二进制/.gaiax研所模板）
/// @param folderPath 模板路径  folderPath/fileName.fileType
/// @param fileName 文件名称
/// @param fileType 文件类型
+ (NSString *)loadBinaryFilePathWithFolderPath:(NSString *)folderPath
                                      fileName:(NSString *)fileName
                                      fileType:(NSString * _Nullable)fileType;


/// 根据budle路径获取模板文件（明文模板）
/// @param folderPath 模板文件根路径 folderPath/templateId/fileName.fileType
/// @param templateId 模板id
/// @param fileName 文件名称
/// @param fileType 文件类型
+ (NSString *)loadTextFilePathWithFolderPath:(NSString *)folderPath
                                  templateId:(NSString *)templateId
                                    fileName:(NSString *)fileName
                                    fileType:(NSString *)fileType;


/// GaiaX资源下载路径
/// cache/gaiax_template_download/
+ (NSString *)templateResourceDownloadPath;

/// GaiaX资源沙盒模板路径
/// document/gaiax_template_center/
+ (NSString *)templateResourceStoragePath;

/// 数据库路径
/// document/gaiax_template_center/db/
+ (NSString *)dataBaseStoragePath;


@end

NS_ASSUME_NONNULL_END
