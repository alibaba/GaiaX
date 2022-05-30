//
//  GXTemplateManager.m
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

#import "GXTemplateManager.h"
#import "GXRegisterCenter.h"
#import "GXTemplateLoader.h"
#import "NSDictionary+GX.h"
#import "GXTemplateItem.h"
#import "GXCacheCenter.h"
#import "GXUtils.h"

@implementation GXTemplateManager

//读取模板文件信息
+ (NSDictionary * _Nullable)loadTemplateContentWithTemplateItem:(GXTemplateItem *)templateItem{
    //基础信息
    BOOL isLocal = templateItem.isLocal;
    //读取模板信息
    NSDictionary *resultDict = nil;
    if (isLocal) {
        //只读取本地模板
        resultDict = [TheGXRegisterCenter.defaultTemplateSource getTemplateInfoWithTemplateItem:templateItem];
    } else {
        //读取预览，远端，本地，以及推送等模板
        resultDict = [self gx_loadTemplateContentWithTemplateItem:templateItem];
    }
    
    return resultDict;
}


//读取模板中的某个文件信息
+ (NSDictionary * _Nullable)loadTemplateContentWithTemplateItem:(GXTemplateItem *)templateItem
                                               templateFileType:(GXTemplateFileType)type{
    NSDictionary *resultDict = nil;
    //处理模板信息
    NSDictionary *templateDict = [self loadTemplateContentWithTemplateItem:templateItem];
    if ([GXUtils isValidDictionary:templateDict]) {
        switch (type) {
            case GXTemplateFileTypeDataBinding:{
                //获取databinding数据 db
                resultDict = [templateDict gx_dictionaryForKey:@"db"];
            }
                break;
            case GXTemplateFileTypeHierarchy:{
                //获取层级数据 vh
                resultDict = [templateDict gx_dictionaryForKey:@"vh"];
            }
                break;
            case GXTemplateFileTypeCSS:{
                //获取css数据 sy
                resultDict = [templateDict gx_dictionaryForKey:@"sy"];
            }
                break;

            default:
                break;
        }
    }
    
    return resultDict;
}


#pragma mark - 读取优先级逻辑

+ (NSDictionary * _Nullable)gx_loadTemplateContentWithTemplateItem:(GXTemplateItem *)templateItem {
    
    NSDictionary *templateInfo = nil;
    if (![templateItem isAvailable]) {
        return templateInfo;
    }
    
    //读取数据源
    NSArray *templateSources = TheGXRegisterCenter.templateSources;
    for (int i = 0; i < templateSources.count; i++) {
        id <GXTemplateSourceProtocal> source = templateSources[i];
        NSDictionary *tmpTemplateInfo = [source getTemplateInfoWithTemplateItem:templateItem];
        if (tmpTemplateInfo.count > 0) {
            templateInfo = tmpTemplateInfo;
            break;
        }
    }
    
    return templateInfo;
}


@end
