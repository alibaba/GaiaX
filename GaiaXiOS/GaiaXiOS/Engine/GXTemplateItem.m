//
//  GXTemplateItem.m
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

#import "GXTemplateItem.h"
#import "GxFunctionDef.h"
#import "GXUtils.h"

@implementation GXTemplateItem

- (void)setBizId:(NSString *)bizId{
    if ([GXUtils isValidString:bizId]) {
        _bizId = bizId;
    } else {
        _bizId = nil;
    }
}

- (void)setTemplateId:(NSString *)templateId{
    if ([GXUtils isValidString:templateId]) {
        _templateId = templateId;
    } else {
        _templateId = nil;
    }
}

- (void)setTemplateVersion:(NSString *)templateVersion{
    if ([GXUtils isValidVersion:templateVersion]) {
        _templateVersion = templateVersion;
    } else {
        _templateVersion = nil;
    }
}

//标识
- (NSString *)identifier {
    if (!_identifier) {
        if (self.templateId.length > 0) {
            _identifier = [NSString stringWithFormat:@"%@%@", self.templateId, (self.templateVersion ?: @"")];
        }
    }
    return _identifier;
}

//模板是否有效
- (BOOL)isAvailable{
    return self.templateId.length > 0;
}

//模板是否相等
- (BOOL)isEqual:(id)object {
    //不存在 || class不一致
    if (!object || ![object isKindOfClass:[self class]]) {
        return NO;
    }
    
    //地址一样
    if (self == object) {
        return YES;
    }

    //属性判断
    GXTemplateItem *item = object;
    BOOL bizId = _bizId ? [_bizId isEqualToString:item.bizId] : !item.bizId;
    BOOL templateId = _templateId ? [_templateId isEqualToString:item.templateId] : !item.templateId;
    BOOL templateVersion = _templateVersion ? [_templateVersion isEqualToString:item.templateVersion] : !item.templateVersion;
    return bizId && templateId && templateVersion;
}

- (void)dealloc{
    GXLog(@"[GaiaX] GXTemplateItem释放 -- %@", self);
}


@end
