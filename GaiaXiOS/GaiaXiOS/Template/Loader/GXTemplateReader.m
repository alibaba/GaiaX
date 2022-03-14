//
//  GXTemplateReader.m
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

#import "GXTemplateReader.h"
#import "GXTemplatePathHelper.h"
#import "GXTemplateParser.h"
#import "GXFileHandler.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "GXUtils.h"

@implementation GXTemplateReader

- (NSDictionary *)readTemplateContenttWithFolderPath:(NSString * _Nonnull)folderPath
                                          templateId:(NSString * _Nonnull)templateId
                                     templateVersion:(NSString * _Nullable)templateVersion {
    //默认值
    NSMutableDictionary *resultDict = nil;
    
    //获取二进制文件路径
    NSString *gaiaXPath = [GXTemplatePathHelper loadBinaryFilePathWithFolderPath:folderPath fileName:templateId fileType:nil];
    
    //判断文件是否存在
    if ([GXFileHandler isDirectoryItemAtPath:gaiaXPath]) {
        //读取明文模板
        resultDict = [self loadTextTemplateWithFolderPath:folderPath templateId:templateId templateVersion:templateVersion];
    } else {
        //获取二进制模板
        resultDict = [self loadBinaryTemplateWithFilePath:gaiaXPath templateId:templateId templateVersion:templateVersion];
    }
        
    return resultDict;
}


//读取二进制模板信息
- (NSMutableDictionary *)loadBinaryTemplateWithFilePath:(NSString *)filePath
                                             templateId:(NSString * _Nonnull)templateId
                                        templateVersion:(NSString * _Nullable)templateVersion{
    // 解析二进制数据文件
    int fileCount = 0;
    File**files = malloc(sizeof(File*)*100);
    const char *strs = [filePath UTF8String];
    parseFile((char *)strs, files, &fileCount);
    NSMutableDictionary *fileJson = [NSMutableDictionary dictionary];
    for (int i = 0; i < fileCount; i++) {
        NSString *fileContent = [NSString stringWithUTF8String:files[i] -> fileContent];
        NSString *fileName = [NSString stringWithUTF8String:files[i] -> fileName];
        if (fileContent.length && fileName.length) {
            [fileJson setValue:fileContent forKey:fileName];
        }
    }
    // 释放内存
    freeFiles(files, fileCount);
    
    //解析模板内容
    NSMutableDictionary *resultDict = nil;
    if (fileJson.count) {
        resultDict = [NSMutableDictionary dictionary];
        
        //默认信息
        NSDictionary *dictionary = nil;
        NSData *data = nil;
        
        //视图层级解析(index.json)
        NSString *indexJson = [fileJson valueForKey:kGXComDef_KW_IndexJson];
        if (indexJson.length) {
            data = [indexJson dataUsingEncoding:NSUTF8StringEncoding];
            if (data) {
                dictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
                resultDict[kGXComDef_KW_VH] = dictionary;
            }
        }
        GXAssert(dictionary != nil, @"[二进制模板] [%@] index.json文件不能为空", templateId);
        
        // CSS样式解析(index.css)
        NSString *indexCss = [fileJson valueForKey:kGXComDef_KW_IndexCSS];
        dictionary = [GXUtils parserStyleString:indexCss];
        resultDict[kGXComDef_KW_SY] = dictionary;
        
        //绑定关系解析(index.databinding)
        NSString *indexDatabinding = [fileJson valueForKey:kGXComDef_KW_IndexDataBinding];
        if (indexDatabinding.length) {
            data = [indexDatabinding dataUsingEncoding:NSUTF8StringEncoding];
            if (data) {
                dictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
                resultDict[kGXComDef_KW_DB] = dictionary;
            }
        }
        
        //js事件解析(index.js)
        NSString *indexJs = [fileJson valueForKey:kGXComDef_KW_IndexJS];
        if (indexJs.length) {
            resultDict[kGXComDef_KW_JS] = indexJs;
        }
        
        //增加version标识
        if (resultDict.count > 0 && templateVersion.length) {
            resultDict[@"version"] = templateVersion;
        }
        
    }
    GXAssert(fileJson.count != 0, @"[二进制模板] [%@] 文件解析失败", templateId);
    
    return resultDict;
}


