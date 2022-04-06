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
    NSString *bizId = templateItem.bizId;
    NSString *templateId = templateItem.templateId;
    NSString *templateVersion = templateItem.templateVersion;
    
    //读取模板信息
    NSDictionary *resultDict = nil;
    if (isLocal) {
        //只读取本地模板
        resultDict = [[GXTemplateLoader defaultLoader] loadTemplateInfoWithBizId:bizId templateId:templateId];
    } else {
        //读取预览，远端，本地，以及推送等模板
        resultDict = [self loadTemplateContentWithBizId:bizId templateId:templateId templateVersion:templateVersion];
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

+ (NSDictionary * _Nullable)loadTemplateContentWithBizId:(NSString *)bizId
                                              templateId:(NSString *)templateId
                                         templateVersion:(NSString *)templateVersion {
    
    NSDictionary *templateDict = nil;
    if (![GXUtils isValidString:templateId]) {
        return templateDict;
    }
    
    //预览优先
    id previewSource = TheGXRegisterCenter.previewSource;
    if (previewSource && [previewSource respondsToSelector:@selector(getPreviewTemplateWithTemplateId:)]) {
        templateDict = [previewSource getPreviewTemplateWithTemplateId:templateId];
        if (templateDict) {
            return templateDict;
        }
    }

    // ① 若templateId 与 templateVersion均不为空，则优先返回对应的服务端模板
    if (templateDict.count <= 0) {
        templateDict = [[GXTemplateLoader defaultLoader] loadTemplateInfoWithTemplateId:templateId templateVersion:templateVersion];
    }
    
    // ② 优先返回服务端返回的动态模板，并按照template_version版本号倒序排列
    if (templateDict.count <= 0) {
        templateDict = [[GXTemplateLoader defaultLoader] loadTemplateInfoOrderByTemplateId:templateId];
    }
    
    // ③ bizId不为空,则返回业务Bundle中存放的模板信息
    if (templateDict.count <= 0) {
        bizId = [GXUtils isValidString:bizId] ? bizId : @"GaiaX";
        templateDict = [[GXTemplateLoader defaultLoader] loadTemplateInfoWithBizId:bizId templateId:templateId];
    }
    
    return templateDict;
}


@end
