//
//  GXFileHandler.h
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

@interface GXFileHandler : NSObject

//Caches目录
+ (NSString *)pathForCachesDirectory;
+ (NSString *)pathForCachesDirectoryWithPath:(NSString *)path;

//Library目录
+ (NSString *)pathForLibraryDirectory;
+ (NSString *)pathForLibraryDirectoryWithPath:(NSString *)path;

//Document目录
+ (NSString *)pathForDocumentsDirectory;
+ (NSString *)pathForDocumentsDirectoryWithPath:(NSString *)path;

//Application目录
+ (NSString *)pathForApplicationSupportDirectory;
+ (NSString *)pathForApplicationSupportDirectoryWithPath:(NSString *)path;


@end

@interface GXFileHandler (extension)

//文件是否存在
+ (BOOL)isFileExistAtPath:(NSString *)path;

//是否为文件夹
+ (BOOL)isDirectoryItemAtPath:(NSString *)path;

//文件创建时间
+ (NSString *)creatTimeForItemAtPath:(NSString *)path;

@end

NS_ASSUME_NONNULL_END
