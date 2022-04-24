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

@property (nonatomic, strong) NSMutableDictionary *previewSource;

@end

@implementation GaiaXPreviewTemplateSource

- (NSMutableDictionary *)previewSource{
    if (!_previewSource) {
        _previewSource = [NSMutableDictionary dictionary];
    }
    return _previewSource;
}


#pragma mark - GXITemplateSource

- (void)addPreviewTemplate:(NSDictionary *)aTemplate forTemplateId:(NSString *)templateId{
    [self.previewSource gx_setObject:aTemplate forKey:templateId];
}

- (NSDictionary *)getPreviewTemplateWithTemplateId:(NSString *)templateId{
    NSDictionary *resultDict = [self.previewSource gx_dictionaryForKey:templateId];
    return resultDict;
}

- (void)clearPreviewTemplates{
    [self.previewSource removeAllObjects];
}

@end
