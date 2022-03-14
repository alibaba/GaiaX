//
//  NSDictionary+GX.h
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

@interface NSDictionary (GX)

//Returns boolValue，the default is NO
- (BOOL)gx_boolForKey:(NSString *)key;

//Returns intValue，the default is 0
- (int)gx_intForKey:(NSString *)key;
//Returns integerValue，the default is 0
- (NSInteger)gx_integerForKey:(NSString *)key;

//Returns floatValue，the default is 0.0
- (float)gx_floatForKey:(NSString *)key;
//Returns  doubleValue，the default is 0.0
- (double)gx_doubleForKey:(NSString *)key;

//Returns the object stringValue or description, the default is nil
- (NSString * _Nullable)gx_stringForKey:(NSString *)key;

//Returns a non-nil string, the default is @""
- (NSString *)gx_safeStringForKey:(NSString *)key;

//Returns array
- (NSArray *)gx_arrayForKey:(NSString *)key;
- (NSMutableArray * _Nullable)gx_mutableArrayForKey:(NSString *)key;

//Returns dictionary
- (NSDictionary *)gx_dictionaryForKey:(NSString *)key;
- (NSMutableDictionary *)gx_mutableDictionaryForKey:(NSString *)key;

//Returns the value of key and class, default is nil
- (id _Nullable)gx_valueForKey:(NSString *)key withClass:(Class)aClass;

//dictionary to string
- (NSString *)gx_JSONString;
//string to dictionary
+ (NSDictionary * _Nullable)gx_dictionaryFromJSONString:(NSString *)jsonString;

//deepcopy
-(NSMutableDictionary *)gx_mutableDeepCopy;

@end


@interface NSMutableDictionary(GX)

//Set the object safely
-(void)gx_setObject:(id)anObject forKey:(id)aKey;

//Set the value safely
-(void)gx_setValue:(id)value forKey:(NSString*)key;

@end

NS_ASSUME_NONNULL_END
