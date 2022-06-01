//
//  GXUtils.m
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

#import "GXUtils.h"
#import "GXCommonDef.h"
#import "GXCssParser.h"
#import "GXFunctionDef.h"
#import "NSDictionary+GX.h"
#import "GXTemplateLoader.h"

@implementation GXUtils

//uuid
+ (NSString *)uuidString
{
    CFUUIDRef uuid = CFUUIDCreate(kCFAllocatorDefault);
    NSString *uuidString = (__bridge NSString*)CFUUIDCreateString(kCFAllocatorDefault, uuid);
    CFRelease(uuid);
    return uuidString;
}

//boolValue
+ (BOOL)boolValue:(id)value {
    if (!value){
        return NO;
    }
    
    //存在时判断逻辑
    if ([value isKindOfClass:[NSNumber class]]) {
        return [(NSNumber *)value boolValue];
        
    } else if ([value isKindOfClass:[NSString class]]) {
        //字符串判断
        NSString *str = (NSString *)value;
        if ([str isEqualToString:@"0"] || [str isEqualToString:@"false"]) {
            return NO;
        } else {
            return str.length > 0 ? YES : NO;
        }
        
    } else if ([value isKindOfClass:[NSArray class]]) {
        //数组依赖count判断
        return ((NSArray *)value).count > 0 ? YES : NO;
        
    } else if ([value isKindOfClass:[NSDictionary class]]) {
        //字典依赖于count判断
        return ((NSDictionary *)value).count > 0 ? YES : NO;
    }
    
    return value ? YES : NO;
}

//字符串是否为数字
+ (BOOL)isNumber:(NSString *)string{
    if([self isPureFloat:string]){
        return YES;
    }
    return NO;
}

//判断是否为整形：
+ (BOOL)isPureInt:(NSString*)string{
    NSScanner* scan = [NSScanner scannerWithString:string];
    int val;
    return[scan scanInt:&val] && [scan isAtEnd];
}

//判断是否为浮点形：
+ (BOOL)isPureFloat:(NSString*)string{
    NSScanner* scan = [NSScanner scannerWithString:string];
    float val;
    return[scan scanFloat:&val] && [scan isAtEnd];
}

//有效字符串
+ (BOOL)isString:(NSString *)str{
    if (str && [str isKindOfClass:[NSString class]]){
        return YES;
    } else {
        return NO;
    }
}

+ (BOOL)isValidString:(NSString *)str{
    if (str && [str isKindOfClass:[NSString class]] && str.length > 0){
        return YES;
    } else {
        return NO;
    }
}

//有效数组
+ (BOOL)isArray:(NSArray *)array{
    if (array && [array isKindOfClass:[NSArray class]]) {
        return YES;
    } else {
        return NO;
    }
}

+ (BOOL)isValidArray:(NSArray *)array{
    if (array && [array isKindOfClass:[NSArray class]] && array.count > 0) {
        return YES;
    } else {
        return NO;
    }
}

+ (BOOL)isMutableArray:(NSArray *)array{
    if (@available(iOS 10.0, *)) {
        return [array isKindOfClass:[NSMutableArray class]];
    } else {
        NSString *classStr = NSStringFromClass(array.class);
        return [classStr isEqualToString:@"__NSArrayyM"];
    }
}

//有效字典
+ (BOOL)isDictionary:(NSDictionary *)dict{
    if (dict && [dict isKindOfClass:[NSDictionary class]]){
        return YES;
    } else {
        return NO;
    }
}

+ (BOOL)isValidDictionary:(NSDictionary *)dict{
    if (dict && [dict isKindOfClass:[NSDictionary class]] && dict.count > 0){
        return YES;
    } else {
        return NO;
    }
}

+ (BOOL)isMutableDictionary:(NSDictionary *)dict{
    if (@available(iOS 10.0, *)) {
        return [dict isKindOfClass:[NSMutableDictionary class]];
    } else {
        NSString *classStr = NSStringFromClass(dict.class);
        return [classStr isEqualToString:@"__NSDictionaryM"];
    }
}

+ (BOOL)isValidMapTable:(NSMapTable *)table{
    if (table && [table isKindOfClass:[NSMapTable class]] && table.count > 0) {
        return YES;
    }
    return NO;
}

//在主线程执行事件
+ (void)executeActionOnMainThread:(dispatch_block_t)block {
    if (block) {
        if ([NSThread isMainThread]) {
            block();
        } else {
            dispatch_async(dispatch_get_main_queue(), ^{
                block();
            });
        }
    }
}

@end


@implementation GXUtils (Css)

// 解析css
+ (NSMutableDictionary *)parserStyleString:(NSString *)styleString{
    if (![self isValidString:styleString]) {
        return nil;
    }
    
    //创建解析
    NSMutableDictionary *styleDictionary = nil;
    //css解析
    GXCssParser *cssParser = [[GXCssParser alloc] init];
    NSDictionary *tmpStyleSheet = [cssParser parse:styleString];
    //判断数据有效性
    if (tmpStyleSheet.count) {
        //创建样式字典
        styleDictionary = [NSMutableDictionary dictionary];
        for (NSString *key in tmpStyleSheet) {//css样式解析，优先解析#，再解析.
            if ([key hasPrefix:kGXComDef_KW_Sharp]) {
                NSString *newKey = [NSString stringWithFormat:@"%@", [key substringFromIndex:1]];
                NSMutableSet *set = tmpStyleSheet[key];
                NSMutableDictionary *dict = [NSMutableDictionary dictionary];
                [set enumerateObjectsUsingBlock:^(GXCssItem *item, BOOL *stop) {
                    [dict gx_setValue:item.propertyValue forKey:item.propertyName];
                }];
                styleDictionary[newKey] = dict;
                
            } else if ([key hasPrefix:kGXComDef_KW_Dot]) {
                NSString *newKey = [NSString stringWithFormat:@"%@", [key substringFromIndex:1]];
                NSMutableSet *set = tmpStyleSheet[key];
                NSMutableDictionary *dict = [NSMutableDictionary dictionary];
                [set enumerateObjectsUsingBlock:^(GXCssItem *item, BOOL *stop) {
                    [dict gx_setValue:item.propertyValue forKey:item.propertyName];
                }];
                styleDictionary[newKey] = dict;
            }
        }
    }
    
    return styleDictionary;
}

@end


@implementation GXUtils (Template)

+ (BOOL)isValidVersion:(NSString *)version{
    if ([self isValidString:version] && ![version isEqualToString:@"(null)"]) {
        float fVersion = [version floatValue];
        if (fVersion >= 0) {
            return YES;
        }
    }
    return NO;
}

/// 读取模板信息
+ (NSDictionary *)loadTemplateContenttWithFolderPath:(NSString *)folderPath
                                          templateId:(NSString *)templateId
                                     templateVersion:(NSString *)templateVersion{
    return [[GXTemplateLoader defaultLoader] loadTemplateContenttWithFolderPath:folderPath
                                                                     templateId:templateId
                                                                templateVersion:templateVersion];
}

@end
