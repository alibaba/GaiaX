//
//  NSDictionary+GX.m
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

#import "NSDictionary+GX.h"
#import "NSArray+GX.h"
#import "GXUtils.h"

@implementation NSDictionary (GX)

- (BOOL)gx_boolForKey:(NSString *)key{
    id value = [self objectForKey:key];
    if ([value respondsToSelector:@selector(boolValue)]) {
        return [value boolValue];
    }
    return NO;
}

- (int)gx_intForKey:(NSString *)key{
    id value = [self objectForKey:key];
    if ([value respondsToSelector:@selector(intValue)]) {
        return [value intValue];
    }
    return 0;
}

- (NSInteger)gx_integerForKey:(NSString *)key{
    id value = [self objectForKey:key];
    if ([value respondsToSelector:@selector(integerValue)]) {
        return [value integerValue];
    }
    return 0;
}

- (float)gx_floatForKey:(NSString *)key{
    id value = [self objectForKey:key];
    if ([value respondsToSelector:@selector(floatValue)]) {
        return [value floatValue];
    }
    return 0.0;
}

- (double)gx_doubleForKey:(NSString *)key{
    id value = [self objectForKey:key];
    if ([value respondsToSelector:@selector(doubleValue)]) {
        return [value doubleValue];
    }
    return 0.0;
}

- (NSString * _Nullable)gx_stringForKey:(NSString *)key{
    id value = [self objectForKey:key];
    if ([value isKindOfClass:[NSString class]]) {
        return value;
    } else if ([value respondsToSelector:@selector(stringValue)]) {
        return [value stringValue];
    } else {
        return nil;
    }
}

- (NSString *)gx_safeStringForKey:(NSString *)key{
    NSString *stringValue = [self gx_stringForKey:key];
    return stringValue ? stringValue : @"";
}

- (NSArray *)gx_arrayForKey:(NSString *)key{
    return [self gx_valueForKey:key withClass:[NSArray class]];
}

- (NSMutableArray * _Nullable)gx_mutableArrayForKey:(NSString *)key{
    NSArray *array = [self gx_arrayForKey:key];
    if (array) {
        if ([array isKindOfClass:[NSMutableArray class]]) {
            return (NSMutableArray *)array;
        } else {
            return [NSMutableArray arrayWithArray:array];
        }
    } else {
        return nil;
    }
}

- (NSDictionary *)gx_dictionaryForKey:(NSString *)key{
    return [self gx_valueForKey:key withClass:[NSDictionary class]];
}

- (NSMutableDictionary *)gx_mutableDictionaryForKey:(NSString *)key{
    NSDictionary *dictionary = [self gx_dictionaryForKey:key];
    if (dictionary) {
        if ([GXUtils isMutableDictionary:dictionary]) {
            return (NSMutableDictionary *)dictionary;
        } else {
            return [NSMutableDictionary dictionaryWithDictionary:dictionary];
        }
    } else {
        return nil;
    }
}

//根据key 和 class获取对应的value值
- (id _Nullable)gx_valueForKey:(NSString *)key withClass:(Class)aClass{
    id value = [self objectForKey:key];
    return [value isKindOfClass:aClass] ? value : nil;
}


- (NSString *)gx_JSONString{
    NSString *jsonString = @"";
    if (!self || ![self isKindOfClass:[NSDictionary class]] || [self count] == 0) {
        return jsonString;
    }
    
    //转化
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:self options:NSJSONWritingPrettyPrinted error:&error];
    if (jsonData) {
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    return jsonString;
}

+ (NSDictionary * _Nullable)gx_dictionaryFromJSONString:(NSString *)jsonString{
    NSDictionary *dic = nil;
    if (jsonString && [jsonString isKindOfClass:[NSString class]] && jsonString.length) {
        //获取data
        NSError *error;
        NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
        if (jsonData && [jsonData isKindOfClass:[NSData class]]) {
            NSDictionary *dictionary = [NSJSONSerialization JSONObjectWithData:jsonData
                                                                       options:NSJSONReadingMutableContainers
                                                                         error:&error];
            //数据转化成功
            if (dictionary && [dictionary isKindOfClass:[NSDictionary class]]) {
                dic = dictionary;
            }
        }
    }
    return dic;
}

-(NSMutableDictionary *)gx_mutableDeepCopy{
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:self.count];
    NSArray *keys = self.allKeys;
    for(id key in keys){
        //初始值
        id copyValue = nil;
        id value=[self objectForKey:key];
        if ([value isKindOfClass:[NSDictionary class]]) {
            //字典深拷贝
            copyValue=[value gx_mutableDeepCopy];
        } else if ([value isKindOfClass:[NSArray class]]){
            //数组深拷贝
            copyValue=[value gx_mutableDeepCopy];
        } else {
            //否则直接赋值
            copyValue = value;
        }
        [dict gx_setObject:copyValue forKey:key];
    }
    return dict;
}


- (NSDictionary *)dictionaryByMergingWith:(NSDictionary *) dict {
    return [NSDictionary dictionaryByMerging:self with: dict];
}

+ (NSDictionary *)dictionaryByMerging:(NSDictionary *)dict1 with:(NSDictionary *) dict2 {
    NSMutableDictionary *resultDict = [NSMutableDictionary dictionaryWithDictionary:dict1];
    [dict2 enumerateKeysAndObjectsUsingBlock: ^(id key, id obj, BOOL *stop) {
        if (![dict1 objectForKey:key]) {
            if ([obj isKindOfClass:[NSDictionary class]]) {
                NSDictionary *tmpDict = (NSDictionary *)[dict1 objectForKey: key];
                NSDictionary *newVal = [tmpDict dictionaryByMergingWith:(NSDictionary *)obj];
                [resultDict setObject:newVal forKey:key];
            } else {
                [resultDict setObject:obj forKey: key];
            }
        }
    }];
    return resultDict;
}

@end


@implementation NSMutableDictionary (GX)

-(void)gx_setObject:(id)anObject forKey:(id)aKey{
    if(!aKey || !anObject) {
        return;
    }
    [self setObject:anObject forKey:aKey];
}

-(void)gx_setValue:(id)value forKey:(NSString*)key{
    if(![key isKindOfClass:[NSString class]]) {
        return;
    }
    [self setValue:value forKey:key];
}

@end
