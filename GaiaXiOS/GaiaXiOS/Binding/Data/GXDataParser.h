//
//  GXDataParser.h
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
@class GXExpression;

NS_ASSUME_NONNULL_BEGIN

@interface GXDataParser : NSObject

/// 通过节点的databinding获取真实数据
/// @param data 业务数据
/// @param sourceDict 数据源
+(NSMutableDictionary *)parseData:(NSDictionary *)data withSource:(NSDictionary *)sourceDict;

@end

NS_ASSUME_NONNULL_END
