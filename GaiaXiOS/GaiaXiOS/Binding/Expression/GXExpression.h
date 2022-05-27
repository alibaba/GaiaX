//
//  GXExpression.h
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

NS_ASSUME_NONNULL_BEGIN

@interface GXExpression : NSObject

//用于初始化表达式
+ (void)setup;

/// 通过表达式获取值
/// @param expression 表达式内容（number，string类型）
/// @param source 数据源
+ (id)valueWithExpression:(id)expression Source:(NSDictionary *)source;

@end



NS_ASSUME_NONNULL_END
