//
//  GXCacheCenter.m
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

#import "GXCacheCenter.h"

@implementation GXCacheCenter

+ (instancetype)defaulCenter{
    static dispatch_once_t onceToken;
    static GXCacheCenter *center = nil;
    dispatch_once(&onceToken, ^{
        center = [[GXCacheCenter alloc] init];
    });
    return center;
}

- (GXCache *)templateCahche{
    if (!_templateCahche) {
        _templateCahche = [[GXCache alloc] initWithCacheCount:0];
    }
    return _templateCahche;
}


- (GXCache *)expressionCahche{
    if (!_expressionCahche) {
        _expressionCahche = [[GXCache alloc] initWithCacheCount:200];
    }
    return _expressionCahche;
}


- (GXCache *)regularCahche{
    if (!_regularCahche) {
        _regularCahche = [[GXCache alloc] initWithCacheCount:50];
    }
    return _regularCahche;
}

@end
