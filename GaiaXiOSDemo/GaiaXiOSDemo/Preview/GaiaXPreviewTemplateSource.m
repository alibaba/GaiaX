//
//  GaiaXPreviewTemplateSource.m
//  GaiaXiOSDemo
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#import "GaiaXPreviewTemplateSource.h"

@interface GaiaXPreviewTemplateSource ()

@property (nonatomic, strong) NSMutableDictionary *dict;

@end

@implementation GaiaXPreviewTemplateSource

- (NSMutableDictionary *)dict{
    if (!_dict) {
        _dict = [NSMutableDictionary dictionary];
    }
    return _dict;
}


#pragma mark - GXITemplateSource

- (void)addTemplate:(NSDictionary *)aTemplate forTemplateId:(NSString *)templateId{
    [self.dict gx_setObject:aTemplate forKey:templateId];
}

- (NSDictionary *)getTemplateWithTemplateId:(NSString *)templateId{
    NSDictionary *resultDict = [self.dict gx_dictionaryForKey:templateId];
    return resultDict;
}

- (void)clearAllTemplates{
    [self.dict removeAllObjects];
}

@end
