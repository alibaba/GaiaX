//
//  GXUtils.h
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

@interface GXUtils : NSObject

//get uuid
+ (NSString *)uuidString;

//get value is valid
+ (BOOL)boolValue:(id)value;

//the string is a number？
+ (BOOL)isNumber:(NSString *)str;

//is valid string（length > 0）
+ (BOOL)isString:(NSString *)str;
+ (BOOL)isValidString:(NSString *)str;

//is valid array
+ (BOOL)isArray:(NSArray *)array;
+ (BOOL)isValidArray:(NSArray *)array;
+ (BOOL)isMutableArray:(NSArray *)array;

//is valid dictionary
+ (BOOL)isDictionary:(NSDictionary *)dict;
+ (BOOL)isValidDictionary:(NSDictionary *)dict;
+ (BOOL)isMutableDictionary:(NSDictionary *)dict;

//is valid mapTab
+ (BOOL)isValidMapTable:(NSMapTable *)table;

//execute in main thread
+ (void)executeActionOnMainThread:(dispatch_block_t)block;

@end


@interface GXUtils (Css)

//parser css string
+ (NSMutableDictionary *)parserStyleString:(NSString *)styleString;

@end


@interface GXUtils (Template)

//is valid version
+ (BOOL)isValidVersion:(NSString *)version;

@end

NS_ASSUME_NONNULL_END
