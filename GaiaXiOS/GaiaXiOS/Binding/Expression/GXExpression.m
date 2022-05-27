//
//  GXExpression.m
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

#import "GXExpression.h"
#import "GXNativeAnalyze.h"


@implementation GXExpression

+ (void)setup{
    [GXNativeAnalyze sharedInstance];
}

+ (id)valueWithExpression:(id)expression Source:(NSDictionary *)source{
    id value = [[GXNativeAnalyze sharedInstance] valueWithExpression:expression Source:source];
    return value;
}

@end
