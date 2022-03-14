//
//  NSArray+GX.m
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

#import "NSArray+GX.h"
#import "NSDictionary+GX.h"

@implementation NSArray (GX)

- (id)gx_objectAtIndex:(NSUInteger)index{
    if (index >= self.count) {
        return nil;
    }
    return [self objectAtIndex:index];
}

- (NSString *)gx_JSONString{
    NSString *jsonString = @"";
    if (!self || ![self isKindOfClass:[NSArray class]] || [self count] == 0) {
        return jsonString;
    }
    
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:self options:NSJSONWritingPrettyPrinted error:&error];
    if (jsonData) {
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    return jsonString;
}

+ (NSArray *)gx_arrayFromJSONString:(NSMutableString *)jsonString;{
    NSArray *array = nil;
    if (jsonString && [jsonString isKindOfClass:[NSString class]] && jsonString.length) {
        //获取data
        NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
        NSError *error;
        if (jsonData && [jsonData isKindOfClass:[NSData class]]) {
            NSArray *tmpArray = [NSJSONSerialization JSONObjectWithData:jsonData
                                                                options:NSJSONReadingMutableContainers
                                                                  error:&error];
            //转换成功
            if (tmpArray && [tmpArray isKindOfClass:[NSArray class]]) {
                array = tmpArray;
            }
        }
    }
    
    return array;
}

- (NSMutableArray *)gx_mutableDeepCopy{
    //初始化
    NSMutableArray *array = [NSMutableArray arrayWithCapacity:self.count];
    //遍历
    [self enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        id objOject = nil;
        if ([obj isKindOfClass:[NSDictionary class]]) {
            //字典深拷贝
            objOject = [obj gx_mutableDeepCopy];
        } else if ([obj isKindOfClass:[NSArray class]]){
            //数组深拷贝
            objOject = [obj gx_mutableDeepCopy];
        } else {
            //直接赋值
            objOject = obj;
        }
        [array gx_addObject:objOject];
    }];
    return array;
}

@end


@implementation NSMutableArray (GX)

-(void)gx_addObject:(id)anObject{
    if (!anObject) {
        return;
    }
    
    [self addObject:anObject];
}

- (void)gx_removeObjectAtIndex:(NSUInteger)index{
    if (index >= self.count) {
        return;
    }
    
    [self removeObjectAtIndex:index];
}

- (void)gx_insertObject:(id)anObject atIndex:(NSUInteger)index{
    if (!anObject || index > self.count) {
        return;
    }
    
    [self insertObject:anObject atIndex:index];
}

- (void)gx_replaceObjectAtIndex:(NSUInteger)index withObject:(id)anObject{
    if (!anObject || index >= self.count) {
        return;
    }
    
    [self replaceObjectAtIndex:index withObject:anObject];
}

@end
