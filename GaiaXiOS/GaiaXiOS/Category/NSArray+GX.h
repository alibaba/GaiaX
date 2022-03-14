//
//  NSArray+GX.h
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

@interface NSArray (GX)

//Safely get the value by index
- (id)gx_objectAtIndex:(NSUInteger)index;

//array to string
- (NSString *)gx_JSONString;

//string to array
+ (NSArray *)gx_arrayFromJSONString:(NSMutableString *)jsonString;

//deepCopy
- (NSMutableArray *)gx_mutableDeepCopy;

@end

@interface NSMutableArray(GX)

//add object safely
- (void)gx_addObject:(id)anObject;

//remove object safely
- (void)gx_removeObjectAtIndex:(NSUInteger)index;

//insert object safely
- (void)gx_insertObject:(id)anObject atIndex:(NSUInteger)index;

//replace object safely
- (void)gx_replaceObjectAtIndex:(NSUInteger)index withObject:(id)anObject;

@end

NS_ASSUME_NONNULL_END
