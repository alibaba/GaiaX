//
//  GXTemplateParser.m
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

#import "GXTemplatePathHelper.h"
#import "GXFileHandler.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "GXUtils.h"

NS_INLINE NSArray * GXSupportFileType() {
    return @[
        kGXComDef_KW_JS,
        kGXComDef_KW_CSS,
        kGXComDef_KW_Data,
        kGXComDef_KW_Json,
        kGXComDef_KW_DataBinding
    ];
}

@implementation GXTemplatePathHelper

// 获取业务budle下指定模版文件夹路径
+ (NSString *)loadBizBundlePathWithBundleName:(NSString *)bundleName{
    //有效性判断
    if (![GXUtils isValidString:bundleName] || ![bundleName containsString:@".bundle"]){
        return nil;
    }

    //返回bundle路径
    
    
#if DEBUG == 1
    NSString *bizBundlePath = nil;
    if ([bundleName isEqualToString:@"GaiaXiOSTests.bundle"]) {
        NSBundle *bundle = [NSBundle bundleForClass:NSClassFromString(@"GaiaXiOSTests")];
        bizBundlePath = [bundle pathForResource:bundleName ofType:nil];

    } else {
        bizBundlePath = [[NSBundle mainBundle] pathForResource:bundleName ofType:nil];
    }
#else
    NSString *bizBundlePath = [[NSBundle mainBundle] pathForResource:bundleName ofType:nil];
#endif


    return bizBundlePath;
}


// 根据bundle路径获取模板文件（二进制/.gaiax研所模板）
+ (NSString *)loadBinaryFilePathWithFolderPath:(NSString *)folderPath
                                        fileName:(NSString *)fileName
                                        fileType:(NSString *)fileType{
    //有效性判断
    if (![GXUtils isValidString:fileName] ||
        ![GXUtils isValidString:folderPath] ||
        (fileType && ![fileType isEqualToString:kGXComDef_KW_Gaiax])) {
        return nil;
    }
    
    //获取path
    NSString *templateFilePath = nil;
    if (fileType) {
        templateFilePath = [folderPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.%@", fileName,fileType]];
    } else {
        templateFilePath = [folderPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@", fileName]];
    }
    return templateFilePath;
}


// 根据budle路径获取模板文件（明文模板）
+ (NSString *)loadTextFilePathWithFolderPath:(NSString *)folderPath
                                  templateId:(NSString *)templateId
                                    fileName:(NSString *)fileName
                                    fileType:(NSString *)fileType{
    //有效性判断
    if (![GXUtils isValidString:fileName] ||
        ![GXUtils isValidString:fileType] ||
        ![GXUtils isValidString:templateId] ||
        ![GXUtils isValidString:folderPath]) {
        return nil;
    }
    
    //是否支持类型
    if (![GXSupportFileType() containsObject:fileType]) {
        return nil;
    }
    
    //获取path
    NSString *templateFilePath = nil;
    NSString *templateDir = [folderPath stringByAppendingPathComponent:templateId];
    //获取文件路径
    templateFilePath = [templateDir stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.%@", fileName,fileType]];
    return templateFilePath;
}


#pragma mark - path

// 下载路径
// cache/gaiax_template_download/
+ (NSString *)templateResourceDownloadPath{
    NSString *downloadPath = [[GXFileHandler pathForCachesDirectory] stringByAppendingPathComponent:KGXTemplateResourceDownloadStoragePath];
    return downloadPath;
}

// 沙盒模板路径
// document/gaiax_template_center/
+ (NSString *)templateResourceStoragePath{
    NSString *templatePath = [[GXFileHandler pathForDocumentsDirectory] stringByAppendingPathComponent:kGXTemplateResourceStoragePath];
    return templatePath;
}

// 数据库路径
// document/gaiax_template_center/db/
+ (NSString *)dataBaseStoragePath{
    NSString *dataBasePath = [[self templateResourceStoragePath] stringByAppendingPathComponent:@"db"];
    return dataBasePath;
}


@end