//读取明文模板信息
- (NSMutableDictionary *)loadTextTemplateWithFolderPath:(NSString * _Nonnull)folderPath
                                             templateId:(NSString * _Nonnull)templateId
                                        templateVersion:(NSString * _Nullable)templateVersion{
    
    NSMutableDictionary *resultDict = [NSMutableDictionary dictionary];
    NSDictionary *dictionary = nil;
    NSData *data = nil;
    
    //视图层级解析(index.json)
    NSString *jsonPath = [GXTemplatePathHelper loadTextFilePathWithFolderPath:folderPath
                                                                   templateId:templateId
                                                                     fileName:kGXComDef_KW_Index
                                                                     fileType:kGXComDef_KW_Json];
    if ([GXFileHandler isFileExistAtPath:jsonPath]) {
        data = [NSData dataWithContentsOfFile:jsonPath];
        if (data) {
            dictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
            resultDict[kGXComDef_KW_VH] = dictionary;
        }
    }
    GXAssert(dictionary != nil, @"[明文模板] [%@] index.json文件不能为空", templateId);
    //index.json为空，直接返回
    if (dictionary == nil) {
        return resultDict;
    }
    
    
    // CSS样式解析(index.css)
    NSString *cssPath = [GXTemplatePathHelper loadTextFilePathWithFolderPath:folderPath
                                                                  templateId:templateId
                                                                    fileName:kGXComDef_KW_Index
                                                                    fileType:kGXComDef_KW_CSS];
    NSString *styleString = [NSString stringWithContentsOfFile:cssPath encoding:NSUTF8StringEncoding error:nil];
    dictionary = [GXUtils parserStyleString:styleString];
    resultDict[kGXComDef_KW_SY] = dictionary;
    
    //绑定关系解析(index.databinding)
    NSString *databindingPath = [GXTemplatePathHelper loadTextFilePathWithFolderPath:folderPath
                                                                          templateId:templateId
                                                                            fileName:kGXComDef_KW_Index
                                                                            fileType:kGXComDef_KW_DataBinding];
    data = [NSData dataWithContentsOfFile:databindingPath];
    if (data) {
        dictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
        resultDict[kGXComDef_KW_DB] = dictionary;
    }
    
    //js事件解析(index.js)
    NSString *jsPath = [GXTemplatePathHelper loadTextFilePathWithFolderPath:folderPath
                                                                 templateId:templateId
                                                                   fileName:kGXComDef_KW_Index
                                                                   fileType:kGXComDef_KW_JS];
    if (jsPath.length) {
        NSString *jsContent = [NSString stringWithContentsOfFile:jsPath encoding:NSUTF8StringEncoding error:nil];
        if (jsContent.length > 0) {
            resultDict[kGXComDef_KW_JS] = jsContent;
        }
    }
    
    //增加version标识
    if (resultDict.count > 0 && templateVersion.length) {
        resultDict[@"version"] = templateVersion;
    }
    
    return resultDict;
}


#pragma mark -

//读取模板信息
- (NSMutableDictionary *)readTemplateInfo:(NSDictionary *)templateInfo
{
    NSString *indexCss = templateInfo[@"index.css"];
    NSString *indexDataStr = templateInfo[@"index.data"];
    NSString *indexJsonStr = templateInfo[@"index.json"];
    NSString *indexDatabindingStr = templateInfo[@"index.databinding"];

    NSMutableDictionary *result = [NSMutableDictionary dictionary];
    //视图层级解析
    NSData *data = [indexJsonStr dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *dictionary = nil;
    if (data) {
        dictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    }
    if (dictionary != nil) {
        result[kGXComDef_KW_VH] = dictionary;
    }
    GXAssert(dictionary != nil, @"[明文模板] index.json文件不能为空");

    // CSS样式解析
    NSMutableDictionary *styleDictionary = [GXUtils parserStyleString:indexCss];
    if (styleDictionary != nil) {
        result[kGXComDef_KW_SY] = styleDictionary;
    }
    
    //获取事件绑定关系
    data = [indexDatabindingStr dataUsingEncoding:NSUTF8StringEncoding];
    if (data) {
        NSDictionary *databindingDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
        if (databindingDict != nil) {
            result[kGXComDef_KW_DB] = databindingDict;
        }
    }
    
    //js事件解析
    NSString *indexJs = [templateInfo valueForKey:kGXComDef_KW_IndexJS];
    if (indexJs.length) {
        result[kGXComDef_KW_JS] = indexJs;
    }
    
    // mock数据
    data = [indexDataStr dataUsingEncoding:NSUTF8StringEncoding];
    if (data) {
        NSDictionary *dataDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
        if (dataDict != nil) {
            result[kGXComDef_KW_DA] = dataDict;
        }
    }
    
    return result;
}


@end
